package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.util.ImmutableArray;

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
	
	// Test Helpers
	private Conjunction getEmptyConjunction(String name){
		Constant c = new Constant(name, name);
		Predication p = new Predication(name, new ImmutableArray<Term>(new Term[] {c}));
		return new Conjunction(p);
	}
	
	private PlanGraphStep getTestStep(String name){
		Conjunction preconditions = getEmptyConjunction("preconditions");
		Conjunction effects = getEmptyConjunction("effects");
		return new PlanGraphStep(new Step(name, preconditions, effects));
	}
	
	private PlanGraphStep getStep(String stepName, List<PlanGraphStep> steps){
		PlanGraphStep thisStep = getTestStep(stepName);
		for(PlanGraphStep step : steps)
			if(thisStep.GetStep().name.equals(step.GetStep().name))
				return step;
		return null;
	}
	
	@Test
	public void checkGetStep(){
		PlanGraphStep thisStep = getTestStep("test1");
		PlanGraphStep otherStep = getTestStep("test2");
		List<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		steps.add(thisStep);
		steps.add(otherStep);
		assertEquals(thisStep, getStep("test1", steps));
		assertEquals(otherStep, getStep("test2", steps));
	}
	
	private PlanGraphStep getCakeDomainStep(String cakeStepName){
		PlanGraph initialCakeGraph = createCakePlanGraph();
		PlanGraph firstCakeStep = new PlanGraph(initialCakeGraph);
		return getStep(cakeStepName, firstCakeStep.getCurrentSteps());
	}
	
	@Test
	public void checkGetCakeDomainStep(){
		String actionName = "(eat Cake)";
		PlanGraphStep test = getTestStep(actionName);
		PlanGraphStep result = getCakeDomainStep(actionName);
		assertEquals(0, test.GetStep().compareTo(result.GetStep()));
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
		// Test mutex Literals does not contain any entries for (not (at cargo london))
		Constant cargo = new Constant("cargo", "Cargo");
		Constant rocket = new Constant("rocket", "Rocket"); 
		Constant nola = new Constant("location", "NOLA"); 
		Constant london = new Constant("location", "London");
		Predication cargoAtLondon = new Predication("at", cargo, london);
		NegatedLiteral notCargoAtLondon = cargoAtLondon.negate();
		PlanGraphLiteral cargoIsNotAtLondon0 = new PlanGraphLiteral(notCargoAtLondon, 0);
		PlanGraphLiteral cargoIsNotAtLondon1 = new PlanGraphLiteral(notCargoAtLondon, 1);
		List<PlanGraphLiteral> cinalMutexLiterals = null;
		for(PlanGraphLiteral key : firstRocketStep.getMutuallyExclusiveLiterals().keySet()){
			if(key.equals(cargoIsNotAtLondon0) || key.equals(cargoIsNotAtLondon1)){
				cinalMutexLiterals = firstRocketStep.getMutuallyExclusiveLiterals().get(key);
			}
		}
		assertNull(cinalMutexLiterals);
		// Test mutually exclusive Steps and Literals  during this step
		assertEquals(7, firstRocketStep.getMutuallyExclusiveSteps().size());
 		assertEquals(8, firstRocketStep.getMutuallyExclusiveLiterals().size());
		// Test literals
		assertEquals(10,firstRocketStep.getAllLiterals().size());
		assertEquals(9, firstRocketStep.getCurrentLiterals().size());
		// Test mutuallyExclusiveStep specifics (fly Rocket NOLA NOLA)
//		(fly Rocket NOLA NOLA)[1]=[(Persistence Step (at Rocket NOLA))[1], 	<----
//		          				 (load Cargo Rocket NOLA)[1], 				<----
//		          				 (fly Rocket NOLA London)[1] 				<----
//		          				]
		Predication rocketAtNola = new Predication("at", rocket, nola);
		NegatedLiteral notRocketAtNola = rocketAtNola.negate();
		Conjunction flyNola2Precondition = new Conjunction(rocketAtNola);
		Conjunction flyNola2Effect= new Conjunction(notRocketAtNola, rocketAtNola);
		Step flyRocketNolaNola = new Step("(fly Rocket NOLA NOLA)", flyNola2Precondition, flyNola2Effect);
		PlanGraphStep pgFlyRocketNolaNola0 = new PlanGraphStep(flyRocketNolaNola, 0);
		PlanGraphStep pgFlyRocketNolaNola1 = new PlanGraphStep(flyRocketNolaNola, 1);
		List<PlanGraphStep> mutexStepsForFlyNolaNola = new ArrayList<PlanGraphStep>();
		for(PlanGraphStep key : firstRocketStep.getMutuallyExclusiveSteps().keySet()){
			if((key.GetStep().compareTo(pgFlyRocketNolaNola0.GetStep()) == 0) || 
			   (key.GetStep().compareTo(pgFlyRocketNolaNola1.GetStep()) == 0)){
				mutexStepsForFlyNolaNola = firstRocketStep.getMutuallyExclusiveSteps().get(key);
				break;
			}
		}
		assertEquals(3,mutexStepsForFlyNolaNola.size());
	}
	
	@Test
	public void entireCakePlanGraph()
	{
		Problem cakeProblem = createCakeProblem();
		PlanGraph cakePlanGraph = PlanGraph.create(cakeProblem);
		PlanGraph cakePlanGraphExt = new PlanGraph(cakePlanGraph);
		assertNotNull(cakePlanGraph);
		assertNotNull(cakePlanGraphExt);
	}
	
	@Test
	public void entireRocketPlanGraph()
	{
		Problem rocketProblem = createEasyCargoProblem();
		PlanGraph rocketPlanGraph = PlanGraph.create(rocketProblem);
		assertNotNull(rocketPlanGraph);
	}
	
	@Test
	public void nullIsMutex(){
		PlanGraph initialCakeGraph = createCakePlanGraph();
		PlanGraph firstCakeStep = new PlanGraph(initialCakeGraph);
		PlanGraphStep noopCargoNola = null;
		PlanGraphStep flyRocketNolaLondon = null;
		assertFalse(firstCakeStep.isMutex(noopCargoNola, flyRocketNolaLondon));
	}
	
	@Test
	public void isMutex(){
		PlanGraph initialCakeGraph = createCakePlanGraph();
		PlanGraph firstCakeStep = new PlanGraph(initialCakeGraph);
		// Build Steps
		PlanGraphStep eatCake = getCakeDomainStep("(eat Cake)");
		PlanGraphStep noopHaveCake = getCakeDomainStep("(Persistence Step (have Cake))");
		assertEquals(true, firstCakeStep.isMutex(eatCake, noopHaveCake));
//		Step eatCake = getCake;
//
//		String actionName = "(eat Cake)";
//		PlanGraphStep test = getTestStep(actionName);
//		PlanGraphStep result = getCakeDomainStep(actionName);
//		Step noopCargoNola = null;
//		Step flyRocketNolaLondon = null;
////		loadCargoRocketNola = new PlanGraphStep();
//		assertEquals(true,firstCakeStep.isMutex(loadCargoRocketNola, noopCargoNola));
//		assertTrue(firstCakeStep.isMutex(loadCargoRocketNola, noopCargoNola));
//		assertTrue(firstCakeStep.isMutex(loadCargoRocketNola, flyRocketNolaLondon));
//		assertFalse(firstCakeStep.isMutex(noopCargoNola, flyRocketNolaLondon));
	}
	
//	@Test
//	public void isGoalNonMutex(){
//		assertFalse(true);
//	}
//	
//	@Test
//	public void isContainsGoal(){
//		assertFalse(true);
//	}
//	
//	@Test
//	public void getSolvingActions(){
//		assertFalse(true);
//	}
//	
//	@Test
//	public void leveledOff(){
//		PlanGraph initialCakeGraph = createCakePlanGraph();
//		PlanGraph firstCakeStep = new PlanGraph(initialCakeGraph);
//		PlanGraph secondCakeStep = new PlanGraph(firstCakeStep);
//		PlanGraph leveledCakeStep = new PlanGraph(secondCakeStep);
//		assertTrue(leveledCakeStep.isLeveledOff());
//	}
//	
//	@Test
//	public void planGraphToString(){
//		assertFalse(true);
//	}
}
