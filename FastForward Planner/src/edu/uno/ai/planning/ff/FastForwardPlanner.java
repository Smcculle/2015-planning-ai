package edu.uno.ai.planning.ff;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class FastForwardPlanner extends Planner<FastForwardSearch> {

	public FastForwardPlanner() {
		super("FF");
	}

	@Override
	protected FastForwardSearch makeSearch(Problem problem) {
		return new FastForwardSearch(new StateSpaceProblem(problem));
	}

}
