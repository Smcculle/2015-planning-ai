package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.util.ConversionUtil;

/**
 * A PlanGraphLevel is a substructure of PlanGraph
 * Each level contains facts/literals and actions/steps
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraphLevel 
{
	/** PlanGraphLevel's Parent **/
	private PlanGraphLevel _parent;
	
	/** List of all unique PlanGraphSteps in PlanGraph **/
	protected ArrayList<PlanGraphStep> _steps;
	
	/** List of all unique PlanGraphLiterals in PlanGraph **/
	protected ArrayList<PlanGraphLiteral> _effects;
	
	/** List of all Persistence Steps (easier record keeping) **/
	private ArrayList<PlanGraphStep> _persistenceSteps;

	/** Current level number **/
	private int _level;
	
	/** The PlanGraph structure containing this level **/
	protected PlanGraph _planGraph;
	
	/**
	 * Constructs a new PlanGraphLevel
	 * Does not create additional lists for facts/effect and steps/actions.
	 * This constructor is specifically intended to create root level of PlanGraph
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
	 * Constructs a new PlanGraphLevel child
	 * Does not create additional lists for facts/effect and steps/actions.
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
	 * Does this level contain goal effects/facts?
	 * 
	 * @param goal Goal Expression
	 * @return true if goal effect/facts are within this level, false otherwise
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
	 * Previous/Parent level of this PlanGraphLevel
	 * 
	 * @return PlanGraphLevel Parent PlanGraphLevel or null if root
	 */
	public PlanGraphLevel getParent()
	{
		return _parent;
	}

	/**
	 * Is this PlanGraphLevel leveled off?
	 * Determines this by checking the size of current steps and effects.
	 * 
	 * @return true if PlanGraph is leveled off, false otherwise
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
	 * Number of Effects at this level
	 * 
	 * @return integer Number of Effects at this level
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
	 * Number of Steps at this level
	 * 
	 * @return integer Number of Steps at this level
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
	 * Does PlanGraphStep exist at this level?
	 * 
	 * @param pgStep PlanGraphStep to test if exists
	 * @return true if PlanGraphStep exists at this level, false otherwise
	 */
	public boolean exists(PlanGraphStep pgStep) 
	{
		if (pgStep== null) return false;
		// return PlanGraphStep exists at this Level
		return pgStep.existsAtLevel(getLevel());
	}

	/**
	 * Does PlanGraphLiteral exist at this level?
	 * 
	 * @param pgLiteral PlanGraphLiteral to test if exists
	 * @return true if PlanGraphLiteral exists at this level, false otherwise
	 */
	public boolean exists(PlanGraphLiteral pgLiteral) 
	{		
		if (pgLiteral == null) return false;
		// return PlanGraphStep exists at this Level
		return pgLiteral.existsAtLevel(getLevel());
	}
	
	/**
	 * Helper function to see if Step exists at this level
	 * 
	 * @param step Step to test if exists at this level
	 * @return true if PlanGraphStep of Step exists at this level, false otherwise
	 */
	public boolean exists(Step step) 
	{
		return exists(_planGraph.getPlanGraphStep(step));
	}

	/**
	 * Helper function to see if Literal exists at this level
	 * 
	 * @param literal SteLiteral test if exists at this level
	 * @return true if PlanGraphLiteral of Literal exists at this level, false otherwise
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
	 * Any effect that was not explicitly specified is initiated
	 * at root Level as NegatedLiteral
	 * 
	 * @param initialState Expression of the initial state
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
	 * Updates all non-persistent steps in PlanGraphLevel
	 * Run at Constructor
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
