package edu.uno.ai.planning.gp;

import java.util.Stack;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.PlanGraph;

// for memoization
import java.util.HashMap;
// second attempt for greater speed
import java.util.HashSet;

public class DepthFirstSearch extends Search {

	public final PlanGraph graph;
	private final Stack<SubgraphNode> stack = new Stack<>();
	private final SubgraphRoot root;
	
	// for memoization
	private HashMap<Integer, String> memos4 = new HashMap<Integer, String>();
	
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
			/** Check against the memos. */
			
			String m = node.toString2();
			int me = node.getMe();
			
			String s = memos4.get(me);
			if (s != null && s.equals(m)){
				//	System.out.println("FFOUND");
					stack.pop();
					if (stack.isEmpty()) return null;
					continue;				
			}
//			System.out.println("nah");
	//		System.out.println("Not found: " + m);

			SubgraphNode child = node.expand();
			if(child == null)
				stack.pop();
			else
				stack.push(child);
			if(node.level == 0 && problem.isSolution(node.plan))
				return node.plan;
			/** If the node isn't a solution, add it to the memos. */
			else if (node.level < node.plan.size() - 1){
			// idk what's happening
				memos4.put(me,  m);
	//			else System.out.println("Not adding.");
				
			}
		}
		return null;
	}
}