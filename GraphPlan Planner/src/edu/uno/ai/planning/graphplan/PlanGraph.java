package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.Hashtable;
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
	/** PlanGraph's Parent */
	PlanGraph _parent;
	
	/** List of all unique effects in PlanGraph */
	ArrayList<PlanGraphLiteral> _effects;
	
	/** List of all unique steps in PlanGraph */
	ArrayList<PlanGraphStep> _steps;
	
	/** List of all Persistence Steps (easier record keeping) */
	ArrayList<PlanGraphStep> _persistenceSteps;
	
	/** List of all mutually exclusive steps in current PlanGraph Level */
	Map<PlanGraphStep, ArrayList<PlanGraphStep>> _mutexSteps = new Hashtable<PlanGraphStep, ArrayList<PlanGraphStep>>();
	
	/** List of all mutually exclusive literals in current PlanGraph Level */
	Map<PlanGraphLiteral, ArrayList<PlanGraphLiteral>> _mutexLiterals = new Hashtable<PlanGraphLiteral, ArrayList<PlanGraphLiteral>>();
	
	/**
	 * Constructs a new root of PlanGraph
	 * 
	 * @param problem The StateSpaceProblem to setup PlanGraph Steps and Effects
	 */
	public PlanGraph (StateSpaceProblem problem)
	{
		_parent = null;
		_effects = new ArrayList<PlanGraphLiteral>();
		_steps = new ArrayList<PlanGraphStep>();
		_persistenceSteps = new ArrayList<PlanGraphStep>();
		
		addAllSteps(problem.steps);
		addAllEffects(problem.steps);
		addAllPerstitenceSteps();
		setInitialEffects(problem.initial);
		setNonSpecifiedInitialEffects(problem.initial);
	}

	/**
	 * Constructs a new root of PlanGraph
	 * 
	 * @param problem The Problem to setup PlanGraph Steps and Effects
	 */
	public PlanGraph (Problem problem)
	{
		this(new StateSpaceProblem(problem));
	}
	
	/**
	 * Constructs a new PlanGraph child
	 * 
	 * @param parent The parent of new PlanGraph
	 */
	public PlanGraph (PlanGraph parent)
	{
		_parent = parent;
		_effects = parent._effects;
		_steps = parent._steps;
		_persistenceSteps = parent._persistenceSteps;
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
		if (_parent == null)
			return 0;
		
		return _parent.getLevel() + 1;
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
	 * Returns an ArrayList<PlanGraphLiteral> of all effect Literals up to this level
	 *  	
	 * @return effectLiterals The list of PlanGraphLiterals in PlanGraph up to this level 
	 */
	public ArrayList<PlanGraphLiteral> getCurrentLiterals()
	{
		ArrayList<PlanGraphLiteral> effectLiterals = new ArrayList<PlanGraphLiteral>();		
		for (PlanGraphLiteral effect : _effects)
			if (0 <= effect.GetInitialLevel() && effect.GetInitialLevel() <= getLevel())
				effectLiterals.add(effect);
		return effectLiterals;
	}
	
	/**
	 * Returns an ArrayList<PlanGraphStep> of all Steps up to this level
	 * 	
	 * @return steps The list of PlanGraphSteps in PlanGraph up to this level
	 */
	public ArrayList<PlanGraphStep> getCurrentSteps()
	{
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();		
		for (PlanGraphStep step : _steps)
			if (0 <= step.GetInitialLevel() && step.GetInitialLevel() <= getLevel())
				steps.add(step);
		return steps;	
	}
	
	/**
	 * Returns all possible effect Literals in all PlanGraphs.
	 * 	
	 * @return ArrayList<PlanGraphLiteral> All steps used in all PlanGraph
	 */
	public ArrayList<PlanGraphLiteral> getAllLiterals()
	{
		return _effects;
	}
	
	/**
	 * Returns all possible Steps in all PlanGraphs.
	 * 	
	 * @return ArrayList<PlanGraphStep> All steps used in all PlanGraph
	 */
	public ArrayList<PlanGraphStep> getAllSteps()
	{
		return _steps;
	}
	
	/**
	 * Adds a PlanGraphStep to PlanGraph.
	 * Does not add a step if already exist in PlanGraph.
	 * Only adds a step if current all preconditions of step exist in parent's effects.
	 * Also computes all new mutual exclusions introduced
	 * 
	 * @param PlanGraphStep Step to be added
	 */
	public void addStep(PlanGraphStep step)
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
				getPlanGraphLiteral(literal).SetInitialLevel(getLevel());
		}
		
		checkForInconsistentEffects();
		checkForInterference();
		checkForCompetingNeeds();
		checkForOpposites();
		checkForInconsistentSupport();
	}
	
	/**
	 * Helper method so that Step can be used instead of PlanGraphStep
	 * for addStep.
	 * 
	 * @param step Step to be added
	 * @return True if step can be added (whether is already exists or not) False otherwise.
	 */
	public void addStep(Step step)
	{
		addStep(getPlanGraphStep(step));
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
				if (literal == planGraphLiteral.getLiteral())
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
			PlanGraphStep planGraphStep = new PlanGraphStep(step);
			_steps.add(planGraphStep);
			_persistenceSteps.add(planGraphStep);
		}
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
				addStep(step);
	}
	
	/**
	 * Checks to see if there are inconsistent effects with newly added step.
	 * Inconsistent effect: One action negates an effect of the other.
	 */
	private void checkForInconsistentEffects() 
	{
		ArrayList<PlanGraphStep> currentSteps = getCurrentSteps();
		for (PlanGraphStep step : currentSteps)
		{
			for (PlanGraphStep otherStep : currentSteps)
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
		ArrayList<PlanGraphStep> currentSteps = getCurrentSteps();
		for (PlanGraphStep step : currentSteps)
		{
			for (PlanGraphStep otherStep : currentSteps)
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
		ArrayList<PlanGraphStep> currentSteps = getCurrentSteps();
		for (PlanGraphStep step : currentSteps)
		{
			for (PlanGraphStep otherStep : currentSteps)
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
					PlanGraphLiteral negatedEffect = getPlanGraphLiteral(effect.getLiteral().negate());
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
		ArrayList<PlanGraphStep> steps = getCurrentSteps();
		ArrayList<PlanGraphLiteral> effects = getCurrentLiterals();
		for (PlanGraphLiteral effect : effects)
		{
			for (PlanGraphLiteral otherEffect : effects)
			{
				if (effect != otherEffect)
				{
					ArrayList<PlanGraphStep> stepsWithEffect = new ArrayList<PlanGraphStep>();
					ArrayList<PlanGraphStep> stepsWithOtherEffect = new ArrayList<PlanGraphStep>();
					
					for (PlanGraphStep step : steps)
						if (expressionToLiterals(step.GetStep().effect).contains(effect))
							stepsWithEffect.add(step);
					
					for (PlanGraphStep step : steps)
						if (expressionToLiterals(step.GetStep().effect).contains(otherEffect))
							stepsWithOtherEffect.add(step);
					
					boolean allSupportingStepsAreMutex = true;
					for (PlanGraphStep step : stepsWithEffect)
					{
						for (PlanGraphStep otherStep : stepsWithOtherEffect)
							if (step != otherStep)
								if (!_mutexSteps.get(step).contains(otherStep))
								{
									allSupportingStepsAreMutex = false;
									break;
								}
						
						if (!allSupportingStepsAreMutex) break;
					}
					
					if (allSupportingStepsAreMutex)
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
	
	/**
	 * Helper function to get PlanGraphStep from step
	 * 
	 * @param step Step to get PlanGraphStep
	 * @return planGraphStep Corresponding PlanGraphStep
	 */
	private PlanGraphStep getPlanGraphStep(Step step)
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
	private PlanGraphLiteral getPlanGraphLiteral(Literal literal)
	{
		for (PlanGraphLiteral planGraphLiteral : _effects)
			if (planGraphLiteral.getLiteral() == literal)
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
		str += "Level " + getLevel() + "\n";
		
		str += "Steps [" + getCurrentSteps().size() + "]:\n";
		for (PlanGraphStep step : getCurrentSteps())
			str += step.toString() + "\n";
		
		str += "Effects [" + getCurrentLiterals().size() + "]:\n";
		for (PlanGraphLiteral literal : getCurrentLiterals())
			str += literal.toString() + "\n";
		
		return str;
	}
}