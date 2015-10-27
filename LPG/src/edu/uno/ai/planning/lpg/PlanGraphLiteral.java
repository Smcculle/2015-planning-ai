package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.List;

import edu.uno.ai.planning.logic.Literal;

/**
 * PlanGraphLiteral is a wrapper class that wraps a Literal
 * with integer of initial level Literal appeared along with a list
 * of PlanGraphSteps that are its parents (the steps that cause this
 * PlanGraphLiteral as their effects) and a list of PlanGraphSteps that are its
 * children (the steps that require this PlanGraphLiteral as its
 * precondition) 
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
	
	/** 
	 * List of PlanGraphSteps wrapping the Steps that have this 
	 * PlanGraphLiteral as an effect
	 */
	private List<PlanGraphStep> _parents;
	
	/** 
	 * List of PlanGraphSteps wrapping the Steps that have this 
	 * PlanGraphLiteral as a precondition
	 */
	private List<PlanGraphStep> _children;
	
	/**
	 * Creates a wrapped Literal with a set initialLevel with the given children and parents
	 * 
	 * @param literal Literal to be wrapped
	 * @param initialLevel First level Literal appears in PlanGraph
	 * @param children Children Nodes of this PlanGraphLiteral within a PlanGraph
	 * @param parents Parent Nodes of this PlanGraphLiteral within a PlanGraph
	 */
	public PlanGraphLiteral(Literal literal, int initialLevel, List<PlanGraphStep> children, List<PlanGraphStep> parents)
	{
		_literal = literal;
		_initialLevel = initialLevel;
		_parents = new ArrayList<PlanGraphStep>(parents);
		_children = new ArrayList<PlanGraphStep>(children);
	}
	
	/**
	 * Creates a wrapped Literal with an initialLevel of -1 and the given children and parents
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param literal Literal to be wrapped
	 * @param children Children Nodes of this PlanGraphLiteral within a PlanGraph
	 * @param parents Parent Nodes of this PlanGraphLiteral within a PlanGraph
	 */
	public PlanGraphLiteral(Literal literal, List<PlanGraphStep> children, List<PlanGraphStep> parents)
	{
		this(literal, -1, children, parents);
	}

	/**
	 * Creates a wrapped Literal with a set initialLevel and empty lists for its children and parents nodes
	 * 
	 * @param literal Literal to be wrapped
	 * @param initialLevel First level Literal appears in PlanGraph
	 */
	public PlanGraphLiteral(Literal literal, int initialLevel){
		this(literal, initialLevel, new ArrayList<PlanGraphStep>(), new ArrayList<PlanGraphStep>());
	}

	/**
	 * Creates a wrapped Literal with an initialLevel of -1 and empty lists for its children and parents nodes
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param literal Literal to be wrapped
	 */
	public PlanGraphLiteral(Literal literal){
		this(literal, -1, new ArrayList<PlanGraphStep>(), new ArrayList<PlanGraphStep>());
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
	
	@Override
	/**
	 * Return whether the PlanGraphLiteral is a valid Node in the PlanGraph it belongs to
	 * 
	 * @param level
	 * @return getInitialLevel() > -1 && getInitialLevel() <= level
	 */
	public boolean existsAtLevel(int level)
	{
		boolean hasValidInitialLevel = _initialLevel > -1;
		boolean isUnderOrInLevel = _initialLevel <= level;
		return hasValidInitialLevel && isUnderOrInLevel;
	}

	@Override
	/**
	 * Return a copy of the list of PlanGraphStep nodes that wrap
	 * steps that have this PlanGraphLiteral's literal as an effect
	 * 
	 * @return for(PlanGraphStep step : getParentNodes())
	 * 				step.getStep().effects.contains(getLiteral())
	 */
	public List<PlanGraphStep> getParentNodes() {
		return new ArrayList<PlanGraphStep>(_parents);
	}

	@Override
	/**
	 * Return a copy of the list of PlanGraphStep nodes that wrap
	 * steps that have this PlanGraphLiteral's literal as a precondition
	 * 
	 * @return for(PlanGraphStep step : getChildNodes())
	 * 				step.getStep().preconditions.contains(getLiteral())
	 */
	public List<PlanGraphStep> getChildNodes() {
		return new ArrayList<PlanGraphStep>(_children);
	}
	
	/**
	 * Protected method to add a new PlanGraphStep to this PlanGraphLiteral's list of Parent Nodes
	 * 
	 * @param newStep new PlanGraphStep that is a parent of this PlanGraphLiteral in a PlanGraph
	 * @ensure getParentNodes().contains(newStep)
	 */
	protected void addParentStep(PlanGraphStep newStep){
		_parents.add(newStep);
	}

	/**
	 * Protected method to add a new PlanGraphStep to this PlanGraphLiteral's list of Children Nodes
	 * 
	 * @param newStep new PlanGraphStep that is a child of this PlanGraphLiteral in a PlanGraph
	 * @ensure getChildNodes().contains(newStep)
	 */
	protected void addChildStep(PlanGraphStep newStep){
		_children.add(newStep);
	}
	
	/**
	 * @return literal Wrapped Literal
	 */
	public Literal getLiteral()
	{
		return _literal;
	}
	
	/**
	 * Return whether or not two PlanGraphLiterals represent the same Literal
	 * 
	 * @param pgLiteral PlanGraphLiteral for comparison
	 * @return getLiteral.compareTo(pgLiteral.getLiteral()) == 0
	 */
	public boolean equals(PlanGraphLiteral pgLiteral){
		return pgLiteral != null && getLiteral().compareTo(pgLiteral.getLiteral()) == 0;  
	}
	
	@Override
	/**
	 * @return String representation of this PlanGraphLiteral  
	 */
	public String toString()
	{
		String output = _literal.toString();
		output += "[" + _initialLevel + "]";
		return output;
	}
}