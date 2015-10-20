package edu.uno.ai.planning.graphplan;
import java.util.ArrayList;


public class GraphPlanNode {
	
	ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
	ArrayList<PlanGraphLiteral>  literals = new ArrayList<PlanGraphLiteral>();
	int level;
	
	public GraphPlanNode() {
		
	}
	
	public void addSteps(PlanGraphStep x){
		steps.add(x);
	}
	
	public void addLiterals(PlanGraphLiteral l){
		literals.add(l);
	}
	
	public void setLevel(int x){
		level = x;
	}

}
