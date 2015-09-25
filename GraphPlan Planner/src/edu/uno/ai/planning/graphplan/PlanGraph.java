package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.logic.Conjunction;
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
public class PlanGraph {
	
	private final ArrayList<Step> _currentSteps = new ArrayList<Step>();
	
	private PlanGraph _parent;
	
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
	 * Returns an ArrayList<Expression> of all effect Literals
	 *  	
	 * @return effectLiterals The list of all effect literals in PlanGraph 
	 */
	public ArrayList<Expression> GetEffectLiterals()
	{
		ArrayList<Expression> effectLiterals = new ArrayList<Expression>();
		
		if (_parent != null)
			effectLiterals.addAll(_parent.GetEffectLiterals());
		
		for (Step step : _currentSteps)
			for (Expression effectLiteral : GetLiterals(step.effect))
				if (!effectLiterals.contains(effectLiteral))
					effectLiterals.add(effectLiteral);
		
		return effectLiterals;
	}
	
	/**
	 * Returns all Steps in the current PlanGraph
	 * 	
	 * @return ArrayList<Expression> All steps used in current PlanGraph
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
	 * @return ArrayList<Expression> All steps used in all PlanGraph
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
		for (Step existingStep : GetAllSteps())
			if (existingStep.equals(step))
				return;
		
		if (_parent != null)
		{			
			ArrayList<Expression> existingLiterals = _parent.GetEffectLiterals();
			ArrayList<Expression> preconditionLiterals = GetLiterals(step.precondition);
			
			for (Expression precondition : preconditionLiterals)
				if (precondition instanceof NegatedLiteral)
					if(!existingLiterals.contains(precondition))
						if(!existingLiterals.contains(precondition.negate()))
							existingLiterals.add(precondition);
			
			if (existingLiterals.containsAll(preconditionLiterals))
				_currentSteps.add(step);
		}
		else
			_currentSteps.add(step);
	}
	
	/**
	 * Return an array of steps which will be the plan to get to the goal.
	 * 
	 * @param goal The Expression that represents the goal
	 * @return ArrayList<Step> List of steps to get to goal
	 */
	public ArrayList<Step> GetPlan(Expression goal)
	{		
		ArrayList<Step> plan = new ArrayList<Step>();
		ArrayList<Expression> goalLiterals = GetLiterals(goal);
		return GetPlan(goalLiterals, plan);
	}
	
	/**
	 * Recursive function that will return the lists of steps to arrive at goal
	 * 	
	 * @param goalLiterals The list of literals that need to be obtained
	 * @param plan The plan as it is being created
	 * @return The final plan with all steps leading to the goal
	 */
	private ArrayList<Step> GetPlan(ArrayList<Expression> goalLiterals, ArrayList<Step> plan)
	{		
		if (_parent == null)
			return plan;
		
		ArrayList<Step> currentPlan = new ArrayList<Step>();
		for (Step step : _currentSteps)
		{
			ArrayList<Expression> effectLiterals = GetLiterals(step.effect);
			boolean stepAlreadyAdded = false;
			for (int i = goalLiterals.size() - 1; i >= 0; i--)
			{
				Expression goalLiteral = goalLiterals.get(i);
				if (effectLiterals.contains(goalLiteral))
				{
					if (!stepAlreadyAdded && !plan.contains(step))
						currentPlan.add(step);
					
					goalLiterals.remove(i);
					
					stepAlreadyAdded = true;
				}
			}
			
			if (goalLiterals.size() <= 0)
				break;
		}
		
		for (Step step : currentPlan)
			for (Expression preconditionLiteral : GetLiterals(step.precondition))
				if (!goalLiterals.contains(preconditionLiteral))
					goalLiterals.add(preconditionLiteral);
		
		plan.addAll(currentPlan);
		return _parent.GetPlan(goalLiterals, plan);
	}

	/**
	 * Helper function to get all the literals from an Expression
	 * 
	 * @param expression The Expression to convert to list
	 * @return ArrayList<Expression> List of literals in expression
	 */
	private ArrayList<Expression> GetLiterals(Expression expression)
	{
		ArrayList<Expression> literals = new ArrayList<Expression>();
		if (expression instanceof Literal)
			literals.add(expression);
		else
		{
			for (Expression conjunct : ((Conjunction) expression).arguments)
				literals.add(conjunct);
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
