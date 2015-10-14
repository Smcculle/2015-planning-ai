package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Step;

/**
 * PlanGraphStep is a wrapper class that wraps a Step
 * with integer of initial level Step appeared.
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraphStep 
{
	/** The wrapped Step **/
	private Step _step;
	
	/** The level the Step first appeared in PlanGraph **/
	private int _initialLevel;
	
	private boolean _isPersistent;
	
	static public PlanGraphStep createPersistentStep(Step step)
	{
		PlanGraphStep persistentStep = new PlanGraphStep(step);
		persistentStep._isPersistent = true;
		return persistentStep;
	}
	
	 /**
	 * Creates a wrapped Step with a set initialLevel
	 * 
	 * @param step Step to be wrapped
	 * @param initialLevel First level Literal appears in PlanGraph
	 */
	public PlanGraphStep(Step step, int initialLevel)
	{
		_step = step;
		_initialLevel = initialLevel;
	}
	
	/**
	 * Creates a wrapped Step with an initialLevel at -1
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param step Step to be wrapped
	 */
	public PlanGraphStep(Step step)
	{
		_step = step;
		_initialLevel = -1;
	}
	
	/**
	 * @return initialLevel First level Step appears in PlanGraph
	 */
	public int getInitialLevel()
	{
		return _initialLevel;
	}
	
	public boolean isPersistent()
	{
		return _isPersistent;
	}
	
	public ArrayList<PlanGraphLiteral> getParents(int level)
	{
		ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
		// TODO Get all possible literals 
		return literals;
	}
	
	public ArrayList<PlanGraphLiteral> getChildren(int level)
	{
		ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
		// TODO Get all possible literals 
		return literals;
	}
	
	/**
	 * Change/Set first level Step appears in PlanGraph
	 * 
	 * @param initialLevel First level Step appears in PlanGraph
	 */
	public void SetInitialLevel(int levelNumber) 
	{
		_initialLevel = levelNumber;
	}
	
	/**
	 * @return step Wrapped Step
	 */
	public Step getStep()
	{
		return _step;
	}
	
	@Override
	public String toString()
	{
		String output = _step.toString();
		output += "[" + _initialLevel + "]";
		return output;
	}
}