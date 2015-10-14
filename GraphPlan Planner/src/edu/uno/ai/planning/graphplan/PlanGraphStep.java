package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.List;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.util.ConversionUtil;

/**
 * PlanGraphStep is a wrapper class that wraps a Step
 * with integer of initial level Step appeared.
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
public class PlanGraphStep extends Step implements PlanGraphNode
{
	
	/** The level the Step first appeared in PlanGraph **/
	private int _initialLevel;
	private boolean _isPersistent;

	private List<PlanGraphLiteral> _parents;
	private List<PlanGraphLiteral> _children;

	public PlanGraphStep(String name, Expression precondition, Expression effect, int initialLevel)
	{
		super(name, precondition, effect);
		// Get PlanGraphLiterals Parent Nodes Preconditions 
		List<Literal> preconditionLiterals = ConversionUtil.expressionToLiterals(precondition);
		_parents = new ArrayList<PlanGraphLiteral>();
		for(Literal preConLiteral : preconditionLiterals){
			PlanGraphLiteral preconPGLit = new PlanGraphLiteral(preConLiteral);
			_parents.add(preconPGLit);
		}
		// Get PlanGraphLiterals Parent Nodes Effects
		List<Literal> effectLiterals = ConversionUtil.expressionToLiterals(effect);
		_children = new ArrayList<PlanGraphLiteral>();
		for(Literal effectLiteral : effectLiterals){
			PlanGraphLiteral effectPGLit = new PlanGraphLiteral(effectLiteral);
			_parents.add(effectPGLit);
		}
		_initialLevel = initialLevel;
		_isPersistent = false;
	}
	
	
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
		super(step.name, step.precondition, step.effect);
		// Get PlanGraphLiterals Parent Nodes Preconditions 
		List<Literal> preconditionLiterals = ConversionUtil.expressionToLiterals(precondition);
		_parents = new ArrayList<PlanGraphLiteral>();
		for(Literal preConLiteral : preconditionLiterals){
			PlanGraphLiteral preconPGLit = new PlanGraphLiteral(preConLiteral);
			_parents.add(preconPGLit);
		}
		// Get PlanGraphLiterals Parent Nodes Effects
		List<Literal> effectLiterals = ConversionUtil.expressionToLiterals(effect);
		_children = new ArrayList<PlanGraphLiteral>();
		for(Literal effectLiteral : effectLiterals){
			PlanGraphLiteral effectPGLit = new PlanGraphLiteral(effectLiteral);
			_parents.add(effectPGLit);
		}
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
		this(step, -1);
	}

	@Override
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

	protected void addPlanGraphChild(PlanGraphLiteral newStep){
		_children.add(newStep);
	}
	
	protected void addPlanGraphParent(PlanGraphLiteral newStep){
		_parents.add(newStep);
	}

	@Override
	public String toString()
	{
		String output = super.toString();
		output += "[" + _initialLevel + "]";
		return output;
	}

	@Override
	public List<PlanGraphLiteral> getParentNodes() {
		return _parents;
	}

	@Override
	public List<PlanGraphLiteral> getChildNodes() {
		return _children;
	}

}