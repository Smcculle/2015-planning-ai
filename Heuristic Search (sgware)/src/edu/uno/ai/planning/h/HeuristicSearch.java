package edu.uno.ai.planning.h;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.StateSpaceSearch;

public class HeuristicSearch extends StateSpaceSearch {
	
	public final Heuristic heuristic;
	
	public HeuristicSearch(StateSpaceProblem problem, Heuristic heuristic) {
		super(problem);
		this.heuristic = heuristic;
	}

	@Override
	public Plan findNextSolution() {
		return null;
	}
}
