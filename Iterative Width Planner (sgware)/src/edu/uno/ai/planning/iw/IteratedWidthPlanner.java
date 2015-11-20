package edu.uno.ai.planning.iw;

import edu.uno.ai.planning.ss.StateSpacePlanner;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.StateSpaceSearch;

public class IteratedWidthPlanner extends StateSpacePlanner {
	
	public IteratedWidthPlanner() {
		super("IW2");
	}

	@Override
	protected StateSpaceSearch makeStateSpaceSearch(StateSpaceProblem problem) {
		return new IteratedWidthSearch(problem, 2);
	}
}
