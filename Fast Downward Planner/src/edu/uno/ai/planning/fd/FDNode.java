package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.SearchLimitReachedException;

public class FDNode {

	/** The plan taken to reach this state */
	public final TotalOrderMPTPlan plan;
	
	/** The current state */
	public final MPTState state;
	
	/** This node's parent node (i.e. the state before the last step) */
	public final FDNode parent;
	
	/** 
	 * This node's children (i.e. each possible next state).
	 * Note that this set is empty until {@link #expand()} has been called.
	 */
	public final Iterable<FDNode> children = new ArrayList<>();
	
	/** The total number of visited nodes in this node's subtree (including this node) */
	private int visited = 0;
	
	/** The total number of expanded nodes in this node's subtree */
	private int expanded = 0;
	
	/** The heuristic value of this state */
	public double heuristic;

	/**
	 * Constructs a new node with a given parent and most recent step.
	 * 
	 * @param parent the previous state
	 * @param step the step to take in the previous state
	 */
	private FDNode(FDNode parent, MPTStep step){
		this.plan = parent.plan.addStep(step);
		this.state = parent.state.apply(step);
		this.parent = parent;
	}

	/**
	 * Constructs a new root node with the given initial state.
	 * 
	 * @param initial the problem's initial state
	 */
	FDNode(MPTState initial) {
		this.plan = new TotalOrderMPTPlan();
		this.state = initial;
		this.parent = null;
	}
	
	/**
	 * Returns the total number of visited nodes in this node's subtree
	 * (including this node).
	 * 
	 * @return the number of visited nodes
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
	public final FDNode getRoot() {
		FDNode current = this;
		while(current.parent != null)
			current = current.parent;
		return current;
	}
	
	/**
	 * Marks this node as visited and expands all of its children (i.e. all
	 * possible next states).
	 */
	public void expand(){
		FDRoot root = (FDRoot) getRoot();
		ArrayList<FDNode> children = (ArrayList<FDNode>) this.children;
		if(root.limit == root.countVisited())
			throw new SearchLimitReachedException();
		for(MPTStep step : root.search.problem.mptSteps){
			if(state.isTrue(step.precondition)){
				children.add(new FDNode(this, step));
			}
		}
		FDNode ancestor = this;
		while(ancestor != null) {
			ancestor.visited++;
			ancestor.expanded += children.size();
			ancestor = ancestor.parent;
		}
	}
}
