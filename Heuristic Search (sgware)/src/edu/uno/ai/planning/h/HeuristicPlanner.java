package edu.uno.ai.planning.h;

import edu.uno.ai.planning.ss.StateSpacePlanner;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public abstract class HeuristicPlanner extends StateSpacePlanner {

	public HeuristicPlanner(String name) {
		super(name);
	}

	@Override
	protected abstract HeuristicSearch makeStateSpaceSearch(StateSpaceProblem problem);
}
