package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;

public class PlanGraphLevelMutex extends PlanGraphLevel
{
	/** List of all mutually exclusive steps in current PlanGraph Level */
	Map<PlanGraphStep, ArrayList<PlanGraphStep>> _mutexSteps;
	
	/** List of all mutually exclusive literals in current PlanGraph Level */
	Map<PlanGraphLiteral, ArrayList<PlanGraphLiteral>> _mutexLiterals;
	
	private PlanGraphLevelMutex _parent;
		
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
		checkForOpposites();
	}
	
	/**
	 * Constructs a new PlanGraph child
	 * 
	 * @param parent The parent of new PlanGraph
	 */
	public PlanGraphLevelMutex (PlanGraphLevelMutex parent)
	{
		super(parent);
		_parent = parent;
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
		if (step == null || otherStep == null)
			return false;
		
		if (_mutexSteps.containsKey(step))
			return _mutexSteps.get(step).contains(otherStep);
		
		return false;
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
		PlanGraphStep pgStep = _planGraph.getPlanGraphStep(step);
		PlanGraphStep phOtherStep = _planGraph.getPlanGraphStep(otherStep);
		return isMutex(pgStep, phOtherStep);
	}
	
	@Override
	public boolean containsGoal(Expression goal)
	{
		if (!super.containsGoal(goal))
			return false;
		
		ArrayList<Literal> literals = PlanGraph.expressionToLiterals(goal);
		for (Literal literal : literals)
		{
			PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
			for (Literal otherLiteral : literals)
			{
				PlanGraphLiteral pgOtherLiteral = _planGraph.getPlanGraphLiteral(otherLiteral);
				if (!pgLiteral.equals(pgOtherLiteral))
					if (_mutexLiterals.containsKey(pgLiteral))
						if (_mutexLiterals.get(pgLiteral).contains(pgOtherLiteral))
							return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isLeveledOff()
	{
		if (_parent == null)
			return false;
		
		if (_parent.countCurrentEffects() == countCurrentEffects())
			if (_parent.countCurrentSteps() == countCurrentSteps())
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
	 * Checks to see if there are inconsistent effects with newly added step.
	 * Inconsistent effect: One action negates an effect of the other.
	 */
	private void checkForInconsistentEffects() 
	{
		for (PlanGraphStep step : _steps)
		{
			if (exists(step))
			{
			for (PlanGraphStep otherStep : _steps)
			{
				if (step != otherStep)
				{
					if (exists(otherStep))
					{
//						ArrayList<Literal> stepEffectLiterals = PlanGraph.expressionToLiterals(step.getStep().effect);
//						ArrayList<Literal> otherStepEffectLiterals = PlanGraph.expressionToLiterals(otherStep.getStep().effect);
						ArrayList<Literal> stepEffectLiterals = PlanGraph.expressionToLiterals(step.effect);
						ArrayList<Literal> otherStepEffectLiterals = PlanGraph.expressionToLiterals(otherStep.effect);
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
			if (exists(step))
			{
				for (PlanGraphStep otherStep : _steps)
				{
					if (step != otherStep)
					{
						if (exists(otherStep))
						{
//							ArrayList<Literal> stepEffectLiterals = PlanGraph.expressionToLiterals(step.getStep().effect);
//							ArrayList<Literal> otherStepPreconditionLiterals = PlanGraph.expressionToLiterals(otherStep.getStep().precondition);
							ArrayList<Literal> stepEffectLiterals = PlanGraph.expressionToLiterals(step.effect);
							ArrayList<Literal> otherStepPreconditionLiterals = PlanGraph.expressionToLiterals(otherStep.precondition);
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
			if (exists(step))
			{
				for (PlanGraphStep otherStep : _steps)
				{
					if (step != otherStep)
					{
						if (exists(otherStep))
						{
//							ArrayList<Literal> stepPreconditionLiterals = PlanGraph.expressionToLiterals(step.getStep().precondition);
//							ArrayList<Literal> otherStepPreconditionLiterals = PlanGraph.expressionToLiterals(otherStep.getStep().precondition);
							ArrayList<Literal> stepPreconditionLiterals = PlanGraph.expressionToLiterals(step.precondition);
							ArrayList<Literal> otherStepPreconditionLiterals = PlanGraph.expressionToLiterals(otherStep.precondition);
							for (Expression literal : stepPreconditionLiterals)
							{
								if (_parent._mutexLiterals.containsKey(literal))
								{
									boolean isLiteralMutexWithOtherLiteral = false;
									for(Literal otherLiteral : otherStepPreconditionLiterals)
										if (_parent._mutexLiterals.get(literal).contains(_planGraph.getPlanGraphLiteral(otherLiteral)))
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
		}
	}
	
	/**
	 * Checks to see if newly added effects contain opposites.
	 * Opposites: One literal is a negation of another.
	 */
	private void checkForOpposites() 
	{
		for (PlanGraphLiteral effect : _effects)
		{
			if(exists(effect))
			{
				for (PlanGraphLiteral otherEffect : _effects)
				{
					if (!effect.equals(otherEffect))
					{
						if (exists(otherEffect))
						{
							PlanGraphLiteral negatedEffect = _planGraph.getPlanGraphLiteral(effect.getLiteral().negate());
							if (negatedEffect.equals(otherEffect))
								addMutexLiteral(effect, otherEffect);
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
		for (PlanGraphLiteral effect : _effects)
		{
			if (exists(effect))
			{
				for (PlanGraphLiteral otherEffect : _effects)
				{
					if (effect != otherEffect)
					{
						if (exists(otherEffect))
						{
							ArrayList<PlanGraphStep> stepsWithEffect = new ArrayList<PlanGraphStep>();
							ArrayList<PlanGraphStep> stepsWithOtherEffect = new ArrayList<PlanGraphStep>();
							
							// Get steps containing effect
							for (PlanGraphStep step : _steps)
								if (exists(step))
//									if (PlanGraph.expressionToLiterals(step.getStep().effect).contains(effect.getLiteral()))
									if (PlanGraph.expressionToLiterals(step.effect).contains(effect.getLiteral()))
										stepsWithEffect.add(step);
							
							// Get steps containing otherEffect
							for (PlanGraphStep step : _steps)
								if (exists(step))
//									if (PlanGraph.expressionToLiterals(step.getStep().effect).contains(otherEffect.getLiteral()))
									if (PlanGraph.expressionToLiterals(step.effect).contains(otherEffect.getLiteral()))
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
								if (!allSupportingStepsAreMutex)
									break;
							}
							
							if (allSupportingStepsAreMutex)
								addMutexLiteral(effect, otherEffect);
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
	 * The String representation of the current PlanGraph
	 * 
	 * @return string String representation of the current PlanGraph
	 */
	@Override
	public String toString()
	{
		String str = super.toString();
		
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
}
