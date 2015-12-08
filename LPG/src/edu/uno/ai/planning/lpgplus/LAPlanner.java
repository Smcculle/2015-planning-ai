package edu.uno.ai.planning.lpgplus;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

/**
 * 
 *
 * @author Shane McCulley
 */
public class LAPlanner extends Planner<LASearch> {

	/**
	 * Constructs a new LPG planner with the given name.
	 * 
	 * @param name the name of the planner
	 */
	public LAPlanner() {
		super("LPG");
	}

	@Override
	protected final LASearch makeSearch(Problem problem) {
		return new LASearch(problem);
	}
	
}