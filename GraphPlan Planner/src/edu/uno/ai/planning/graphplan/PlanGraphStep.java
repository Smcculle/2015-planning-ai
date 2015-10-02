package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.Step;

public class PlanGraphStep 
{
	Step _step;
	 int _initialLevel;
	
	public PlanGraphStep(Step step, int initialLevel)
	{
		_step = step;
		_initialLevel = initialLevel;
	}
	
	public PlanGraphStep(Step step)
	{
		_step = step;
		_initialLevel = -1;
	}
	
	public int GetInitialLevel()
	{
		return _initialLevel;
	}
	
	public void SetInitialLevel(int levelNumber) 
	{
		_initialLevel = levelNumber;
	}
	
	public Step GetStep()
	{
		return _step;
	}
}