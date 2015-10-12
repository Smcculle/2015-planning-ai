package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;

public class Graphplan {

	Problem prob;
	PlanGraph pg;
	
	public Graphplan(Problem problem) {
		prob = problem;
		makePlanGraph(problem);

	}
	
	public void makePlanGraph(Problem problem){
		pg = PlanGraph.create(problem);
	}
	
	
	public void search(){
		ArrayList<Literal> goalLiterals = new ArrayList<Literal>();
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		ArrayList<Literal> effectLiterals = new ArrayList<Literal>();
		goalLiterals = expressionToLiterals(prob.goal);
		steps = pg.getAllSteps();
		for (PlanGraphStep step: steps){
			for (Literal effectLiteral: expressionToLiterals(step.GetStep().effect)){
				effectLiterals.add(effectLiteral);
			}
		}
		for (Literal goalLiteral: goalLiterals){
			for (Literal effectLiteral: effectLiterals){
				if (effectLiteral == goalLiteral){
					
				}
			}
		}
	}
	
	public void stepsAtTime(int time){
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		
	}
	
	/**
	 * Helper function to get all the literals from an Expression
	 * 
	 * @param expression The Expression to convert to list
	 * @return ArrayList<Literal> List of literals in expression
	 */
	private ArrayList<Literal> expressionToLiterals(Expression expression)
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
