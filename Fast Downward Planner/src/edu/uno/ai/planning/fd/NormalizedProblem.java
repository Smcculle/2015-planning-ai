package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.fd.Utilities;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.HashSubstitution;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class NormalizedProblem extends Problem{
	public ImmutableArray<Step> steps; //Every possible step that could be taken in a solution to this problem 

	public NormalizedProblem(Problem problem){
		super(problem.name, problem.domain, problem.objects, problem.initial, problem.goal);
		this.steps = collectSteps(problem);
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
		if(paramIndex == operator.parameters.length){
			Step step = operator.makeStep(substitution);
			boolean invalid = false;
			for(Expression e : ((Conjunction)step.effect).arguments){
				Atom a = Utilities.stripTypesFromAtom(e);
				ArrayList<Term> duplicateCheck = new ArrayList<Term>();
				for(Term t : a.terms){
					if(duplicateCheck.contains(t))
						invalid = true;
					else
						duplicateCheck.add(t);
				}
			}
			if(!invalid)
				steps.add(operator.makeStep(substitution));
		}
		else {
			Variable parameter = operator.parameters.get(paramIndex);
			for(Constant object : problem.objects) {
				substitution.set(parameter, object);
				collectSteps(problem, operator, substitution, paramIndex + 1, steps);
			}
		}
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


}
