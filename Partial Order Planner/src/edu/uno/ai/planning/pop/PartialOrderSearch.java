package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pop.PartialOrderNode; 
import edu.uno.ai.planning.pop.PartialOrderProblem;
import edu.uno.ai.planning.pop.PartialOrderRoot;

/**
 * Represents a search space whose
 * {@link edu.uno.ai.planning.pop.PartialOrderNodes nodes} are plans
 * and whose edges are orderings?
 * 
 * @author
 */
public abstract class PartialOrderSearch extends Search {
	
	/** The Partial Order problem being solved */
	public final PartialOrderProblem problem;
	
	/** The root node of the search space (null plan?) */
	public final PartialOrderNode root;
	
	/** The search limit on visited nodes (-1 if no limit) */
	int limit = -1;
	
	/**
	 * Creates a partial order search for a given problem.
	 * 
	 * @param problem the problem whose  space will be searched
	 */
	public PartialOrderSearch(PartialOrderProblem problem) {
		super(problem);
		this.problem = problem;
		this.root = new PartialOrderRoot(this);
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
