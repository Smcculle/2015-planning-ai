package edu.uno.ai.planning.pg;

import lpsolve.LpSolveException;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.gp.LPDepthFirstSearch;

public class LPGraphPlan extends LPPlanGraphPlanner {

	public LPGraphPlan() {
		super("LPGP");
	}

	@Override
	protected Search makeSearch(LPPlanGraph graph) {
		try{
			return new LPDepthFirstSearch(graph);
		}catch(LpSolveException ex){
			String error = "Error running LPGraphPlan makeSearch:\r\n"+ex.getMessage();
			throw new RuntimeException(error);
		}
	}

}
