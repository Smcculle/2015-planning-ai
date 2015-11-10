package edu.uno.ai.planning.ss;

import java.util.ArrayList;

import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;

/**
 * Represents a node in a state space search graph.  A node is considered
 * expanded once it is created.  A node is considered visited once its
 * {@link #expand()} method has been called, at which point it expands all of
 * its possible successor nodes (i.e. all possible next states).
 * 
 * @author Stephen G. Ware
 */
public class StateSpaceNode {

	/** The plan taken to reach this state */
	public final TotalOrderPlan plan;
	
	/** The current state */
	public final State state;
	
	/** This node's parent node (i.e. the state before the last step) */
	public final StateSpaceNode parent;
	
	/** 
	 * This node's children (i.e. each possible next state).
	 * Note that this set is empty until {@link #expand()} has been called.
	 */
	public final Iterable<StateSpaceNode> children = new ArrayList<>();
	
	/** The total number of visited nodes in this node's subtree (including this node) */
	private int visited = 0;
	
	/** The total number of expanded nodes in this node's subtree */
	private int expanded = 0;
	
	/**
	 * Constructs a new node with a given parent and most recent step.
	 * 
	 * @param parent the previous state
	 * @param step the step to take in the previous state
	 */
	private StateSpaceNode(StateSpaceNode parent, Step step) {
		this.plan = parent.plan.addStep(step);
		this.state = parent.state.apply(step);
		this.parent = parent;
	}
	
	/**
	 * Constructs a new root node with the given initial state.
	 * 
	 * @param initial the problem's initial state
	 */
	StateSpaceNode(State initial) {
		this.plan = new TotalOrderPlan();
		this.state = initial;
		this.parent = null;
	}
	
	/**
	 * Returns the total number of visited nodes in this node's subtree
	 * (including this node).
	 * 
	 * @return the number of visted nodes
	 */
	public final int countVisited() {
		return visited;
	}
	
	/**
	 * Returns the total number of expanded nodes in this node's subtree.
	 * 
	 * @return the number of expanded nodes
	 */
	public final int countExpanded() {
		return expanded;
	}
	
	/**
	 * Returns the root node of the search space.
	 * 
	 * @return the root
	 */
	public final StateSpaceNode getRoot() {
		StateSpaceNode current = this;
		while(current.parent != null)
			current = current.parent;
		return current;
	}
	
	/**
	 * Marks this node as visited and expands all of its children (i.e. all
	 * possible next states).
	 */
	public void expand() {
		StateSpaceRoot root = (StateSpaceRoot) getRoot();
		ArrayList<StateSpaceNode> children = (ArrayList<StateSpaceNode>) this.children;
		if(root.limit == root.countVisited())
			throw new SearchLimitReachedException();
		for(Step step : root.search.problem.steps)
			if(step.precondition.isTrue(state))
				children.add(new StateSpaceNode(this, step));
		StateSpaceNode ancestor = this;
		while(ancestor != null) {
			ancestor.visited++;
			ancestor.expanded += children.size();
			ancestor = ancestor.parent;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateSpaceNode other = (StateSpaceNode) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}


	
}
