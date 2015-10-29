package edu.uno.ai.planning.gp;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.PlanGraph;

public class GraphPlanSearch extends Search {

	private final PlanGraph graph;
	private DepthFirstSearch search = null;
	private int limit = Planner.NO_NODE_LIMIT;
	private int visited = 0;
	private int expanded = 0;
	
	public GraphPlanSearch(Problem problem) {
		super(problem);
		graph = new PlanGraph(problem);
		graph.initialize(problem.initial);
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
				search = new DepthFirstSearch(graph);
				if(limit != Planner.NO_NODE_LIMIT)
					search.setNodeLimit(limit);
			}
			if(limit == 0)
				return null;
			Plan plan = search.findNextSolution();
			if(plan == null) {
				visited += search.countVisited();
				expanded += search.countExpanded();
				limit -= search.countVisited();
				search = null;
				graph.extend();
			}
			else
				return plan;
		}
	}
}
