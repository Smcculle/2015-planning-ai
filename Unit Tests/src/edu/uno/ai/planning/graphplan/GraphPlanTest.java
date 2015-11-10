/**
 * GraphPlanTest 
 * @author Lindsey Dale, John Montgomery
 */

package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Result;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.ss.TotalOrderPlan;

public class GraphPlanTest {

	public static final int NODE_LIMIT = 10000;
	public static final long TIME_LIMIT = Planner.NO_TIME_LIMIT;
	
	
	/**
	 * Test Graph Plan with the Do Nothing problem.
	 * This problem should a) succeed and b) require no steps.
	 */
	@Test 
	public void testDoNothing() throws FileNotFoundException{
		/** Create the Problem and the GraphPlanSearch. */
		Problem doNothingProblem = doNothing();
		GraphPlanSearch doNothing = new GraphPlanSearch(doNothingProblem);
		/** 
		 * Do Nothing is in the blocks domain.
		 * This problem already has the goal present in the initial state.
		 * First, test the solution.
		 */
		assertFalse(doNothing.isFinished());		
		TotalOrderPlan solutionDoNothing = (TotalOrderPlan) doNothing.search();
		assertNotNull(solutionDoNothing);							/** Assert that the solution returned was valid (i.e. a solution was found) */
		assertTrue(doNothingProblem.isSolution(solutionDoNothing));	/** Assert that the solution returned was indeed a correct solution */
		assertFalse(doNothing.isFinished());						/** Checking interior structure: do nothing should not affect the value of boolean finished, since that only occurs when the initial literals don't contain the goals and the Plan Graph must continue */
		assertTrue(doNothing.checkGoalInInitial());					/** Do nothing should have goal in initial */
		assertTrue(doNothing.getNoSteps());							/** Do nothing should require no steps */
		assertTrue(doNothing.getNodesSetSize() == 0);				/** Assert that no nodes were added to the Nodes set (method defineNode was never called) */

		/** Test the solution further with its iterator. */
		assertTrue(solutionDoNothing.size() == 0);					/** The solution size should be 0, as no steps were required. */
		Iterator<Step> iteratorDoNothing = solutionDoNothing.iterator();/** Create an iterator */
		assertFalse(iteratorDoNothing.hasNext());					/** There should be no steps in the do nothing solution, so the iterator should be empty (and never return true for hasNext). */
		
	}
	
	/**
	 * Test Graph Plan using the Cake problem.
	 * This problem should a) succeed and b) require at least two steps (eat and bake cake). 
	 */
	@Test 
	public void testCake() throws FileNotFoundException{
		/** Create the Problem and the GraphPlanSearch */
		Problem cakeProblem = createCakeProblem();
		GraphPlanSearch cake = new GraphPlanSearch(cakeProblem);

		/**
		 * The Cake problem here starts off with having a cake,
		 * and the goal needs there to be a cake and to have eaten one. 
		 * The steps in order to take this goal should be two: eat a cake, and then bake another.
		 * First, test the solution.
		 */
		assertFalse(cake.isFinished());
		TotalOrderPlan solutionCake = (TotalOrderPlan) cake.search();
		assertNotNull(solutionCake);							/** A valid (i.e. not null) solution should have been returned. */
		assertTrue(cakeProblem.isSolution(solutionCake));		/** Important: assert that the solution returned was indeed a correct solution. */
		assertTrue(cake.isFinished());							/** In contrast to the above Do Nothing domain, this boolean should return as true, since it was altered during node expansion. */
		assertFalse(cake.checkGoalInInitial());					/** Cake should not have goal in initial state. */
		assertFalse(cake.getNoSteps());							/** Assert that cake did require steps to be taken. */
		assertTrue(cake.getNodesSetSize() >= 1);				/** At least some nodes should have been added to the node set (success of defineNodes and the first one createGoalNode) */
		
		/** Test with the Iterator. */
		assertTrue(solutionCake.size() >= 2);					/** The Cake should have at least 2 steps (eat and then bake) */
		Iterator<Step> iteratorCake = solutionCake.iterator();	/** Create an iterator for the solution */
		assertTrue(iteratorCake.hasNext());						/** There should be some steps */

		/** To test the presence of specific Steps, an ArrayList of Strings is used. */
		ArrayList<String> cakeSteps = new ArrayList<String>();
		/** Populate the ArrayList with String representations of the Steps. */
		while (iteratorCake.hasNext()){
			cakeSteps.add(iteratorCake.next().toString());
		}
		/** There should at least be 2 items (the necessary steps), plus any persistence ones. */
		assertTrue(cakeSteps.size() >= 2);
		/** Check for the presence of the required steps. */
		assertTrue(cakeSteps.contains("(eat Cake)"));
		assertTrue(cakeSteps.contains("(bake Cake)"));
	}

	/**
	 * Test the Graph Plan using the Rocket problem (easy cargo problem)
	 * This problem should a) succeed and b) require at least 4 steps, listed below.
	 */
	@Test 
	public void testRocket() throws FileNotFoundException{
		/** Create the problem and the GraphPlanSearch. */
		Problem rocketProblem = createEasyCargoProblem();
		GraphPlanSearch rocket = new GraphPlanSearch(rocketProblem);

		/**
		 * Here we start with the Rocket and Cargo in New Orleans, 
		 * and we need the Rocket in New Orleans and the Cargo in London.
		 * The steps will require loading the Cargo on the Rocket, 
		 * flying the Rocket to London, unloading the Cargo, and flying back to NOLA.
		 * First, test the solution.
		 */
		assertFalse(rocket.isFinished());
		TotalOrderPlan solutionRocket = (TotalOrderPlan) rocket.search();
		assertNotNull(solutionRocket);						/** A valid (non-null) solution should have been returned. */
		assertTrue(rocketProblem.isSolution(solutionRocket));/** Important: was this successfully a solution? */
		assertTrue(rocket.isFinished());						/** Check interior structure (boolean finished should be true after the node creation has taken place) */
		assertFalse(rocket.checkGoalInInitial());			/** This Rocket problem shouldn't have the goal present in its initial conditions. */
		assertFalse(rocket.getNoSteps());					
		assertTrue(rocket.getNodesSetSize() >= 1);			/** Assert that the size of the Nodes set isn't 0 (that defineNodes executed correctly and that createGoalNode worked) */

		/** Test the steps with the iterator. */
		assertTrue(solutionRocket.size() >= 4);				/** There should be at least 4 steps (persistence ones don't matter) */
		Iterator<Step> iteratorRocket = solutionRocket.iterator();/** Create an iterator from the solution. */
		assertTrue(iteratorRocket.hasNext());				/** There should be at least something in the iterator. */

		/** In order to check the presence of the required steps, make an ArrayList of Strings and add String representations of each Step to the ArrayList. */
		ArrayList<String> rocketSteps = new ArrayList<String>();
		while (iteratorRocket.hasNext()){
			rocketSteps.add(iteratorRocket.next().toString());
		}
		/** There should be at least 4 steps in this list (persistence steps are fine) */
		assertTrue(rocketSteps.size() >= 4);
		/** Check for the presence of the required steps. */
		assertTrue(rocketSteps.contains("(load Cargo Rocket NOLA)"));
		assertTrue(rocketSteps.contains("(fly Rocket NOLA London)"));
		assertTrue(rocketSteps.contains("(unload Cargo Rocket London)"));
		assertTrue(rocketSteps.contains("(fly Rocket London NOLA)"));
		
	}
	
	/**
	 * Test the Graph Plan with the Reverse 2 Blocks problem.
	 * This problem should a) succeed and b) require at least 2 steps. 
	 */
	@Test 
	public void testReverse2() throws FileNotFoundException{
		/** Create the problem and the GraphPlanSearch */
		Problem reverse2Problem = createReverse2BlocksProblem();
		GraphPlanSearch reverse2 = new GraphPlanSearch(reverse2Problem);
		
		/**
		 * This problem starts with A clear, A on B, and B on the table.
		 * The goal is to get the opposite, with A on the table and B on A. 
		 * To do this it's necessary to move A to the table and then B on A.
		 * First, test the solution.
		 */
		assertFalse(reverse2.isFinished());
		TotalOrderPlan solutionReverse2 = (TotalOrderPlan) reverse2.search();
		assertNotNull(solutionReverse2);						/** The solution should be valid (i.e. not null) */
		assertTrue(reverse2Problem.isSolution(solutionReverse2));	/** Assert that the solution was a valid solution for this problem. */
		assertTrue(reverse2.isFinished());					/** Assert true that it's finished, that the interior architecture worked correctly. */
		assertFalse(reverse2.checkGoalInInitial());			/** This problem shouldn't have the goal in its initial state (i.e. it requires expansion). */
		assertFalse(reverse2.getNoSteps());			
		assertTrue(reverse2.getNodesSetSize() >= 1);		/** Assert that the nodes set list is at least one- that the CreateGoalNode and DefineNode worked */
		
		/** Test via the Iterator. */
		assertTrue(solutionReverse2.size() >= 2);			/** We should have at least 2 steps. */
		Iterator<Step> iteratorReverse2 = solutionReverse2.iterator();/**Create the iterator */
		assertTrue(iteratorReverse2.hasNext());				/** There should be items present. */
		
		/** Iterate through the steps. Use an ArrayList of Strings to make sure we have at least required steps. */
		ArrayList<String> reverse2Steps = new ArrayList<String>();
		while (iteratorReverse2.hasNext()){
			reverse2Steps.add(iteratorReverse2.next().toString());
		}
		/** There should be at least those 2 steps (persistence steps are fine) */
		assertTrue(reverse2Steps.size() >= 2);
		/** Check for the presence of those two steps. */
		assertTrue(reverse2Steps.contains("(moveToTable a b)"));
		assertTrue(reverse2Steps.contains("(move b table a)"));
		
	}
	
	/** 
	 * Methods to create Problems. 
	 * The doNothing problem (problem that has goals in its initials)
	 * EasyCargoProblem (Rocket)
	 * Cake Problem
	 * Reverse 2 Blocks Problem
	 * @return Problem the created Problem by the specified Benchmark
	 */

	private Problem doNothing() {
		try {
			return new Benchmark("blocks", "do_nothing").getProblem();
		} catch (IOException e) {
			return null;
		}
	}
		
	private Problem createEasyCargoProblem() {
		try {
			return new Benchmark("rocket", "rocket_test").getProblem();
		} catch (IOException e) {
			return null;
		}
	}

	private Problem createCakeProblem() {
		try {
			return new Benchmark("cake", "cake_test").getProblem();
		} catch (IOException e) {
			return null;
		}
	}
	
	private Problem createReverse2BlocksProblem(){
		try{
			return new Benchmark("blocks", "reverse_2").getProblem();
		}catch(IOException e){
			return null;
		}
	}

	
}
