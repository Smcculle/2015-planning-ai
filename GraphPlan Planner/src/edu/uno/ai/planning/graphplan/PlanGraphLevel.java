package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;

public class PlanGraphLevel 
{
	/** PlanGraph's Parent */
	private PlanGraphLevel _parent;
	
	/** List of all unique steps in PlanGraph */
	protected ArrayList<PlanGraphStep> _steps;
	
	/** List of all unique effects in PlanGraph */
	protected ArrayList<PlanGraphLiteral> _effects;
	
	/** List of all Persistence Steps (easier record keeping) */
	private ArrayList<PlanGraphStep> _persistenceSteps;

	/** Current level number */
	private int _level;
	
	protected PlanGraph _planGraph;
	
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
		_planGraph = parent._planGraph;
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
		ArrayList<Literal> literals = PlanGraph.expressionToLiterals(goal);
		for (Literal literal : literals)
		{
			PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
			if (!exists(pgLiteral))
				return false;
		}
		return true;
	}
	
	public PlanGraphLevel getParent()
	{
		return _parent;
	}

	public boolean isLeveledOff()
	{
		if (_parent == null)
			return false;

		if (_parent.countCurrentEffects() == countCurrentEffects())
			if (_parent.countCurrentSteps() == countCurrentSteps())
				return true;

		return false;
	}
	
	public int countCurrentEffects()
	{
		int count = 0;
		for (PlanGraphLiteral effect : _effects)
			if(exists(effect))
				count++;
		return count;
	}
	
	public int countCurrentSteps()
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
		
		if (step.getInitialLevel() == -1)
		{
			step.SetInitialLevel(getLevel());
			ArrayList<Literal> literals = PlanGraph.expressionToLiterals(step.getStep().effect);
			for (Literal literal : literals)
				if (!exists(literal))
					_planGraph.getPlanGraphLiteral(literal).SetInitialLevel(_level);
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
		
		Step step = planGraphStep.getStep();
		for (Literal literal : PlanGraph.expressionToLiterals(step.precondition))
		{
			boolean didFindValue = false;
			for (PlanGraphLiteral planGraphLiteral : _effects)
			{
				if (_parent.exists(planGraphLiteral))
				{
					if (literal.equals(planGraphLiteral.getLiteral()))
					{
						didFindValue = true;
						break;
					}
				}
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
		ArrayList<Literal> literals = PlanGraph.expressionToLiterals(initialState.toExpression());
		for (Literal literal : literals)
			for (PlanGraphLiteral planGraphLiteral : _effects)
				if (literal.equals(planGraphLiteral.getLiteral()))
					planGraphLiteral.SetInitialLevel(getLevel());
	}

	private void setNonSpecifiedInitialEffects(State initialState) 
	{
		ArrayList<Literal> literals = PlanGraph.expressionToLiterals(initialState.toExpression());
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
			Literal literal = (Literal)persistenceStep.getStep().effect;
			if (!exists(persistenceStep))
				if (_parent.exists(literal))
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
		
		str += "Steps [" + countCurrentSteps() + "]:\n";
		for (PlanGraphStep step : _steps)
			if (exists(step))
				str += "-" + step.toString() + "\n";
		
		str += "Effects [" + countCurrentEffects() + "]:\n";
		for (PlanGraphLiteral effect : _effects)
			if (exists(effect))
				str += "-" + effect.toString() + "\n";
		
		return str;
	}

	public boolean exists(PlanGraphStep pgStep) 
	{
		if (pgStep== null) return false;
		// Between 0 and current level
		return 0 <= pgStep.getInitialLevel() && pgStep.getInitialLevel() <= _level;
	}

	public boolean exists(PlanGraphLiteral pgLiteral) 
	{		
		if (pgLiteral == null) return false;
		// Between 0 and current level
		return 0 <= pgLiteral.GetInitialLevel() && pgLiteral.GetInitialLevel() <= _level;
	}
	
	public boolean exists(Step step) 
	{
		return exists(_planGraph.getPlanGraphStep(step));
	}

	public boolean exists(Literal literal) 
	{		
		return exists(_planGraph.getPlanGraphLiteral(literal));
	}
}
