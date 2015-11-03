package edu.uno.ai.planning.h;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.ss.StateSpaceNode;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class HillClimbingSearch extends HeuristicSearch {

	private final HeuristicComparator comparator;
	private HeuristicNode best;
	
	public HillClimbingSearch(StateSpaceProblem problem, StateHeuristic heuristic, HeuristicComparator comparator) {
		super(problem, heuristic, comparator);
		this.comparator = comparator;
		best = queue.peek();
	}

	@Override
	public Plan findNextSolution() {
		while(queue.size() > 0) {
			StateSpaceNode current = queue.pop();
			current.expand();
			for(StateSpaceNode child : current.children) {
				HeuristicNode hnode = queue.push(child);
				if(comparator.compare(hnode, best) < 0) {
					queue.clear();
					queue.push(hnode.state);
				}
			}
			if(problem.goal.isTrue(current.state))
				return current.plan;
		}
		return null;
	}
}
