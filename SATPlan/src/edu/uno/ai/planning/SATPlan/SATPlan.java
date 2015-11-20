package edu.uno.ai.planning.SATPlan;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

public class SATPlan extends Planner{

	protected ISATSolver satSolver;

	/**
	 * Constructs a new partial order planner with a given name.
	 * 
	 * @param name the name of the planner
	 */
	public SATPlan(String name, ISATSolver satSolver) {
		super(name);
		this.satSolver = satSolver;
	}

	@Override
	protected final SATModelSearch makeSearch(Problem problem) {
		return new SATModelSearch(problem, satSolver);
	}

	public static SATPlan withWalkSAT() {
		return new SATPlan("SATPLAN_WALKSAT", new WalkSAT(10000, 10000, 0.5));
	}

	public static SATPlan withDavisPutnamSAT() {
		return new SATPlan("SATPLAN_DAVIS_PUTNAM", new SATSolver());
	}
}
