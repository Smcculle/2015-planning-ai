package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.*;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class PlanGraphTest {
	
	private static String DOMAIN = "cargo";
	private static String PROBLEM = "deliver_1";
	
	private static String LOCATION_TYPE = "location";
	private static String ROCKET_TYPE = "rocket";
	private static String CARGO_TYPE = "cargo";

	private Domain createCargoDomain(Constant rocket, Constant box, Constant nola, Constant london){
		// Cargo Constants
		ImmutableArray<Constant> cargoConstants = new ImmutableArray<Constant>(new Constant[]{rocket, box, nola, london});
		// Cargo Operators
		ImmutableArray<Operator> cargoOperators = new ImmutableArray<Operator>(new Operator[]{
//			createLoadOperator(rocket, box, nola, london),
//			createUnloadOperator(rocket, box, nola, london), 
//			createFlyOperator(rocket, box, nola, london)
			createLoadOperator(rocket, box, nola),
			createLoadOperator(rocket, box, london),
			createUnloadOperator(rocket, box, nola),
			createUnloadOperator(rocket, box, london), 
			createFlyOperator(rocket, nola, london), 
			createFlyOperator(rocket, london, nola)
				
		});
		Domain cargoDomain = new Domain("Cargo Domain", cargoConstants, cargoOperators);
		return cargoDomain;
	}
	
	private Operator createLoadOperator(Constant rocket, Constant box, Constant location){
		//	  (:action load
		//    :parameters (?cargo - cargo ?plane - plane ?airport - airport)
		//    :precondition (and (at ?plane ?airport)
		//                       (at ?cargo ?airport))
		//    :effect (and (not (at ?cargo ?airport))
		//                 (in ?cargo ?plane)))
		Variable c = new Variable(CARGO_TYPE, "Cargo");
		Variable r = new Variable(ROCKET_TYPE, "Rocket");
		Variable l = new Variable(LOCATION_TYPE, "Location");
		ImmutableArray<Variable> loadParameters = new ImmutableArray<Variable>(new Variable[] {c, r ,l});
		Predication rocketAtAirport = new Predication("at", rocket, location);
		Predication cargoAtAirport = new Predication("at", box, location);
		NegatedLiteral cargoNotAtAirport = cargoAtAirport.negate();
		Predication cargoInRocket = new Predication("in", box, rocket);
		Conjunction loadPreconditions = new Conjunction(rocketAtAirport, cargoAtAirport);
		Conjunction loadEffects = new Conjunction(cargoNotAtAirport, cargoInRocket);
		return new Operator("Load", loadParameters, loadPreconditions, loadEffects);
	}
	
	private Operator createUnloadOperator(Constant cargo, Constant rocket, Constant location){
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
		Predication cargoInRocket = new Predication("in", cargo, rocket);
		NegatedLiteral cargoNotInRocket = cargoInRocket.negate();
		Predication rocketAtAirport = new Predication("at", rocket, location);
		Predication cargoAtAirport = new Predication("at", cargo, location);
		Conjunction unloadPreconditions = new Conjunction(cargoInRocket, rocketAtAirport);
		Conjunction unloadEffects = new Conjunction(cargoAtAirport,cargoNotInRocket);
		return new Operator("Unload", unloadParameters, unloadPreconditions, unloadEffects);
	}

	private Operator createFlyOperator(Constant rocket, Constant fromLocation, Constant toLocation){
		//	  (:action fly
		//	    :parameters (?plane - plane ?from - airport ?to - airport)
		//	    :precondition (at ?plane ?from)
		//	    :effect (and (not (at ?plane ?from))
		//	                 (at ?plane ?to))))
		Predication rocketAtFromLocation = new Predication("at", rocket, fromLocation);
		NegatedLiteral rocketNotAtFromLocation = rocketAtFromLocation.negate();
		Predication rocketAtToLocation = new Predication("at", rocket, toLocation);
		Conjunction flyPreconditions = new Conjunction(rocketAtFromLocation);
		Conjunction flyEffects = new Conjunction(rocketNotAtFromLocation, rocketAtToLocation);
		ImmutableArray<Variable> flyParameters = new ImmutableArray<Variable>(new Variable[]{
				new Variable(ROCKET_TYPE,"Rocket"), new Variable(LOCATION_TYPE, "From Location"), new Variable(LOCATION_TYPE, "To Location")});
		return new Operator("Fly", flyParameters, flyPreconditions, flyEffects);
	}
	
	private Problem createEasyCargoProblem(){
		Constant rocket = new Constant(ROCKET_TYPE, "Rocket1");
		Constant box = new Constant(CARGO_TYPE, "Box1");
		Constant nola = new Constant(LOCATION_TYPE, "NOLA");
		Constant london = new Constant(LOCATION_TYPE, "London");
		ImmutableArray<Constant> cargoObjects = new ImmutableArray<Constant>(new Constant[]{rocket, box, nola, london});
		
		Domain cargoDomain = createCargoDomain(rocket, box, nola, london);

		State initialState = getState(new Predication("at", box, nola),
									  new Predication("at", rocket, nola));
		Predication boxAtLondon = new Predication("at", box, london);
		Predication rocketAtNola = new Predication("at", rocket, nola);
		Conjunction goal = new Conjunction(boxAtLondon,rocketAtNola);
		return new Problem("Cargo", cargoDomain, cargoObjects, initialState, goal);
	}
	
//	private static final State = null;
	Expression startPreconditions = null;
	Conjunction initialConditions = null;
	
	@Test
	public void test1(){
		Problem cargoProblem = createEasyCargoProblem();
		PlanGraph graph = new PlanGraph(cargoProblem);

		assertNotNull(graph);
		assertEquals(1,graph.getAllSteps().size());
		assertEquals(0,graph.getCurrentSteps());
	}
	
	@Test
	public void test2(){
		Benchmark benchmark = new Benchmark(DOMAIN, PROBLEM);
		Problem p = null;
		try{
			p = benchmark.getProblem();
		}catch(IOException ex){
			System.out.println(ex.getMessage());
		}
		PlanGraph graph = p == null ? null : new PlanGraph(p);
		
		assertNotNull(graph);
		assertEquals(1,graph.getAllSteps().size());
		assertEquals(0,graph.getCurrentSteps());
	}
	
	private State getState(Literal...literals){
		MutableState initialState = new MutableState();
		for(Literal e : literals){
			initialState.impose(e);
		}
		return initialState.clone();
	}

}
