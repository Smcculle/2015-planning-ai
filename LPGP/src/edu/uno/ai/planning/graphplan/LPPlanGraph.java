package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.logic.Predication;

public class LPPlanGraph extends PlanGraph {
	
	private Expression goal;
	private Problem problem;
	
	/**
	 * Default value of PlanGraph must calculate mutual exclusions.
	 * 
	 * @param problem The Problem in which to setup PlanGraph
	 */
	public LPPlanGraph (Problem problem)
	{
		super(problem, true);
		this.goal = problem.goal;
		this.problem = problem;
	}
	
	/**
	 * Adds all possible persistence steps from _effects.
	 * Overrides previous method to disregard facts used 
	 * to keep track of durative invariant actions
	 */
	@Override
	protected void addAllPerstitenceSteps()
	{
		for (PlanGraphLiteral planGraphLiteral : _effects)
		{
			Literal literal = planGraphLiteral.getLiteral();
			Predication literalPred = literal instanceof Predication ? (Predication) literal : 
											(Predication) ((NegatedLiteral) literal).argument;
			if(!literalPred.predicate.endsWith("-inv")){
				Step step = new Step("(Persistence Step " + literal.toString() + ")", literal, literal);
				PlanGraphStep planGraphStep = PlanGraphStep.createPersistentStep(step);
				_steps.add(planGraphStep);
				_persistenceSteps.add(planGraphStep);
			}
		}
	}

	public Expression getGoal(){
		return this.goal;
	}
	
	public Problem getProblem(){
		return this.problem;
	}
}
