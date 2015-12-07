package edu.uno.ai.planning.h;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.ss.StateSpaceNode;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class CompleteSearch extends HeuristicSearch {

	public CompleteSearch(StateSpaceProblem problem, StateHeuristic heuristic, HeuristicComparator comparator) {
		super(problem, heuristic, comparator);
	}

	@Override
	public Plan findNextSolution() {
		while(queue.size() > 0) {
			StateSpaceNode current = queue.pop();
			current.expand();
			for(StateSpaceNode child : current.children)
				queue.push(child);
			if(problem.goal.isTrue(current.state))
				return current.plan;
		}
		return null;
	}
}
