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
import edu.uno.ai.planning.logic.NegatedLiteral;

/**
 * A PlanGraph Structure is
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraph 
{
	/** List of all steps in current PlanGraph Level */
	ArrayList<Step> _currentSteps = new ArrayList<Step>();
	
	/** List of all mutually exclusive steps in current PlanGraph Level */
	Map<Step, ArrayList<Step>> _mutexSteps = new Hashtable<Step, ArrayList<Step>>();
	
	/** List of all mutually exclusive literals in current PlanGraph Level */
	Map<Literal, ArrayList<Literal>> _mutexLiterals = new Hashtable<Literal, ArrayList<Literal>>();
	
	/** PlanGraph's Parent */
	PlanGraph _parent;
	
	/**
	 * Constructs a new root PlanGraph
	 * 
	 * @param initialState The initial state of the problem
	 */
	public PlanGraph (State initialState)
	{
		_parent = null;
		Step step = new Step("Initial State", initialState.toExpression(), initialState.toExpression());
		_currentSteps.add(step);
	}
	
	/**
	 * Constructs a new PlanGraph child
	 * 
	 * @param parent The parent of new PlanGraph
	 */
	public PlanGraph (PlanGraph parent)
	{
		_parent = parent;
		ArrayList<Literal> parentEffectLiterals = parent.GetEffectLiterals();
		for (Literal literal : parentEffectLiterals)
			_currentSteps.add(new Step("Persistence Step", literal, literal));
	}
	
	/**
	 * Returns the PlanGraph's level number. Level number starts at
	 * 0 for the root node.
	 * 
	 * @return integer Level number
	 */
	public int GetLevelNumber()
	{
		if (_parent == null)
			return 0;
		
		return _parent.GetLevelNumber() + 1;
	}
	
	/**
	 * Returns the PlanGraph's parent
	 * 	
	 * @return PlanGraph The Parent of PlanGraph
	 */
	public PlanGraph GetParent()
	{
		return _parent;
	}
	
	/**
	 * Returns an ArrayList<Literal> of all effect Literals
	 *  	
	 * @return effectLiterals The list of all effect literals in PlanGraph 
	 */
	public ArrayList<Literal> GetEffectLiterals()
	{
		ArrayList<Literal> effectLiterals = new ArrayList<Literal>();		
		for (Step step : _currentSteps)
			for (Literal effectLiteral : GetLiterals(step.effect))
				if (!effectLiterals.contains(effectLiteral))
					effectLiterals.add(effectLiteral);
		return effectLiterals;
	}
	
	/**
	 * Returns all Steps in the current PlanGraph
	 * 	
	 * @return ArrayList<Literal> All steps used in current PlanGraph
	 */
	public ArrayList<Step> GetCurrentSteps()
	{
		return _currentSteps;
	}
	
	/**
	 * Returns all Steps in all PlanGraphs.
	 * In other words, get all steps from the root PlanGraph to current
	 * PlanGraph.
	 * 	
	 * @return ArrayList<Literal> All steps used in all PlanGraph
	 */
	public ArrayList<Step> GetAllSteps()
	{
		ArrayList<Step> allSteps = new ArrayList<Step>();
		allSteps.addAll(_currentSteps);
		
		if (_parent != null)
			allSteps.addAll(_parent.GetAllSteps());
		
		return allSteps;
	}
	
	/**
	 * Adds a step to PlanGraph.
	 * Does not add a step if already exist in PlanGraph.
	 * Only adds a step if current all preconditions of step exist in parent's effects.
	 * 
	 * @param step
	 */
	public void AddStep(Step step)
	{		
		int previousCurrentStepsSize = _currentSteps.size();
		if (_parent != null)
		{			
			ArrayList<Literal> existingLiterals = _parent.GetEffectLiterals();
			ArrayList<Literal> preconditionLiterals = GetLiterals(step.precondition);
			
			// Add implied NOT fact if not explicitly stated as Literal
			for (Expression precondition : preconditionLiterals)
				if (precondition instanceof NegatedLiteral)
					if(!existingLiterals.contains(precondition))
						if(!existingLiterals.contains(precondition.negate()))
							existingLiterals.add((NegatedLiteral)precondition);
			
			if (existingLiterals.containsAll(preconditionLiterals))
				_currentSteps.add(step);
		}
		else
			_currentSteps.add(step);
		
		if (_currentSteps.size() != previousCurrentStepsSize)
		{
			CheckForInconsistentEffects();
			CheckForInterference();
			CheckForCompetingNeeds();
			CheckForOpposites();
			CheckForInconsistentSupport();
		}
	}
	
	/**
	 * Returns a Map of Mutually Exclusive Steps.
	 * 
	 * @return Map<Step, ArrayList<Step>> Mutually Exclusive Steps.
	 */
	public Map<Step, ArrayList<Step>> GetMutuallyExclusiveSteps()
	{
		return _mutexSteps;
	}
	
	/**
	 * Returns a Map of Mutually Exclusive Literals.
	 * 
	 * @return Map<Literal, ArrayList<Literal>> Mutually Exclusive Literals.
	 */
	public Map<Literal, ArrayList<Literal>> GetMutuallyExclusiveLiterals()
	{
		return _mutexLiterals;
	}
	
	/**
	 * Checks to see if there are inconsistent effects with newly added step.
	 * Inconsistent effect: One action negates an effect of the other.
	 */
	private void CheckForInconsistentEffects() 
	{
		for (Step step : _currentSteps)
		{
			for (Step otherStep : _currentSteps)
			{
				if (step != otherStep)
				{
					ArrayList<Literal> stepEffectLiterals = GetLiterals(step.effect);
					ArrayList<Literal> otherStepEffectLiterals = GetLiterals(otherStep.effect);
					for (Expression literal : stepEffectLiterals)
					{
						Expression negatedLiteral = literal.negate();
						if (otherStepEffectLiterals.contains(negatedLiteral))
						{
							if (_mutexSteps.containsKey(step))
							{
								ArrayList<Step> steps = _mutexSteps.get(step);
								if (!steps.contains(otherStep))
									steps.add(otherStep);
							}
							else
							{
								ArrayList<Step> steps = new ArrayList<Step>();
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
	private void CheckForInterference() 
	{
		for (Step step : _currentSteps)
		{
			for (Step otherStep : _currentSteps)
			{
				if (step != otherStep)
				{
					ArrayList<Literal> stepEffectLiterals = GetLiterals(step.effect);
					ArrayList<Literal> otherStepPreconditionLiterals = GetLiterals(otherStep.precondition);
					for (Expression literal : stepEffectLiterals)
					{
						Expression negatedLiteral = literal.negate();
						if (otherStepPreconditionLiterals.contains(negatedLiteral))
						{
							if (_mutexSteps.containsKey(step))
							{
								ArrayList<Step> steps = _mutexSteps.get(step);
								if (!steps.contains(otherStep))
									steps.add(otherStep);
							}
							else
							{
								ArrayList<Step> steps = new ArrayList<Step>();
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
	private void CheckForCompetingNeeds() 
	{
		for (Step step : _currentSteps)
		{
			for (Step otherStep : _currentSteps)
			{
				if (step != otherStep)
				{
					ArrayList<Literal> stepPreconditionLiterals = GetLiterals(step.precondition);
					ArrayList<Literal> otherStepPreconditionLiterals = GetLiterals(otherStep.precondition);
					for (Expression literal : stepPreconditionLiterals)
					{
						Expression negatedLiteral = literal.negate();
						if (otherStepPreconditionLiterals.contains(negatedLiteral))
						{
							if (_mutexSteps.containsKey(step))
							{
								ArrayList<Step> steps = _mutexSteps.get(step);
								if (!steps.contains(otherStep))
									steps.add(otherStep);
							}
							else
							{
								ArrayList<Step> steps = new ArrayList<Step>();
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
	private void CheckForOpposites() 
	{
		ArrayList<Literal> effects = GetEffectLiterals();
		for (Literal effect : effects)
		{
			for (Literal otherEffect : effects)
			{
				if (effect != otherEffect)
				{
					Literal negatedEffect = effect.negate();
					if (negatedEffect == otherEffect)
					{
						if (_mutexLiterals.containsKey(effect))
						{
							ArrayList<Literal> literals = _mutexLiterals.get(effect);
							if (!literals.contains(otherEffect))
								literals.add(otherEffect);
						}
						else
						{
							ArrayList<Literal> literals = new ArrayList<Literal>();
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
	private void CheckForInconsistentSupport() 
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Helper function to get all the literals from an Expression
	 * 
	 * @param expression The Expression to convert to list
	 * @return ArrayList<Literal> List of literals in expression
	 */
	private ArrayList<Literal> GetLiterals(Expression expression)
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
		
		str += "---------------\n";
		str += "Level " + GetLevelNumber() + "\n";
		str += "Steps [" + _currentSteps.size() + "]:\n";
		for (Step step : _currentSteps)
			str += step.toString() + "\n";
		str += "Effects [" + GetEffectLiterals().size() + "]:\n";
		for (Expression literal : GetEffectLiterals())
			str += literal.toString() + "\n";
		
		return str;
	}
}