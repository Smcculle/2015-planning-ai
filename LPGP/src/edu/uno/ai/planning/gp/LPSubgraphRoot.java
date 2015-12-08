package edu.uno.ai.planning.gp;

import lpsolve.LpSolveException;
import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.pg.LPPlanGraph;

public class LPSubgraphRoot extends LPSubgraphNode {

	int limit = Planner.NO_NODE_LIMIT;
	
	LPSubgraphRoot(LPPlanGraph graph) throws LpSolveException {
		super(graph);
	}
	
	void setNodeLimit(int limit) {
		this.limit = limit;
	}

}
