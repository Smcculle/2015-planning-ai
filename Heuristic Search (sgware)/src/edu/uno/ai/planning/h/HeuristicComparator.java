package edu.uno.ai.planning.h;

import edu.uno.ai.planning.Plan;

public interface HeuristicComparator {

	public default double compare(HeuristicNode n1, HeuristicNode n2) {
		return compare(n1.state.plan, n1.heuristic, n2.state.plan, n2.heuristic);
	}
	
	public double compare(Plan p1, double h1, Plan p2, double h2);
}
