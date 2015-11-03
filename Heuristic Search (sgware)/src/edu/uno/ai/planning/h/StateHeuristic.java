package edu.uno.ai.planning.h;

import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Heuristic;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public abstract class StateHeuristic implements Heuristic<State> {

	public final StateSpaceProblem problem;
	
	public StateHeuristic(StateSpaceProblem problem) {
		this.problem = problem;
	}
	
	@Override
	public abstract double evaluate(State current);
}
