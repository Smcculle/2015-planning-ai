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
		
	private ImmutableArray<Operator> steps; //The set of possible steps in the problem
	
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
	private PartialOrderNode(Step[] stepsAvailable, DAG currentOrdering, CausalLink[] currentLinks, 
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
		this.steps = baseProblem.domain.operators;
		this.orderings = new DAG(); //This will be the null plan aka initial state and goal
		this.causalLinks = new ImmutableArray<CausalLink>(); //make a new set of links which is empty for now
		this.binds = new ListBindings();
		this.threats = new ImmutableArray<Threats>();
		this.agenda = new ImmutableArray(new Expression[0]);
		
	}
	
}