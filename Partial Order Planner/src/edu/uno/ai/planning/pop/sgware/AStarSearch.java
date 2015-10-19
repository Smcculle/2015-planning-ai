package edu.uno.ai.planning.pop.sgware;

import java.util.Comparator;
import java.util.PriorityQueue;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.ss.TotalOrderPlan;

public class AStarSearch extends Search {

	private static final Comparator<PlanSpaceNode> A_STAR_COMPARATOR = new Comparator<PlanSpaceNode>() {

		@Override
		public int compare(PlanSpaceNode n1, PlanSpaceNode n2) {
			// Work done = number of steps; Work to be done = number of flaws.
			int comparison = (n1.steps.length - 2 + n1.flaws.size()) - (n2.steps.length - 2 + n2.flaws.size());
			// Break ties greedily.
			if(comparison == 0)
				comparison = n1.flaws.size() - n2.flaws.size();
			// Break further ties by preferring older nodes to newer nodes.
			if(comparison == 0)
				comparison = n1.id - n2.id;
			return comparison;
		}
	};
	
	private final PriorityQueue<PlanSpaceNode> queue = new PriorityQueue<>(A_STAR_COMPARATOR);
	private final PlanSpaceRoot root;
	
	public AStarSearch(Problem problem) {
		super(problem);
		root = new PlanSpaceRoot(problem);
		queue.add(root);
	}

	@Override
	public int countVisited() {
		return root.visited;
	}

	@Override
	public int countExpanded() {
		return root.expanded;
	}

	@Override
	public void setNodeLimit(int limit) {
		root.setNodeLimit(limit);
	}

	@Override
	public Plan findNextSolution() {
		while(!queue.isEmpty()) {
			PlanSpaceNode node = queue.poll();
			// A node with no flaws is a solution.
			if(node.flaws.size() == 0)
				return makeSolution(node);
			// If flaws exist, expand the children of this node.
			else
				node.expand(queue);
		}
		return null;
	}
	
	private static final Plan makeSolution(PlanSpaceNode node) {
		TotalOrderPlan plan = new TotalOrderPlan();
		for(Step step : node.orderings)
			if(!step.isStart() && !step.isEnd())
				plan = plan.addStep(step.makeStep(node.bindings));
		return plan;
	}
}
