package edu.uno.ai.planning.h;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.StateSpaceSearch;

public abstract class HeuristicSearch extends StateSpaceSearch {
	
	public static final HeuristicComparator A_STAR = new HeuristicComparator() {
		@Override
		public double compare(Plan p1, double h1, Plan p2, double h2) {
			double comparison = (p1.size() + h1) - (p2.size() + h2);
			if(comparison == 0)
				return GREEDY.compare(p1, h1, p2, h2);
			else
				return comparison;
		}
	};
	
	public static final HeuristicComparator GREEDY = new HeuristicComparator() {
		@Override
		public double compare(Plan p1, double h1, Plan p2, double h2) {
			return h1 - h2;
		}
	};
	
	protected final HeuristicQueue queue;
	
	public HeuristicSearch(StateSpaceProblem problem, StateHeuristic heuristic, HeuristicComparator comparator) {
		super(problem);
		this.queue = new HeuristicQueue(heuristic, comparator);
		this.queue.push(root);
	}
}
