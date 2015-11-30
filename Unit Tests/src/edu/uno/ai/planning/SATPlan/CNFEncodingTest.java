package edu.uno.ai.planning.SATPlan;

import edu.uno.ai.planning.Settings;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.util.ImmutableArray;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CNFEncodingTest {		
	Predication A = new Predication("test", new Constant(Settings.DEFAULT_TYPE, "A")); 
	Predication B = new Predication("test", new Constant(Settings.DEFAULT_TYPE, "B"));
	Predication C = new Predication("test", new Constant(Settings.DEFAULT_TYPE, "C")); 
	Predication D = new Predication("test", new Constant(Settings.DEFAULT_TYPE, "D"));
	
	Conjunction AB = new Conjunction(A,B);
	Conjunction ABC = new Conjunction(A,B,C);
	Conjunction AC = new Conjunction(A,C);
	Conjunction CD = new Conjunction(C,D);
	Conjunction ABCD = new Conjunction(A,B,C,D);
	
	NegatedLiteral notA = A.negate();
	NegatedLiteral notB = B.negate();
	NegatedLiteral notC = C.negate();

	Conjunction ANotBNotC = new Conjunction(A, notB, notC);
	Conjunction ANotBNotCBCNotA = new Conjunction(A, notB, notC, B, C, notA);
	Conjunction notAC = new Conjunction(notA, C);

	private CNFEncoding encoding;

	Step testStep1 = new Step("testStep1", AB, notAC);
	Step testStep2 = new Step("testStep2", notAC, AC);
	Step testStep3 = new Step("testStep3", AC, C);

	ArrayList<Step> allSteps = new ArrayList<Step>(){{
		add(testStep1);
		add(testStep2);
		add(testStep3);
	}};

	@Before
	public void Setup(){
		encoding = new CNFEncoding(new SATSolver());
	}

	@Test
	public void conjunctionFromExpressionTest(){		
		ArrayList<ArrayList<BooleanVariable>> cnf = encoding.conjunctionFromExpression(A, 0);
		assertTrue(cnf.size() == 1);		
		assertTrue(cnf.get(0).get(0).name.equals("(test A) - 0"));
		
		cnf = encoding.conjunctionFromExpression(B, 25);
		assertTrue(cnf.get(0).get(0).name.equals("(test B) - 25"));
		
		cnf = encoding.conjunctionFromExpression(AB, 2);
		assertTrue(cnf.size() == 2);		
		assertTrue(cnf.get(0).get(0).name.equals("(test A) - 2"));
		assertTrue(cnf.get(1).get(0).name.equals("(test B) - 2"));
		
		cnf = encoding.conjunctionFromExpression(ABCD, 5);
		assertTrue(cnf.size() == 4);		
		assertTrue(cnf.get(0).get(0).name.equals("(test A) - 5"));
		assertTrue(cnf.get(1).get(0).name.equals("(test B) - 5"));
		assertTrue(cnf.get(2).get(0).name.equals("(test C) - 5"));
		assertTrue(cnf.get(3).get(0).name.equals("(test D) - 5"));
		
		cnf = encoding.conjunctionFromExpression(ABC, 2);
		assertTrue(cnf.size() == 3);		
		assertTrue(cnf.get(0).get(0).name.equals("(test A) - 2"));
		assertTrue(cnf.get(1).get(0).name.equals("(test B) - 2"));		
		assertTrue(cnf.get(2).get(0).name.equals("(test C) - 2"));	
		
		cnf = encoding.conjunctionFromExpression(AC, 2);
		assertTrue(cnf.size() == 2);		
		assertTrue(cnf.get(0).get(0).name.equals("(test A) - 2"));
		assertTrue(cnf.get(1).get(0).name.equals("(test C) - 2"));
		
		cnf = encoding.conjunctionFromExpression(CD, 2);
		assertTrue(cnf.size() == 2);		
		assertTrue(cnf.get(0).get(0).name.equals("(test C) - 2"));
		assertTrue(cnf.get(1).get(0).name.equals("(test D) - 2"));		
		
		Expression nullExpression = null;	
		cnf = encoding.conjunctionFromExpression(nullExpression, 0);
		assertTrue(cnf.size() == 0);
		
		cnf = encoding.conjunctionFromExpression(ANotBNotC, 2);
		assertTrue(cnf.size() == 3);		
		assertTrue(cnf.get(0).get(0).name.equals("(test A) - 2"));
		assertTrue(cnf.get(0).get(0).negation  == Boolean.FALSE);
		
		assertTrue(cnf.get(1).get(0).name.equals("(test B) - 2"));
		assertTrue(cnf.get(1).get(0).negation  == Boolean.TRUE);
		
		assertTrue(cnf.get(2).get(0).name.equals("(test C) - 2"));
		assertTrue(cnf.get(2).get(0).negation  == Boolean.TRUE);
		
		cnf = encoding.conjunctionFromExpression(ANotBNotCBCNotA, 2);
		assertTrue(cnf.size() == 6);		
		assertTrue(cnf.get(0).get(0).name.equals("(test A) - 2"));
		assertTrue(cnf.get(0).get(0).negation  == Boolean.FALSE);
		
		assertTrue(cnf.get(5).get(0).name.equals("(test A) - 2"));
		assertTrue(cnf.get(5).get(0).negation  == Boolean.TRUE);		
	}
	
	@Test
	public void argumentToBooleanVariableTest(){
		CNFEncoding encoding = new CNFEncoding(new SATSolver());
		BooleanVariable bnv = encoding.argumentToBooleanVariable(A, 1);
		assertTrue(bnv.name.equals("(test A) - 1"));
		assertTrue(bnv.negation == Boolean.FALSE);
		
		bnv = encoding.argumentToBooleanVariable(notA, 1);
		assertTrue(bnv.name.equals("(test A) - 1"));
		assertTrue(bnv.negation == Boolean.TRUE);		
	}
	
	@Test
	public void argumentToNegativeBooleanVariableTest(){		
		CNFEncoding encoding = new CNFEncoding(new SATSolver());
		BooleanVariable bnv = encoding.argumentToNegativeBooleanVariable(A, 1);
		assertTrue(bnv.name.equals("(test A) - 1"));
		assertTrue(bnv.negation == Boolean.TRUE);
		
		bnv = encoding.argumentToNegativeBooleanVariable(notA, 1);
		assertTrue(bnv.name.equals("(test A) - 1"));
		assertTrue(bnv.negation == Boolean.FALSE);		
	}
	
	@Test
	public void stepToConjunctionTest(){
		ArrayList<ArrayList<BooleanVariable>> conjunction = encoding.stepToConjunction(testStep1, 1);
		
		assertTrue(conjunction.size() == 4);
		assertTrue(arrayListStringComparator(conjunction.get(0), "~testStep1 - 1 V (test A) - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(1), "~testStep1 - 1 V (test B) - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(2), "~testStep1 - 1 V ~(test A) - 2"));
		assertTrue(arrayListStringComparator(conjunction.get(3), "~testStep1 - 1 V (test C) - 2"));
		
		conjunction = encoding.stepToConjunction(testStep2, 1);

		assertTrue(conjunction.size() == 4);
		assertTrue(arrayListStringComparator(conjunction.get(0), "~testStep2 - 1 V ~(test A) - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(1), "~testStep2 - 1 V (test C) - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(2), "~testStep2 - 1 V (test A) - 2"));
		assertTrue(arrayListStringComparator(conjunction.get(3), "~testStep2 - 1 V (test C) - 2"));
	}

	@Test
	public void frameAxiomBuilderTest(){
		encoding.stepToConjunction(testStep1 ,1);
		encoding.stepToConjunction(testStep2, 1);

		assertEquals(encoding.frameAxiomBuilder.size(), 3);
		assertThat(encoding.frameAxiomBuilder.get(A).size(), equalTo(1));
		assertThat(encoding.frameAxiomBuilder.get(notA).size(), equalTo(1));
		assertThat(encoding.frameAxiomBuilder.get(C).size(), equalTo(2));
	}

	@Test
	public void explanatoryFrameAxiomTest(){
		Step testStep1 = new Step("testStep1", AB, notAC);
		allSteps = new ArrayList<Step>(){{
			add(testStep1);
		}};
		encoding.stepToConjunction(testStep1 ,1);

		ArrayList<ArrayList<BooleanVariable>> result = encoding.getExplanatoryFrameAxioms(1, allSteps);

		int counter = 0;
		for(ArrayList<BooleanVariable> dij : result){
			System.out.println(counter++ +  encoding.getStringFromListOfBooleanVariables(dij));
		}

		assertThat(result.size(), equalTo(2));
	}
	
	@Test
	public void onlyOneActionOccursAtEachStepTest(){
		Step[] steps = new Step[3];
		steps[0] = new Step("testStep1", AB, notAC);
		steps[1] = new Step("testStep2", AB, notAC);
		steps[2] = new Step("testStep3", AB, notAC);
		
		CNFEncoding encoding = new CNFEncoding(new SATSolver());
		ImmutableArray<Step> stepsArray = new ImmutableArray<Step>(steps);
		
		ArrayList<ArrayList<BooleanVariable>> conjunction = encoding.onlyOneActionOccursAtEachStep(stepsArray, 1);
		
		for (ArrayList<BooleanVariable> tt : conjunction){
			arrayListStringComparator(tt, "");
		}	
		
		assertTrue(conjunction.size() == 6);
		assertTrue(arrayListStringComparator(conjunction.get(0), "~testStep1 - 1 V ~testStep2 - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(1), "~testStep1 - 1 V ~testStep3 - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(2), "~testStep2 - 1 V ~testStep1 - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(3), "~testStep2 - 1 V ~testStep3 - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(4), "~testStep3 - 1 V ~testStep1 - 1"));
		assertTrue(arrayListStringComparator(conjunction.get(5), "~testStep3 - 1 V ~testStep2 - 1"));
	}
	
	
	private Boolean arrayListStringComparator(ArrayList<BooleanVariable> disjunction, String cnf){
		String resultString = "";
		for (BooleanVariable BV : disjunction){
			resultString += (BV.negation ? "~" : "") + BV.name + " V ";
		}		
		if (resultString.length() > 3) resultString = resultString.substring(0, resultString.length() - 3);		
		return resultString.equals(cnf);
	}
}
