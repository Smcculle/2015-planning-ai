package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ImmutableArray;

public class PlanGraphLevel 
{
	/** PlanGraph's Parent */
	private PlanGraphLevel _parent;
	
	/** List of all unique steps in PlanGraph */
	private ArrayList<PlanGraphStep> _steps;
	
	/** List of all unique effects in PlanGraph */
	private ArrayList<PlanGraphLiteral> _effects;
	
	/** List of all Persistence Steps (easier record keeping) */
	private ArrayList<PlanGraphStep> _persistenceSteps;

	/** Current level number */
	private int _level;
	
	private PlanGraph _planGraph;
	
	/**
	 * Constructs a new root of PlanGraph
	 * 
	 * @param problem The StateSpaceProblem to setup PlanGraph Steps and Effects
	 */
	public PlanGraphLevel (Problem problem, ArrayList<PlanGraphStep> steps, ArrayList<PlanGraphLiteral> effects,
			ArrayList<PlanGraphStep> persistenceSteps, PlanGraph planGraph)
	{
		_parent = null;
		_steps = steps;
		_effects = effects;
		_persistenceSteps = persistenceSteps;
		_planGraph = planGraph;
		_level = 0;
		setInitialEffects(problem.initial);
		setNonSpecifiedInitialEffects(problem.initial);
	}
	
	/**
	 * Constructs a new PlanGraph child
	 * 
	 * @param parent The parent of new PlanGraph
	 */
	public PlanGraphLevel (PlanGraphLevel parent)
	{
		_parent = parent;
		_effects = parent._effects;
		_steps = parent._steps;
		_persistenceSteps = parent._persistenceSteps;
		_level = _parent._level + 1;
		setPerstitenceStepLevels();
		addAllPossibleNewSteps();
	}

	/**
	 * Returns the PlanGraph's level number. Level number starts at
	 * 0 for the root node.
	 * 
	 * @return integer Level number
	 */
	public int getLevel()
	{
		return _level;
	}
	
	public boolean containsGoal(Expression goal)
	{
		ArrayList<Literal> literals = expressionToLiterals(goal);
		ArrayList<PlanGraphLiteral> currentPlanGraphLiterals = getCurrentLiterals();
		
		for (Literal literal : literals)
			if (!currentPlanGraphLiterals.contains(getPlanGraphLiteral(literal)))
				return false;
		
		return true;
	}

	public boolean isLeveledOff()
	{
		if (_parent == null)
			return false;

		if (_parent.currentEffectCount() == currentEffectCount())
			if (_parent.currentStepCount() == currentStepCount())
				return true;

		return false;
	}
	
	public int currentEffectCount()
	{
		int count = 0;
		for (PlanGraphLiteral effect : _effects)
			if(exists(effect))
				count++;
		return count;
	}
	
	public int currentStepCount()
	{
		int count = 0;
		for (PlanGraphStep step : _steps)
			if(exists(step))
				count++;
		return count;
	}
	
	/**
	 * Updates a PlanGraphStep on PlanGraph.
	 * Does not add a step if already exist in PlanGraph.
	 * Only updates a step if current all preconditions of step exist in parent's effects.
	 * Also computes all new mutual exclusions introduced
	 * 
	 * @param PlanGraphStep Step to be added
	 */
	private void updateStep(PlanGraphStep step)
	{
		if (step == null)
			return;
		
		if (_parent == null)
			return;
		
		if (!isPreconditionSatisfied(step))
			return;
		
		if (step.GetInitialLevel() == -1)
		{
			step.SetInitialLevel(getLevel());
			for (Literal literal : expressionToLiterals(step.GetStep().effect))
				if (getPlanGraphLiteral(literal).GetInitialLevel() == -1)
					getPlanGraphLiteral(literal).SetInitialLevel(getLevel());
		}
	}
	
	/**
	 * Checks effects of parent to see if preconditions are met.
	 * 
	 * @param planGraphStep Step to test precondition
	 * @return True if preconditions exist in parent.
	 */
	private boolean isPreconditionSatisfied(PlanGraphStep planGraphStep) {
		if (_parent == null)
			return false;
		
		Step step = planGraphStep.GetStep();
		for (Literal literal : expressionToLiterals(step.precondition))
		{
			boolean didFindValue = false;
			for (PlanGraphLiteral planGraphLiteral : _parent.getCurrentLiterals())
				if (literal.equals(planGraphLiteral.getLiteral()))
				{
					didFindValue = true;
					break;
				}
			
			if (!didFindValue)
				return false;
		}
		return true;
	}
	
	
	
	/**
	 * Adds effect of initial state to PlanGraph root
	 * 
	 * @param initialState State from which to add effects
	 */
	private void setInitialEffects(State initialState) 
	{
		ArrayList<Literal> literals = expressionToLiterals(initialState.toExpression());
		for (Literal literal : literals)
			for (PlanGraphLiteral planGraphLiteral : _effects)
				if (literal.equals(planGraphLiteral.getLiteral()))
					planGraphLiteral.SetInitialLevel(getLevel());
	}

	private void setNonSpecifiedInitialEffects(State initialState) 
	{
		ArrayList<Literal> literals = expressionToLiterals(initialState.toExpression());
		for (PlanGraphLiteral planGraphLiteral : _effects)
			if (planGraphLiteral.getLiteral() instanceof NegatedLiteral)
				if (!literals.contains(planGraphLiteral.getLiteral().negate()))
					planGraphLiteral.SetInitialLevel(getLevel());
	}
	
	/**
	 * Checks to see if any new persistence steps can be created at PlanGraph level.
	 */
	private void setPerstitenceStepLevels()
	{
		for (PlanGraphStep persistenceStep : _persistenceSteps)
		{
			Literal literal = (Literal)persistenceStep.GetStep().effect;
			if (persistenceStep.GetInitialLevel() == -1)
				if (getCurrentLiterals().contains(getPlanGraphLiteral(literal)))
					persistenceStep.SetInitialLevel(getLevel());
		}
	}
	
	private void addAllPossibleNewSteps() 
	{
		for (PlanGraphStep step : _steps)
			if (!_persistenceSteps.contains(step))
				updateStep(step);
	}
	
	/**
	 * The String representation of the current PlanGraph
	 * 
	 * @return string String representation of the current PlanGraph
	 */
	@Override
	public String toString()
	{
		String str = "";
		
		str += "--------------------------------\n";
		str += "PlanGraph Level " + getLevel() + "\n";
		str += "--------------------------------\n";
		
		str += "Steps [" + currentStepCount() + "]:\n";
		for (PlanGraphStep step : _steps)
			if (exists(step))
				str += "-" + step.toString() + "\n";
		
		str += "Effects [" + currentEffectCount() + "]:\n";
		for (PlanGraphLiteral effect : _effects)
			if (exists(effect))
				str += "-" + effect.toString() + "\n";
		
		return str;
	}

	public boolean exists(PlanGraphStep pgStep) 
	{
		// Between 0 and current level
		return 0 <= pgStep.GetInitialLevel() && pgStep.GetInitialLevel() <= _level;
	}

	public boolean exists(PlanGraphLiteral pgLiteral) 
	{		
		// Between 0 and current level
		return 0 <= pgLiteral.GetInitialLevel() && pgLiteral.GetInitialLevel() <= _level;
	}
}
