package edu.uno.ai.planning.SATPlan;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class WalkSATTest {
	public static final boolean NEGATED = true;

	WalkSAT solver;

	Variable va;
	Variable vb;
	Variable vc;
	Variable vd;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		// Fix random seed so that we can verify the random functions
		WalkSAT.random = new Random(0);

		solver = new WalkSAT(0, 0, 0);

		va = new Variable("a", false);
		vb = new Variable("b", false);
		vc = new Variable("c", false);
		vd = new Variable("d", false);
	}

	@Test
	public void testVariable() {
		Variable a = new Variable("a", true);
		assertTrue(a.getValue());
		assertTrue(a.isSatisfied());

		Variable b = new Variable("b", false);
		assertFalse(b.getValue());
		assertFalse(b.isSatisfied());

		a.flip();
		b.flip();
		assertFalse(a.isSatisfied());
		assertTrue(b.isSatisfied());

		// We're using fixed random seed. The result has to be the same
		// all the time.
		a.pickRandomValue();
		assertTrue(a.isSatisfied());
		a.pickRandomValue();
		assertTrue(a.isSatisfied());
		a.pickRandomValue();
		assertFalse(a.isSatisfied());

		a.setValue(true);
		assertTrue(a.isSatisfied());
		a.freeze();
		assertTrue(a.isFrozen());

		exception.expect(RuntimeException.class);
		exception.expectMessage("Cannot set value on a frozen variable");
		a.flip();
	}

	@Test
	public void testLiteral() {
		Literal a;

		a = new Literal(new Variable("a", true));
		assertTrue(a.isSatisfied());
		a = new Literal(new Variable("a", false));
		assertFalse(a.isSatisfied());
		a = new Literal(new Variable("a", false), NEGATED);
		assertTrue(a.isSatisfied());
		a = new Literal(new Variable("a", true), NEGATED);
		assertFalse(a.isSatisfied());

		assertFalse(a.isFrozen());
		a.variable.freeze();
		assertTrue(a.isFrozen());
	}

	@Test
	public void testClause() {
		Literal a = new Literal(va);
		Literal b = new Literal(vb);
		Literal c = new Literal(va, NEGATED);

		// Test variable extraction
		Clause clause = new Clause(Arrays.asList(a, b, c));
		assertThat(clause.variables.size(), equalTo(2));
		assertTrue(clause.variables.contains(va));
		assertTrue(clause.variables.contains(vb));

		// Test picking random variable
		// Once again we're using fixed random seed
		assertThat(clause.pickRandomVariable(), is(vb));
		assertThat(clause.pickRandomVariable(), is(vb));
		assertThat(clause.pickRandomVariable(), is(va));

		assertFalse(clause.isEmpty());
		assertTrue((new Clause(new LinkedList<>())).isEmpty());
	}

	@Test
	public void testClauseSatisfiability() {
		Literal a = new Literal(va);
		Literal b = new Literal(vb);
		Literal c = new Literal(new Variable("c", true), NEGATED);

		Clause clause = new Clause(Arrays.asList(a, b, c));
		assertFalse(clause.isSatisfied());
		a.variable.flip();
		assertFalse(clause.satisfied()); // cached information
		assertTrue(clause.isSatisfied());
		assertTrue(clause.satisfied());

		clause = new Clause(new LinkedList<>());
		assertTrue(clause.isSatisfied());
	}

	@Test
	public void testClausePurification() {
		Clause clause;

		// Find pure and freeze
		clause = new Clause(Arrays.asList(new Literal(va), new Literal(vb)));
		assertFalse(clause.isPure());
		assertFalse(clause.findPureAndFreeze());
		clause = new Clause(Collections.singletonList(new Literal(va)));
		assertTrue(clause.isPure());
		assertTrue(clause.findPureAndFreeze());
		assertTrue(clause.isSatisfied());
		assertTrue(va.isSatisfied());
		assertTrue(va.isFrozen());

		// Find pure and freeze (but it's already frozen and the clause becomes
		// unsatisfiable)
		vc.freeze();
		clause = new Clause(Collections.singletonList(new Literal(vc)));
		assertTrue(clause.findPureAndFreeze());
		assertFalse(vc.getValue());
		assertFalse(clause.isSatisfied());
		assertTrue(clause.isUnsatisfiable());

		// Test unsatisfiability
		clause = new Clause(Collections.singletonList(new Literal(vb)));
		vb.setValue(false);
		assertFalse(clause.isUnsatisfiable());
		vb.freeze();
		assertTrue(clause.isUnsatisfiable());

		// This clause is clearly unsatisfiable but the Clause#isUnsatisfiable
		// method still returns false. It's purpose is only to detect simple
		// pure clauses.
		clause = new Clause(Arrays.asList(new Literal(va), new Literal(va, NEGATED)));
		assertFalse(clause.isUnsatisfiable());

		// Remove frozen
		va = new Variable("a", false);
		vb = new Variable("b", false);
		vc = new Variable("c", false);

		clause = new Clause(Arrays.asList(new Literal(va), new Literal(vb), new Literal(vc)));
		va.freeze();
		clause = clause.removeFrozen();
		assertThat(clause.variables.size(), equalTo(2));
		assertTrue(clause.variables.contains(vb));
		assertTrue(clause.variables.contains(vc));

		// The frozen literal is satisfied, therefore the clause is satisfied
		// and can by emptied.
		clause = new Clause(Arrays.asList(new Literal(va), new Literal(vb, NEGATED), new Literal(vc)));
		vb.freeze();
		clause = clause.removeFrozen();
		assertTrue(clause.isEmpty());
	}

	@Test
	public void testProblem() {
		Clause c1 = new Clause(Collections.singletonList(new Literal(va)));
		Clause c2 = new Clause(Collections.singletonList(new Literal(vb, NEGATED)));
		Clause c3 = new Clause(Arrays.asList(new Literal(vc), new Literal(vd, NEGATED)));

		Problem problem = new Problem(Arrays.asList(c1, c2, c3));
		assertFalse(problem.isSatisfied());
		assertThat(problem.getUnsatisfiedClause(), is(c1));
		assertThat(problem.variables, is(problem.getSolution()));
		assertThat(problem.variables.size(), is(4));
		assertTrue(problem.variables.containsAll(Arrays.asList(va, vb, vc, vd)));

		va.flip();
		assertTrue(problem.isSatisfied());
		exception.expect(RuntimeException.class);
		exception.expectMessage("No unsatisfied clause. This method should not be called");
		problem.getUnsatisfiedClause();
	}

	@Test
	public void testProblemPickLeastDamagingVariable() {
		Clause c1 = new Clause(Arrays.asList(new Literal(va), new Literal(vb, NEGATED)));
		Clause c2 = new Clause(Arrays.asList(new Literal(vb), new Literal(vc, NEGATED)));
		Clause c3 = new Clause(Arrays.asList(new Literal(va), new Literal(vb), new Literal(vc)));

		assertTrue(c1.isSatisfied());
		assertTrue(c2.isSatisfied());
		assertFalse(c3.isSatisfied());

		Problem problem = new Problem(Arrays.asList(c1, c2, c3));
		assertFalse(problem.isSatisfied());
		assertThat(problem.pickLeastDamagingVariable(c1), is(va));

		assertFalse(problem.isSatisfied());
		assertThat(problem.pickLeastDamagingVariable(c2), anyOf(is(vc), is(vb)));

		assertFalse(problem.isSatisfied());
		assertThat(problem.pickLeastDamagingVariable(c3), is(va));
	}

	@Test
	public void testPurifyCompletely() {
		// Conjunction that should completely disappear
		Clause c1 = new Clause(Collections.singletonList(new Literal(va, NEGATED)));
		Clause c2 = new Clause(Arrays.asList(new Literal(va), new Literal(vb, NEGATED)));
		Clause c3 = new Clause(Arrays.asList(new Literal(va), new Literal(vb), new Literal(vc)));

		Problem problem = solver.purify(new Problem(Arrays.asList(c1, c2, c3)));
		assertThat(problem.clauses.size(), is(0));
		assertThat(solver.pures.size(), is(3));
		assertFalse(va.getValue());
		assertFalse(vb.getValue());
		assertTrue(vc.getValue());
		assertTrue(va.isFrozen());
		assertTrue(vb.isFrozen());
		assertTrue(vc.isFrozen());
	}

	@Test
	public void testPurifyUnsatisfiable() {
		// Unsatisfiable conjunction
		Clause c1 = new Clause(Collections.singletonList(new Literal(va, NEGATED)));
		Clause c2 = new Clause(Collections.singletonList(new Literal(va)));
		Problem problem = solver.purify(new Problem(Arrays.asList(c1, c2)));
		assertThat(problem, equalTo(null));

		// Another unsatisfiable conjunction
		c1 = new Clause(Collections.singletonList(new Literal(va, NEGATED)));
		c2 = new Clause(Collections.singletonList(new Literal(vb)));
		Clause c3 = new Clause(Arrays.asList(new Literal(va), new Literal(vb, NEGATED)));
		problem = solver.purify(new Problem(Arrays.asList(c1, c2, c3)));
		assertThat(problem, equalTo(null));

	}

	@Test
	public void testPurifyWithSomethingLeft() {
		Clause c1 = new Clause(Collections.singletonList(new Literal(va)));
		Clause c2 = new Clause(Arrays.asList(new Literal(va, NEGATED), new Literal(vb, NEGATED)));
		Clause c3 = new Clause(Arrays.asList(new Literal(va, NEGATED), new Literal(vb), new Literal(vc), new Literal(vd)));
		Problem problem = solver.purify(new Problem(Arrays.asList(c1, c2, c3)));
		assertThat(problem.clauses.size(), is(1));
		assertTrue(problem.variables.contains(vc));
		assertTrue(problem.variables.contains(vd));
		assertThat(solver.pures.size(), is(2));
		assertTrue(va.getValue());
		assertFalse(vb.getValue());
		assertTrue(va.isFrozen());
		assertTrue(vb.isFrozen());
	}
}
