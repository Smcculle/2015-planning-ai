package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

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
	private Literal _literal;
	
	/** The level the Literal first appeared in PlanGraph **/
	private int _initialLevel;
	
	private PlanGraph _planGraph;
	
	/**
	 * Creates a wrapped Literal with a set initialLevel
	 * 
	 * @param literal Literal to be wrapped
	 * @param initialLevel First level Literal appears in PlanGraph
	 */
	public PlanGraphLiteral(Literal literal, int initialLevel, PlanGraph planGraph)
	{
		_literal = literal;
		_initialLevel = initialLevel;
		_planGraph = planGraph;
	}
	
	/**
	 * Creates a wrapped Literal with an initialLevel at -1
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param literal Literal to be wrapped
	 */
	public PlanGraphLiteral(Literal literal, PlanGraph planGraph)
	{
		this(literal, -1, planGraph);
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
	
	public ArrayList<PlanGraphStep> getParents(int level)
	{
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		// TODO Get all possible steps 
		return steps;
	}
	
	public ArrayList<PlanGraphStep> getChildren(int level)
	{
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		// TODO Get all possible steps 
		return steps;
	}
	
	public boolean equals(PlanGraphLiteral pgLiteral){
		return getLiteral().compareTo(pgLiteral.getLiteral()) == 0;  
	}
	
	@Override
	public String toString()
	{
		String output = _literal.toString();
		output += "[" + _initialLevel + "]";
		return output;
	}
}