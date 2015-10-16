package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.List;

import edu.uno.ai.planning.Step;

/**
 * PlanGraphStep is a wrapper class that wraps a Step
 * with integer of initial level Step appeared.
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraphStep implements PlanGraphNode
{
	
	/** The level the Step first appeared in PlanGraph **/
	private int _initialLevel;
	private boolean _isPersistent;

	private Step _step;
	private List<PlanGraphLiteral> _parents;
	private List<PlanGraphLiteral> _children;

	
	static public PlanGraphStep createPersistentStep(Step step)
	{
		PlanGraphStep persistentStep = new PlanGraphStep(step);
		persistentStep._isPersistent = true;
		return persistentStep;
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
		_parents = new ArrayList<PlanGraphLiteral>();
		_children = new ArrayList<PlanGraphLiteral>();
		_isPersistent = false;
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
		_parents = new ArrayList<PlanGraphLiteral>();
		_children = new ArrayList<PlanGraphLiteral>();
		_isPersistent = false;
	}
	
	/**
	 * Creates a wrapped Step with an initialLevel at -1
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param step Step to be wrapped
	 */
	public PlanGraphStep(Step step, int initialLevel, List<PlanGraphLiteral> parents, List<PlanGraphLiteral> children)
	{
		this(step, initialLevel);
		_initialLevel = initialLevel;
		_parents = new ArrayList<PlanGraphLiteral>(parents);
		_children = new ArrayList<PlanGraphLiteral>(children);
	}
	
	/**
	 * Creates a wrapped Step with an initialLevel at -1
	 * Note: -1 meaning no level has been yet set
	 * 
	 * @param step Step to be wrapped
	 */
	public PlanGraphStep(Step step, List<PlanGraphLiteral> parents, List<PlanGraphLiteral> children)
	{
		this(step, -1, parents, children);
	}
	
	public boolean isPersistent()
	{
		return _isPersistent;
	}

	@Override
	/**
	 * @return initialLevel First level Step appears in PlanGraph
	 */
	public int getInitialLevel()
	{
		return _initialLevel;
	}
	
	@Override
	/**
	 * Change/Set first level Step appears in PlanGraph
	 * 
	 * @param initialLevel First level Step appears in PlanGraph
	 */
	public void setInitialLevel(int levelNumber) 
	{
		_initialLevel = levelNumber;
	}
	
	public boolean existsAtLevel(int level){
		boolean hasValidInitialLevel = _initialLevel > -1;
		boolean isUnderOrInLevel = _initialLevel <= level;
		return hasValidInitialLevel && isUnderOrInLevel;
	}

	@Override
	public List<PlanGraphLiteral> getParentNodes() {
		return _parents;
	}

	@Override
	public List<PlanGraphLiteral> getChildNodes() {
		return _children;
	}

	protected void addChildLiteral(PlanGraphLiteral newLiteral){
		_children.add(newLiteral);
	}
	
	protected void addParentLiteral(PlanGraphLiteral newLiteral){
		_parents.add(newLiteral);
	}
	
	public Step getStep(){
		return _step;
	}
	
	public boolean equals(PlanGraphStep pgStep){
		return getStep().compareTo(pgStep.getStep()) == 0;  
	}

	@Override
	public String toString()
	{
		String output = _step.toString();
		output += "[" + _initialLevel + "]";
		return output;
	}

}