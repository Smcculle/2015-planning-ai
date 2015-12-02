package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Search;

public class LPGraphPlanSearchAlgorithm extends Search {

		
	public LPGraphPlanSearchAlgorithm(LPPlanGraph graph){
		super(graph.getProblem());
//			this.graph = graph;
//			this.root = new LPSubgraphRoot(graph);
//			stack.push(root);
	}

		@Override
		public int countVisited() {
//			return root.descendants;
			return -1;
		}
//
		@Override
		public int countExpanded() {
//			return root.descendants;
			return -1;
		}
//
		@Override
		public void setNodeLimit(int limit) {
//			root.setNodeLimit(limit);
		}

	@Override
	public Plan findNextSolution() {
//			while(!stack.isEmpty()) {
//				SubgraphNode node = stack.peek();
//				SubgraphNode child = node.expand();
//				if(child == null)
//					stack.pop();
//				else
//					stack.push(child);
//				if(node.level == 0 && problem.isSolution(node.plan))
//					return node.plan;
//			}
		return null;
	}

}
