package edu.uno.ai.planning.SATPlan;

import static org.junit.Assert.*;
import edu.uno.ai.planning.SATPlan.BooleanVariable;
import edu.uno.ai.planning.SATPlan.SATProblem;
import edu.uno.ai.planning.SATPlan.SATSolver;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class SATSolverTest extends SATSolver{
	
	BooleanVariable A = new BooleanVariable("A", null, Boolean.FALSE);
	BooleanVariable NotA = new BooleanVariable("A", null, Boolean.TRUE);
	BooleanVariable B = new BooleanVariable("B", null, Boolean.FALSE);
	BooleanVariable NotB = new BooleanVariable("B", null, Boolean.TRUE);
	BooleanVariable C = new BooleanVariable("C", null, Boolean.FALSE);
	BooleanVariable NotC = new BooleanVariable("C", null, Boolean.TRUE);
	BooleanVariable D = new BooleanVariable("D", null, Boolean.FALSE);
	BooleanVariable NotD = new BooleanVariable("D", null, Boolean.TRUE);
	BooleanVariable E = new BooleanVariable("E", null, Boolean.FALSE);
	BooleanVariable NotE = new BooleanVariable("E", null, Boolean.TRUE);
	
	BooleanVariable test;
	
	ArrayList<BooleanVariable> Empty = new ArrayList<BooleanVariable>();
	
	ArrayList<BooleanVariable> _B = new ArrayList<BooleanVariable>(Arrays.asList(B));
	ArrayList<BooleanVariable> _NotB = new ArrayList<BooleanVariable>(Arrays.asList(NotB));
	ArrayList<BooleanVariable> _D = new ArrayList<BooleanVariable>(Arrays.asList(D));
	ArrayList<BooleanVariable> CvEvNotB = new ArrayList<BooleanVariable>(Arrays.asList(C, E, NotB));
	ArrayList<BooleanVariable> BvA = new ArrayList<BooleanVariable>(Arrays.asList(B, A));
	ArrayList<BooleanVariable> CvNotE = new ArrayList<BooleanVariable>(Arrays.asList(C, NotE));
	ArrayList<BooleanVariable> NotAvD = new ArrayList<BooleanVariable>(Arrays.asList(NotA, D));
	ArrayList<BooleanVariable> AvNotD = new ArrayList<BooleanVariable>(Arrays.asList(A, NotD));
	ArrayList<BooleanVariable> AvBvC = new ArrayList<BooleanVariable>(Arrays.asList(A, B, C));
	ArrayList<BooleanVariable> AvBvCvDvE = new ArrayList<BooleanVariable>(Arrays.asList(A, B, C, D, E));
	
	ArrayList<BooleanVariable> NotAvNotB = new ArrayList<BooleanVariable>(Arrays.asList(NotA, NotB));
	ArrayList<BooleanVariable> NotAvNotC = new ArrayList<BooleanVariable>(Arrays.asList(NotA, NotC));
	ArrayList<BooleanVariable> NotAvNotD = new ArrayList<BooleanVariable>(Arrays.asList(NotA, NotD));
	ArrayList<BooleanVariable> NotAvNotE = new ArrayList<BooleanVariable>(Arrays.asList(NotA, NotE));
	
	ArrayList<BooleanVariable> NotBvNotA = new ArrayList<BooleanVariable>(Arrays.asList(NotB, NotA));
	ArrayList<BooleanVariable> NotBvNotC = new ArrayList<BooleanVariable>(Arrays.asList(NotB, NotC));
	ArrayList<BooleanVariable> NotBvNotD = new ArrayList<BooleanVariable>(Arrays.asList(NotB, NotD));
	ArrayList<BooleanVariable> NotBvNotE = new ArrayList<BooleanVariable>(Arrays.asList(NotB, NotE));
	
	ArrayList<BooleanVariable> NotCvNotA = new ArrayList<BooleanVariable>(Arrays.asList(NotC, NotA));
	ArrayList<BooleanVariable> NotCvNotB = new ArrayList<BooleanVariable>(Arrays.asList(NotC, NotB));
	ArrayList<BooleanVariable> NotCvNotD = new ArrayList<BooleanVariable>(Arrays.asList(NotC, NotD));
	ArrayList<BooleanVariable> NotCvNotE = new ArrayList<BooleanVariable>(Arrays.asList(NotC, NotE));
	
	ArrayList<BooleanVariable> NotDvNotA = new ArrayList<BooleanVariable>(Arrays.asList(NotD, NotA));
	ArrayList<BooleanVariable> NotDvNotB = new ArrayList<BooleanVariable>(Arrays.asList(NotD, NotB));
	ArrayList<BooleanVariable> NotDvNotC = new ArrayList<BooleanVariable>(Arrays.asList(NotD, NotC));
	ArrayList<BooleanVariable> NotDvNotE = new ArrayList<BooleanVariable>(Arrays.asList(NotD, NotE));
	
	ArrayList<BooleanVariable> NotEvNotA = new ArrayList<BooleanVariable>(Arrays.asList(NotE, NotA));
	ArrayList<BooleanVariable> NotEvNotB = new ArrayList<BooleanVariable>(Arrays.asList(NotE, NotB));
	ArrayList<BooleanVariable> NotEvNotC = new ArrayList<BooleanVariable>(Arrays.asList(NotE, NotC));
	ArrayList<BooleanVariable> NotEvNotD = new ArrayList<BooleanVariable>(Arrays.asList(NotE, NotD));
	
	static ArrayList<BooleanVariable> mainList = new ArrayList<BooleanVariable>();
	static ArrayList<ArrayList<BooleanVariable>> conjunction = new ArrayList<ArrayList<BooleanVariable>>();
	
	SATProblem problem;
	
	@Test
	public void booleanVariablesEqualTest(){
		assertTrue(BooleanVariable.booleanVariablesEqual(A, A));
		assertTrue(BooleanVariable.booleanVariablesEqual(NotD, NotD));
		
		assertFalse(BooleanVariable.booleanVariablesEqual(A, B));
		assertFalse(BooleanVariable.booleanVariablesEqual(A, NotA));
	}
	
	@Test
	public void booleanVariablesOppositesTest(){		
		assertTrue(BooleanVariable.booleanVariablesOpposites(A, NotA));
		assertTrue(BooleanVariable.booleanVariablesOpposites(NotD, D));
		
		assertFalse(BooleanVariable.booleanVariablesOpposites(A, B));
		assertFalse(BooleanVariable.booleanVariablesOpposites(A, A));
	}
	
	@Test
	public void containsEqualBooleanVariableTest(){
		assertTrue(BooleanVariable.containsEqualBooleanVariable(_B, B));
		assertTrue(BooleanVariable.containsEqualBooleanVariable(NotAvD, D));
		assertTrue(BooleanVariable.containsEqualBooleanVariable(NotAvD, NotA));
		
		assertFalse(BooleanVariable.containsEqualBooleanVariable(BvA, C));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(NotAvD, A));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(NotAvD, NotD));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(Empty, A));
	}
	
	@Test
	public void containsOppositeBooleanVariableTest(){
		assertTrue(BooleanVariable.containsOppositeBooleanVariable(_B, NotB));
		assertTrue(BooleanVariable.containsOppositeBooleanVariable(NotAvD, NotD));
		assertTrue(BooleanVariable.containsOppositeBooleanVariable(NotAvD, A));
		
		assertFalse(BooleanVariable.containsOppositeBooleanVariable(_B, B));
		assertFalse(BooleanVariable.containsOppositeBooleanVariable(NotAvD, C));
		assertFalse(BooleanVariable.containsOppositeBooleanVariable(NotAvD, D));
	}
	
	@Test
	public void removeBooleanVariableTest(){
		assertEquals(BooleanVariable.removeBooleanVariable(_B, B), Empty);
			_B.add(B);
		assertEquals(BooleanVariable.removeBooleanVariable(_B, NotB), Empty);
			_B.add(B);
		assertEquals(BooleanVariable.removeBooleanVariable(BvA, C), BvA);
		assertEquals(BooleanVariable.removeBooleanVariable(NotAvD, A), _D);
			NotAvD.add(NotA);		
		assertNotEquals(BooleanVariable.removeBooleanVariable(NotAvD, C), _D);
	}
	
	@Test
	public void setMainListValueTest(){
		mainList.clear();		
		mainList = SATSolver.setMainListValue(mainList, B, Boolean.TRUE);
		assertTrue(mainList.isEmpty());
		
		mainList.add(A);
		mainList = SATSolver.setMainListValue(mainList, B, Boolean.TRUE);
		BooleanVariable test = mainList.get(0);
		assertFalse(test.value == Boolean.TRUE);
		
		mainList.clear();
		mainList.add(D);
		mainList.add(A);
		mainList.add(NotB);
		mainList = SATSolver.setMainListValue(mainList, A, Boolean.TRUE);
		test = mainList.get(1);
		assertTrue(test.value == Boolean.TRUE);
		test = mainList.get(0);
		assertFalse(test.value == Boolean.TRUE);
		test = mainList.get(2);
		assertFalse(test.value == Boolean.TRUE);
	}
	
	@Test
	public void hasUnitTest(){
		conjunction.clear();
		assertFalse(SATSolver.hasUnit(conjunction));
		
		conjunction.clear();
		conjunction.add(_B);
		assertTrue(SATSolver.hasUnit(conjunction));
		
		conjunction.clear();
		conjunction.add(NotAvNotB);
		conjunction.add(NotAvNotC);
		conjunction.add(NotAvNotD);
		assertFalse(SATSolver.hasUnit(conjunction));
		
		conjunction.clear();
		conjunction.add(NotAvNotB);
		conjunction.add(_B);
		conjunction.add(NotAvNotD);
		assertTrue(SATSolver.hasUnit(conjunction));
	}
	
	@Test
	public void unitPropagationTest(){		
		mainList.clear();
		conjunction.clear();
		conjunction.add(NotAvNotB);
		conjunction.add(_B);
		conjunction.add(NotAvNotD);
		problem = new SATProblem(conjunction, mainList);
		problem = SATSolver.unitPropagation(problem);
		assertTrue(problem.conjunction.size() == 2);
		assertFalse(BooleanVariable.containsEqualBooleanVariable(problem.conjunction.get(0), B));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(problem.conjunction.get(1), B));
		
		mainList.clear();
		conjunction.clear();
		conjunction.add(_B);
		problem = new SATProblem(conjunction, mainList);
		problem = SATSolver.unitPropagation(problem);
		assertTrue(problem.conjunction.isEmpty());
	}
	
	@Test
	public void getPuresTest(){
		ArrayList<BooleanVariable> pures = new ArrayList<BooleanVariable>();
		
		conjunction.clear();
		conjunction.add(NotAvNotB);
		conjunction.add(NotAvNotC);
		conjunction.add(NotAvNotD);
		conjunction.add(NotAvNotE);
		conjunction.add(AvBvCvDvE);
		
		pures = SATSolver.getPures(conjunction);
		
		assertTrue(pures.isEmpty());
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, A));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, B));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, C));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, D));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, E));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, NotA));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, NotB));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, NotC));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, NotD));
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, NotE));
		conjunction.clear();
		pures.clear();
		
		conjunction.add(NotAvNotB);
		conjunction.add(NotAvNotC);
		conjunction.add(NotAvNotD);
		conjunction.add(NotAvNotE);
		
		pures = SATSolver.getPures(conjunction);
		
		assertFalse(pures.isEmpty());
		assertFalse(BooleanVariable.containsEqualBooleanVariable(pures, A));
		assertTrue(BooleanVariable.containsEqualBooleanVariable(pures, NotA));
		assertTrue(BooleanVariable.containsEqualBooleanVariable(pures, NotB));
		assertTrue(BooleanVariable.containsEqualBooleanVariable(pures, NotC));
		assertTrue(BooleanVariable.containsEqualBooleanVariable(pures, NotD));
		assertTrue(BooleanVariable.containsEqualBooleanVariable(pures, NotE));
		conjunction.clear();
	}
	
	@Test
	public void removePuresTest(){
		ArrayList<BooleanVariable> pures = new ArrayList<BooleanVariable>();
		
		conjunction.clear();
		conjunction.add(NotAvNotB);
		conjunction.add(NotAvNotC);
		conjunction.add(NotAvNotD);
		conjunction.add(NotAvNotE);
		
		pures = SATSolver.getPures(conjunction);
		conjunction = SATSolver.removePures(conjunction, pures);
		assertTrue(conjunction.isEmpty());
		
		pures.clear();
		conjunction.clear();
		conjunction.add(BvA);
		conjunction.add(NotAvNotB);
		conjunction.add(NotAvNotC);
		conjunction.add(NotCvNotB);
		
		pures = SATSolver.getPures(conjunction);
		conjunction = SATSolver.removePures(conjunction, pures);
		ArrayList<ArrayList<BooleanVariable>> conjunction2 = new ArrayList<ArrayList<BooleanVariable>>(Arrays.asList(BvA, NotAvNotB));
		assertTrue(conjunction.equals(conjunction2));
	}
	
	@Test
	public void satisfiableTest(){
		mainList.clear();
		conjunction.clear();
		conjunction.add(BvA);
		conjunction.add(NotAvNotB);
		conjunction.add(NotAvNotC);
		conjunction.add(NotCvNotB);
		problem = new SATProblem(conjunction, mainList);
		assertTrue(SATSolver.satisfiable(problem, mainList));
		
		mainList.clear();
		conjunction.clear();
		conjunction.add(_B);
		conjunction.add(NotAvNotB);
		conjunction.add(NotCvNotB);
		problem = new SATProblem(conjunction, mainList);
		assertTrue(SATSolver.satisfiable(problem, mainList));
		
		mainList.clear();
		conjunction.clear();
		conjunction.add(_B);
		conjunction.add(NotCvNotB);
		conjunction.add(AvNotD);
		conjunction.add(NotAvD);
		problem = new SATProblem(conjunction, mainList);
		assertTrue(SATSolver.satisfiable(problem, mainList));
		
		mainList.clear();
		conjunction.clear();
		conjunction.add(_B);
		conjunction.add(_NotB);
		conjunction.add(AvNotD);
		conjunction.add(NotAvD);
		problem = new SATProblem(conjunction, mainList);
		assertFalse(SATSolver.satisfiable(problem, mainList));
	}
}








































