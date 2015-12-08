package edu.uno.ai.planning.jjsatplan;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

public class SATPlan extends Planner{

	/**
	 * Constructs a new partial order planner with a given name.
	 */
	public SATPlan() {
		super("DPLL Planner");
	}

	@Override
	protected final SATModelSearch makeSearch(Problem problem) {
		return new SATModelSearch(problem);
	}
}
