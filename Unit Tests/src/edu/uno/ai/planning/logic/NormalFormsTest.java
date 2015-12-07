package edu.uno.ai.planning.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.uno.ai.planning.Settings;

/**
 * Utility methods for converting expressions to conjunctive/disjunctive normal
 * forms.
 * 
 * @author Edward Thomas Garcia
 * 
 *         Note: Conjunctive Normal Form is defined as (or NormalForms.toCNF
 *         returns) a Conjunction of Disjunctive Clauses Disjunctive Normal Form
 *         is defined as (or NormalForms.toDNF returns) a Disjunction of
 *         Conjunctive Clauses
 */

public class NormalFormsTest extends NormalForms {

	// Test Constants
	Constant constA = new Constant(Settings.DEFAULT_TYPE, "A");
	Constant constB = new Constant(Settings.DEFAULT_TYPE, "B");
	Constant constC = new Constant(Settings.DEFAULT_TYPE, "C");

	// Test Variables
	Variable varX = new Variable(Settings.DEFAULT_TYPE, "X");
	Variable varY = new Variable(Settings.DEFAULT_TYPE, "Y");
	Variable varZ = new Variable(Settings.DEFAULT_TYPE, "Z");

	// Test Literals
	Predication A = new Predication("test", constA); // aka test(A)
	Predication B = new Predication("test", constB);
	Predication C = new Predication("test", constC);
	Predication X = new Predication("test", varX);
	Predication Y = new Predication("test", varY);
	Predication Z = new Predication("test", varZ);

	// Test Negated Literals
	NegatedLiteral notA = A.negate();
	NegatedLiteral notB = B.negate();
	NegatedLiteral notC = C.negate();
	NegatedLiteral notX = X.negate();
	NegatedLiteral notY = Y.negate();
	NegatedLiteral notZ = Z.negate();

	// Test Empty Objects
	Expression nullExpression = null;
	Conjunction emptyConjunction = new Conjunction(nullExpression);
	Disjunction emptyDisjunction = new Disjunction(nullExpression);
	Negation emptyNegation = new Negation(nullExpression);

	// Test Double Negation
	Negation notNotA = new Negation(notA);
	Negation notNotB = new Negation(notB);
	Negation notNotC = new Negation(notC);
	Negation notNotX = new Negation(notX);
	Negation notNotY = new Negation(notY);
	Negation notNotZ = new Negation(notZ);

	// Test Conjunctions
	Conjunction AB = new Conjunction(A, B);
	Conjunction ABC = new Conjunction(A, B, C);
	Conjunction AX = new Conjunction(A, X);
	Conjunction XY = new Conjunction(X, Y);
	Conjunction XYZ = new Conjunction(X, Y, Z);
	Negation not_AB_ = new Negation(AB);
	Negation not_ABC_ = new Negation(ABC);
	Negation not_AX_ = new Negation(AX);
	Negation not_XY_ = new Negation(XY);
	Negation not_XYZ_ = new Negation(XYZ);

	// Test Disjunctions
	Disjunction AvB = new Disjunction(A, B);
	Disjunction AvBvC = new Disjunction(A, B, C);
	Disjunction AvX = new Disjunction(A, X);
	Disjunction XvY = new Disjunction(X, Y);
	Disjunction XvYvZ = new Disjunction(X, Y, Z);
	Negation not_AvB_ = new Negation(AvB);
	Negation not_AvBvC_ = new Negation(AvBvC);
	Negation not_AvX_ = new Negation(AvX);
	Negation not_XvY_ = new Negation(XvY);
	Negation not_XvYvZ_ = new Negation(XvYvZ);

	// Test Other Boolean Expressions
	Conjunction notANotB = new Conjunction(notA, notB);
	Disjunction notAvNotB = new Disjunction(notA, notB);
	Disjunction notAvNotX = new Disjunction(notA, notX);
	Disjunction notAvNotBvNotC = new Disjunction(notA, notB, notC);
	Conjunction notANotX = new Conjunction(notA, notX);
	Conjunction notANotBNotC = new Conjunction(notA, notB, notC);
	Disjunction _AB_vCv_XYZ_ = new Disjunction(AB, C, XYZ);
	Disjunction _ANotB_v_XY_v_BXNotY_ = new Disjunction(new Conjunction(A, notB), XY, new Conjunction(B, X, notY));
	Conjunction _AvB__NotBvCvNotX__XvNotY_ = new Conjunction(new Disjunction(A, B), new Disjunction(notB, C, notX),
			new Disjunction(X, notY));

	@Test
	public void isLiteralTest() {
		assertTrue(isLiteral(A));
		assertTrue(isLiteral(X));
		assertTrue(isLiteral(notA));
		assertTrue(isLiteral(notX));
		assertFalse(isLiteral(nullExpression));
		assertFalse(isLiteral(emptyConjunction));
		assertFalse(isLiteral(emptyDisjunction));
		assertFalse(isLiteral(emptyNegation));
		assertFalse(isLiteral(notNotA));
		assertFalse(isLiteral(notNotX));
		assertFalse(isLiteral(AB));
		assertFalse(isLiteral(AX));
		assertFalse(isLiteral(ABC));
		assertFalse(isLiteral(AvB));
		assertFalse(isLiteral(AvX));
		assertFalse(isLiteral(AvBvC));
		assertFalse(isLiteral(not_AB_));
		assertFalse(isLiteral(not_AX_));
		assertFalse(isLiteral(not_ABC_));
		assertFalse(isLiteral(not_AvB_));
		assertFalse(isLiteral(not_AvX_));
		assertFalse(isLiteral(not_AvBvC_));
		assertFalse(isLiteral(notANotB));
		assertFalse(isLiteral(notAvNotB));
		assertFalse(isLiteral(_AB_vCv_XYZ_));
		assertFalse(isLiteral(_ANotB_v_XY_v_BXNotY_));
		assertFalse(isLiteral(_AvB__NotBvCvNotX__XvNotY_));
		assertFalse(isLiteral(A.toCNF()));
		assertFalse(isLiteral(A.toDNF()));
	}

	@Test
	public void isClauseTest() {
		assertTrue(isClause(new Disjunction(X)));
		assertTrue(isClause(new Conjunction(notX)));
		assertTrue(isClause(AB));
		assertTrue(isClause(AX));
		assertTrue(isClause(ABC));
		assertTrue(isClause(AvB));
		assertTrue(isClause(AvX));
		assertTrue(isClause(AvBvC));
		assertTrue(isClause(notANotB));
		assertTrue(isClause(notAvNotB));
		assertFalse(isClause(nullExpression));
		assertFalse(isClause(emptyConjunction));
		assertFalse(isClause(emptyDisjunction));
		assertFalse(isClause(emptyNegation));
		assertFalse(isClause(A));
		assertFalse(isClause(X));
		assertFalse(isClause(notA));
		assertFalse(isClause(notX));
		assertFalse(isClause(notNotA));
		assertFalse(isClause(notNotX));
		assertFalse(isClause(not_AB_));
		assertFalse(isClause(not_AX_));
		assertFalse(isClause(not_ABC_));
		assertFalse(isClause(not_AvB_));
		assertFalse(isClause(not_AvX_));
		assertFalse(isClause(not_AvBvC_));
		assertFalse(isClause(_AB_vCv_XYZ_));
		assertFalse(isClause(_ANotB_v_XY_v_BXNotY_));
		assertFalse(isClause(_AvB__NotBvCvNotX__XvNotY_));
		assertFalse(isClause(A.toCNF()));
		assertFalse(isClause(A.toDNF()));
	}

	@Test
	public void isConjunctiveClauseTest() {
		assertTrue(isConjunctiveClause(new Conjunction(notX)));
		assertTrue(isConjunctiveClause(AB));
		assertTrue(isConjunctiveClause(AX));
		assertTrue(isConjunctiveClause(ABC));
		assertTrue(isConjunctiveClause(notANotB));
		assertFalse(isConjunctiveClause(nullExpression));
		assertFalse(isConjunctiveClause(emptyConjunction));
		assertFalse(isConjunctiveClause(emptyDisjunction));
		assertFalse(isConjunctiveClause(emptyNegation));
		assertFalse(isConjunctiveClause(A));
		assertFalse(isConjunctiveClause(X));
		assertFalse(isConjunctiveClause(new Disjunction(X)));
		assertFalse(isConjunctiveClause(notA));
		assertFalse(isConjunctiveClause(notX));
		assertFalse(isConjunctiveClause(notNotA));
		assertFalse(isConjunctiveClause(notNotX));
		assertFalse(isConjunctiveClause(AvB));
		assertFalse(isConjunctiveClause(AvX));
		assertFalse(isConjunctiveClause(AvBvC));
		assertFalse(isConjunctiveClause(not_AB_));
		assertFalse(isConjunctiveClause(not_AX_));
		assertFalse(isConjunctiveClause(not_ABC_));
		assertFalse(isConjunctiveClause(not_AvB_));
		assertFalse(isConjunctiveClause(not_AvX_));
		assertFalse(isConjunctiveClause(not_AvBvC_));
		assertFalse(isConjunctiveClause(notAvNotB));
		assertFalse(isConjunctiveClause(_AB_vCv_XYZ_));
		assertFalse(isConjunctiveClause(_ANotB_v_XY_v_BXNotY_));
		assertFalse(isConjunctiveClause(_AvB__NotBvCvNotX__XvNotY_));
		assertFalse(isConjunctiveClause(A.toCNF()));
		assertFalse(isConjunctiveClause(A.toDNF()));
	}

	@Test
	public void isDisjunctiveClauseTest() {
		assertTrue(isDisjunctiveClause(new Disjunction(X)));
		assertTrue(isDisjunctiveClause(new Disjunction(notX)));
		assertTrue(isDisjunctiveClause(AvB));
		assertTrue(isDisjunctiveClause(AvX));
		assertTrue(isDisjunctiveClause(AvBvC));
		assertTrue(isDisjunctiveClause(notAvNotB));
		assertFalse(isDisjunctiveClause(nullExpression));
		assertFalse(isDisjunctiveClause(emptyConjunction));
		assertFalse(isDisjunctiveClause(emptyDisjunction));
		assertFalse(isDisjunctiveClause(emptyNegation));
		assertFalse(isDisjunctiveClause(A));
		assertFalse(isDisjunctiveClause(X));
		assertFalse(isDisjunctiveClause(notA));
		assertFalse(isDisjunctiveClause(notX));
		assertFalse(isDisjunctiveClause(notNotA));
		assertFalse(isDisjunctiveClause(notNotX));
		assertFalse(isDisjunctiveClause(AB));
		assertFalse(isDisjunctiveClause(AX));
		assertFalse(isDisjunctiveClause(ABC));
		assertFalse(isDisjunctiveClause(not_AB_));
		assertFalse(isDisjunctiveClause(not_AX_));
		assertFalse(isDisjunctiveClause(not_ABC_));
		assertFalse(isDisjunctiveClause(not_AvB_));
		assertFalse(isDisjunctiveClause(not_AvX_));
		assertFalse(isDisjunctiveClause(not_AvBvC_));
		assertFalse(isDisjunctiveClause(notANotB));
		assertFalse(isDisjunctiveClause(_AB_vCv_XYZ_));
		assertFalse(isDisjunctiveClause(_ANotB_v_XY_v_BXNotY_));
		assertFalse(isDisjunctiveClause(_AvB__NotBvCvNotX__XvNotY_));
		assertFalse(isDisjunctiveClause(A.toCNF()));
		assertFalse(isDisjunctiveClause(A.toDNF()));
	}

	@Test
	public void isCNFTest() {
		assertTrue(isCNF(new Conjunction(new Disjunction(notX))));
		assertTrue(isCNF(new Conjunction(new Disjunction(A), new Disjunction(B))));
		assertTrue(isCNF(new Conjunction(AvB)));
		assertTrue(isCNF(_AvB__NotBvCvNotX__XvNotY_));
		assertTrue(isCNF(A.toCNF()));
		assertFalse(isCNF(nullExpression));
		assertFalse(isCNF(emptyConjunction));
		assertFalse(isCNF(emptyDisjunction));
		assertFalse(isCNF(emptyNegation));
		assertFalse(isCNF(A));
		assertFalse(isCNF(X));
		assertFalse(isCNF(new Disjunction(X)));
		assertFalse(isCNF(notA));
		assertFalse(isCNF(notX));
		assertFalse(isCNF(new Conjunction(notX)));
		assertFalse(isCNF(notNotA));
		assertFalse(isCNF(notNotX));
		assertFalse(isCNF(AB));
		assertFalse(isCNF(AX));
		assertFalse(isCNF(ABC));
		assertFalse(isCNF(AvB));
		assertFalse(isCNF(AvX));
		assertFalse(isCNF(AvBvC));
		assertFalse(isCNF(not_AB_));
		assertFalse(isCNF(not_AX_));
		assertFalse(isCNF(not_ABC_));
		assertFalse(isCNF(not_AvB_));
		assertFalse(isCNF(not_AvX_));
		assertFalse(isCNF(not_AvBvC_));
		assertFalse(isCNF(notANotB));
		assertFalse(isCNF(notAvNotB));
		assertFalse(isCNF(_AB_vCv_XYZ_));
		assertFalse(isCNF(_ANotB_v_XY_v_BXNotY_));
		assertFalse(isCNF(A.toDNF()));

		assertTrue(isCNF(notNotA.toCNF()));
		assertTrue(isCNF(_AB_vCv_XYZ_.toCNF()));
		assertTrue(isCNF(_ANotB_v_XY_v_BXNotY_.toCNF()));
		assertTrue(isCNF(_AvB__NotBvCvNotX__XvNotY_.toCNF()));
	}

	@Test
	public void isDNFTest() {
		assertTrue(isDNF(new Disjunction(new Conjunction(notX))));
		assertTrue(isDNF(new Disjunction(AB)));
		assertTrue(isDNF(new Disjunction(new Conjunction(A), new Conjunction(B))));
		assertTrue(isDNF(_ANotB_v_XY_v_BXNotY_));
		assertTrue(isDNF(A.toDNF()));
		assertFalse(isDNF(nullExpression));
		assertFalse(isDNF(emptyConjunction));
		assertFalse(isDNF(emptyDisjunction));
		assertFalse(isDNF(emptyNegation));
		assertFalse(isDNF(A));
		assertFalse(isDNF(X));
		assertFalse(isDNF(new Disjunction(X)));
		assertFalse(isDNF(notA));
		assertFalse(isDNF(notX));
		assertFalse(isDNF(new Conjunction(notX)));
		assertFalse(isDNF(notNotA));
		assertFalse(isDNF(notNotX));
		assertFalse(isDNF(AB));
		assertFalse(isDNF(AX));
		assertFalse(isDNF(ABC));
		assertFalse(isDNF(AvB));
		assertFalse(isDNF(AvX));
		assertFalse(isDNF(AvBvC));
		assertFalse(isDNF(not_AB_));
		assertFalse(isDNF(not_AX_));
		assertFalse(isDNF(not_ABC_));
		assertFalse(isDNF(not_AvB_));
		assertFalse(isDNF(not_AvX_));
		assertFalse(isDNF(not_AvBvC_));
		assertFalse(isDNF(notANotB));
		assertFalse(isDNF(notAvNotB));
		assertFalse(isDNF(_AB_vCv_XYZ_));
		assertFalse(isDNF(_AvB__NotBvCvNotX__XvNotY_));
		assertFalse(isDNF(A.toCNF()));

		assertTrue(isDNF(notNotA.toDNF()));
		assertTrue(isDNF(_AB_vCv_XYZ_.toDNF()));
		assertTrue(isDNF(_ANotB_v_XY_v_BXNotY_.toDNF()));
		assertTrue(isDNF(_AvB__NotBvCvNotX__XvNotY_.toDNF()));
	}

	@Test
	public void toCNFTest() {
		assertNull(toCNF((Conjunction) nullExpression));
		// assertEquals(emptyConjunction, toCNF(emptyConjunction)); // TODO
		// Failing Edge Case: Expression's checking equals, otherNAB is NULL
		// assertEquals(emptyConjunction, toCNF(emptyDisjunction)); // TODO
		// Failing Edge Case: Expression's checking equals, otherNAB is NULL
		// assertEquals(null, toCNF((Disjunction)nullExpression)); // TODO
		// Failing Edge Case: Expression's checking equals, otherNAB is NULL
		assertEquals(toCNF(new Conjunction(A)), new Conjunction(new Disjunction(A)));
		assertEquals(toCNF(new Conjunction(X)), new Conjunction(new Disjunction(X)));
		assertEquals(toCNF(new Disjunction(A)), new Conjunction(new Disjunction(A)));
		assertEquals(toCNF(new Disjunction(X)), new Conjunction(new Disjunction(X)));
		assertEquals(toCNF(new Conjunction(notA)), new Conjunction(new Disjunction(notA)));
		assertEquals(toCNF(new Conjunction(notX)), new Conjunction(new Disjunction(notX)));
		assertEquals(toCNF(new Conjunction(notNotA)), new Conjunction(new Disjunction(A)));
		assertEquals(toCNF(new Conjunction(notNotX)), new Conjunction(new Disjunction(X)));
		assertNotEquals(toCNF(AB), AB);
		assertEquals(toCNF(AB), new Conjunction(new Disjunction(A), new Disjunction(B)));
		assertEquals(toCNF(AX), new Conjunction(new Disjunction(A), new Disjunction(X)));
		assertEquals(toCNF(ABC), new Conjunction(new Disjunction(A), new Disjunction(B), new Disjunction(C)));
		assertEquals(toCNF(AvB), new Conjunction(AvB));
		assertEquals(toCNF(AvX), new Conjunction(AvX));
		assertEquals(toCNF(AvBvC), new Conjunction(AvBvC));
		assertEquals(toCNF(new Conjunction(not_AB_)), new Conjunction(notAvNotB));
		assertEquals(toCNF(new Conjunction(not_AX_)), new Conjunction(notAvNotX));
		assertEquals(toCNF(new Conjunction(not_ABC_)), new Conjunction(notAvNotBvNotC));
		assertEquals(toCNF(new Conjunction(not_AvB_)), new Conjunction(new Disjunction(notA), new Disjunction(notB)));
		assertEquals(toCNF(new Conjunction(not_AvX_)), new Conjunction(new Disjunction(notA), new Disjunction(notX)));
		assertEquals(toCNF(new Conjunction(not_AvBvC_)),
				new Conjunction(new Disjunction(notA), new Disjunction(notB), new Disjunction(notC)));
		assertEquals(toCNF(notANotB), new Conjunction(new Disjunction(notA), new Disjunction(notB)));
		assertEquals(toCNF(notAvNotB), new Conjunction(notAvNotB));
		assertEquals(toCNF(_AB_vCv_XYZ_),
				new Conjunction(new Disjunction(X, A, C), new Disjunction(Y, A, C), new Disjunction(Z, A, C),
						new Disjunction(X, B, C), new Disjunction(Y, B, C), new Disjunction(Z, B, C)));
		assertEquals(toCNF(_ANotB_v_XY_v_BXNotY_),
				new Conjunction(new Disjunction(X, A), new Disjunction(B, Y, A), new Disjunction(X, notB)));
		assertEquals(toCNF(_AvB__NotBvCvNotX__XvNotY_), _AvB__NotBvCvNotX__XvNotY_);
	}

	@Test
	public void toDNFTest() {
		assertEquals(toDNF((Disjunction) nullExpression), null);
		// assertEquals(toDNF(emptyConjunction), emptyConjunction); // TODO
		// Failing Edge Case: Expression's checking equals, otherNAB is NULL
		// assertEquals(toDNF(emptyDisjunction), emptyConjunction); // TODO
		// Failing Edge Case: Expression's checking equals, otherNAB is NULL
		// assertEquals(toDNF((Conjunction)nullExpression), null); // TODO
		// Failing Edge Case: Expression's checking equals, otherNAB is NULL
		assertEquals(toDNF(new Conjunction(A)), new Disjunction(new Conjunction(A)));
		assertEquals(toDNF(new Conjunction(X)), new Disjunction(new Conjunction(X)));
		assertEquals(toDNF(new Disjunction(A)), new Disjunction(new Conjunction(A)));
		assertEquals(toDNF(new Disjunction(X)), new Disjunction(new Conjunction(X)));
		assertEquals(toDNF(new Conjunction(notA)), new Disjunction(new Conjunction(notA)));
		assertEquals(toDNF(new Conjunction(notX)), new Disjunction(new Conjunction(notX)));
		assertEquals(toDNF(new Conjunction(notNotA)), new Disjunction(new Conjunction(A)));
		assertEquals(toDNF(new Conjunction(notNotX)), new Disjunction(new Conjunction(X)));
		assertEquals(toDNF(AB), new Disjunction(AB));
		assertEquals(toDNF(AX), new Disjunction(AX));
		assertEquals(toDNF(ABC), new Disjunction(ABC));
		assertEquals(toDNF(AvB), new Disjunction(new Conjunction(A), new Conjunction(B)));
		assertNotEquals(toDNF(AvB), AvB);
		assertEquals(toDNF(AvX), new Disjunction(new Conjunction(A), new Conjunction(X)));
		assertEquals(toDNF(AvBvC), new Disjunction(new Conjunction(A), new Conjunction(B), new Conjunction(C)));
		assertEquals(toDNF(new Conjunction(not_AB_)), new Disjunction(new Conjunction(notA), new Conjunction(notB)));
		assertEquals(toDNF(new Conjunction(not_AX_)), new Disjunction(new Conjunction(notA), new Conjunction(notX)));
		assertEquals(toDNF(new Conjunction(not_ABC_)),
				new Disjunction(new Conjunction(notA), new Conjunction(notB), new Conjunction(notC)));
		assertEquals(toDNF(new Conjunction(not_AvB_)), new Disjunction(notANotB));
		assertEquals(toDNF(new Conjunction(not_AvX_)), new Disjunction(notANotX));
		assertEquals(toDNF(new Conjunction(not_AvBvC_)), new Disjunction(notANotBNotC));
		assertEquals(toDNF(notANotB), new Disjunction(notANotB));
		assertEquals(toDNF(notAvNotB), new Disjunction(new Conjunction(notA), new Conjunction(notB)));
		assertEquals(toDNF(_AB_vCv_XYZ_), new Disjunction(AB, new Conjunction(C), XYZ));
		assertEquals(_ANotB_v_XY_v_BXNotY_, toDNF(_ANotB_v_XY_v_BXNotY_));
		assertEquals(
				new Disjunction(new Conjunction(X, notB, A), new Conjunction(notY, notB, A), new Conjunction(X, C, A),
						new Conjunction(notY, C, A), new Conjunction(notY, notX, A), new Conjunction(X, C, B),
						new Conjunction(notY, C, B), new Conjunction(notY, notX, B)),
				toDNF(_AvB__NotBvCvNotX__XvNotY_));
		// (A || B) && (~B || C || ~X) && (X || ~Y)
		// (A && ~B && X) || (A && ~B && ~Y) || (A && C && X) || (A && C && ~Y)
		// || (A && ~X && ~Y) || (B && C && X) || (B && C && ~Y) || (B && ~X &&
		// ~Y)
	}
}