package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Expression;

public class PlanGraphLevelMutexTest {
	
	private Expression cakeGoal;
	
	private PlanGraphLevelMutex cakeTestLevel0;
	private PlanGraphLevelMutex cakeTestLevel1;
	private PlanGraphLevelMutex cakeTestLevel2;
	private PlanGraphLevelMutex cakeTestLevel3;
	private PlanGraphLevelMutex rocketTestLevel0;
	private PlanGraphLevelMutex rocketTestLevel1;
	private PlanGraphLevelMutex rocketTestLevel2;

	private PlanGraphStep eatCake;
	private PlanGraphStep persistenceHaveCake;
	private PlanGraphStep flyRocketNolaLondon;
	private PlanGraphStep loadCargoRocketNola;
	private PlanGraphStep unloadCargoRocketLondon;

	private PlanGraphLiteral haveCake;
	private PlanGraphLiteral notHaveCake;
	private PlanGraphLiteral atCargoLondon;
	private PlanGraphLiteral inCargoRocket;
	
	@Before
	public void setUpLevels(){
		Problem cakeProblem = null;
		Problem rocketProblem = null;
		try 
		{
			cakeProblem = new Benchmark("cake", "cake_test").getProblem();
			rocketProblem = new Benchmark("rocket", "rocket_test").getProblem();
		}
		catch (IOException e)
		{
			System.out.println("Could not load domain");
			System.out.println(e.getMessage());
		}
		
		// Test Levels
		cakeGoal = cakeProblem.goal;
		PlanGraph cakeGraph = new PlanGraph(cakeProblem, true);
		cakeTestLevel0 = new PlanGraphLevelMutex(cakeProblem, cakeGraph.getAllPossiblePlanGraphSteps(), 
				cakeGraph.getAllPossiblePlanGraphEffects(), cakeGraph.getPersistantSteps(), cakeGraph); 
		cakeTestLevel1 = new PlanGraphLevelMutex(cakeTestLevel0); 
		cakeTestLevel2 = new PlanGraphLevelMutex(cakeTestLevel1);
		cakeTestLevel3 = new PlanGraphLevelMutex(cakeTestLevel2);

		PlanGraph rocketGraph = new PlanGraph(rocketProblem, true);
		rocketTestLevel0 = new PlanGraphLevelMutex(rocketProblem, rocketGraph.getAllPossiblePlanGraphSteps(), 
				rocketGraph.getAllPossiblePlanGraphEffects(), rocketGraph.getPersistantSteps(), rocketGraph); 
		rocketTestLevel1 = new PlanGraphLevelMutex(rocketTestLevel0); 
		rocketTestLevel2 = new PlanGraphLevelMutex(rocketTestLevel1);
		
		// Set up test helpers
		for(PlanGraphStep step : cakeGraph.getAllPossiblePlanGraphSteps())
			if(step.getStep().toString().equals("(eat Cake)"))
				eatCake = cakeGraph.getPlanGraphStep(step.getStep());
			else if(step.getStep().toString().equalsIgnoreCase("(Persistence Step (have Cake))"))
				persistenceHaveCake = cakeGraph.getPlanGraphStep(step.getStep());
		
		for(PlanGraphStep step : rocketGraph.getAllPossiblePlanGraphSteps())
			if(step.getStep().toString().equals("(fly Rocket NOLA London)"))
				flyRocketNolaLondon = rocketGraph.getPlanGraphStep(step.getStep());
			else if(step.getStep().toString().equals("(load Cargo Rocket NOLA)"))
				loadCargoRocketNola = rocketGraph.getPlanGraphStep(step.getStep());
			else if(step.getStep().toString().equals("(unload Cargo Rocket London)"))
				unloadCargoRocketLondon = rocketGraph.getPlanGraphStep(step.getStep());

		for(PlanGraphLiteral literal : cakeGraph.getAllPossiblePlanGraphEffects())
			if(literal.getLiteral().toString().equals("(have Cake)"))
				haveCake = cakeGraph.getPlanGraphLiteral(literal.getLiteral());
			else if(literal.getLiteral().toString().equalsIgnoreCase("(not (have Cake))"))
				notHaveCake = cakeGraph.getPlanGraphLiteral(literal.getLiteral());
		
		for(PlanGraphLiteral literal : rocketGraph.getAllPossiblePlanGraphEffects())
			if(literal.getLiteral().toString().equals("(at Cargo London)"))
				atCargoLondon = rocketGraph.getPlanGraphLiteral(literal.getLiteral());
			else if(literal.getLiteral().toString().equals("(in Cargo Rocket)"))
				inCargoRocket = rocketGraph.getPlanGraphLiteral(literal.getLiteral());
	}
	
	@Test
	public void getExclusiveSteps(){
		assertEquals(0,cakeTestLevel0.getMutuallyExclusiveSteps().size());
		assertEquals(3,cakeTestLevel1.getMutuallyExclusiveSteps().size());
		assertEquals(6,cakeTestLevel2.getMutuallyExclusiveSteps().size());

		assertEquals(0,rocketTestLevel0.getMutuallyExclusiveSteps().size());
		assertEquals(7,rocketTestLevel1.getMutuallyExclusiveSteps().size());
		assertEquals(16,rocketTestLevel2.getMutuallyExclusiveSteps().size());
	}
	
	@Test
	public void getExclusiveLiterals(){
		assertEquals(0,cakeTestLevel0.getMutuallyExclusiveLiterals().size());
		assertEquals(4,cakeTestLevel1.getMutuallyExclusiveLiterals().size());
		assertEquals(4,cakeTestLevel2.getMutuallyExclusiveLiterals().size());

		assertEquals(0,rocketTestLevel0.getMutuallyExclusiveLiterals().size());
		assertEquals(8,rocketTestLevel1.getMutuallyExclusiveLiterals().size());
		assertEquals(10,rocketTestLevel2.getMutuallyExclusiveLiterals().size());
	}
	
	@Test
	public void isMutexPGStep(){
		assertFalse(cakeTestLevel0.isMutex(persistenceHaveCake, eatCake));
		assertTrue(cakeTestLevel1.isMutex(persistenceHaveCake, eatCake));
		assertTrue(cakeTestLevel2.isMutex(persistenceHaveCake, eatCake));

		assertFalse(rocketTestLevel0.isMutex(flyRocketNolaLondon, loadCargoRocketNola));
		assertTrue(rocketTestLevel1.isMutex(flyRocketNolaLondon, loadCargoRocketNola));
		assertTrue(rocketTestLevel2.isMutex(flyRocketNolaLondon, loadCargoRocketNola));
	}
	
	@Test
	public void isMutexStep(){
		assertFalse(cakeTestLevel0.isMutex(persistenceHaveCake.getStep(), eatCake.getStep()));
		assertTrue(cakeTestLevel1.isMutex(persistenceHaveCake.getStep(), eatCake.getStep()));
		assertTrue(cakeTestLevel2.isMutex(persistenceHaveCake.getStep(), eatCake.getStep()));

		assertFalse(rocketTestLevel0.isMutex(flyRocketNolaLondon.getStep(), loadCargoRocketNola.getStep()));
		assertTrue(rocketTestLevel1.isMutex(flyRocketNolaLondon.getStep(), loadCargoRocketNola.getStep()));
		assertTrue(rocketTestLevel2.isMutex(flyRocketNolaLondon.getStep(), loadCargoRocketNola.getStep()));
	}
	
	@Test
	public void isMutexPGLiteral(){
		assertFalse(cakeTestLevel0.isMutex(haveCake, notHaveCake));
		assertTrue(cakeTestLevel1.isMutex(haveCake, notHaveCake));
		assertTrue(cakeTestLevel2.isMutex(haveCake, notHaveCake));

		assertFalse(rocketTestLevel0.isMutex(atCargoLondon, inCargoRocket));
		assertFalse(rocketTestLevel1.isMutex(atCargoLondon, inCargoRocket));
		assertTrue(rocketTestLevel2.isMutex(atCargoLondon, inCargoRocket));
	}
	
	@Test
	public void isMutexLiteral(){
		assertFalse(cakeTestLevel0.isMutex(haveCake.getLiteral(), notHaveCake.getLiteral()));
		assertTrue(cakeTestLevel1.isMutex(haveCake.getLiteral(), notHaveCake.getLiteral()));
		assertTrue(cakeTestLevel2.isMutex(haveCake.getLiteral(), notHaveCake.getLiteral()));

		assertFalse(rocketTestLevel0.isMutex(atCargoLondon.getLiteral(), inCargoRocket.getLiteral()));
		assertFalse(rocketTestLevel1.isMutex(atCargoLondon.getLiteral(), inCargoRocket.getLiteral()));
		assertTrue(rocketTestLevel2.isMutex(atCargoLondon.getLiteral(), inCargoRocket.getLiteral()));
	}
	
	@Test
	public void isLeveledOff(){
		assertFalse(cakeTestLevel0.isLeveledOff());
		assertFalse(cakeTestLevel1.isLeveledOff());
		assertFalse(cakeTestLevel2.isLeveledOff());
		assertTrue(cakeTestLevel3.isLeveledOff());
	}
	
	@Test
	public void containsGoal(){
		assertFalse(cakeTestLevel0.containsGoal(cakeGoal));
		assertFalse(cakeTestLevel1.containsGoal(cakeGoal));
		assertTrue(cakeTestLevel2.containsGoal(cakeGoal));
	}
	
	@Test
	public void getParent(){
		assertEquals(cakeTestLevel0.getParent(), null);
		assertEquals(cakeTestLevel1.getParent(), cakeTestLevel0);
		assertEquals(cakeTestLevel2.getParent(), cakeTestLevel1);
		assertEquals(cakeTestLevel3.getParent(), cakeTestLevel2);

		assertEquals(rocketTestLevel0.getParent(), null);
		assertEquals(rocketTestLevel1.getParent(), rocketTestLevel0);
		assertEquals(rocketTestLevel2.getParent(), rocketTestLevel1);
	}
	
	@Test 
	public void getLevel(){
		assertEquals(0, cakeTestLevel0.getLevel());
		assertEquals(1, cakeTestLevel1.getLevel());
		assertEquals(2, cakeTestLevel2.getLevel());
		assertEquals(3, cakeTestLevel3.getLevel());

		assertEquals(0, rocketTestLevel0.getLevel());
		assertEquals(1, rocketTestLevel1.getLevel());
		assertEquals(2, rocketTestLevel2.getLevel());
	}
	
	@Test
	public void pgStepExists(){
		assertFalse(cakeTestLevel0.exists(eatCake));
		assertTrue(cakeTestLevel1.exists(eatCake));
		assertTrue(cakeTestLevel2.exists(eatCake));
		assertTrue(cakeTestLevel3.exists(eatCake));

		assertFalse(rocketTestLevel0.exists(eatCake));
		assertFalse(rocketTestLevel0.exists(loadCargoRocketNola));
		assertTrue(rocketTestLevel1.exists(loadCargoRocketNola));
		assertTrue(rocketTestLevel2.exists(loadCargoRocketNola));
		assertFalse(rocketTestLevel0.exists(unloadCargoRocketLondon));
		assertFalse(rocketTestLevel1.exists(unloadCargoRocketLondon));
		assertTrue(rocketTestLevel2.exists(unloadCargoRocketLondon));
	}
	
	@Test
	public void stepExists(){
		assertFalse(cakeTestLevel0.exists(eatCake.getStep()));
		assertTrue(cakeTestLevel1.exists(eatCake.getStep()));
		assertTrue(cakeTestLevel2.exists(eatCake.getStep()));
		assertTrue(cakeTestLevel3.exists(eatCake.getStep()));

		assertFalse(rocketTestLevel0.exists(eatCake.getStep()));
		assertFalse(rocketTestLevel0.exists(loadCargoRocketNola.getStep()));
		assertTrue(rocketTestLevel1.exists(loadCargoRocketNola.getStep()));
		assertTrue(rocketTestLevel2.exists(loadCargoRocketNola.getStep()));
		assertFalse(rocketTestLevel0.exists(unloadCargoRocketLondon.getStep()));
		assertFalse(rocketTestLevel1.exists(unloadCargoRocketLondon.getStep()));
		assertTrue(rocketTestLevel2.exists(unloadCargoRocketLondon.getStep()));
	}
	
	@Test
	public void pgLiteralExists(){
		assertFalse(cakeTestLevel0.exists(notHaveCake));
		assertTrue(cakeTestLevel1.exists(notHaveCake));
		assertTrue(cakeTestLevel2.exists(notHaveCake));
		assertTrue(cakeTestLevel3.exists(notHaveCake));

		assertFalse(rocketTestLevel0.exists(notHaveCake));
		assertFalse(rocketTestLevel0.exists(atCargoLondon));
		assertFalse(rocketTestLevel1.exists(atCargoLondon));
		assertTrue(rocketTestLevel2.exists(atCargoLondon));
		assertFalse(rocketTestLevel0.exists(inCargoRocket));
		assertTrue(rocketTestLevel1.exists(inCargoRocket));
		assertTrue(rocketTestLevel2.exists(inCargoRocket));
	}
	
	@Test
	public void literalExists(){
		assertFalse(cakeTestLevel0.exists(notHaveCake.getLiteral()));
		assertTrue(cakeTestLevel1.exists(notHaveCake.getLiteral()));
		assertTrue(cakeTestLevel2.exists(notHaveCake.getLiteral()));
		assertTrue(cakeTestLevel3.exists(notHaveCake.getLiteral()));

		assertFalse(rocketTestLevel0.exists(notHaveCake.getLiteral()));
		assertFalse(rocketTestLevel0.exists(atCargoLondon.getLiteral()));
		assertFalse(rocketTestLevel1.exists(atCargoLondon.getLiteral()));
		assertTrue(rocketTestLevel2.exists(atCargoLondon.getLiteral()));
		assertFalse(rocketTestLevel0.exists(inCargoRocket.getLiteral()));
		assertTrue(rocketTestLevel1.exists(inCargoRocket.getLiteral()));
		assertTrue(rocketTestLevel2.exists(inCargoRocket.getLiteral()));
	}
}
