package edu.uno.ai.planning.graphplan;
import java.util.ArrayList;

import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.util.ConversionUtil;


public class GraphPlanNode {
	
	private ArrayList<PlanGraphStep> steps;			// list of steps at this node
	private ArrayList<PlanGraphLiteral>  literals;	// list of literals at this node
	private int level;								// level at which this node appears
	
	/** Constructor with level specified */
	public GraphPlanNode(int level) {
		steps = new ArrayList<PlanGraphStep>();
		literals = new ArrayList<PlanGraphLiteral>();
		this.level = level;
	}
	
	public GraphPlanNode() {
		steps = new ArrayList<PlanGraphStep>();
		literals = new ArrayList<PlanGraphLiteral>();
		level = 0;
	}	
	
	public int getLevel(){
		return level;
	}
	
	
	/**
	 * Get the steps within this node.
	 */
	public ArrayList<PlanGraphStep> getSteps(){
		return steps;
	}
	
	/**
	 * Get the literals within this node.
	 */
	
	public ArrayList<PlanGraphLiteral> getLiterals(){
		return literals;
	}
	
	/** Clear steps, for when node is being recalculated. */
	public void clearSteps(){
		steps.clear();
	}
	
	/** Clear literals, for when node is being recalculated. */
	public void clearLiterals(){
		literals.clear();
	}
	
	/** Add a single step to the steps for this level */
	public void addSteps(PlanGraphStep x){
		steps.add(x);
	}
	
	/** Add all steps that exist at this level to the steps arraylist from the parent PlanGraph */
	public void addAllSteps(PlanGraph parent){
		for (PlanGraphStep step : parent.getAllPossiblePlanGraphSteps()){
			if (parent.existsAtLevel(step, level)){
				steps.add(step);
			}
		}
	}
	
	/** Get all possible steps that can lead to these literals at this level */
	public ArrayList<PlanGraphStep> getPrevSteps(){
		ArrayList<PlanGraphStep> result = new ArrayList<PlanGraphStep>();
		for (PlanGraphLiteral literal : literals){
			for (PlanGraphStep step : literal.getChildNodes()){
				result.add(step);
			}
		}
		return result;
	}
	
	/** Get all the possible next steps that can come from the literals at this level */
	public ArrayList<PlanGraphStep> getNextSteps(){
		ArrayList<PlanGraphStep> result = new ArrayList<PlanGraphStep>();
		for (PlanGraphLiteral literal : literals){
			for (PlanGraphStep step : literal.getParentNodes()){
				result.add(step);
			}
		}
		return result;
	}	
	
	/** Add a single literal to the literals for this level */
	public void addLiterals(PlanGraphLiteral l){
		literals.add(l);
	}
	
	/** Add all literals from this level to the literals list from the parent PlanGraph */
	public void addAllLiterals(PlanGraph parent){
		for (PlanGraphLiteral literal : parent.getAllPossiblePlanGraphEffects()){
			if (parent.existsAtLevel(literal, level)){
				literals.add(literal);
			}
		}
	}	

	/** Get all possible literals that can lead to the steps at this level */
	public ArrayList<PlanGraphLiteral> getPrevLits(){
		ArrayList<PlanGraphLiteral> result = new ArrayList<PlanGraphLiteral>();
		for (PlanGraphStep step : steps){
			for (PlanGraphLiteral literal : step.getChildNodes()){
				result.add(literal);
			}
		}
		return result;
	}
	
	/** Get all the possible next literals that can come from the steps at this level */
	public ArrayList<PlanGraphLiteral> getNextLits(){
		ArrayList<PlanGraphLiteral> result = new ArrayList<PlanGraphLiteral>();
		for (PlanGraphStep step : steps){
			for (PlanGraphLiteral literal : step.getParentNodes()){
				result.add(literal);
			}
		}
		return result;
	}		
	
	public void applySteps(ArrayList<PlanGraphStep> steps){
		
		ArrayList<PlanGraphLiteral> temp = new ArrayList<PlanGraphLiteral>();

		for (PlanGraphStep step : steps){
			for (PlanGraphLiteral literal : getPrevLits()){
				if (isStepApplicableEffect(step, literal)){
					temp.add(literal);
				}
			}
		}
		
		ArrayList<PlanGraphLiteral> temp2 = new ArrayList<PlanGraphLiteral>();
		for (PlanGraphLiteral literal : literals){
			temp2.add(literal);
		}
		
		for (PlanGraphStep step : steps){
			for (Literal literal : ConversionUtil.expressionToLiterals(step.getStep().effect)){
				PlanGraphLiteral n = new PlanGraphLiteral(literal.negate());
				if (temp2.contains(n)){
					temp2.remove(n);
				}
			}
		}
		
		for (PlanGraphLiteral literal : temp){
			temp2.add(literal);
		}
		
		literals = temp2;
		
	}

	/** see if the step's effect matches our literal */
	public boolean isStepApplicableEffect(PlanGraphStep step, PlanGraphLiteral literal){
		return ( (literal.getLiteral().equals(step.getStep().effect)) );
	}	
	
	/** Set the level */
	public void setLevel(int x){
		level = x;
	}

}
