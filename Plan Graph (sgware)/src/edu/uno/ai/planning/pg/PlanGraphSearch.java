package edu.uno.ai.planning.pg;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;

public class PlanGraphSearch extends Search {

	private final PlanGraphPlanner planner;
	private final PlanGraph graph;
	private Search search = null;
	private int limit = Planner.NO_NODE_LIMIT;
	private int visited = 0;
	private int expanded = 0;
	
	public PlanGraphSearch(PlanGraphPlanner planner, Problem problem) {
		super(problem);
		this.planner = planner;
		this.graph = new PlanGraph(problem, true);
		this.graph.initialize(problem.initial);
	}

	@Override
	public int countVisited() {
		if(search == null)
			return visited;
		else
			return visited + search.countVisited();
	}

	@Override
	public int countExpanded() {
		if(search == null)
			return expanded;
		else
			return expanded + search.countExpanded();
	}

	@Override
	public void setNodeLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public Plan findNextSolution() {
		while(!graph.goalAchieved() && !graph.hasLeveledOff())
			graph.extend();
		while(true) {
			if(search == null) {
				search = planner.makeSearch(graph);
				search.setNodeLimit(limit);
			}
			Plan plan = search.findNextSolution();
			if(plan == null) {
				visited += search.countVisited();
				expanded += search.countExpanded();
				if(limit != Planner.NO_NODE_LIMIT)
					limit -= search.countVisited();
				search = null;
				graph.extend();
			}
			else
				return plan;
		}
	}
}
