package edu.uno.ai.planning.pg;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;

public abstract class PlanGraphPlanner extends Planner<PlanGraphSearch> {

	public PlanGraphPlanner(String name) {
		super(name);
	}
	
	@Override
	protected PlanGraphSearch makeSearch(Problem problem) {
		return new PlanGraphSearch(this, problem);
	}
	
	protected abstract Search makeSearch(PlanGraph graph);
}
