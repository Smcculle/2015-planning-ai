package edu.uno.ai.planning.SATPlan;

import java.util.List;

public interface ISATSolver {
	List<BooleanVariable> getModel(SATProblem problem);

	/**
	 * Return number of examined variables in the last run before the solution
	 * was found (or the algorithm ended).
	 * (Applies to WalkSAT)
	 * @return number of examined variables
	 */
	int countVisited();

	/**
	 * Return number of flipped variables in the last run before the solution
	 * was found (or the algorithm ended).
	 * (Applies to WalkSAT)
	 * @return number of flipped variables
	 */
	int countExpanded();
}
