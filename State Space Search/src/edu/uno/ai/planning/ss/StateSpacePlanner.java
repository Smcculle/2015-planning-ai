package edu.uno.ai.planning.ss;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;

/**
 * A planner which searches the space of states for the solution to a given
 * problem.
 * 
 * @author Stephen G. Ware
 */
public abstract class StateSpacePlanner extends Planner<StateSpaceSearch> {

	/**
	 * Constructs a new state space planner with a given name.
	 * 
	 * @param name the name of the planner
	 */
	public StateSpacePlanner(String name) {
		super(name);
	}

	@Override
	protected final StateSpaceSearch makeSearch(Problem problem) {
		return makeStateSpaceSearch(new StateSpaceProblem(problem));
	}
	
	/**
	 * Given some state space problem to be solved, this method constructs the
	 * appropriate kind of {@link StateSpaceSearch} to solve it based on this
	 * planner.
	 * 
	 * @param problem the state space problem to be solved
	 * @return a state space search object for solving this problem
	 */
	protected abstract StateSpaceSearch makeStateSpaceSearch(StateSpaceProblem problem);
}
