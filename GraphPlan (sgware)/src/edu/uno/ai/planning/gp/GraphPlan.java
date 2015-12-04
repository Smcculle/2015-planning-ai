package edu.uno.ai.planning.gp;

import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.pg.PlanGraphPlanner;

public class GraphPlan extends PlanGraphPlanner {

	public GraphPlan() {
		super("SGP");
	}

	@Override
	protected Search makeSearch(PlanGraph graph) {
		System.out.println(graph.size());
		return new DepthFirstSearch(graph);
	}
}
