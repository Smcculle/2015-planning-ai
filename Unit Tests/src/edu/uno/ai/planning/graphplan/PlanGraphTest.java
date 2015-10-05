package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.*;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Problem;

public class PlanGraphTest {
	
	private Problem createEasyCargoProblem()
	{
		try 
		{
			return new Benchmark("rocket", "rocket_test").getProblem();
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	private Problem createCakeProblem()
	{
		try 
		{
			return new Benchmark("cake", "cake_test").getProblem();
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	private PlanGraph createCakePlanGraph(){
		Problem cakeProblem = createCakeProblem();
		PlanGraph graph = new PlanGraph(cakeProblem);
		return graph;
	}
	
	private PlanGraph createRocketPlanGraph(){
		Problem cargoProblem = createEasyCargoProblem();
		PlanGraph graph = new PlanGraph(cargoProblem);
		return graph;
	}
	
	@Test
	public void constructorCakeDomain(){
		// TEST CAKE DOMAIN
		PlanGraph initialCakeGraph = createCakePlanGraph();

		// Test existence
		assertNotNull(initialCakeGraph);
		// Test parent and level for new Plan Graph
		assertNull(initialCakeGraph.getParent());
		assertEquals(0, initialCakeGraph.getLevel());
		// Test nothing should be mutually exclusive in initial state
		assertEquals(0, initialCakeGraph.getMutuallyExclusiveSteps().size());
		assertEquals(0, initialCakeGraph.getMutuallyExclusiveLiterals().size());
		// Test steps
		assertEquals(6,initialCakeGraph.getAllSteps().size());
		assertEquals(0, initialCakeGraph.getCurrentSteps().size());
		// Test literals
		assertEquals(4,initialCakeGraph.getAllLiterals().size());
		assertEquals(2, initialCakeGraph.getCurrentLiterals().size());
	}
	
	@Test
	public void firstStepCakeDomain(){
		PlanGraph initialCakeGraph = createCakePlanGraph();
		PlanGraph firstCakeStep = new PlanGraph(initialCakeGraph);

		// Test existence
		assertNotNull(firstCakeStep);
		// Test parent and level for new Plan Graph
		assertNotNull(firstCakeStep.getParent());
		assertEquals(firstCakeStep.getParent(), initialCakeGraph);
		assertEquals(1, firstCakeStep.getLevel());
		// Test steps
		assertEquals(6,firstCakeStep.getAllSteps().size());
		assertEquals(3, firstCakeStep.getCurrentSteps().size());
		// Test nothing should be mutually exclusive in initial state
		assertEquals(3, firstCakeStep.getMutuallyExclusiveSteps().size());
		assertEquals(4, firstCakeStep.getMutuallyExclusiveLiterals().size());
		// Test literals
		assertEquals(4,firstCakeStep.getAllLiterals().size());
		assertEquals(4, firstCakeStep.getCurrentLiterals().size());	
	}
	
	@Test
	public void constructorRocketDomain(){
		// TEST ROCKET DOMAIN
		PlanGraph initialRocketGraph = createRocketPlanGraph();

		// Test existence
		assertNotNull(initialRocketGraph);
		// Test parent and level for new Plan Graph
		assertNull(initialRocketGraph.getParent());
		assertEquals(0, initialRocketGraph.getLevel());
		// Test nothing should be mutually exclusive in initial state
		assertEquals(0, initialRocketGraph.getMutuallyExclusiveSteps().size());
		assertEquals(0, initialRocketGraph.getMutuallyExclusiveLiterals().size());
		// Test steps
		assertEquals(18,initialRocketGraph.getAllSteps().size());
		assertEquals(0, initialRocketGraph.getCurrentSteps().size());
		// Test literals
		assertEquals(10,initialRocketGraph.getAllLiterals().size());
		assertEquals(5, initialRocketGraph.getCurrentLiterals().size());
	}
	
	@Test
	public void firstStepRocketDomain(){
		PlanGraph initialRocketGraph = createRocketPlanGraph();
		PlanGraph firstRocketStep = new PlanGraph(initialRocketGraph);

		// Test existence
		assertNotNull(firstRocketStep);
		// Test parent and level for new Plan Graph
		assertNotNull(firstRocketStep.getParent());
		assertEquals(firstRocketStep.getParent(), initialRocketGraph);
		assertEquals(1, firstRocketStep.getLevel());
		// Test steps
		assertEquals(18,firstRocketStep.getAllSteps().size());
		assertEquals(8, firstRocketStep.getCurrentSteps().size());
		// Test mutually exclusive Steps and Literals  during this step
		assertEquals(7, firstRocketStep.getMutuallyExclusiveSteps().size());
 		assertEquals(8, firstRocketStep.getMutuallyExclusiveLiterals().size());
		// Test literals
		assertEquals(10,firstRocketStep.getAllLiterals().size());
		assertEquals(9, firstRocketStep.getCurrentLiterals().size());
		// Test mutuallyExclusiveStep specifics (fly Rocket NOLA NOLA)
		
	}
}
