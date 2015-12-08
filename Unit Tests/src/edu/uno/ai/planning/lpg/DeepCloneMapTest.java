package edu.uno.ai.planning.lpg;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.graphplan.PlanGraph;
import edu.uno.ai.planning.graphplan.PlanGraphLiteral;
import edu.uno.ai.planning.graphplan.PlanGraphStep;
import edu.uno.ai.planning.logic.Expression;

public class DeepCloneMapTest {
	
	private static PlanGraph graph; 
	
	@BeforeClass
	public static void getMaps() {
		Problem cakeProblem = createCakeProblem();
		graph = new PlanGraph(cakeProblem, true);
	}
	
	private static Problem createCakeProblem() {
		
		try {
			return new Benchmark("cake", "cake_test").getProblem();
		}
		catch (IOException e){
			return null;
		}
	}
	
	/* Initialization methods for the maps we will be testing copy on */
	
	private static Map<PlanGraphLiteral, PlanGraphStep> getTestPersistentSteps() {
		
		/* get literal to step mapping for persistent steps */
		HashMap<PlanGraphLiteral, PlanGraphStep> persistentSteps = new HashMap<PlanGraphLiteral, PlanGraphStep>();
		for(PlanGraphStep step : graph.getPersistantSteps())
			persistentSteps.put(step.getChildNodes().get(0), step);
		
		return persistentSteps;
	}
	
	private static Map<Integer, Set<PlanGraphStep>> getTestSteps() {
		Map<Integer, Set<PlanGraphStep>> steps = new HashMap<Integer, Set<PlanGraphStep>>();
		
		Iterator<PlanGraphStep> stepIterator = graph.getAllPossiblePlanGraphSteps().iterator();
		System.out.println(graph.countLevels());
		for (int i = 0; i < graph.countLevels(); i++ ) {
			steps.put(i, new HashSet<PlanGraphStep>());
			if (stepIterator.hasNext()) 
				steps.get(i).add(stepIterator.next());
		}
		
		return steps;
	}
	
	private static Map<Integer, Set<PlanGraphLiteral>> getTestFacts() {
		Map<Integer, Set<PlanGraphLiteral>> facts = new HashMap<Integer, Set<PlanGraphLiteral>>();
		
		Iterator<PlanGraphLiteral> stepIterator = graph.getAllPossiblePlanGraphEffects().iterator();
		for (int i = 0; i < graph.countLevels(); i++ ) {
			facts.put(i, new HashSet<PlanGraphLiteral>());
			if (stepIterator.hasNext()) 
				facts.get(i).add(stepIterator.next());
		}
		
		return facts;
	}


	private static Map<Integer, List<LPGInconsistency>> getTestInconsistencies() {
		
		Map<Integer, List<LPGInconsistency>> inconsistencies = new HashMap<Integer, List<LPGInconsistency>>();
		for (int i = 0; i < graph.countLevels(); i++ ) {
			inconsistencies.put(i, new ArrayList<LPGInconsistency>());
		}
		
		/* initialize and add mutex steps */
		PlanGraphStep testMutex1 = new PlanGraphStep(new Step("testMutex1", Expression.TRUE, Expression.TRUE));
		PlanGraphStep testMutex2 = new PlanGraphStep(new Step("testMutex2", Expression.TRUE, Expression.TRUE));
		PlanGraphStep testMutex3 = new PlanGraphStep(new Step("testMutex3", Expression.TRUE, Expression.TRUE));
		
		LPGInconsistency mutex1 = new MutexRelation(testMutex1, testMutex2, 1);
		inconsistencies.get(1).add(mutex1);
		
		LPGInconsistency mutex2 = new MutexRelation(testMutex1, testMutex3, 1);
		inconsistencies.get(1).add(mutex2);
	
		LPGInconsistency mutex3 = new MutexRelation(testMutex2, testMutex3, 2);
		inconsistencies.get(2).add(mutex3);
		
		LPGInconsistency mutex4 = new MutexRelation(testMutex1, testMutex3, 2);
		inconsistencies.get(2).add(mutex4);
		
		/* initialize and add USP */
		PlanGraphLiteral testUSP1 = graph.getAllPossiblePlanGraphEffects().get(0);
		PlanGraphLiteral testUSP2 = graph.getAllPossiblePlanGraphEffects().get(1);
		
		inconsistencies.get(1).add(new UnsupportedPrecondition(testUSP1, 1));
		inconsistencies.get(1).add(new UnsupportedPrecondition(testUSP2, 1));
		
		inconsistencies.get(2).add(new UnsupportedPrecondition(testUSP1, 1));
		inconsistencies.get(2).add(new UnsupportedPrecondition(testUSP1, 2));
		
		return inconsistencies;
	}

	/* testing functionality of DeepCloneMap */
	
	@Test
	public void testIntegerKeys() {
		
		/* testing accurate return of integer key values */
		int five = 5;
		Object testFive = DeepCloneMap.deepClone(five);
		assertEquals(five, testFive);
		assertTrue(testFive instanceof Integer);
	
	}
	
	@Test
	public void copiesCollectionCorrectly() {
		Map<Integer, List<LPGInconsistency>> inconsistencies = getTestInconsistencies();
		Map<Integer, Set<PlanGraphStep>> steps = getTestSteps();
		
		/* test copying of a list */
		List<LPGInconsistency> inconsistencyList1 = inconsistencies.get(1);
		List<LPGInconsistency> inconsistencyList2 = DeepCloneMap.deepClone(inconsistencyList1);
		List<LPGInconsistency> inconsistencyList3 = DeepCloneMap.deepClone(inconsistencyList2);
		
		assertEquals(inconsistencyList1, inconsistencyList2);
		assertEquals(inconsistencyList2, inconsistencyList2);
		
		inconsistencyList1.remove(0);
		assertNotEquals(inconsistencyList1, inconsistencyList2);
		
		inconsistencyList2.add(new UnsupportedPrecondition(null, 0));
		assertNotEquals(inconsistencyList2, inconsistencyList3);
		
		
		/* test copying of a set */
		Set<PlanGraphStep> stepsSet1 = steps.get(1);
		Set<PlanGraphStep> stepsSet2 = DeepCloneMap.deepClone(stepsSet1);
		Set<PlanGraphStep> stepsSet3 = DeepCloneMap.deepClone(stepsSet2);
		
		assertEquals(stepsSet1, stepsSet2);
		assertEquals(stepsSet2, stepsSet3);
		
		/* remove an element from stepSet 1*/
		Iterator<PlanGraphStep> stepIterator = stepsSet1.iterator();
		assertTrue(stepIterator.hasNext());
		stepIterator.next();
		stepIterator.remove();
		
		assertNotEquals(stepsSet1, stepsSet2);
		
		stepsSet2.add(new PlanGraphStep(null, 1));
		assertNotEquals(stepsSet2, stepsSet3);
		
	}
	
	@Test
	public void copiesMapsCorrectly() {
		Map<Integer, List<LPGInconsistency>> inconsistencies = getTestInconsistencies();
		Map<Integer, Set<PlanGraphStep>> steps = getTestSteps();
		Map<Integer, Set<PlanGraphLiteral>> facts = getTestFacts();
		Map<PlanGraphLiteral, PlanGraphStep> persistentSteps = getTestPersistentSteps();
		
		Map<Integer, List<LPGInconsistency>> inconsistenciesCopy = DeepCloneMap.deepClone(inconsistencies);
		Map<Integer, Set<PlanGraphStep>> stepsCopy = DeepCloneMap.deepClone(steps);
		Map<Integer, Set<PlanGraphLiteral>> factsCopy = DeepCloneMap.deepClone(facts);
		Map<PlanGraphLiteral, PlanGraphStep> persistentStepsCopy = DeepCloneMap.deepClone(persistentSteps);
		
		assertEquals(inconsistencies, inconsistenciesCopy);
		assertEquals(steps, stepsCopy);
		assertEquals(facts, factsCopy);
		assertEquals(persistentSteps, persistentStepsCopy);
		
		/* test values are copied by value */
		inconsistencies.get(0).add(new UnsupportedPrecondition(null, 0));
		assertNotEquals(inconsistencies, inconsistenciesCopy);
		
		steps.get(0).add(new PlanGraphStep(null, 0));
		assertNotEquals(steps, stepsCopy);
		
		facts.get(0).add(new PlanGraphLiteral(null, 0));
		assertNotEquals(facts, factsCopy);
		
		Iterator<Entry<PlanGraphLiteral, PlanGraphStep>> it = persistentSteps.entrySet().iterator();
		assertTrue(it.hasNext());
		it.next();
		it.remove();
		
		assertNotEquals(persistentSteps, persistentStepsCopy);
		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwsExceptionForInvalidCollections(){
		Queue<Integer> testQueue1 = new PriorityQueue<Integer>();
		Queue<Integer> testQueue2 = DeepCloneMap.deepClone(testQueue1); 
	}
	
}
