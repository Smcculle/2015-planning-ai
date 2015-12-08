package edu.uno.ai.planning.hsp;



/**
 * A Heuristic search planner that uses best-first search
 * 
 *@author Connor Montgomery
 */
public class HSPlanner extends StateSpacePlanner {

	/**
	 * Creates a new Heuristic Search planner.
	 */
	public HSPlanner() {
		super("HSP");
	}

	@Override
	protected HeuristicSearch makeStateSpaceSearch(StateSpaceProblem problem) {
		return new HeuristicSearch(problem);
	}
}
