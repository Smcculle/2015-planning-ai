package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

public class POPUnLOC extends Planner<AStarSearch> {

	public POPUnLOC() {
		super("POPUnLOC");
	}

	@Override
	protected AStarSearch makeSearch(Problem problem) {
		return new AStarSearch(problem);
	}
}
