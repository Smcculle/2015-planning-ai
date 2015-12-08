package edu.uno.ai.planning.shsp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.h.StateHeuristic;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ImmutableArray;

public abstract class HSPHeuristic extends StateHeuristic {

	public static final int INFINITY = Integer.MAX_VALUE;
	private final ImmutableArray<Literal> literals;
	private final HashMap<Literal, Integer> cost = new HashMap<>();
	
	public HSPHeuristic(StateSpaceProblem problem) {
		super(problem);
		HashSet<Literal> literals = new HashSet<>();
		for(Step step : problem.steps) {
			everyLiteral(step.precondition, literal -> literals.add(literal));
			everyLiteral(step.effect, literal -> literals.add(literal));
		}
		this.literals = new ImmutableArray<>(literals.toArray(new Literal[literals.size()]));
	}
	
	private static final void everyLiteral(Expression expression, Consumer<Literal> consumer) {
		if(expression instanceof Literal)
			consumer.accept((Literal) expression);
		else if(expression instanceof Conjunction)
			for(Expression argument : ((Conjunction) expression).arguments)
				everyLiteral(argument, consumer);
		else
			throw new UnsupportedOperationException(expression.getClass() + " not supported.");
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
		for(Literal literal : literals) {
			if(current.isTrue(literal))
				cost.put(literal, 0);
		}
		LinkedList<Step> steps = new LinkedList<>();
		for(Step step : problem.steps)
			steps.add(step);
		boolean again = true;
		while(again && cost(problem.goal) == INFINITY) {
			again = false;
			Iterator<Step> iterator = steps.iterator();
			while(iterator.hasNext()) {
				Step step = iterator.next();
				int cost = cost(step.precondition);
				if(cost != INFINITY) {
					again = setCost(step.effect, cost + 1) || again;
					iterator.remove();
				}
			}
		}
		return cost(problem.goal);
	}
	
	private final boolean setCost(Expression expression, int value) {
		if(expression instanceof Literal) {
			Literal literal = (Literal) expression;
			int current = cost(literal);
			if(value < current) {
				cost.put(literal, value);
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
