package edu.uno.ai.planning.bfs;

import java.util.LinkedList;
import java.util.Queue;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.ss.StateSpaceNode;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.StateSpaceSearch;

/**
 * Explores a problem's search space in a naive, breadth-first fashion.
 * 
 * @author Stephen G. Ware
 */
public class BreadthFirstSearch extends StateSpaceSearch {
	
	/** The queue in which the frontier is stored */
	protected final Queue<StateSpaceNode> queue = new LinkedList<>();
	
	/**
	 * Creates a new breadth first search process.
	 * 
	 * @param problem the problem to be explored
	 */
	public BreadthFirstSearch(StateSpaceProblem problem) {
		super(problem);
		queue.add(root);
	}

	@Override
	public Plan findNextSolution() {
		while(!queue.isEmpty()) {
			StateSpaceNode node = queue.poll();
			node.expand();
			if(problem.goal.isTrue(node.state))
				return node.plan;
			for(StateSpaceNode child : node.children)
				queue.add(child);
		}
		return null;
	}
}
