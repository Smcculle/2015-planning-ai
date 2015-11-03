package edu.uno.ai.planning.h;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.State;

public interface HeuristicComparator {

	public double compare(Plan p1, State s1, double h1, Plan p2, State s2, double h2);
}
