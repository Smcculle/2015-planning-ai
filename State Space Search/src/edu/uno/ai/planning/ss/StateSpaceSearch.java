package edu.uno.ai.planning.ss;

import edu.uno.ai.planning.Search;

/**
 * Represents a search space whose
 * {@link edu.uno.ai.planning.ss.StateSpaceNode nodes} are states
 * and whose edges are steps.
 * 
 * @author Stephen G. Ware
 */
public abstract class StateSpaceSearch extends Search {
	
	/** The state space problem being solved */
	public final StateSpaceProblem problem;
	
	/** The root node of the search space (i.e. a plan with 0 steps) */
	public final StateSpaceNode root;
	
	/** The search limit on visited nodes (-1 if no limit) */
	int limit = -1;
	
	/**
	 * Creates a state space search for a given problem.
	 * 
	 * @param problem the problem whose state space will be searched
	 */
	public StateSpaceSearch(StateSpaceProblem problem) {
		super(problem);
		this.problem = problem;
		this.root = new StateSpaceRoot(this);
	}

	@Override
	public int countVisited() {
		return root.countVisited();
	}

	@Override
	public int countExpanded() {
		return root.countExpanded();
	}

	@Override
	public void setNodeLimit(int limit) {
		((StateSpaceRoot) root).setNodeLimit(limit);
	}
}
