package edu.uno.ai.planning.gp;

import java.util.Stack;

import lpsolve.LpSolveException;
import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.LPPlanGraph;

public class LPDepthFirstSearch extends Search {

	public final LPPlanGraph graph;
	private final Stack<SubgraphNode> stack = new Stack<>();
	private final LPSubgraphRoot root;
	
	public LPDepthFirstSearch(LPPlanGraph graph) throws LpSolveException {
		super(graph.problem);
		this.graph = graph;
		this.root = new LPSubgraphRoot(graph);
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
