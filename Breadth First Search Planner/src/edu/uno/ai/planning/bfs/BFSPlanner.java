package edu.uno.ai.planning.bfs;

import edu.uno.ai.planning.ss.StateSpacePlanner;
import edu.uno.ai.planning.ss.StateSpaceProblem;

/**
 * A simple breadth-first search planner.
 * 
 * @author Stephen G. Ware
 */
public class BFSPlanner extends StateSpacePlanner {

	/**
	 * Creates a new BFS planner.
	 */
	public BFSPlanner() {
		super("BFS");
	}

	@Override
	protected BreadthFirstSearch makeStateSpaceSearch(StateSpaceProblem problem) {
		return new BreadthFirstSearch(problem);
	}
}
