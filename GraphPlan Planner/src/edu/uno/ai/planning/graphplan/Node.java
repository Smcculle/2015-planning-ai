package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
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
		if (steps.size() == 0) {
			steps.add(step);
			System.out.println("step changed");
			return;
		}
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
	
	public int contains(PlanGraphLiteral literal){
		if (literals.contains(new NegatedLiteral(literal.getLiteral().negate()))) return -1;
		if (literals.contains(literal)) return 1;
		return 0;
	}
	
	public int contains(Expression ex){
		if (literals.contains(new NegatedLiteral(expressionToLiterals(ex).get(0)))) return -1;
		if (literals.contains(expressionToLiterals(ex).get(0))) return 1;
		return 0;
	}
	
	public boolean contains(PlanGraphStep step){
		return (steps.contains(step));
	}
	
	public void resetLiterals(){
		literals.clear();
	}
	
	public void resetSteps(){
		steps.clear();
	}
	
	public void reset(){
		literals.clear();
		steps.clear();
		parent = null;
		level = -1;
	}
	
	public String toString(){
		String res = "";
		if (literals.size() == 0){
			res = res + "No literals.";
		}
		else{
			res = res + "Literals: ";
			for (PlanGraphLiteral l : literals){
				res = res + l.toString() + " ";
			}
		}
		if (steps.size() == 0){
			res = res + "\nNo Steps.";
		}
		else{
			res = res + "\nSteps: ";
			for (PlanGraphStep s : steps){
				res = res + s.toString() + " ";
			}
		}
		return res;
	}
	
	/**
	 * Helper function to get all the literals from an Expression
	 * 
	 * @param expression The Expression to convert to list
	 * @return ArrayList<Literal> List of literals in expression
	 */
	public ArrayList<Literal> expressionToLiterals(Expression expression)
	{
		ArrayList<Literal> literals = new ArrayList<Literal>();
		if (expression instanceof Literal)
			literals.add((Literal)expression);
		else
		{
			Conjunction cnf = (Conjunction)expression.toCNF();
			for (Expression disjunction : cnf.arguments)
				if (((Disjunction) disjunction).arguments.length == 1)
					literals.add((Literal)((Disjunction) disjunction).arguments.get(0));
				// else -- Do Nothing!
		}
		return literals;
	}	
	
}