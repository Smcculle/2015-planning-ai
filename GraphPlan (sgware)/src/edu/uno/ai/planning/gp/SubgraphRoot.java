package edu.uno.ai.planning.gp;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.pg.PlanGraph;

public class SubgraphRoot extends SubgraphNode {

	int limit = Planner.NO_NODE_LIMIT;
	
	SubgraphRoot(PlanGraph graph) {
		super(graph);
	}
	
	void setNodeLimit(int limit) {
		this.limit = limit;
	}
}
