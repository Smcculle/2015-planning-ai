package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.util.ConversionUtil;

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
	
	/**
	 * Boolean method to return whether or not the given Expression
	 * exists within the effects found at this PlanGraphLevel of the
	 * PlanGraph 
	 * @param goal
	 * @return for(literal : goal)
	 * 			if (!exists(literal))
	 * 				return false
	 */
	public boolean containsGoal(Expression goal)
	{		
		ArrayList<Literal> literals = ConversionUtil.expressionToLiterals(goal);
		for (Literal literal : literals)
		{
			PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
			if (!exists(pgLiteral))
				return false;
		}
		return true;
	}
	
	/**
	 * Return the previous PlanGraphLevel in the PlanGraph
	 * @return if(getLevel() == 0)
	 * 			return null
	 * 		   else
	 * 			return parent
	 */
	public PlanGraphLevel getParent()
	{
		return _parent;
	}

	/**
	 * Boolean method to return whether or not this 
	 * PlanGraphLevel has leveled off compared to its
	 * parent PlanGraphLevel
	 * @return parent != null && 
	 * 		   parent.currentEffects != this.currentEffects &&
	 * 		   parent.currentSteps != this.currentSteps 
	 */
	public boolean isLeveledOff()
	{
		if (_parent == null)
			return false;

		if (_parent.countCurrentEffects() == countCurrentEffects())
			if (_parent.countCurrentSteps() == countCurrentSteps())
				return true;

		return false;
	}
	
	/**
	 * Get the number of Effects that exist in this PlanGraphLevel
	 * @return currentEffects.size()
	 */
	public int countCurrentEffects()
	{
		int count = 0;
		for (PlanGraphLiteral effect : _effects)
			if(exists(effect))
				count++;
		return count;
	}

	/**
	 * Get the number of Steps that exist in this PlanGraphLevel
	 * @return currentSteps.size()
	 */
	public int countCurrentSteps()
	{
		int count = 0;
		for (PlanGraphStep step : _steps)
			if(exists(step))
				count++;
		return count;
	}

	/**
	 * Boolean method to return whether or not the given
	 * PGStep exists in this PlanGraphLevel of the PlanGraph
	 * @param pgStep
	 * @return pgStep.getInitialLevel() > -1 &&
	 * 		   pgStep.getInitialLevel() <= getLevel()
	 */
	public boolean exists(PlanGraphStep pgStep) 
	{
		if (pgStep== null) return false;
		// return PlanGraphStep exists at this Level
		return pgStep.existsAtLevel(getLevel());
	}

	/**
	 * Boolean method to return whether or not the given
	 * PGLiteral exists in this PlanGraphLevel of the PlanGraph
	 * @param pgLiteral
	 * @return pgLiteral.getInitialLevel() > -1 &&
	 * 		   pgLiteral.getInitialLevel() <= getLevel()
	 */
	public boolean exists(PlanGraphLiteral pgLiteral) 
	{		
		if (pgLiteral == null) return false;
		// return PlanGraphStep exists at this Level
		return pgLiteral.existsAtLevel(getLevel());
	}

	/**
	 * Boolean method to return whether or not the given
	 * Step exists in this PlanGraphLevel of the PlanGraph
	 * @param step
	 * @return PlanGraphStep(step).getInitialLevel() > -1 &&
	 * 		   PlanGraphStep(step).getInitialLevel() <= getLevel()
	 */
	public boolean exists(Step step) 
	{
		return exists(_planGraph.getPlanGraphStep(step));
	}

	/**
	 * Boolean method to return whether or not the given
	 * Literal exists in this PlanGraphLevel of the PlanGraph
	 * @param literal
	 * @return PlanGraphLiteral(literal).getInitialLevel() > -1 &&
	 * 		   PlanGraphLiteral(literal).getInitialLevel() <= getLevel()
	 */
	public boolean exists(Literal literal) 
	{		
		return exists(_planGraph.getPlanGraphLiteral(literal));
	}
	
	/**
	 * The String representation of the current PlanGraphLevel
	 * 
	 * @return string String representation of the current PlanGraphLevel
	 * 		listing the level number, the number of current steps, the current
	 * 		steps, the number of current effects, and the current effects
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
	
	// Private methods
	
	/**
	 * Adds effect of initial state to root PlanGraphLevel 
	 * 
	 * @param initialState State from which to add effects
	 */
	private void setInitialEffects(State initialState) 
	{
		ArrayList<Literal> literals = ConversionUtil.expressionToLiterals(initialState.toExpression());
		for (Literal literal : literals)
			for (PlanGraphLiteral planGraphLiteral : _effects)
				if (literal.equals(planGraphLiteral.getLiteral()))
					planGraphLiteral.setInitialLevel(getLevel());
	}

	/**
	 * Adds negated effect not mentioned in initialState to root PlanGraphLevel
	 * 
	 * @param initialState State from which to add effects
	 */
	private void setNonSpecifiedInitialEffects(State initialState) 
	{
		ArrayList<Literal> literals = ConversionUtil.expressionToLiterals(initialState.toExpression());
		for (PlanGraphLiteral planGraphLiteral : _effects)
			if (planGraphLiteral.getLiteral() instanceof NegatedLiteral)
				if (!literals.contains(planGraphLiteral.getLiteral().negate()))
					planGraphLiteral.setInitialLevel(getLevel());
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
					persistenceStep.setInitialLevel(getLevel());
		}
	}
	
	/**
	 * Sets InitialLevel of Steps that require it for this
	 * level of the PlanGraph
	 */
	private void addAllPossibleNewSteps() 
	{
		for (PlanGraphStep step : _steps)
			if (!_persistenceSteps.contains(step))
				updateStep(step);
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
			step.setInitialLevel(getLevel());
			ArrayList<Literal> literals = ConversionUtil.expressionToLiterals(step.getStep().effect);
			for (Literal literal : literals)
				if (!exists(literal))
					_planGraph.getPlanGraphLiteral(literal).setInitialLevel(_level);
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
		for (Literal literal : ConversionUtil.expressionToLiterals(step.precondition))
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
	
}
