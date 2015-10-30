package edu.uno.ai.planning.gp;

import java.util.Stack;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.PlanGraph;

public class DepthFirstSearch extends Search {

	public final PlanGraph graph;
	private final Stack<SubgraphNode> stack = new Stack<>();
	private final SubgraphRoot root;
	
	public DepthFirstSearch(PlanGraph graph) {
		super(graph.problem);
		this.graph = graph;
		this.root = new SubgraphRoot(graph);
		stack.push(root);
	}

	@Override
	public int countVisited() {
		return root.descendants;
	}

	@Override
	public int countExpanded() {
		return root.descendants;
	}

	@Override
	public void setNodeLimit(int limit) {
		root.setNodeLimit(limit);
	}

	@Override
	public Plan findNextSolution() {
		while(!stack.isEmpty()) {
			SubgraphNode node = stack.peek();
			SubgraphNode child = node.expand();
			if(child == null)
				stack.pop();
			else
				stack.push(child);
			if(node.level == 0 && problem.isSolution(node.plan))
				return node.plan;
		}
		return null;
	}
}
