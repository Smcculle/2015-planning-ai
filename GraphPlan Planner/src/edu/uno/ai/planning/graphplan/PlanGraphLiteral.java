package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.logic.Literal;

public class PlanGraphLiteral 
{
	Literal _effect;
	 int _initialLevel;
	
	public PlanGraphLiteral(Literal effect, int initialLevel)
	{
		_effect = effect;
		_initialLevel = initialLevel;
	}
	
	public PlanGraphLiteral(Literal effect)
	{
		_effect = effect;
		_initialLevel = -1;
	}
	
	public int GetInitialLevel()
	{
		return _initialLevel;
	}
	
	public void SetInitialLevel(int level)
	{
		_initialLevel = level;
	}
	
	public Literal getEffectLiteral()
	{
		return _effect;
	}
}