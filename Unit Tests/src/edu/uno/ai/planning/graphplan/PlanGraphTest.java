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
import edu.uno.ai.planning.logic.Expression;
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
			if(thisStep.getStep().name.equals(step.getStep().name))
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
	
	@Test
	public void checkGetCakeDomainStep(){
		String actionName = "(eat Cake)";
		PlanGraphStep test = getTestStep(actionName);
		PlanGraph cakeGraph = createCakePlanGraph();
		PlanGraphStep result = getStep(actionName, cakeGraph.getAllPossiblePlanGraphSteps());
		assertEquals(0, test.getStep().compareTo(result.getStep()));
	}
	
	private PlanGraph createCakePlanGraph(){
		Problem cakeProblem = createCakeProblem();
		PlanGraph graph = new PlanGraph(cakeProblem, true);
		return graph;
	}
	
	private PlanGraph createRocketPlanGraph(){
		Problem cargoProblem = createEasyCargoProblem();
		PlanGraph graph = new PlanGraph(cargoProblem, true);
		return graph;
	}
	
	private Expression getHaveCakeAndEatIt(){
		Problem cakeProblem = createCakeProblem();
		return cakeProblem.goal;
	}
	
	
	// TESTS
	
	@Test
	public void constructorCakeDomain(){
		// TEST CAKE DOMAIN
		PlanGraph cakeGraph = createCakePlanGraph();
		PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)cakeGraph.getRootLevel(); 

		// Test existence
		assertNotNull(rootLevel);
		// Test parent and level for new Plan Graph
		assertNull(rootLevel.getParent());
		assertEquals(0, rootLevel.getLevel());
		// Test nothing should be mutually exclusive in initial state
		assertEquals(0, rootLevel.getMutuallyExclusiveSteps().size());
		assertEquals(0, rootLevel.getMutuallyExclusiveLiterals().size());
		// Test steps
		assertEquals(0, rootLevel.countCurrentSteps());
		// Test literals
		assertEquals(2, rootLevel.countCurrentEffects());
	}
	
	@Test
	public void firstStepCakeDomain(){
		PlanGraph cakeGraph = createCakePlanGraph();
		PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)cakeGraph.getRootLevel();
		PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)cakeGraph.getLevel(1);

		// Test existence
		assertNotNull(firstLevel);
		// Test parent and level for new Plan Graph
		assertNotNull(firstLevel.getParent());
		assertEquals(firstLevel.getParent(), rootLevel);
		assertEquals(1, firstLevel.getLevel());
		// Test steps
		assertEquals(3, firstLevel.countCurrentSteps());
		// ---------
		assertEquals(3, firstLevel.getMutuallyExclusiveSteps().size());
		assertEquals(4, firstLevel.getMutuallyExclusiveLiterals().size());
		// Test literals
		assertEquals(4, firstLevel.countCurrentEffects());	
	}
	
	@Test
	public void constructorRocketDomain(){
		// TEST ROCKET DOMAIN
		PlanGraph rocketGraph = createRocketPlanGraph();
		PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)rocketGraph.getRootLevel();

		// Test existence
		assertNotNull(rootLevel);
		// Test parent and level for new Plan Graph
		assertNull(rootLevel.getParent());
		assertEquals(0, rootLevel.getLevel());
		// Test nothing should be mutually exclusive in initial state
		assertEquals(0, rootLevel.getMutuallyExclusiveSteps().size());
		assertEquals(0, rootLevel.getMutuallyExclusiveLiterals().size());
		// Test steps
		assertEquals(0, rootLevel.countCurrentSteps());
		// Test literals
		assertEquals(5, rootLevel.countCurrentEffects());
	}
	
	@Test
	public void firstStepRocketDomain(){
		PlanGraph rocketGraph = createRocketPlanGraph();
		PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)rocketGraph.getRootLevel();
		PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)rocketGraph.getLevel(1);

		// Test existence
		assertNotNull(firstLevel);
		// Test parent and level for new Plan Graph
		assertNotNull(firstLevel.getParent());
		assertEquals(firstLevel.getParent(), rootLevel);
		assertEquals(1, firstLevel.getLevel());
		// Test steps
		assertEquals(8, firstLevel.countCurrentSteps());
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
		for(PlanGraphLiteral key : firstLevel.getMutuallyExclusiveLiterals().keySet()){
			if(key.equals(cargoIsNotAtLondon0) || key.equals(cargoIsNotAtLondon1)){
				cinalMutexLiterals = firstLevel.getMutuallyExclusiveLiterals().get(key);
			}
		}
		assertNull(cinalMutexLiterals);
		// Test mutually exclusive Steps and Literals  during this step
		assertEquals(7, firstLevel.getMutuallyExclusiveSteps().size());
 		assertEquals(8, firstLevel.getMutuallyExclusiveLiterals().size());
		// Test literals
		assertEquals(9, firstLevel.countCurrentEffects());
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
		for(PlanGraphStep key : firstLevel.getMutuallyExclusiveSteps().keySet()){
			if((key.getStep().compareTo(pgFlyRocketNolaNola0.getStep()) == 0) || 
			   (key.getStep().compareTo(pgFlyRocketNolaNola1.getStep()) == 0)){
				mutexStepsForFlyNolaNola = firstLevel.getMutuallyExclusiveSteps().get(key);
				break;
			}
		}
		assertEquals(3,mutexStepsForFlyNolaNola.size());
	}
	
	@Test
	public void entireCakePlanGraph()
	{
		Problem cakeProblem = createCakeProblem();
		PlanGraph cakeGraph = new PlanGraph(cakeProblem);
		assertNotNull(cakeGraph);
		assertEquals(2, cakeGraph.countLevels());
		cakeGraph.extend();
		assertEquals(3, cakeGraph.countLevels());
	}
	
	@Test
	public void entireRocketPlanGraph()
	{
		Problem rocketProblem = createEasyCargoProblem();
		PlanGraph rocketGraph = new PlanGraph(rocketProblem);
		assertNotNull(rocketGraph);
	}
	
	@Test
	public void nullIsMutex(){
		PlanGraph cakeGraph = createCakePlanGraph();
		PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)cakeGraph.getLevel(1);
		
		PlanGraphStep noopCargoNola = null;
		PlanGraphStep flyRocketNolaLondon = null;
		assertFalse(firstLevel.isMutex(noopCargoNola, flyRocketNolaLondon));
	}
	
	@Test
	public void isMutex(){
		PlanGraph cakeGraph = createCakePlanGraph();
		PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)cakeGraph.getLevel(1);
		
		// Build Steps
		PlanGraphStep eatCake = getStep("(eat Cake)", cakeGraph.getAllPossiblePlanGraphSteps());
		PlanGraphStep noopHaveCake = getStep("(Persistence Step (have Cake))", cakeGraph.getAllPossiblePlanGraphSteps());
		PlanGraphStep noopNotEatCake = getStep("(Persistence Step (not (eaten Cake)))", cakeGraph.getAllPossiblePlanGraphSteps());
		assertTrue(firstLevel.isMutex(eatCake, noopHaveCake));
		assertTrue(firstLevel.isMutex(eatCake, noopNotEatCake));
		assertFalse(firstLevel.isMutex(noopHaveCake, noopNotEatCake));
	}
	
	@Test
	public void isContainsGoal(){
		Expression goal = getHaveCakeAndEatIt();
		PlanGraph cakeGraph = createCakePlanGraph();
		assertTrue(cakeGraph.containsGoal(goal));

		PlanGraphLevel rootLevel = cakeGraph.getRootLevel();
		PlanGraphLevel firstLevel = cakeGraph.getLevel(1);
		PlanGraphLevel secondLevel = cakeGraph.getLevel(2);
		assertFalse(rootLevel.containsGoal(goal));
		assertFalse(firstLevel.containsGoal(goal));
		assertTrue(secondLevel.containsGoal(goal));
	}
	
	@Test
	public void leveledOff(){
		PlanGraph cakeGraph = createCakePlanGraph();
		assertFalse(cakeGraph.isLeveledOff());
		cakeGraph.extend();
		assertTrue(cakeGraph.isLeveledOff());
		cakeGraph.extend();
		assertTrue(cakeGraph.isLeveledOff());
	}
	
//	@Test
//	public void planGraphToString(){
//		assertFalse(true);
//	}
}
