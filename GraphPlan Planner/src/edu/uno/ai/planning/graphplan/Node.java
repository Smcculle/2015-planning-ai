package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.logic.NegatedLiteral;

public class Node{
	PlanGraph parent;
	int level;
	ArrayList<PlanGraphLiteral> literals;
	ArrayList<PlanGraphStep> steps;
	
	public Node(){
		literals = new ArrayList<PlanGraphLiteral>();
		steps = new ArrayList<PlanGraphStep>();
		level = -1;
		parent = null;
	}
	
	public Node(ArrayList<PlanGraphLiteral> literals, ArrayList<PlanGraphStep> steps, int level, PlanGraph parent){
		this.literals = literals;
		this.steps = steps;
		this.level = level;
		this.parent = parent;
	}

	public Node copy(){
		return new Node(literals, steps, level, parent);
	}
	
	public void setParent(PlanGraph parent){
		this.parent = parent;
	}

	public void setLevel(int level){
		this.level = level;
	}
	
	public void changeStep(PlanGraphStep step){
		if (steps.size() == 0) steps.add(step);
		else{
			for (int i = 0; i < steps.size() - 1; i++){
				if( (steps.get(i).GetStep().precondition.equals(step.GetStep().precondition))  ||  (steps.get(i).GetStep().effect.equals(step.GetStep().effect)) ){
					steps.set(i, step);
					return;
				}
			}
			steps.add(step);
		}
	}
	
	public void changeLiteral(PlanGraphLiteral literal){
		if (literals.size() == 0) literals.add(literal);
		else{
			for (int i = 0; i < literals.size() - 1; i++){
				if( (literals.get(i).equals(literal)) || (literals.get(i).equals(new NegatedLiteral(literal.getLiteral().negate())) )){
					literals.set(i, literal);
					return;
				}
			}
			literals.add(literal);				
		}
	}
	
	public void reset(){
		literals.clear();
		steps.clear();
		parent = null;
		level = -1;
	}
	
}