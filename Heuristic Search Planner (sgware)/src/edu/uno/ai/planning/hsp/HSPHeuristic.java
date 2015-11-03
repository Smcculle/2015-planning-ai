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
		while(cost(problem.goal) != INFINITY)
			for(Step step : problem.steps)
				setCost(step.effect, cost(step.precondition) + 1);
		return cost(problem.goal);
	}
	
	private final void setCost(Expression expression, int value) {
		if(expression instanceof Literal) {
			Literal literal = (Literal) expression;
			int current = cost.get(literal);
			cost.put((Literal) expression, Math.min(current, value));
		}
		else if(expression instanceof Conjunction)
			for(Expression argument : ((Conjunction) expression).arguments)
				setCost(argument, value);
		else
			throw new UnsupportedOperationException(expression.getClass() + " not supported.");
	}
}
