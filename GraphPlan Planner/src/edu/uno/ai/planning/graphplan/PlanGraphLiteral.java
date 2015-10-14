package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.List;

import edu.uno.ai.planning.logic.Literal;

/**
 * PlanGraphLiteral is a wrapper class that wraps a Literal
 * with integer of initial level Literal appeared.
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraphLiteral implements PlanGraphNode 
{
	/** The wrapped Literal **/
	private Literal _literal;
	
	/** The level the Literal first appeared in PlanGraph **/
	private int _initialLevel;
	
	private List<PlanGraphStep> _parents;
	private List<PlanGraphStep> _children;
	
	/**
	 * Creates a wrapped Literal with a set initialLevel
	 * 
	 * @param literal Literal to be wrapped
	 * @param initialLevel First level Literal appears in PlanGraph
	 */
	public PlanGraphLiteral(Literal literal, int initialLevel, List<PlanGraphStep> children, List<PlanGraphStep> parents)
	{
		_literal = literal;
		_initialLevel = initialLevel;
		_parents = parents;
		_children = children;
	}
	
	/**
	 * Creates a wrapped Literal with an initialLevel at -1
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param literal Literal to be wrapped
	 */
	public PlanGraphLiteral(Literal literal, List<PlanGraphStep> children, List<PlanGraphStep> parents)
	{
		this(literal, -1, children, parents);
	}
	
	public PlanGraphLiteral(Literal literal, int initialLevel){
		this(literal, initialLevel, new ArrayList<PlanGraphStep>(), new ArrayList<PlanGraphStep>());
	}
	
	public PlanGraphLiteral(Literal literal){
		this(literal, -1, new ArrayList<PlanGraphStep>(), new ArrayList<PlanGraphStep>());
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

	@Override
	/**
	 * @return initialLevel First level Literal appears in PlanGraph
	 */
	public int getInitialLevel()
	{
		return _initialLevel;
	}

	@Override
	/**
	 * Change/Set first level Literal appears in PlanGraph
	 * 
	 * @param initialLevel First level Literal appears in PlanGraph
	 */
	public void setInitialLevel(int initialLevel)
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
	
	protected void addParentStep(PlanGraphStep newStep){
		_parents.add(newStep);
	}
	
	protected void addChildStep(PlanGraphStep newStep){
		_children.add(newStep);
	}

	@Override
	public List<PlanGraphStep> getParentNodes() {
		return _parents;
	}

	@Override
	public List<PlanGraphStep> getChildNodes() {
		return _children;
	}
}