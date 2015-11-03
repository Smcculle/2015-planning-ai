package edu.uno.ai.planning.hsp;

import java.util.HashMap;

import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.h.StateHeuristic;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public abstract class HSPHeuristic extends StateHeuristic {

	public static final int INFINITY = Integer.MAX_VALUE;
	private final HashMap<Literal, Integer> cost = new HashMap<>();
	
	public HSPHeuristic(StateSpaceProblem problem) {
		super(problem);
	}
	
	protected final int cost(Literal literal) {
		Integer i = cost.get(literal);
		if(i == null)
			return INFINITY;
		else
			return i;
	}
	
	protected abstract int cost(Expression expression);
	
	@Override
	public double evaluate(State current) {
		cost.clear();
		for(Step step : problem.steps)
			if(step.precondition.isTrue(current))
				setCost(step.precondition, 0);
		boolean again = true;
		while(again && cost(problem.goal) == INFINITY) {
			again = false;
			for(Step step : problem.steps) {
				int cost = cost(step.precondition);
				if(cost != INFINITY)
					again = setCost(step.effect, cost + 1) || again;
			}
		}
		return cost(problem.goal);
	}
	
	private final boolean setCost(Expression expression, int value) {
		if(expression instanceof Literal) {
			Literal literal = (Literal) expression;
			int current = cost(literal);
			if(value < current) {
				cost.put((Literal) expression, value);
				return true;
			}
			else
				return false;
		}
		else if(expression instanceof Conjunction) {
			boolean result = false;
			for(Expression argument : ((Conjunction) expression).arguments)
				result = setCost(argument, value) || result;
			return result;
		}
		else
			throw new UnsupportedOperationException(expression.getClass() + " not supported.");
	}
}
