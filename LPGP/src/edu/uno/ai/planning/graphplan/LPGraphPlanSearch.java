package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;

public class LPGraphPlanSearch extends GraphPlanSearch {

	private final LPPlanGraphPlanner planner;
	private final LPPlanGraph graph;
	private Search search = null;
	private int limit = Planner.NO_NODE_LIMIT;
	private int visited = 0;
	private int expanded = 0;
	
	public LPGraphPlanSearch(LPPlanGraphPlanner planner, Problem problem) {
		super(problem);
		this.planner = planner;
		this.graph = new LPPlanGraph(problem);
	}
	
//	public LPGraphPlanSearch(LPGraphPlan planner, LPPlanGraph graph){
//		super(graph.getProblem());
//		this.planner = planner;
//		this.graph = graph;
//	}

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
		System.out.println("Start Search");
		while(!graph.containsGoal(graph.getGoal()) && !graph.isLeveledOff())
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
			else{
				System.out.println("End Search");
				return plan;
			}
		}
	}
}
