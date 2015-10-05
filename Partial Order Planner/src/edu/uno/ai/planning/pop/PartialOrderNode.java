package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.ListBindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.util.*;


public class PartialOrderNode{
		
	public ImmutableList<PartialStep> steps; //The set of steps already in the plan
	
	public POPGraph orderings; //The orderings of the steps currently in the plan
	
	public ImmutableList<CausalLink> causalLinks; //Links between steps that need to be preserved
	
	public ListBindings binds; //The binds for the variables in this problem
	
	public ImmutableArray<Flaw> flaws;
	
	
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
	public PartialOrderNode(ImmutableList<PartialStep> stepsPlanned, POPGraph currentOrdering, ImmutableList<CausalLink> currentLinks, 
			ListBindings binds, Flaw[] flaws, PartialOrderNode parent) {
		this.steps = stepsPlanned;
		this.binds = binds;
		this.parent = parent;
		this.orderings = currentOrdering;
		this.causalLinks = currentLinks;
		this.flaws = new ImmutableArray<Flaw>(flaws);
		
	}
	
	/**
	 * Constructs a new root node with the given initial state.
	 * 
	 * @param initial the problem's initial state
	 */
	PartialOrderNode(Problem baseProblem) {
		this.steps = new ImmutableList<PartialStep>();
		
		//The dummy start step which has no preconditions but effects which are the initial state of the world
		PartialStep start = new PartialStep("Start", null, null, baseProblem.initial.toExpression());
		this.steps = this.steps.add(start);
		
		//Dummy end step which has no effects but preconditions which are the goal of the problem
		PartialStep end = new PartialStep("End", null, baseProblem.goal, null);
		this.steps = this.steps.add(end);

		
		this.binds = new ListBindings(); //an empty set of bindings
		
		//If the goal only has 1 literal to satisfy put it into the agenda
		if(baseProblem.goal instanceof Literal){
			Flaw flaw = new OpenCondition((Literal)baseProblem.goal,end);
			this.flaws = new ImmutableArray<Flaw>(new Flaw[]{flaw});
		}
		//otherwise we need to go through each conjunct in the goal and add each of those literals to the agenda
		else{
			Conjunction goal = (Conjunction) baseProblem.goal;
			Flaw[] temp = new Flaw[goal.arguments.length];
			for(int i=0; i<goal.arguments.length; i++){
				temp[i] = (Flaw) new OpenCondition((Literal) goal.arguments.get(i), end) ;
				
			}
			this.flaws = new ImmutableArray<Flaw>(temp);
		}
		
		this.parent = null;//this is the first node so it has no parent...
		
		this.orderings = new POPGraph(); //This will be the null plan aka initial state and goal
		
		this.causalLinks = new ImmutableList<CausalLink>(); //make a new set of links which is empty for now
		
		
	}
	
}