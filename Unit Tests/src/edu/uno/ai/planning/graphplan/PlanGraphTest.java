package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import org.junit.*;

import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class PlanGraphTest {
	
	private static String LOCATION_TYPE = "location";
	private static String ROCKET_TYPE = "rocket";
	private static String CARGO_TYPE = "cargo";
	private static String CAKE_TYPE = "cake";
	
	// CARGO DOMAIN

	private Domain createCargoDomain(Constant rocket, Constant box, Constant nola, Constant london){
		// Cargo Constants
		ImmutableArray<Constant> cargoConstants = new ImmutableArray<Constant>(new Constant[]{rocket, box, nola, london});
		// Cargo Operators
		ImmutableArray<Operator> cargoOperators = new ImmutableArray<Operator>(new Operator[]{
			createLoadOperator(),
			createUnloadOperator(), 
			createFlyOperator()
		});
		Domain cargoDomain = new Domain("Cargo Domain", cargoConstants, cargoOperators);
		return cargoDomain;
	}
	
	private Operator createLoadOperator(){
		//	  (:action load
		//    :parameters (?cargo - cargo ?plane - plane ?airport - airport)
		//    :precondition (and (at ?plane ?airport)
		//                       (at ?cargo ?airport))
		//    :effect (and (not (at ?cargo ?airport))
		//                 (in ?cargo ?plane)))
		Variable c = new Variable(CARGO_TYPE, "Cargo");
		Variable r = new Variable(ROCKET_TYPE, "Rocket");
		Variable l = new Variable(LOCATION_TYPE, "Location");
		ImmutableArray<Variable> loadParameters = new ImmutableArray<Variable>(new Variable[] {c, r ,l});;
		Predication rocketAtAirport = new Predication("at", r, l);
		Predication cargoAtAirport = new Predication("at", c, l);
		NegatedLiteral cargoNotAtAirport = cargoAtAirport.negate();
		Predication cargoInRocket = new Predication("in", c, r);
		Conjunction loadPreconditions = new Conjunction(rocketAtAirport, cargoAtAirport);
		Conjunction loadEffects = new Conjunction(cargoNotAtAirport, cargoInRocket);
		return new Operator("Load", loadParameters, loadPreconditions, loadEffects);
	}
	
	private Operator createUnloadOperator(){
		//	  (:action unload
		//	    :parameters (?cargo - cargo ?plane - plane ?airport - airport)
		//	    :precondition (and (in ?cargo ?plane)
		//	                       (at ?plane ?airport))
		//	    :effect (and (at ?cargo ?airport)
		//	                 (not (in ?cargo ?plane))))
		Variable c = new Variable(CARGO_TYPE, "Cargo");
		Variable r = new Variable(ROCKET_TYPE, "Rocket");
		Variable l = new Variable(LOCATION_TYPE, "Location");
		ImmutableArray<Variable> unloadParameters = new ImmutableArray<Variable>(new Variable[]{c, r, l});
		Predication cargoInRocket = new Predication("in", c, r);
		NegatedLiteral cargoNotInRocket = cargoInRocket.negate();
		Predication rocketAtAirport = new Predication("at", r, l);
		Predication cargoAtAirport = new Predication("at", c, l);
		Conjunction unloadPreconditions = new Conjunction(cargoInRocket, rocketAtAirport);
		Conjunction unloadEffects = new Conjunction(cargoAtAirport,cargoNotInRocket);
		return new Operator("Unload", unloadParameters, unloadPreconditions, unloadEffects);
	}
	

	private Operator createFlyOperator(){
		//	  (:action fly
		//	    :parameters (?plane - plane ?from - airport ?to - airport)
		//	    :precondition (at ?plane ?from)
		//	    :effect (and (not (at ?plane ?from))
		//	                 (at ?plane ?to))))
		Variable r = new Variable(ROCKET_TYPE, "Rocket");
		Variable fromL = new Variable(LOCATION_TYPE, "From Location");
		Variable toL = new Variable(LOCATION_TYPE, "To Location");
		Predication rocketAtFromLocation = new Predication("at", r, fromL);
		NegatedLiteral rocketNotAtFromLocation = rocketAtFromLocation.negate();
		Predication rocketAtToLocation = new Predication("at", r, toL);
		Conjunction flyPreconditions = new Conjunction(rocketAtFromLocation);
		Conjunction flyEffects = new Conjunction(rocketNotAtFromLocation, rocketAtToLocation);
		ImmutableArray<Variable> flyParameters = new ImmutableArray<Variable>(new Variable[]{r, fromL, toL});
		return new Operator("Fly", flyParameters, flyPreconditions, flyEffects);
	}
	
	private Problem createEasyCargoProblem(){
		Constant rocket = new Constant(ROCKET_TYPE, "Rocket1");
		Constant box = new Constant(CARGO_TYPE, "Box1");
		Constant nola = new Constant(LOCATION_TYPE, "NOLA");
		Constant london = new Constant(LOCATION_TYPE, "London");
		ImmutableArray<Constant> cargoObjects = new ImmutableArray<Constant>(new Constant[]{rocket, box, nola, london});
		
		Domain cargoDomain = createCargoDomain(rocket, box, nola, london);

		Predication boxAtNola = new Predication("at", box, nola);
		Predication rocketAtNola = new Predication("at", rocket, nola);
		Predication boxInRocket = new Predication("in", box, rocket);
		NegatedLiteral boxNotInRocket = boxInRocket.negate();
		Predication boxAtLondon = new Predication("at", box, london);
		NegatedLiteral boxNotAtLondon = boxAtLondon.negate();
		Predication rocketAtLondon = new Predication("at", rocket, london);
		NegatedLiteral rocketNotAtLondon = rocketAtLondon.negate();
		State initialState = getState(boxAtNola,rocketAtNola,boxNotInRocket, boxNotAtLondon, rocketNotAtLondon);
		Conjunction goal = new Conjunction(boxAtLondon,rocketAtNola);
		return new Problem("Cargo", cargoDomain, cargoObjects, initialState, goal);
	}
	
	// CAKE DOMAIN
	
	private Operator createBakeCakeOperator(){
		//	 (:action bake
		//		:parameters (?cake - cake)
		//		:precondition (not (have cake))
		//		:effects (have cake)
		Variable c = new Variable(CAKE_TYPE, "Cake");
		Predication haveCake = new Predication("have", c);
		NegatedLiteral notHaveCake = haveCake.negate();
		Conjunction bakePreconditions = new Conjunction(notHaveCake);
		Conjunction bakeEffects = new Conjunction(haveCake);
		ImmutableArray<Variable> bakeParameters = new ImmutableArray<Variable>(new Variable[]{c});
		return new Operator("Bake Cake", bakeParameters, bakePreconditions, bakeEffects);
	}
	
	private Operator createEatCakeOperator(){
		//	 (:action eat
		//	 	:parameters (?cake - cake)
		//		:preconditions (have cake)
		//		:effects (and (not (have cake))
		//					  (eat cake))
		Variable c = new Variable(CAKE_TYPE, "Cake");
		Predication haveCake = new Predication("have", c);
		NegatedLiteral notHaveCake = haveCake.negate();
		Predication eatCake = new Predication("eat", c);
		Conjunction eatPreconditions = new Conjunction(haveCake);
		Conjunction eatEffects = new Conjunction(notHaveCake, eatCake);
		ImmutableArray<Variable> eatParameters = new ImmutableArray<Variable>(new Variable[]{c});
		return new Operator("Eat Cake", eatParameters, eatPreconditions, eatEffects);
	}
	
	private Domain createCakeDomain(Constant cake){
		// Cake Constants
		ImmutableArray<Constant> cakeConstants = new ImmutableArray<Constant>(new Constant[] {cake});
		// Cake Operators
		ImmutableArray<Operator> cakeOperators = new ImmutableArray<Operator>(new Operator[] {
				createBakeCakeOperator(),
				createEatCakeOperator()
		});
		Domain cakeDomain = new Domain("Cake Domain", cakeConstants, cakeOperators);
		return cakeDomain;
	}
	
	private Problem createCakeProblem(){
		Constant cake = new Constant(CAKE_TYPE, "Cake");
		ImmutableArray<Constant> cakeObjects = new ImmutableArray<Constant>(new Constant[] {cake});
		
		Domain cakeDomain = createCakeDomain(cake);
		
		Predication haveCake = new Predication("have", cake);
		Predication eatCake = new Predication("eat", cake);
		NegatedLiteral notEatCake = eatCake.negate();
		
		State initialState = getState(haveCake, notEatCake);
		Conjunction goal = new Conjunction(haveCake, eatCake);
		return new Problem("Cake", cakeDomain, cakeObjects, initialState, goal);
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
		// Test nothing should be mutually exclusive in initial state
		assertEquals(6, firstRocketStep.getMutuallyExclusiveSteps().size());
		assertEquals(8, firstRocketStep.getMutuallyExclusiveLiterals().size());
		// Test literals
		assertEquals(10,firstRocketStep.getAllLiterals().size());
		assertEquals(9, firstRocketStep.getCurrentLiterals().size());
	}

//	private static String DOMAIN = "cargo";
//	private static String PROBLEM = "deliver_1";
//	private PlanGraph createLoadedPlanGraph(){
//		Benchmark benchmark = new Benchmark(DOMAIN, PROBLEM);
//		Problem p = null;
//		try{
//			p = benchmark.getProblem();
//		}catch(IOException ex){
//			System.out.println(ex.getMessage());
//		}
//		PlanGraph graph = p == null ? null : new PlanGraph(p);
//		return graph;
//	}
//	
//	@Test
//	public void testLoadedGraph(){
//		PlanGraph loadedGraph = createLoadedPlanGraph();
//
//		assertNotNull(loadedGraph);
//		assertEquals(18,loadedGraph.getAllSteps().size());
//		assertEquals(0,loadedGraph.getCurrentSteps().size());
//		assertEquals(10, localGraph.getAllLiterals().size());
//		assertEquals(0, localGraph.getCurrentLiterals().size());
//	}
	
	private State getState(Literal...literals){
		MutableState initialState = new MutableState();
		for(Literal e : literals){
			initialState.impose(e);
		}
		return initialState.clone();
	}

}
