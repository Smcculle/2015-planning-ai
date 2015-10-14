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

public class PlanGraphLevelMutex extends PlanGraphLevel
{
	/** List of all mutually exclusive steps in current PlanGraph Level */
	Map<PlanGraphStep, ArrayList<PlanGraphStep>> _mutexSteps;
	
	/** List of all mutually exclusive literals in current PlanGraph Level */
	Map<PlanGraphLiteral, ArrayList<PlanGraphLiteral>> _mutexLiterals;
		
	/**
	 * Constructs a new root of PlanGraph
	 * 
	 * @param problem The StateSpaceProblem to setup PlanGraph Steps and Effects
	 */
	public PlanGraphLevelMutex (Problem problem, ArrayList<PlanGraphStep> steps, ArrayList<PlanGraphLiteral> effects,
			ArrayList<PlanGraphStep> persistenceSteps, PlanGraph planGraph)
	{
		super(problem, steps, effects, persistenceSteps, planGraph);
		_mutexSteps = new Hashtable<PlanGraphStep, ArrayList<PlanGraphStep>>();
		_mutexLiterals = new Hashtable<PlanGraphLiteral, ArrayList<PlanGraphLiteral>>();
	}
	
	/**
	 * Constructs a new PlanGraph child
	 * 
	 * @param parent The parent of new PlanGraph
	 */
	public PlanGraphLevelMutex (PlanGraphLevelMutex parent)
	{
		super(parent);
		_mutexSteps = new Hashtable<PlanGraphStep, ArrayList<PlanGraphStep>>();
		_mutexLiterals = new Hashtable<PlanGraphLiteral, ArrayList<PlanGraphLiteral>>();	
		checkForInconsistentEffects();
		checkForInterference();
		checkForCompetingNeeds();
		checkForOpposites();
		checkForInconsistentSupport();
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
	 * Returns whether otherStep is a mutex action of step
	 * 	Will return false if either step or otherStep is null
	 * @param step
	 * @param otherStep
	 * @return
	 */
	public boolean isMutex(PlanGraphStep step, PlanGraphStep otherStep)
	{
		if(step == null || otherStep == null)
			return false;
		return _mutexSteps.get(step).contains(otherStep);
	}

	/**
	 * Returns whether otherStep is a mutex action of step
	 * 	Will return false if either step or otherStep is null
	 * @param step
	 * @param otherStep
	 * @return
	 */
	public boolean isMutex(Step step, Step otherStep)
	{
		if(step == null || otherStep == null)
			return false;
		PlanGraphStep pgStep = getPlanGraphStep(step);
		PlanGraphStep phOtherStep = getPlanGraphStep(otherStep);
		return isMutex(pgStep, phOtherStep);
	}
	
	@Override
	public boolean containsGoal(Expression goal)
	{
		if (!super.containsGoal(goal))
			return false;
		
		ArrayList<Literal> literals = expressionToLiterals(goal);
		for (Literal literal : literals)
		{
			PlanGraphLiteral pgLiteral = getPlanGraphLiteral(literal);
			for (Literal otherLiteral : literals)
			{
				PlanGraphLiteral pgOtherLiteral = getPlanGraphLiteral(otherLiteral);
				if (!pgLiteral.equals(pgOtherLiteral))
					if (_mutexLiterals.containsKey(pgLiteral))
						if (_mutexLiterals.get(pgLiteral).contains(pgOtherLiteral))
							return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Attempts to return all PlanGraphSteps of the current level of PlanGraph that fulfill a given goal
	 * If the current PlanGraph cannot solve the goal
	 * 		return null
	 * else
	 * 		List<PlanGraphStep> goalActions will be returned of the actions that have effects that match the goal literals
	 * @param goal
	 * @return
	 */
	public List<PlanGraphStep> getSolvingActions(Expression goal){
		if(!containsGoal(goal))
			return null;
		else{
			ArrayList<Literal> goalLiterals = expressionToLiterals(goal);
			ArrayList<PlanGraphStep> currentSteps = getCurrentSteps();
			ArrayList<PlanGraphStep> solvingActions = new ArrayList<PlanGraphStep>();
			for(PlanGraphStep step : currentSteps){
				List<Literal> stepEffects = expressionToLiterals(step.GetStep().effect);
				for(Literal goalLiteral : goalLiterals){
					if(stepEffects.contains(goalLiteral)){
						solvingActions.add(step);
						break;
					}
				}
			}
			return solvingActions;
		}
	}
	
	public boolean isLeveledOff()
	{
		if (_parent == null)
			return false;
		
		if (_parent.currentEffectCount() == currentEffectCount())
			if (_parent.currentStepCount() == currentStepCount())
				if (_parent._mutexLiterals.keySet().size() == _mutexLiterals.keySet().size())
					if (_parent._mutexSteps.keySet().size() == _mutexSteps.keySet().size())
					{
						int parentSize = 0; int size = 0;
						for (PlanGraphStep key : _parent._mutexSteps.keySet())
							parentSize += _parent._mutexSteps.get(key).size();
						for (PlanGraphStep key : _mutexSteps.keySet())
							size += _mutexSteps.get(key).size();
						if (parentSize != size)
							return false;
						
						parentSize = 0; size = 0;
						for (PlanGraphLiteral key : _parent._mutexLiterals.keySet())
							parentSize += _parent._mutexLiterals.get(key).size();
						for (PlanGraphLiteral key : _mutexLiterals.keySet())
							size += _mutexLiterals.get(key).size();
						if (parentSize != size)
							return false;
						
						return true;
					}
		
		return false;
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
				updateStep(step);
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
						if (otherStepEffectLiterals.contains(negatedLiteral) && !stepEffectLiterals.contains(negatedLiteral))
						{
							addMutexStep(step,otherStep);
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
							addMutexStep(step, otherStep);
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
						if (_parent._mutexLiterals.containsKey(literal))
						{
							boolean isLiteralMutexWithOtherLiteral = false;
							for(Literal otherLiteral : otherStepPreconditionLiterals)
								if (_parent._mutexLiterals.get(literal).contains(getPlanGraphLiteral(otherLiteral)))
								{
									isLiteralMutexWithOtherLiteral = true;
									break;
								}
									
							if (isLiteralMutexWithOtherLiteral)
							{
								addMutexStep(step, otherStep);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Helper method to add Mutex Steps to PlanGraph Level
	 * If step was already in Mutex Steps and otherStep was not in list of steps
	 * 		Add otherStep to list of Mutex Steps for step
	 * Else
	 * 		Add step to Mutex Step list with otherStep as its only Mutex Step 
	 * 
	 * @param step
	 * @param otherStep
	 */
	private void addMutexStep(PlanGraphStep step, PlanGraphStep otherStep){
		if (_mutexSteps.containsKey(step))
		{
			ArrayList<PlanGraphStep> steps = _mutexSteps.get(step);
			if (!steps.contains(otherStep))
			{
				steps.add(otherStep);
				addMutexStep(otherStep, step);
			}
		}
		else
		{
			ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
			steps.add(otherStep);
			_mutexSteps.put(step, steps);
			addMutexStep(otherStep, step);
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
				if (!effect.equals(otherEffect))
				{
					PlanGraphLiteral negatedEffect = getPlanGraphLiteral(effect.getLiteral().negate());
					if (negatedEffect.equals(otherEffect))
					{
						addMutexLiteral(effect, otherEffect);
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
						// Get steps containing effect
						if (expressionToLiterals(step.GetStep().effect).contains(effect.getLiteral()))
							stepsWithEffect.add(step);
					
					for (PlanGraphStep step : steps)
						// Get steps containing otherEffect
						if (expressionToLiterals(step.GetStep().effect).contains(otherEffect.getLiteral()))
							stepsWithOtherEffect.add(step);
					
					boolean allSupportingStepsAreMutex = true;
					for (PlanGraphStep step : stepsWithEffect)
					{
						// If Step with Effects is not in list of Mutex Steps, this pair of Steps cannot be mutually exclusive
						if(!_mutexSteps.containsKey(step))
						{
							allSupportingStepsAreMutex = false;
							break;
						} 
						else
						{
							for (PlanGraphStep otherStep : stepsWithOtherEffect)
							{
								if (step != otherStep)
								{
									if (!_mutexSteps.get(step).contains(otherStep))
									{
										allSupportingStepsAreMutex = false;
										break;
									}
								}
								else
								{
									allSupportingStepsAreMutex = false;
									break;
								}
							}
						}
						if (!allSupportingStepsAreMutex) break;
					}
					
					if (allSupportingStepsAreMutex)
					{
						addMutexLiteral(effect, otherEffect);
					}
				}
			}
		}
	}

	
	private void addMutexLiteral(PlanGraphLiteral effect, PlanGraphLiteral otherEffect){
		if (_mutexLiterals.containsKey(effect))
		{
			ArrayList<PlanGraphLiteral> literals = _mutexLiterals.get(effect);
			if (!literals.contains(otherEffect))
			{
				literals.add(otherEffect);
				addMutexLiteral(otherEffect, effect);
			}
		}
		else
		{
			ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
			literals.add(otherEffect);
			_mutexLiterals.put(effect, literals);
			addMutexLiteral(otherEffect, effect);
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
		
		str += "--------------------------------\n";
		str += "PlanGraph Level " + getLevel() + "\n";
		str += "--------------------------------\n";
		
		str += "Steps [" + getCurrentSteps().size() + "]:\n";
		for (PlanGraphStep step : getCurrentSteps())
			str += "-" + step.toString() + "\n";
		
		str += "Effects [" + getCurrentLiterals().size() + "]:\n";
		for (PlanGraphLiteral literal : getCurrentLiterals())
			str += "-" + literal.toString() + "\n";
		
		str += "Mutex Steps [" + _mutexSteps.size() + "]:\n";
		for (PlanGraphStep step : _mutexSteps.keySet())
		{
			str += "-" + step.toString() + "\n";
			for (PlanGraphStep mutexLiteral : _mutexSteps.get(step))
				str += "    -" + mutexLiteral.toString() + "\n";
		}
		
		str += "Mutex Literals [" + _mutexLiterals.size() + "]:\n";
		for (PlanGraphLiteral literal : _mutexLiterals.keySet())
		{
			str += "-" + literal.toString() + "\n";
			for (PlanGraphLiteral mutexLiteral : _mutexLiterals.get(literal))
				str += "    -" + mutexLiteral.toString() + "\n";
		}
		return str;
	}

	public boolean exists(PlanGraphStep pgLiteral) 
	{
		return false;
	}

	public boolean exists(PlanGraphLiteral pgLiteral) 
	{		
		return false;
	}
}
