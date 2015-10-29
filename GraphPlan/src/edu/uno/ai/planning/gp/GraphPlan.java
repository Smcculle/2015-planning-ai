package edu.uno.ai.planning.gp;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

public class GraphPlan extends Planner<GraphPlanSearch> {

	public GraphPlan() {
		super("SGP");
	}

	@Override
	protected GraphPlanSearch makeSearch(Problem problem) {
		return new GraphPlanSearch(problem);
	}
}
