package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.State;
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
	/** List of all steps in PlanGraph */
	ArrayList<PlanGraphStep> _steps;
	
	/** List of all steps in PlanGraph */
	ArrayList<PlanGraphLiteral> _effects;
	
	/** List of all mutually exclusive steps in current PlanGraph Level */
	Map<PlanGraphStep, ArrayList<PlanGraphStep>> _mutexSteps = new Hashtable<PlanGraphStep, ArrayList<PlanGraphStep>>();
	
	/** List of all mutually exclusive literals in current PlanGraph Level */
	Map<PlanGraphLiteral, ArrayList<PlanGraphLiteral>> _mutexLiterals = new Hashtable<PlanGraphLiteral, ArrayList<PlanGraphLiteral>>();
	
	/** PlanGraph's Parent */
	PlanGraph _parent;
	
	/**
	 * Constructs a new root PlanGraph
	 * 
	 * @param initialState The initial state of the problem
	 */
	public PlanGraph (State initialState, StateSpaceProblem problem)
	{
		_steps = new ArrayList<PlanGraphStep>();
		_effects = new ArrayList<PlanGraphLiteral>();
		_parent = null;
		
		addAllSteps(problem.steps);
		addAllEffects(problem.steps);
		setInitialEffects(initialState);
	}
	
	/**
	 * Constructs a new PlanGraph child
	 * 
	 * @param parent The parent of new PlanGraph
	 */
	public PlanGraph (PlanGraph parent)
	{
		_steps = parent._steps;
		_effects = parent._effects;
		_parent = parent;
	}
	
	/**
	 * Returns the PlanGraph's level number. Level number starts at
	 * 0 for the root node.
	 * 
	 * @return integer Level number
	 */
	public int getLevelNumber()
	{
		if (_parent == null)
			return 0;
		
		return _parent.getLevelNumber() + 1;
	}
	
	/**
	 * Returns the PlanGraph's parent
	 * 	
	 * @return PlanGraph The Parent of PlanGraph
	 */
	public PlanGraph getParent()
	{
		return _parent;
	}
	
	/**
	 * Returns an ArrayList<Literal> of all effect Literals
	 *  	
	 * @return effectLiterals The list of all effect literals in PlanGraph 
	 */
	public ArrayList<PlanGraphLiteral> getCurrentLiterals()
	{
		ArrayList<PlanGraphLiteral> effectLiterals = new ArrayList<PlanGraphLiteral>();		
		for (PlanGraphLiteral effect : _effects)
			if (0 <= effect.GetInitialLevel() && effect.GetInitialLevel() <= getLevelNumber())
				effectLiterals.add(effect);
		return effectLiterals;
	}
	
	/**
	 * Returns all Steps in the current PlanGraph
	 * 	
	 * @return ArrayList<Literal> All steps used in current PlanGraph
	 */
	public ArrayList<PlanGraphStep> getCurrentSteps()
	{
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();		
		for (PlanGraphStep step : _steps)
			if (0 <= step.GetInitialLevel() && step.GetInitialLevel() <= getLevelNumber())
				steps.add(step);
		return steps;	
	}
	
	/**
	 * Returns all Steps in all PlanGraphs.
	 * In other words, get all steps from the root PlanGraph to current
	 * PlanGraph.
	 * 	
	 * @return ArrayList<Literal> All steps used in all PlanGraph
	 */
	public ArrayList<PlanGraphStep> getAllSteps()
	{
		return _steps;
	}
	
	/**
	 * Adds a step to PlanGraph.
	 * Does not add a step if already exist in PlanGraph.
	 * Only adds a step if current all preconditions of step exist in parent's effects.
	 * 
	 * @param step
	 */
	public boolean addStep(PlanGraphStep step)
	{
		if (step == null)
			return false;
		
		if (_parent == null)
			return false;
		
		if (!isPreconditionSatisfied(step))
			return false;
		
		if (step.GetInitialLevel() == -1)
		{
			step.SetInitialLevel(getLevelNumber());
			checkForInconsistentEffects();
			checkForInterference();
			checkForCompetingNeeds();
			checkForOpposites();
			checkForInconsistentSupport();
		}
		return true;
	}
	
	public boolean addStep(Step step)
	{
		return addStep(getPlanGraphStep(step));
	}
	
	private boolean isPreconditionSatisfied(PlanGraphStep planGraphStep) {
		if (_parent == null)
			return false;
			
		Step step = planGraphStep.GetStep();
		for (Literal literal : expressionToLiterals(step.precondition))
		{
			boolean didFindValue = false;
			for (PlanGraphLiteral planGraphLiteral : _parent._effects)
				if (literal == planGraphLiteral.getEffectLiteral())
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
	 * Returns a Map of Mutually Exclusive Steps.
	 * 
	 * @return Map<PlanGraphStep, ArrayList<PlanGraphStep>> Mutually Exclusive Steps.
	 */
	public Map<PlanGraphStep, ArrayList<PlanGraphStep>> getMutuallyExclusiveSteps()
	{
		return _mutexSteps;
	}
	
	/**
	 * Returns a Map of Mutually Exclusive Literals.
	 * 
	 * @return Map<PlanGraphLiteral, ArrayList<PlanGraphLiteral>> Mutually Exclusive Literals.
	 */
	public Map<PlanGraphLiteral, ArrayList<PlanGraphLiteral>> getMutuallyExclusiveLiterals()
	{
		return _mutexLiterals;
	}
	
	private void setInitialEffects(State initialState) 
	{
		ArrayList<Literal> literals = expressionToLiterals(initialState.toExpression());
		for (Literal literal : literals)
			for (PlanGraphLiteral planGraphLiteral : _effects)
				if (literal.equals(planGraphLiteral.getEffectLiteral()))
					planGraphLiteral.SetInitialLevel(getLevelNumber());
	}
	
	private void addAllEffects(ImmutableArray<Step> steps) 
	{
		ArrayList<Literal> literals = new ArrayList<Literal>();
		for (Step step : steps)
		{
			for (Literal literal : expressionToLiterals(step.precondition))
			{
				if (!literals.contains(literal))
					literals.add(literal);
				if (!literals.contains(literal.negate()))
					literals.add(literal.negate());
			}
			for (Literal literal : expressionToLiterals(step.effect))
			{
				if (!literals.contains(literal))
					literals.add(literal);
				if (!literals.contains(literal.negate()))
					literals.add(literal.negate());
			}
		}
		
		for (Literal literal : literals)
			_effects.add(new PlanGraphLiteral(literal));
	}
	
	private void addAllSteps(ImmutableArray<Step> steps) 
	{
		for (Step step : steps)
			_steps.add(new PlanGraphStep(step));
	}
	
	/**
	 * Checks to see if there are inconsistent effects with newly added step.
	 * Inconsistent effect: One action negates an effect of the other.
	 */
	private void checkForInconsistentEffects() 
	{
		for (PlanGraphStep step : _steps)
		{
			for (PlanGraphStep otherStep : _steps)
			{
				if (step != otherStep)
				{
					ArrayList<Literal> stepEffectLiterals = expressionToLiterals(step.GetStep().effect);
					ArrayList<Literal> otherStepEffectLiterals = expressionToLiterals(otherStep.GetStep().effect);
					for (Expression literal : stepEffectLiterals)
					{
						Expression negatedLiteral = literal.negate();
						if (otherStepEffectLiterals.contains(negatedLiteral))
						{
							if (_mutexSteps.containsKey(step))
							{
								ArrayList<PlanGraphStep> steps = _mutexSteps.get(step);
								if (!steps.contains(otherStep))
									steps.add(otherStep);
							}
							else
							{
								ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
								steps.add(otherStep);
								_mutexSteps.put(step, steps);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Checks to see if there is interference with newly added step.
	 * Interference: One action negates the precondition of the other.
	 */
	private void checkForInterference() 
	{
		for (PlanGraphStep step : _steps)
		{
			for (PlanGraphStep otherStep : _steps)
			{
				if (step != otherStep)
				{
					ArrayList<Literal> stepEffectLiterals = expressionToLiterals(step.GetStep().effect);
					ArrayList<Literal> otherStepPreconditionLiterals = expressionToLiterals(otherStep.GetStep().precondition);
					for (Expression literal : stepEffectLiterals)
					{
						Expression negatedLiteral = literal.negate();
						if (otherStepPreconditionLiterals.contains(negatedLiteral))
						{
							if (_mutexSteps.containsKey(step))
							{
								ArrayList<PlanGraphStep> steps = _mutexSteps.get(step);
								if (!steps.contains(otherStep))
									steps.add(otherStep);
							}
							else
							{
								ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
								steps.add(otherStep);
								_mutexSteps.put(step, steps);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Checks to see if there are competing need with newly added step.
	 * Competing Needs: Actions have mutually exclusive preconditions.
	 */
	private void checkForCompetingNeeds() 
	{
		for (PlanGraphStep step : _steps)
		{
			for (PlanGraphStep otherStep : _steps)
			{
				if (step != otherStep)
				{
					ArrayList<Literal> stepPreconditionLiterals = expressionToLiterals(step.GetStep().precondition);
					ArrayList<Literal> otherStepPreconditionLiterals = expressionToLiterals(otherStep.GetStep().precondition);
					for (Expression literal : stepPreconditionLiterals)
					{
						Expression negatedLiteral = literal.negate();
						if (otherStepPreconditionLiterals.contains(negatedLiteral))
						{
							if (_mutexSteps.containsKey(step))
							{
								ArrayList<PlanGraphStep> steps = _mutexSteps.get(step);
								if (!steps.contains(otherStep))
									steps.add(otherStep);
							}
							else
							{
								ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
								steps.add(otherStep);
								_mutexSteps.put(step, steps);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Checks to see if newly added effects contain opposites.
	 * Opposites: One literal is a negation of another.
	 */
	private void checkForOpposites() 
	{
		ArrayList<PlanGraphLiteral> effects = getCurrentLiterals();
		for (PlanGraphLiteral effect : effects)
		{
			for (PlanGraphLiteral otherEffect : effects)
			{
				if (effect != otherEffect)
				{
					PlanGraphLiteral negatedEffect = getPlanGraphLiteral(effect.getEffectLiteral().negate());
					if (negatedEffect == otherEffect)
					{
						if (_mutexLiterals.containsKey(effect))
						{
							ArrayList<PlanGraphLiteral> literals = _mutexLiterals.get(effect);
							if (!literals.contains(otherEffect))
								literals.add(otherEffect);
						}
						else
						{
							ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
							literals.add(otherEffect);
							_mutexLiterals.put(effect, literals);
						}
					}
				}
			}
		}
	}

	/**
	 * Checks to see if newly added effects contain inconsistent support.
	 * Inconsistent Support: Every possible pair of actions that could achieve the literals
	 * are mutually exclusive.
	 */
	private void checkForInconsistentSupport() 
	{
		// TODO Auto-generated method stub
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
	
	private PlanGraphStep getPlanGraphStep(Step step)
	{
		for (PlanGraphStep planGraphStep : _steps)
			if (planGraphStep.GetStep() == step)
				return planGraphStep;
		return null;
	}
	
	private PlanGraphLiteral getPlanGraphLiteral(Literal literal)
	{
		for (PlanGraphLiteral planGraphLiteral : _effects)
			if (planGraphLiteral.getEffectLiteral() == literal)
				return planGraphLiteral;
		return null;
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
		if (_parent != null)
			str += _parent.toString();
		
		str += "---------------\n";
		str += "Level " + getLevelNumber() + "\n";
		str += "Steps [" + _steps.size() + "]:\n";
		for (PlanGraphStep step : _steps)
			str += step.GetStep().toString() + "\n";
		str += "Effects [" + getCurrentLiterals().size() + "]:\n";
		for (PlanGraphLiteral literal : getCurrentLiterals())
			str += literal.getEffectLiteral().toString() + "\n";
		
		return str;
	}
}