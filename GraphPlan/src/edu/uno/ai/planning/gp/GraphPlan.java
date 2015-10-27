package edu.uno.ai.planning.gp;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

public class GraphPlan extends Planner<DepthFirstSearch> {

	public GraphPlan() {
		super("SGP");
	}

	@Override
	protected DepthFirstSearch makeSearch(Problem problem) {
		return null;
	}
}
