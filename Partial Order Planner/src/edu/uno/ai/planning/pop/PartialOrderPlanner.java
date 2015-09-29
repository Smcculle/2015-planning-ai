package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

/**
 * 
 * A partial order planner 
 */
public class PartialOrderPlanner extends Planner<PartialOrderSearch> {

	/**
	 * Constructs a new partial order planner with a given name.
	 * 
	 * @param name the name of the planner
	 */
	public PartialOrderPlanner(String name) {
		super(name);
	}

	@Override
	protected final PartialOrderSearch makeSearch(Problem problem) {
		return makePartialOrderSearch(new PartialOrderProblem(problem));
	}
	
	/**
	 * Given some problem to be solved, this method constructs the
	 * appropriate kind of {@link PartialOrderSearch} to solve it based on this
	 * planner.
	 * 
	 * @param problem the state space problem to be solved
	 * @return a state space search object for solving this problem
	 */
	protected PartialOrderSearch makePartialOrderSearch(PartialOrderProblem problem){
		return new PartialOrderSearch(problem);
	}
}
