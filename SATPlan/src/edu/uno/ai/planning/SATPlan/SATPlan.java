package edu.uno.ai.planning.SATPlan;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

public class SATPlan extends Planner{
	/**
	 * Constructs a new partial order planner with a given name.
	 * 
	 * @param name the name of the planner
	 */
	public SATPlan() {
		super("SATPLAN");
	}

	@Override
	protected final SATModelSearch makeSearch(Problem problem) {
		return new SATModelSearch(problem);
	}
}
