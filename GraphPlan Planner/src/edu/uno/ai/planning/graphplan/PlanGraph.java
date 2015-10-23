package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.List;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ConversionUtil;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * A PlanGraph is a structure of PlanGraphLevels
 * Each level contains facts/literals and actions/steps
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraph 
{
	/** Number of Levels **/
	private int _levels;
	
	/** List of all currently extended PlanGraphLevels **/
	private ArrayList<PlanGraphLevel> _levelList;
		
	/** List of all unique PlanGraphSteps in PlanGraph **/
	private ArrayList<PlanGraphStep> _steps;
	
	/** List of all unique PlanGraphLiterals in PlanGraph **/
	private ArrayList<PlanGraphLiteral> _effects;
	
    /** List of all Persistence Steps (easier record keeping) */
    private ArrayList<PlanGraphStep> _persistenceSteps;
	
    /** Will this PlanGraph calculate mutual exclusions? */
    private boolean _calculateMutex;
	
	/**
	 * Constructs a new PlanGraph Structure
	 * Will extend until new PlanGraphLevel contains goal or is leveled off
	 * Contains 1 or more PlanGraphLevels
	 * Contains 1 list of all Actions/Steps that will be used by all PlanGraphLevels
	 * Contains 1 list of all Facts/Literals/Effects that will be used by all PlanGraphLevels
	 * 
	 * @param problem The Problem in which to setup PlanGraph
	 * @param calculateMutex If true, will replace PlanGraphLevels with PlanGraphLevelMutexes
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

		connectParentsToChildren();
		
		PlanGraphLevel rootLevel = _calculateMutex ?
			new PlanGraphLevelMutex(problem, _steps, _effects, _persistenceSteps, this) :
			new PlanGraphLevel(problem, _steps, _effects, _persistenceSteps, this);
			
		_levelList.add(rootLevel);
		_levels = 1;
		
        while (!getMaxLevel().containsGoal(problem.goal) && !getMaxLevel().isLeveledOff())
        	extend();
	}
	
	/**
	 * Default value of PlanGraph is not to calculate mutual exclusions.
	 * 
	 * @param problem The Problem in which to setup PlanGraph
	 */
	public PlanGraph (Problem problem)
	{
		this (problem, false);
	}
	
	/**
	 * Extends the PlanGraph by adding an additional level to PlanGraph
	 */
	public void extend()
	{
		PlanGraphLevel nextLevel = _calculateMutex ?
			new PlanGraphLevelMutex((PlanGraphLevelMutex)getMaxLevel()) :
			new PlanGraphLevel(getMaxLevel());
			
		_levelList.add(nextLevel);
		_levels++;
	}
	
	/**
	 * Get the PlanGraphLevel Instance at a certain level
	 * 
	 * @param level The PlanGraphLevel number
	 * @return planGraphLevel Return planGraphLevel at level number if exists, null otherwise
	 */
	public PlanGraphLevel getLevel(int level)
	{
		if (level < _levels)
			return _levelList.get(level);
		else
			return null;
	}
	
	/**
	 * Get the root PlanGraphLevel (ie PlanGraphLevel @ level 0)
	 * 
	 * @return planGraphLevel Returns root PlanGraphLevel
	 */
	public PlanGraphLevel getRootLevel()
	{
		return _levelList.get(0);
	}
	
	/**
	 * The number of levels in the PlanGraph
	 * 
	 * @return int Number of levels in the PlanGraph
	 */
	public int countLevels()
	{
		return _levels;
	}
	
	 /**
     * Helper function to get PlanGraphStep from Step
     * 
     * @param step Step to get PlanGraphStep
     * @return planGraphStep Corresponding PlanGraphStep
     */
	public PlanGraphStep getPlanGraphStep(Step step)
    {
        for (PlanGraphStep planGraphStep : _steps)
            if (planGraphStep.getStep().compareTo(step) == 0)
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
	
	/**
	 * Does the PlanGraphStep exist at level specified?
	 * 
	 * @param pgStep PlanGraphStep to check if exists at certain level
	 * @param level The level number to check
	 * @return boolean True if planGraphStep exists at level, false otherwise
	 */
	public boolean existsAtLevel(PlanGraphStep pgStep, int level)
	{
		PlanGraphLevel planGraphLevel = getLevel(level);
		return planGraphLevel.exists(pgStep);
	}
	
	/**
	 * The one and only list of all PlanGraphSteps in PlanGraph
	 * 
	 * @return ArrayList<PlanGraphStep> All Possible Plan Graph Steps
	 */
	public ArrayList<PlanGraphStep> getAllPossiblePlanGraphSteps()
	{
		return _steps;
	}

	/**
	 * The one and only list of all PlanGraphEffects in PlanGraph
	 * 
	 * @return ArrayList<PlanGraphEffects> All Possible Plan Graph Effects
	 */
	public ArrayList<PlanGraphLiteral> getAllPossiblePlanGraphEffects()
	{
		return _effects;
	}
	
	/**
	 * Does the PlanGraphLiteral exist at level specified?
	 * 
	 * @param pgLiteral PlanGraphLiteral to check if exists at certain level
	 * @param level The level number to check
	 * @return boolean True if planGraphLiteral exists at level, false otherwise
	 */
	public boolean existsAtLevel(PlanGraphLiteral pgLiteral, int level)
	{
		PlanGraphLevel planGraphLevel = getLevel(level);
		return planGraphLevel.exists(pgLiteral);
	}
	
	/**
	 * Returns the most extended/maximum PlanGraphLevel in PlanGraph
	 * 
	 * @return PlanGraphLevel Maximum PlanGraphLevel in PlanGraph
	 */
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
			for (Literal literal : ConversionUtil.expressionToLiterals(step.effect))
				if (!literals.contains(literal))
					literals.add(literal);
			
			for (Literal literal : ConversionUtil.expressionToLiterals(step.precondition))
				if (!literals.contains(literal))
					literals.add(literal);
			
			for (Literal literal : ConversionUtil.expressionToLiterals(step.effect))
				if (!literals.contains(literal.negate()))
					literals.add(literal.negate());
			
			for (Literal literal : ConversionUtil.expressionToLiterals(step.precondition))
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
	 * Connect all Steps and Effects to their parents and children
	 */
	private void connectParentsToChildren(){
		for(PlanGraphStep step : _steps){
			// Add Step effects as Plan Graph Children
			List<Literal> effectLiterals = ConversionUtil.expressionToLiterals(step.getStep().effect);
			for(Literal literal : effectLiterals)
				for(PlanGraphLiteral effect : _effects)
					if(effect.equals(new PlanGraphLiteral(literal))){
						step.addChildLiteral(effect);
						effect.addParentStep(step);
					}
			// Add Step Preconditions as Plan Graph Parents
			List<Literal> preconditionLiterals = ConversionUtil.expressionToLiterals(step.getStep().precondition);
			for(Literal literal : preconditionLiterals)
				for(PlanGraphLiteral effect : _effects)
					if(effect.equals(new PlanGraphLiteral(literal))){
						step.addParentLiteral(effect);
						effect.addChildStep(step);
					}
		}
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
	 * Does the most extended/maximum PlanGraphLevel contain goal literals/facts? 
	 * 
	 * @param goal Goal expression
	 * @return true if max PlanGraphLevel contains all goal facts, false otherwise
	 */
	public boolean containsGoal(Expression goal)
	{
		return getMaxLevel().containsGoal(goal);
	}
	
	/**
	 * Is the most extended/maximum PlanGraphLevel the same as its parent?
	 * 
	 * @return true if PlanGraph is leveled off, false otherwise
	 */
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