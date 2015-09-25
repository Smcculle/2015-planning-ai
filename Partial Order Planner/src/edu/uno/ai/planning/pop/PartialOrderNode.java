package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.ss.TotalOrderPlan;


public class PartialOrderNode{
	

	/** The plan taken to reach this state */
	public final TotalOrderPlan plan;
	
	/** The current state */
	public final State state;
	
	/** This node's parent node (i.e. the state before the last step) */
	public final PartialOrderNode parent;
	
	
	/**
	 * Constructs a new node with a given parent and most recent step.
	 * 
	 * @param parent the previous state
	 * @param step the step to take in the previous state
	 */
	private PartialOrderNode(PartialOrderNode parent, Step step) {
		this.plan = parent.plan.addStep(step);
		this.state = parent.state.apply(step);
		this.parent = parent;
	}
	
	/**
	 * Constructs a new root node with the given initial state.
	 * 
	 * @param initial the problem's initial state
	 */
	PartialOrderNode(State initial) {
		this.plan = new TotalOrderPlan();
		this.state = initial;
		this.parent = null;
	}
	
}