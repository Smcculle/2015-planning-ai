package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;


public class Graphplan extends Planner<GraphPlanSearch>{


	public Graphplan() {
			super("GP");
	}
	
	@Override
	protected final GraphPlanSearch makeSearch(Problem problem){
		return new GraphPlanSearch(problem);
	}
	
}