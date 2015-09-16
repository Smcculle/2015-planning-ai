package edu.uno.ai.planning.logic;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.uno.ai.planning.Settings;

/**
 * Utility methods for converting expressions to conjunctive normal form.
 * 
 * @author Edward Thomas Garcia
 * 
 * Notes:
 *  Conjunctive Normal Form is defined as (or NormalForms.toCNF returns) a Conjunction of Disjunctive Clauses
 *  Disjunctive Normal Form is defined as (or NormalForms.toDNF returns) a Disjunction of Conjunctive Clauses
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
	Negation not_AB_ = new Negation(AB);
	Negation not_ABC_ = new Negation(ABC);
	Negation not_AX_ = new Negation(AX);
	Negation not_XY_ = new Negation(XY);
	Negation not_XYZ_ = new Negation(XYZ);
	
	// Test Disjunctions
	Disjunction AvB = new Disjunction(A,B);
	Disjunction AvBvC = new Disjunction(A,B,C);
	Disjunction AvX = new Disjunction(A,X);
	Disjunction XvY = new Disjunction(X,Y);
	Disjunction XvYvZ = new Disjunction(X,Y,Z);
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
	Conjunction _AvB__NotBvCvNotX__XvNotY_ = new Conjunction(new Disjunction(A,B), new Disjunction(notB,C,notX), new Disjunction(X,notY));
	
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
		assertEquals(false, isLiteral(not_AB_));
		assertEquals(false, isLiteral(not_AX_));
		assertEquals(false, isLiteral(not_ABC_));
		assertEquals(false, isLiteral(not_AvB_));
		assertEquals(false, isLiteral(not_AvX_));
		assertEquals(false, isLiteral(not_AvBvC_));
		assertEquals(false, isLiteral(notANotB));
		assertEquals(false, isLiteral(notAvNotB));
		assertEquals(false, isLiteral(_AB_vCv_XYZ_));
		assertEquals(false, isLiteral(_ANotB_v_XY_v_BXNotY_));
		assertEquals(false, isLiteral(_AvB__NotBvCvNotX__XvNotY_));
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
		assertEquals(false, isClause(not_AB_));
		assertEquals(false, isClause(not_AX_));
		assertEquals(false, isClause(not_ABC_));
		assertEquals(false, isClause(not_AvB_));
		assertEquals(false, isClause(not_AvX_));
		assertEquals(false, isClause(not_AvBvC_));
		assertEquals(true, isClause(notANotB));
		assertEquals(true, isClause(notAvNotB));
		assertEquals(false, isClause(_AB_vCv_XYZ_));
		assertEquals(false, isClause(_ANotB_v_XY_v_BXNotY_));
		assertEquals(false, isClause(_AvB__NotBvCvNotX__XvNotY_));
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
		assertEquals(false, isConjunctiveClause(not_AB_));
		assertEquals(false, isConjunctiveClause(not_AX_));
		assertEquals(false, isConjunctiveClause(not_ABC_));
		assertEquals(false, isConjunctiveClause(not_AvB_));
		assertEquals(false, isConjunctiveClause(not_AvX_));
		assertEquals(false, isConjunctiveClause(not_AvBvC_));
		assertEquals(true, isConjunctiveClause(notANotB));
		assertEquals(false, isConjunctiveClause(notAvNotB));
		assertEquals(false, isConjunctiveClause(_AB_vCv_XYZ_));
		assertEquals(false, isConjunctiveClause(_ANotB_v_XY_v_BXNotY_));
		assertEquals(false, isConjunctiveClause(_AvB__NotBvCvNotX__XvNotY_));
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
		assertEquals(false, isDisjunctiveClause(not_AB_));
		assertEquals(false, isDisjunctiveClause(not_AX_));
		assertEquals(false, isDisjunctiveClause(not_ABC_));
		assertEquals(false, isDisjunctiveClause(not_AvB_));
		assertEquals(false, isDisjunctiveClause(not_AvX_));
		assertEquals(false, isDisjunctiveClause(not_AvBvC_));
		assertEquals(false, isDisjunctiveClause(notANotB));
		assertEquals(true, isDisjunctiveClause(notAvNotB));
		assertEquals(false, isDisjunctiveClause(_AB_vCv_XYZ_));
		assertEquals(false, isDisjunctiveClause(_ANotB_v_XY_v_BXNotY_));
		assertEquals(false, isDisjunctiveClause(_AvB__NotBvCvNotX__XvNotY_));
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
		assertEquals(false, isCNF(AvB));
		assertEquals(false, isCNF(AvX));
		assertEquals(false, isCNF(AvBvC));
		assertEquals(false, isCNF(not_AB_));
		assertEquals(false, isCNF(not_AX_));
		assertEquals(false, isCNF(not_ABC_));
		assertEquals(false, isCNF(not_AvB_));
		assertEquals(false, isCNF(not_AvX_));
		assertEquals(false, isCNF(not_AvBvC_));
		assertEquals(true, isCNF(notANotB));
		assertEquals(false, isCNF(notAvNotB));
		assertEquals(false, isCNF(_AB_vCv_XYZ_));
		assertEquals(false, isCNF(_ANotB_v_XY_v_BXNotY_));
		assertEquals(true, isCNF(_AvB__NotBvCvNotX__XvNotY_));
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
		assertEquals(false, isDNF(not_AB_));
		assertEquals(false, isDNF(not_AX_));
		assertEquals(false, isDNF(not_ABC_));
		assertEquals(false, isDNF(not_AvB_));
		assertEquals(false, isDNF(not_AvX_));
		assertEquals(false, isDNF(not_AvBvC_));
		assertEquals(false, isDNF(notANotB));
		assertEquals(true, isDNF(notAvNotB));
		assertEquals(true, isDNF(_AB_vCv_XYZ_));
		assertEquals(true, isDNF(_ANotB_v_XY_v_BXNotY_));
		assertEquals(false, isDNF(_AvB__NotBvCvNotX__XvNotY_));
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
		assertEquals(new Conjunction(notAvNotB), toCNF(new Conjunction(not_AB_)));
		assertEquals(new Conjunction(notAvNotX), toCNF(new Conjunction(not_AX_)));
		assertEquals(new Conjunction(notAvNotBvNotC), toCNF(new Conjunction(not_ABC_)));
		assertEquals(notANotB, toCNF(new Conjunction(not_AvB_)));
		assertEquals(notANotX, toCNF(new Conjunction(not_AvX_)));
		assertEquals(notANotBNotC, toCNF(new Conjunction(not_AvBvC_)));
		assertEquals(notANotB, toCNF(notANotB));
		assertEquals(new Conjunction(notAvNotB), toCNF(notAvNotB));
		assertEquals(
			new Conjunction(
				new Disjunction(X,A,C),	new Disjunction(Y,A,C),	new Disjunction(Z,A,C),
				new Disjunction(X,B,C),	new Disjunction(Y,B,C),	new Disjunction(Z,B,C)
			), toCNF(_AB_vCv_XYZ_)
		);
		assertEquals(new Conjunction(new Disjunction(X,A), new Disjunction(B,Y,A), new Disjunction(X, notB)), toCNF(_ANotB_v_XY_v_BXNotY_));
		assertEquals(_AvB__NotBvCvNotX__XvNotY_, toCNF(_AvB__NotBvCvNotX__XvNotY_));
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
		assertEquals(notAvNotB, toDNF(new Disjunction(not_AB_)));
		assertEquals(notAvNotX, toDNF(new Disjunction(not_AX_)));
		assertEquals(notAvNotBvNotC, toDNF(new Disjunction(not_ABC_)));
		assertEquals(new Disjunction(notANotB), toDNF(new Disjunction(not_AvB_)));
		assertEquals(new Disjunction(notANotX), toDNF(new Disjunction(not_AvX_)));
		assertEquals(new Disjunction(notANotBNotC), toDNF(new Disjunction(not_AvBvC_)));
		assertEquals(new Disjunction(notANotB), toDNF(notANotB));
		assertEquals(notAvNotB, toDNF(notAvNotB));
		assertEquals(_AB_vCv_XYZ_, toDNF(_AB_vCv_XYZ_));
		assertEquals(_ANotB_v_XY_v_BXNotY_, toDNF(_ANotB_v_XY_v_BXNotY_));
		assertEquals(new Disjunction(
			new Conjunction(X,notB,A),new Conjunction(notY,notB,A),new Conjunction(X,C,A),new Conjunction(notY,C,A),
			new Conjunction(notY,notX,A),new Conjunction(X,C,B),new Conjunction(notY,C,B),new Conjunction(notY,notX,B)
		), toDNF(_AvB__NotBvCvNotX__XvNotY_));
		// (A || B) && (~B || C || ~X) && (X || ~Y) 
		// (A && ~B && X) || (A && ~B && ~Y) || (A && C && X) || (A && C && ~Y) || (A && ~X && ~Y) || (B && C && X) || (B && C && ~Y) || (B && ~X && ~Y)
	}	
}
