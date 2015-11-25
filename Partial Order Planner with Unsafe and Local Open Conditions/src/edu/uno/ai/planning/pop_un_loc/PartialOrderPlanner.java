package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

public class PartialOrderPlanner extends Planner<AStarSearch> {

	public PartialOrderPlanner() {
		super("SPOP");
	}

	@Override
	protected AStarSearch makeSearch(Problem problem) {
		return new AStarSearch(problem);
	}
}
