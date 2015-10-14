package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;


import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
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
	private int _levels;
	
	private ArrayList<PlanGraphLevel> _levelList;
		
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
		_levelList = new ArrayList<PlanGraphLevel>();
		_steps = new ArrayList<PlanGraphStep>();
		_effects = new ArrayList<PlanGraphLiteral>();
		_persistenceSteps = new ArrayList<PlanGraphStep>();
		_calculateMutex = calculateMutex;
		
		StateSpaceProblem ssProblem = new StateSpaceProblem(problem);
		addAllSteps(ssProblem.steps);
		addAllEffects(ssProblem.steps);
		addAllPerstitenceSteps();
		
		PlanGraphLevel rootLevel = _calculateMutex ?
			new PlanGraphLevelMutex(problem, _steps, _effects, _persistenceSteps, this) :
			new PlanGraphLevel(problem, _steps, _effects, _persistenceSteps, this);
			
		_levelList.add(rootLevel);
		_levels = 1;
		
        while (!getMaxLevel().containsGoal(problem.goal) && !getMaxLevel().isLeveledOff())
        	extend();
	}
	
	public PlanGraph (Problem problem)
	{
		this (problem, false);
	}
	
	public void extend()
	{
		PlanGraphLevel nextLevel = _calculateMutex ?
			new PlanGraphLevelMutex((PlanGraphLevelMutex)getMaxLevel()) :
			new PlanGraphLevel(getMaxLevel());
			
		_levelList.add(nextLevel);
		_levels++;
	}
	
	public PlanGraphLevel getLevel(int level)
	{
		if (level < _levels)
			return _levelList.get(level);
		else
			return null;
	}
	
	public PlanGraphLevel getRootLevel()
	{
		return _levelList.get(0);
	}
	
	public int countLevels()
	{
		return _levels;
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
            if (planGraphStep.getStep() == step)
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
		PlanGraphLevel planGraphLevel = getLevel(level);
		return planGraphLevel.exists(pgStep);
	}
	
	public ArrayList<PlanGraphStep> getAllPossiblePlanGraphSteps()
	{
		return _steps;
	}
	
	public ArrayList<PlanGraphLiteral> getAllPossiblePlanGraphEffects()
	{
		return _effects;
	}
	
	public boolean existsAtLevel(PlanGraphLiteral pgLiteral, int level)
	{
		PlanGraphLevel planGraphLevel = getLevel(level);
		return planGraphLevel.exists(pgLiteral);
	}
	
	private PlanGraphLevel getMaxLevel()
	{
		return _levelList.get(_levels - 1);
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
			_effects.add(new PlanGraphLiteral(literal));
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
			PlanGraphStep planGraphStep = PlanGraphStep.createPersistentStep(step);
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
	static public ArrayList<Literal> expressionToLiterals(Expression expression)
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
	
	public boolean containsGoal(Expression goal)
	{
		return getMaxLevel().containsGoal(goal);
	}
	
	public boolean isLeveledOff()
	{
		return getMaxLevel().isLeveledOff();
	}
	
	@Override
	public String toString()
	{
		String str = "";
		for(PlanGraphLevel level : _levelList)
			str += level.toString();
		
		return str;
	}
}