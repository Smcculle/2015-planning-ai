package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
//import edu.uno.ai.planning.Search;

//public class LPGraphPlan extends LPPlanGraphPlanner {
public class LPGraphPlan extends Planner<GraphPlanSearch> {

	public LPGraphPlan() {
		super("LPGP");
	}

	@Override
//	protected Search makeSearch(LPPlanGraph graph) {
	protected GraphPlanSearch makeSearch(Problem problem){
		return new GraphPlanSearch(problem);
	}

}
