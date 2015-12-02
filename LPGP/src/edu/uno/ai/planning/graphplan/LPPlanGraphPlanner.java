package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;

public abstract class LPPlanGraphPlanner extends Planner<LPGraphPlanSearch>  {

	public LPPlanGraphPlanner(String name){
		super(name);
	}
	
	@Override
	protected LPGraphPlanSearch makeSearch(Problem problem) {
		return new LPGraphPlanSearch(this, problem);
	}
	
	protected abstract Search makeSearch(LPPlanGraph graph);
}
