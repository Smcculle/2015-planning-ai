package edu.uno.ai.planning.h;

import edu.uno.ai.planning.State;

public interface Heuristic {

	public double estimate(State current);
}
