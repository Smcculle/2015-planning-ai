package edu.uno.ai.planning.blackbox;

import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.pg.PlanGraphPlanner;
import edu.uno.ai.planning.pg.StepNode;

public class BlackboxPlan extends PlanGraphPlanner {

	public BlackboxPlan() {
		super("Blackbox");
	}

	@Override
	protected Search makeSearch(PlanGraph graph) {
		return new BlackboxSearch(graph);
	}
}