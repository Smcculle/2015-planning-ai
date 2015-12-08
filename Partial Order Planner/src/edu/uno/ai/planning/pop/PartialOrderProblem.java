package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Problem;

public class PartialOrderProblem extends Problem{
	
	/**
	 * Constructs a new Partial Order problem from a general planning problem.
	 * 
	 * @param problem the planning problem
	 */
	public PartialOrderProblem(Problem problem) {
		super(problem.name, problem.domain, problem.objects, problem.initial, problem.goal);
	}
}
