package edu.uno.ai.planning.iw;

import java.util.LinkedList;
import java.util.Queue;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.ss.StateSpaceNode;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.StateSpaceSearch;

public class IteratedWidthSearch extends StateSpaceSearch {

	public final int maxWidth;
	private final Queue<StateSpaceNode> queue = new LinkedList<>();
	
	public IteratedWidthSearch(StateSpaceProblem problem, int maxWidth) {
		super(problem);
		this.maxWidth = maxWidth;
		queue.add(root);
	}

	@Override
	public Plan findNextSolution() {
		while(!queue.isEmpty()) {
			StateSpaceNode node = queue.poll();
			node.expand();
			for(StateSpaceNode child : node.children)
				if(!prune(child))
					queue.add(child);
			if(problem.goal.isTrue(node.state))
				return node.plan;
		}
		return null;
	}
	
	private final boolean prune(StateSpaceNode node) {
		for(int i=1; i<=maxWidth; i++)
			if(Novelty.hasNovelty(problem, node, i))
				return false;
		return true;
	}
}