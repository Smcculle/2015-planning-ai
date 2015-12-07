package edu.uno.ai.planning.shsp;

import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class AdditiveHeuristic extends HSPHeuristic {

	public AdditiveHeuristic(StateSpaceProblem problem) {
		super(problem);
	}

	@Override
	protected int cost(Expression expression) {
		if(expression instanceof Literal)
			return cost((Literal) expression);
		else if(expression instanceof Conjunction) {
			int total = 0;
			for(Expression argument : ((Conjunction) expression).arguments) {
				int argCost = cost(argument);
				if(argCost == INFINITY)
					return INFINITY;
				else
					total += argCost;
			}
			return total;
		}
		else
			throw new UnsupportedOperationException(expression.getClass() + " not supported.");
	}
}
