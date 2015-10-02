package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.ListBindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import edu.uno.ai.planning.util.*;


public class PartialOrderNode{
		
	public ImmutableList<Step> steps; //The set of steps already in the plan
	
	public DAG orderings; //The orderings of the steps currently in the plan
	
	public ImmutableList<CausalLink> causalLinks; //Links between steps that need to be preserved
	
	public ListBindings binds; //The binds for the variables in this problem
	
	public ImmutableArray<Threats> threats; //The threats to the causal links at this time. 
	
	public ImmutableArray<Literal> agenda; //The open preconditions left to satisfy
	
	
	/** This node's parent node (i.e. the state before the last step) */
	public final PartialOrderNode parent;
	
	
	/**
	 * Constructs a new node with a given all the relevant info.
	 * This assumes that all the changes that needed to be made to the structures that this node depends on have already been
	 * made. i.e. if a step needed to be added to the list of steps for this node, it was added and then passed to this
	 * constructor in stepsPlanned
	 * 
	 * @param stepsPlanned this is the steps that are planned for this node, already has the new step added before creation
	 * @param binds The set of bindings which apply to the node, ?already filled with the new bindings needed?
	 */
	private PartialOrderNode(ImmutableList<Step> stepsPlanned, DAG currentOrdering, ImmutableList<CausalLink> currentLinks, 
			ListBindings binds, Threat[] threats, Literal[] agenda, PartialOrderNode parent) {
		this.steps = stepsPlanned;
		this.binds = binds;
		this.agenda = new ImmutableArray<Literal>(agenda);
		this.parent = parent;
		this.orderings = currentOrdering;
		this.causalLinks = currentLinks;
		this.threats = new ImmutableArray<Threats>(threats);
		
	}
	
	/**
	 * Constructs a new root node with the given initial state.
	 * 
	 * @param initial the problem's initial state
	 */
	PartialOrderNode(Problem baseProblem) {
		this.steps = new ImmutableList<Step>();
		
		//The dummy start step which has no preconditions but effects which are the initial state of the world
		Step start = new Step("Start", null, baseProblem.initial.toExpression());
		this.steps = this.steps.add(start);
		
		//Dummy end step which has no effects but preconditions which are the goal of the problem
		Step end = new Step("End", baseProblem.goal, null);
		this.steps = this.steps.add(end);

		
		this.binds = new ListBindings(); //an empty set of bindings
		
		//If the goal only has 1 literal to satisfy put it into the agenda
		if(baseProblem.goal instanceof Literal){
			this.agenda = new ImmutableArray(new Literal[]{(Literal) baseProblem.goal});
		}
		//otherwise we need to go through each conjunct in the goal and add each of those literals to the agenda
		else{
			Literal[] temp;
			Conjunction goal = (Conjunction) baseProblem.goal;
			for(int i=0; i<goal.arguments.length; i++){
				temp[i] = (Literal) goal.arguments.get(i);
				
			}
			this.agenda = new ImmutableArray<Literal>(temp);
		}
		
		this.parent = null;//this is the first node so it has no parent...
		
		this.orderings = new DAG(); //This will be the null plan aka initial state and goal
		
		this.causalLinks = new ImmutableList<CausalLink>(); //make a new set of links which is empty for now
		
		this.threats = new ImmutableArray(Threats[0]);// this is the set of threats which is empty to start
		
	}
	
}