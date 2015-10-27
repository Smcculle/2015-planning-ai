package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import edu.uno.ai.planning.util.ConversionUtil;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * LPGPlanGraph contains all the LPGPlanGraph levels, each level 
 * containing a set of facts and a single action. 
 * 
 * @author Shane McCulley
 *
 */
public class LPGPlanGraph {
	
	/** List of all levels in the graph*/  
	private ArrayList<LPGPlanGraphLevel> levelList;
	
	/** List of all unique PlanGraphSteps in PlanGraph **/
	private ArrayList<PlanGraphStep> steps;
	
	/** Possible literals mapped to steps that can achieve them **/
	private HashMap<PlanGraphLiteral, HashSet<PlanGraphStep>> effectsMap;
	
	/** number of current inconsistencies in LPGPlanGraph */
	private int inconsistencyCount;
	
	/**
	 * Constructs a new LPGPlanGraph containing a list of levels, possible steps 
	 * and possible effects that are mapped to steps that can achieve those steps. 
	 * It is an empty graph containing facts of initial state and special action 
	 * nodes aStart and aEnd
	 * 
	 * @param problem The problem to be solved
	 */
	public LPGPlanGraph(Problem problem) {
		levelList = new ArrayList<LPGPlanGraphLevel>();
		this.steps = new ArrayList<PlanGraphStep>();
		effectsMap = new HashMap<PlanGraphLiteral, HashSet<PlanGraphStep>>();
		
		StateSpaceProblem ssProblem = new StateSpaceProblem(problem);
		addAllSteps(ssProblem.steps);
		addAllEffects(ssProblem.steps);
		addStartLevel(problem.initial.toExpression());
		addEndLevel(problem.goal);
		
		connectParentsToChildren();
		updateInconsistencyCount();
		
	}
	
	/**
	 * Get the root LPGPlanGraphlevel
	 * 
	 * @return planGraphLevel Returns root PlanGraphLevel
	 */
	public LPGPlanGraphLevel getRootLevel()
	{
		return this.levelList.get(0);
	}
	
	/**
	 * Determines whether we can extract a solution from the current LPGPlanGraph
	 * @return True if no steps have unsupported preconditions, false otherwise.  
	 * 
	 * TODO:  Make sure inconsistencies is current when this is called.  
	 */
	public boolean isSolution(){
		
		/*
		boolean isSolution = true;
		for (LPGPlanGraphLevel level : levelList) {
			if(!level.getUnsupportedPreconditions().isEmpty()){
				isSolution = false;
				break;
			}
		}
		return isSolution;*/
		
		return (this.inconsistencyCount == 0)? true : false;
	}
	/**
	 * Extracts a totally ordered plan based on the actions contained in each level
	 * @return A plan for the problem
	 * 
	 * TODO:  Extract plan 
	 */
	public TotalOrderPlan getTotalOrderPlan(){
		TotalOrderPlan plan = new TotalOrderPlan();
		return plan;
	}
	
	/**
	 * Creates the first level in an empty LPGPlanGraph
	 */
	private void addStartLevel(Expression initialEffects) {
		PlanGraphStep aStart = new PlanGraphStep(
				new Step("aStart", Expression.TRUE, initialEffects));
		levelList.add(new LPGPlanGraphLevel(aStart, null));
		this.steps.add(aStart);
		
	}
	
	/**
	 * Creates the last level in an empty LPGPlanGraph
	 */
	private void addEndLevel(Expression goalPreconditions) {
		
		PlanGraphStep aEnd = new PlanGraphStep(
				new Step("aEnd", goalPreconditions, Expression.TRUE));
		levelList.add(new LPGPlanGraphLevel(aEnd, getRootLevel()));
		this.steps.add(aEnd);	
	}
	
	/**
	 * Calculates a new LPGPlanGraph based on the addition/deletion of an action node to resolve
	 * the given inconsistency 
	 * 
	 * @param inconsistency An unsupported precondition that needs to be resolved
	 * @return All possible LPGPlanGraphs arising from each resolution of inconsistency.  
	 * 
	 * TODO:  All
	 */
	public ArrayList<LPGPlanGraph> getNeighborhood(PlanGraphStep inconsistency) {
		return null;
	}
	
	/**
	 * Returns one of the (possibly many) inconsistencies in the LPGPlanGraph.  Prefers
	 * resolving inconsistencies at earlier level first.  
	 * 
	 * @return A PlanGraphStep that has an unsupported precondition.  
	 * 
	 * TODO:  loop through levels, update inconsistencies, choose randomly 1 weighted for lower levels. 
	 */
	public PlanGraphStep getInconsistency() {
		return null;
	}
	
	/** TODO: counts the number of inconsistencies in the graph */
	public void updateInconsistencyCount(){
		
	}
	
	/** Returns the number of current inconsistencies in the graph */
	public int getInconsistencyCount() {
		return inconsistencyCount;
	}

	/**
	 * Adds all possible steps from problem.
	 * 
	 * @param steps All possible Steps.
	 */
	private void addAllSteps(ImmutableArray<Step> steps) 
	{
		for (Step step : steps)
			this.steps.add(new PlanGraphStep(step));
	}
	
	/**
	 * Adds all effects from all steps, mapped to the
	 * steps that can achieve those effects.  
	 * 
	 * @param steps All possible steps
	 */
	private void addAllEffects(ImmutableArray<Step> steps) {
		for (Step step : steps){
			for (Literal literal : ConversionUtil.expressionToLiterals(step.effect)){
				PlanGraphLiteral newLiteral = new PlanGraphLiteral(literal);
				PlanGraphStep newStep = new PlanGraphStep(step);
				
				if (!effectsMap.containsKey(newLiteral)){
					effectsMap.put(newLiteral, new HashSet<PlanGraphStep>());
					effectsMap.get(newLiteral).add(newStep);
				}
				else
					effectsMap.get(newLiteral).add(newStep);
			}
			
		}
	}
	
	/**
	 * Connect all Steps and Effects to their parents and children
	 */
	private void connectParentsToChildren(){
		for(PlanGraphStep step : this.steps){
			// Add Step effects as Plan Graph Children
			List<Literal> effectLiterals = ConversionUtil.expressionToLiterals(step.getStep().effect);
			for(Literal literal : effectLiterals)
				for(PlanGraphLiteral effect : effectsMap.keySet())
					if(effect.equals(new PlanGraphLiteral(literal))){
						step.addChildLiteral(effect);
						effect.addParentStep(step);
					}
			// Add Step Preconditions as Plan Graph Parents
			List<Literal> preconditionLiterals = ConversionUtil.expressionToLiterals(step.getStep().precondition);
			for(Literal literal : preconditionLiterals)
				for(PlanGraphLiteral effect : effectsMap.keySet())
					if(effect.equals(new PlanGraphLiteral(literal))){
						step.addParentLiteral(effect);
						effect.addChildStep(step);
					}
		}
	}

}
