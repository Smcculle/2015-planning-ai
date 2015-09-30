package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.ListBindings;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import edu.uno.ai.planning.util.ImmutableArray;


public class PartialOrderNode{
		
	private ImmutableArray<Operator> steps; //The set of steps already in the plan
	
	private DAG orderings; //The orderings of the steps currently in the plan
	
	private ImmutableArray<CausalLink> causalLinks; //Links between steps that need to be preserved
	
	private ListBindings binds; //The binds for the variables in this problem
	
	private ImmutableArray<Threats> threats; //The threats to the causal links at this time. 
	
	private ImmutableArray<Expression> agenda; //The open preconditions left to satisfy
	
	
	/** This node's parent node (i.e. the state before the last step) */
	public final PartialOrderNode parent;
	
	
	/**
	 * Constructs a new node with a given all the relevant info.
	 * 
	 * @param parent the previous state
	 * @param step the step to take in the previous state
	 */
	private PartialOrderNode(Step[] stepsPlanned, DAG currentOrdering, CausalLink[] currentLinks, 
			ListBindings binds, Threat[] threats, Expression[] agenda, PartialOrderNode parent) {
		this.steps = stepsAvailable;
		this.orderings = currentOrdering;
		this.causalLinks = currentLinks;
		this.binds = binds;
		this.threats = threats;
		this.agenda = agenda;
		this.parent = parent;
	}
	
	/**
	 * Constructs a new root node with the given initial state.
	 * 
	 * @param initial the problem's initial state
	 */
	PartialOrderNode(Problem baseProblem) {
		//The dummy start step which has no preconditions but effects which are the initial state of the world
		Step start = new Step("Start", null, baseProblem.initial.toExpression());
		
		//Dummy end step which has no effects but preconditions which are the goal of the problem
		Step end = new Step("End", baseProblem.goal, null);
		
		this.steps = new ImmutableArray(new Step[]{start, end});
		this.orderings = new DAG(); //This will be the null plan aka initial state and goal
		this.causalLinks = new ImmutableArray(CausalLink[0]); //make a new set of links which is empty for now
		this.binds = new ListBindings();
		this.threats = new ImmutableArray(Threats[0]);
		this.agenda = new ImmutableArray(new Expression[]{});
		
	}
	
}