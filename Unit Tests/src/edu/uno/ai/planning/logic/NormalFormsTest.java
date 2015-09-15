/**
 * Utility methods for converting expressions to conjunctive normal form.
 * 
 * @author Edward Thomas Garcia
 * 
 * Notes:
 *  Conjunctive Normal Form is defined as (or NormalForms.toCNF returns) a Conjunction of Disjunctive Clauses
 *  Disjunctive Normal Form is defined as (or NormalForms.toDNF returns) a Disjunction of Conjunctive Clauses
 */
package edu.uno.ai.planning.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uno.ai.planning.Settings;

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
	
	// Test Double Negation
	Negation notNotA = new Negation(notA);
	Negation notNotB = new Negation(notB);
	Negation notNotC = new Negation(notC);
	Negation notNotX = new Negation(notX);
	Negation notNotY = new Negation(notY);
	Negation notNotZ = new Negation(notZ);
	
	// Test Conjunctions
	Conjunction AB = new Conjunction(A,B);
	Conjunction ABC = new Conjunction(A,B,C);
	Conjunction AX = new Conjunction(A,X);
	Conjunction XY = new Conjunction(X,Y);
	Conjunction XYZ = new Conjunction(X,Y,Z);
	Negation notAB = new Negation(AB);
	Negation notABC = new Negation(ABC);
	Negation notAX = new Negation(AX);
	Negation notXY = new Negation(XY);
	Negation notXYZ = new Negation(XYZ);
	
	// Test Disjunction
	Disjunction AvB = new Disjunction(A,B);
	Disjunction AvBvC = new Disjunction(A,B,C);
	Disjunction AvX = new Disjunction(A,X);
	Disjunction XvY = new Disjunction(X,Y);
	Disjunction XvYvZ = new Disjunction(X,Y,Z);
	Negation notAvB = new Negation(AvB);
	Negation notAvBvC = new Negation(AvBvC);
	Negation notAvX = new Negation(AvX);
	Negation notXvY = new Negation(XvY);
	Negation notXvYvZ = new Negation(XvYvZ);
	
	// Test Other Boolean Expressions
	Conjunction notANotB = new Conjunction(notA, notB);
	Disjunction notAvNotB = new Disjunction(notA, notB);
	Disjunction notAvNotX = new Disjunction(notA, notX);
	Disjunction notAvNotBvNotC = new Disjunction(notA, notB, notC);
	Conjunction notANotX = new Conjunction(notA, notX);
	Conjunction notANotBNotC = new Conjunction(notA, notB, notC);
	
	@Test
	public void isLiteralTest() {
		assertEquals(true, isLiteral(A));
		assertEquals(true, isLiteral(X));
		assertEquals(true, isLiteral(notA));
		assertEquals(true, isLiteral(notX));
		assertEquals(false, isLiteral(notNotA));
		assertEquals(false, isLiteral(notNotX));
		assertEquals(false, isLiteral(AB));
		assertEquals(false, isLiteral(AX));
		assertEquals(false, isLiteral(ABC));
		assertEquals(false, isLiteral(AvB));
		assertEquals(false, isLiteral(AvX));
		assertEquals(false, isLiteral(AvBvC));
		assertEquals(false, isLiteral(notAB));
		assertEquals(false, isLiteral(notAX));
		assertEquals(false, isLiteral(notABC));
		assertEquals(false, isLiteral(notAvB));
		assertEquals(false, isLiteral(notAvX));
		assertEquals(false, isLiteral(notAvBvC));
		assertEquals(false, isLiteral(notANotB));
		assertEquals(false, isLiteral(notAvNotB));
	}
	
	@Test
	public void isClauseTest() {
		assertEquals(true, isClause(A));
		assertEquals(true, isClause(X));
		assertEquals(true, isClause(notA));
		assertEquals(true, isClause(notX));
		assertEquals(false, isClause(notNotA));
		assertEquals(false, isClause(notNotX));
		assertEquals(true, isClause(AB));
		assertEquals(true, isClause(AX));
		assertEquals(true, isClause(ABC));
		assertEquals(true, isClause(AvB));
		assertEquals(true, isClause(AvX));
		assertEquals(true, isClause(AvBvC));
		assertEquals(false, isClause(notAB));
		assertEquals(false, isClause(notAX));
		assertEquals(false, isClause(notABC));
		assertEquals(false, isClause(notAvB));
		assertEquals(false, isClause(notAvX));
		assertEquals(false, isClause(notAvBvC));
		assertEquals(true, isClause(notANotB));
		assertEquals(true, isClause(notAvNotB));
	}
	
	@Test
	public void isConjunctiveClauseTest() {
		assertEquals(true, isConjunctiveClause(A));
		assertEquals(true, isConjunctiveClause(X));
		assertEquals(true, isConjunctiveClause(notA));
		assertEquals(true, isConjunctiveClause(notX));
		assertEquals(false, isConjunctiveClause(notNotA));
		assertEquals(false, isConjunctiveClause(notNotX));
		assertEquals(true, isConjunctiveClause(AB));
		assertEquals(true, isConjunctiveClause(AX));
		assertEquals(true, isConjunctiveClause(ABC));
		assertEquals(false, isConjunctiveClause(AvB));
		assertEquals(false, isConjunctiveClause(AvX));
		assertEquals(false, isConjunctiveClause(AvBvC));
		assertEquals(false, isConjunctiveClause(notAB));
		assertEquals(false, isConjunctiveClause(notAX));
		assertEquals(false, isConjunctiveClause(notABC));
		assertEquals(false, isConjunctiveClause(notAvB));
		assertEquals(false, isConjunctiveClause(notAvX));
		assertEquals(false, isConjunctiveClause(notAvBvC));
		assertEquals(true, isConjunctiveClause(notANotB));
		assertEquals(false, isConjunctiveClause(notAvNotB));
	}
	
	@Test
	public void isDisjunctiveClauseTest() {
		assertEquals(true, isDisjunctiveClause(A));
		assertEquals(true, isDisjunctiveClause(X));
		assertEquals(true, isDisjunctiveClause(notA));
		assertEquals(true, isDisjunctiveClause(notX));
		assertEquals(false, isDisjunctiveClause(notNotA));
		assertEquals(false, isDisjunctiveClause(notNotX));
		assertEquals(false, isDisjunctiveClause(AB));
		assertEquals(false, isDisjunctiveClause(AX));
		assertEquals(false, isDisjunctiveClause(ABC));
		assertEquals(true, isDisjunctiveClause(AvB));
		assertEquals(true, isDisjunctiveClause(AvX));
		assertEquals(true, isDisjunctiveClause(AvBvC));
		assertEquals(false, isDisjunctiveClause(notAB));
		assertEquals(false, isDisjunctiveClause(notAX));
		assertEquals(false, isDisjunctiveClause(notABC));
		assertEquals(false, isDisjunctiveClause(notAvB));
		assertEquals(false, isDisjunctiveClause(notAvX));
		assertEquals(false, isDisjunctiveClause(notAvBvC));
		assertEquals(false, isDisjunctiveClause(notANotB));
		assertEquals(true, isDisjunctiveClause(notAvNotB));
	}
	
	@Test
	public void isCNFTest() {
		assertEquals(true, isCNF(A));
		assertEquals(true, isCNF(X));
		assertEquals(true, isCNF(notA));
		assertEquals(true, isCNF(notX));
		assertEquals(false, isCNF(notNotA));
		assertEquals(false, isCNF(notNotX));
		assertEquals(true, isCNF(AB));
		assertEquals(true, isCNF(AX));
		assertEquals(true, isCNF(ABC));
		assertEquals(false, isCNF(AvB)); // Is that right?
		assertEquals(false, isCNF(AvX)); // Is that right?
		assertEquals(false, isCNF(AvBvC)); // Is that right?
		assertEquals(false, isCNF(notAB));
		assertEquals(false, isCNF(notAX));
		assertEquals(false, isCNF(notABC));
		assertEquals(false, isCNF(notAvB));
		assertEquals(false, isCNF(notAvX));
		assertEquals(false, isCNF(notAvBvC));
		assertEquals(true, isCNF(notANotB));
		assertEquals(false, isCNF(notAvNotB)); // Is that right?
	}
	
	@Test
	public void isDNFTest() {
		assertEquals(true, isDNF(A));
		assertEquals(true, isDNF(X));
		assertEquals(true, isDNF(notA));
		assertEquals(true, isDNF(notX));
		assertEquals(false, isDNF(notNotA));
		assertEquals(false, isDNF(notNotX));
		assertEquals(false, isDNF(AB));
		assertEquals(false, isDNF(AX));
		assertEquals(false, isDNF(ABC));
		assertEquals(true, isDNF(AvB));
		assertEquals(true, isDNF(AvX));
		assertEquals(true, isDNF(AvBvC));
		assertEquals(false, isDNF(notAB));
		assertEquals(false, isDNF(notAX));
		assertEquals(false, isDNF(notABC));
		assertEquals(false, isDNF(notAvB));
		assertEquals(false, isDNF(notAvX));
		assertEquals(false, isDNF(notAvBvC));
		assertEquals(false, isDNF(notANotB));
		assertEquals(true, isDNF(notAvNotB));
	}
	
	@Test
	public void toCNFTest() {
		assertEquals(new Conjunction(A), toCNF(new Conjunction(A)));
		assertEquals(new Conjunction(X), toCNF(new Conjunction(X)));
		assertEquals(new Conjunction(A), toCNF(new Disjunction(A)));
		assertEquals(new Conjunction(X), toCNF(new Disjunction(X)));
		assertEquals(new Conjunction(notA), toCNF(new Conjunction(notA)));
		assertEquals(new Conjunction(notX), toCNF(new Conjunction(notX)));
		assertEquals(new Conjunction(A), toCNF(new Conjunction(notNotA)));
		assertEquals(new Conjunction(X), toCNF(new Conjunction(notNotX)));
		assertEquals(AB, toCNF(AB));
		assertEquals(AX, toCNF(AX));
		assertEquals(ABC, toCNF(ABC));
		assertEquals(new Conjunction(AvB), toCNF(AvB));
		assertEquals(new Conjunction(AvX), toCNF(AvX));
		assertEquals(new Conjunction(AvBvC), toCNF(AvBvC));
		assertEquals(new Conjunction(notAvNotB), toCNF(new Conjunction(notAB)));
		assertEquals(new Conjunction(notAvNotX), toCNF(new Conjunction(notAX)));
		assertEquals(new Conjunction(notAvNotBvNotC), toCNF(new Conjunction(notABC)));
		assertEquals(notANotB, toCNF(new Conjunction(notAvB)));
		assertEquals(notANotX, toCNF(new Conjunction(notAvX)));
		assertEquals(notANotBNotC, toCNF(new Conjunction(notAvBvC)));
		assertEquals(notANotB, toCNF(notANotB));
		assertEquals(new Conjunction(notAvNotB), toCNF(notAvNotB));
	}
	
	@Test
	public void toDNFTest() {
		assertEquals(new Disjunction(A), toDNF(new Disjunction(A)));
		assertEquals(new Disjunction(X), toDNF(new Disjunction(X)));
		assertEquals(new Disjunction(A), toDNF(new Conjunction(A)));
		assertEquals(new Disjunction(X), toDNF(new Conjunction(X)));
		assertEquals(new Disjunction(notA), toDNF(new Disjunction(notA)));
		assertEquals(new Disjunction(notX), toDNF(new Disjunction(notX)));
		assertEquals(new Disjunction(A), toDNF(new Disjunction(notNotA)));
		assertEquals(new Disjunction(X), toDNF(new Disjunction(notNotX)));
		assertEquals(new Disjunction(AB), toDNF(AB));
		assertEquals(new Disjunction(AX), toDNF(AX));
		assertEquals(new Disjunction(ABC), toDNF(ABC));
		assertEquals(AvB, toDNF(AvB));
		assertEquals(AvX, toDNF(AvX));
		assertEquals(AvBvC, toDNF(AvBvC));
		assertEquals(notAvNotB, toDNF(new Disjunction(notAB)));
		assertEquals(notAvNotX, toDNF(new Disjunction(notAX)));
		assertEquals(notAvNotBvNotC, toDNF(new Disjunction(notABC)));
		assertEquals(new Disjunction(notANotB), toDNF(new Disjunction(notAvB)));
		assertEquals(new Disjunction(notANotX), toDNF(new Disjunction(notAvX)));
		assertEquals(new Disjunction(notANotBNotC), toDNF(new Disjunction(notAvBvC)));
		assertEquals(new Disjunction(notANotB), toDNF(notANotB));
		assertEquals(notAvNotB, toDNF(notAvNotB));
	}	
}
