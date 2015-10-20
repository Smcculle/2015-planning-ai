package edu.uno.ai.planning.graphplan;
import java.util.ArrayList;


public class GraphPlanNode {
	
	ArrayList<PlanGraphStep> steps;			// list of steps at this node
	ArrayList<PlanGraphLiteral>  literals;	// list of literals at this node
	int level;								// level at which this node appears
	
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
	
	public void setLevel(int x){
		level = x;
	}

}
