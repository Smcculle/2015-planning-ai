package edu.uno.ai.planning.ss;

import java.util.ArrayList;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.HashSubstitution;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * A subclass of {@link edu.uno.ai.planning.Problem} that
 * propositionalizes all the {@link #steps} that could possibly occur in a
 * plan. 
 * 
 * @author Stephen G. Ware
 */
public class StateSpaceProblem extends Problem {

	/** Every possible step that could be taken in a solution to this problem */
	public final ImmutableArray<Step> steps;
	
	/**
	 * Constructs a new state space problem from a general planning problem.
	 * 
	 * @param problem the planning problem
	 */
	public StateSpaceProblem(Problem problem) {
		super(problem.name, problem.domain, problem.objects, problem.initial, problem.goal);
		this.steps = collectSteps(problem);
	}

	/**
	 * Returns an array of every possible ground step.
	 * 
	 * @param problem the problem whose steps will be created
	 * @return an array of every possible step
	 */
	private static final ImmutableArray<Step> collectSteps(Problem problem) {
		ArrayList<Step> steps = new ArrayList<>();
		for(Operator operator : problem.domain.operators)
			collectSteps(problem, operator, new HashSubstitution(), 0, steps);
		return new ImmutableArray<>(steps.toArray(new Step[steps.size()]));
	}
	
	/**
	 * A recursive helper method for {@link #collectSteps(Problem)} which
	 * creates all the steps that can be created for a given operator.
	 * 
	 * @param problem the problem whose steps will be created
	 * @param operator the operator whose steps will be created
	 * @param substitution maps the operator's parameters to constants
	 * @param paramIndex the index of the current operator parameter being considered
	 * @param steps a collection of ground steps
	 */
	private static final void collectSteps(Problem problem, Operator operator, HashSubstitution substitution, int paramIndex, ArrayList<Step> steps) {
		if(paramIndex == operator.parameters.length)
			steps.add(operator.makeStep(substitution));
		else {
			Variable parameter = operator.parameters.get(paramIndex);
			for(Constant object : problem.getObjectsByType(parameter.type)) {
				substitution.set(parameter, object);
				collectSteps(problem, operator, substitution, paramIndex + 1, steps);
			}
		}
	}
}
