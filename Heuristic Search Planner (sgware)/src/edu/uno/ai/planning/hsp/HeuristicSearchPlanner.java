package edu.uno.ai.planning.hsp;

import edu.uno.ai.planning.h.CompleteSearch;
import edu.uno.ai.planning.h.StateHeuristic;
import edu.uno.ai.planning.h.HeuristicPlanner;
import edu.uno.ai.planning.h.HeuristicSearch;
import edu.uno.ai.planning.h.HillClimbingSearch;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class HeuristicSearchPlanner extends HeuristicPlanner {
	
	public HeuristicSearchPlanner() {
		super("SHSP");
	}

	@Override
	protected HeuristicSearch makeStateSpaceSearch(StateSpaceProblem problem) {
		StateHeuristic heuristic = new AdditiveHeuristic(problem);
		return new CompleteSearch(problem, heuristic, HeuristicSearch.A_STAR);
	}
}
