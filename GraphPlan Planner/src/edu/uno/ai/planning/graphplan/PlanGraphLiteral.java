package edu.uno.ai.planning.graphplan;

import edu.uno.ai.planning.logic.Literal;

/**
 * PlanGraphLiteral is a wrapper class that wraps a Literal
 * with integer of initial level Literal appeared.
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraphLiteral 
{
	/** The wrapped Literal **/
	Literal _literal;
	
	/** The level the Literal first appeared in PlanGraph **/
	int _initialLevel;
	
	/**
	 * Creates a wrapped Literal with a set initialLevel
	 * 
	 * @param literal Literal to be wrapped
	 * @param initialLevel First level Literal appears in PlanGraph
	 */
	public PlanGraphLiteral(Literal literal, int initialLevel)
	{
		_literal = literal;
		_initialLevel = initialLevel;
	}
	
	/**
	 * Creates a wrapped Literal with an initialLevel at -1
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param literal Literal to be wrapped
	 */
	public PlanGraphLiteral(Literal literal)
	{
		_literal = literal;
		_initialLevel = -1;
	}
	
	/**
	 * @return initialLevel First level Literal appears in PlanGraph
	 */
	public int GetInitialLevel()
	{
		return _initialLevel;
	}
	
	/**
	 * Change/Set first level Literal appears in PlanGraph
	 * 
	 * @param initialLevel First level Literal appears in PlanGraph
	 */
	public void SetInitialLevel(int initialLevel)
	{
		_initialLevel = initialLevel;
	}
	
	/**
	 * @return literal Wrapped Literal
	 */
	public Literal getLiteral()
	{
		return _literal;
	}
	
	@Override
	public String toString()
	{
		String output = _literal.toString();
		output += "[" + _initialLevel + "]";
		return output;
	}
}