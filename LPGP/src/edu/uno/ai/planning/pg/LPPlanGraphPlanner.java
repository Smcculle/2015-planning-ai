package edu.uno.ai.planning.pg;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;

public abstract class LPPlanGraphPlanner extends Planner<LPPlanGraphSearch>  {

	public LPPlanGraphPlanner(String name){
		super(name);
	}
	
	@Override
	protected LPPlanGraphSearch makeSearch(Problem problem) {
		return new LPPlanGraphSearch(this, problem);
	}

	protected abstract Search makeSearch(LPPlanGraph graph);
}
