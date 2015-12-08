package edu.uno.ai.planning.lpg;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

/**
 * 
 *
 * @author Shane McCulley
 */
public class LPGPlanner extends Planner<LPGSearch> {

	/**
	 * Constructs a new LPG planner with the given name.
	 * 
	 * @param name the name of the planner
	 */
	public LPGPlanner() {
		super("LPG");
	}

	@Override
	protected final LPGSearch makeSearch(Problem problem) {
		return new LPGSearch(problem);
	}
	
}