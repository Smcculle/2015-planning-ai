package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * A PlanGraph Structure is
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraph 
{
	private ArrayList<PlanGraphLevel> _levels;
		
	/** List of all unique steps in PlanGraph */
	private ArrayList<PlanGraphStep> _steps;
	
	/** List of all unique effects in PlanGraph */
	private ArrayList<PlanGraphLiteral> _effects;
	
    /** List of all Persistence Steps (easier record keeping) */
    private ArrayList<PlanGraphStep> _persistenceSteps;
	
    /** Will this PlanGraph calculate mutual exclusions? */
    private boolean _calculateMutex;
	
	/**
	 * Constructs a new PlanGraph
	 * 
	 * @param problem The StateSpaceProblem to setup PlanGraph Steps and Effects
	 */
	public PlanGraph (Problem problem, boolean calculateMutex)
	{
		_levels = new ArrayList<PlanGraphLevel>();
		_steps = new ArrayList<PlanGraphStep>();
		_effects = new ArrayList<PlanGraphLiteral>();
		_persistenceSteps = new ArrayList<PlanGraphStep>();
		_calculateMutex = calculateMutex;
		
		StateSpaceProblem ssProblem = new StateSpaceProblem(problem);
		addAllSteps(ssProblem.steps);
		addAllEffects(ssProblem.steps);
		addAllPerstitenceSteps();
		
		PlanGraphLevel level = _calculateMutex ?
			new PlanGraphLevelMutex(problem, _steps, _effects, _persistenceSteps, this) :
			new PlanGraphLevel(problem, _steps, _effects, _persistenceSteps, this);
			
		_levels.add(level);
		
        while (!getMaxLevel().containsGoal(problem.goal) && !getMaxLevel().isLeveledOff())
        	extend();
	}
	
	public void extend()
	{
		PlanGraphLevel nextLevel = _calculateMutex ?
			new PlanGraphLevelMutex((PlanGraphLevelMutex)getMaxLevel()) :
			new PlanGraphLevel(getMaxLevel());
			
		_levels.add(nextLevel);
	}
	
	public PlanGraphLevel getPlanGraphLevel(int level)
	{
		if (_levels.size() < level)
			return _levels.get(level);
		else
			return null;
	}
	
	 /**
     * Helper function to get PlanGraphStep from step
     * 
     * @param step Step to get PlanGraphStep
     * @return planGraphStep Corresponding PlanGraphStep
     */
	public PlanGraphStep getPlanGraphStep(Step step)
    {
        for (PlanGraphStep planGraphStep : _steps)
            if (planGraphStep.GetStep() == step)
                return planGraphStep;
        return null;
    }
	
	/**
	 * Helper function to get PlanGraphLiteral from literal
	 * 
	 * @param literal Literal to get PlanGraphLiteral
	 * @return planGraphLiteral Corresponding PlanGraphLiteral
	 */
	public PlanGraphLiteral getPlanGraphLiteral(Literal literal)
	{
		for (PlanGraphLiteral planGraphLiteral : _effects)
			if (planGraphLiteral.getLiteral().equals(literal))
				return planGraphLiteral;
		return null;
	}
	
	public boolean existsAtLevel(PlanGraphStep pgStep, int level)
	{
		PlanGraphLevel planGraphLevel = getPlanGraphLevel(level);
		return planGraphLevel.exists(pgStep);
	}
	
	public boolean existsAtLevel(PlanGraphLiteral pgLiteral, int level)
	{
		PlanGraphLevel planGraphLevel = getPlanGraphLevel(level);
		return planGraphLevel.exists(pgLiteral);
	}
	
	private PlanGraphLevel getMaxLevel()
	{
		int size = _levels.size();
		return _levels.get(size-1);
	}
	
	/**
	 * Adds all possible effects from all possible steps.
	 * 
	 * @param steps All possible steps
	 */
	private void addAllEffects(ImmutableArray<Step> steps) 
	{
		ArrayList<Literal> literals = new ArrayList<Literal>();
		for (Step step : steps)
		{
			for (Literal literal : expressionToLiterals(step.effect))
				if (!literals.contains(literal))
					literals.add(literal);
			
			for (Literal literal : expressionToLiterals(step.precondition))
				if (!literals.contains(literal))
					literals.add(literal);
			
			for (Literal literal : expressionToLiterals(step.effect))
				if (!literals.contains(literal.negate()))
					literals.add(literal.negate());
			
			for (Literal literal : expressionToLiterals(step.precondition))
				if (!literals.contains(literal.negate()))
					literals.add(literal.negate());
		}
		
		for (Literal literal : literals)
			_effects.add(new PlanGraphLiteral(literal, this));
	}
	
	/**
	 * Adds all possible steps from problem.
	 * 
	 * @param steps All possible Steps.
	 */
	private void addAllSteps(ImmutableArray<Step> steps) 
	{
		for (Step step : steps)
			_steps.add(new PlanGraphStep(step));
	}
	
	/**
	 * Adds all possible persistence steps from _effects.
	 */
	private void addAllPerstitenceSteps()
	{
		for (PlanGraphLiteral planGraphLiteral : _effects)
		{
			Literal literal = planGraphLiteral.getLiteral();
			Step step = new Step("(Persistence Step " + literal.toString() + ")", literal, literal);
			PlanGraphStep planGraphStep = new PlanGraphStep(step);
			_steps.add(planGraphStep);
			_persistenceSteps.add(planGraphStep);
		}
	}
	
	/**
	 * Helper function to get all the literals from an Expression
	 * 
	 * @param expression The Expression to convert to list
	 * @return ArrayList<Literal> List of literals in expression
	 */
	private ArrayList<Literal> expressionToLiterals(Expression expression)
	{
		ArrayList<Literal> literals = new ArrayList<Literal>();
		if (expression instanceof Literal)
			literals.add((Literal)expression);
		else
		{
			Conjunction cnf = (Conjunction)expression.toCNF();
			for (Expression disjunction : cnf.arguments)
				if (((Disjunction) disjunction).arguments.length == 1)
					literals.add((Literal)((Disjunction) disjunction).arguments.get(0));
				// else -- Do Nothing!
		}
		return literals;
	}
}