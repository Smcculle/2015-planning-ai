package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;


public class Graphplan {

	Problem problem;
	PlanGraph pg;
	PlanGraph currentPlanGraph;
	PlanGraph solution;
	int currentLevel = new Integer(1);
	int highestLevel = new Integer(-1);
	ArrayList<PlanGraph> parentList;
	ArrayList<PlanGraph> solutions;
	Set<PlanGraphStep> achieveGoals = new HashSet<PlanGraphStep>();
	
	public Graphplan(Problem problem, PlanGraph plangraph) {
		this.problem = problem;
		pg = plangraph;
		parentList = new ArrayList<PlanGraph>();
		nextPG(pg);
	}
	
	public void extend(){
		if (highestLevel != currentLevel){
			for (PlanGraph node: parentList){
				if (node.getLevel() == currentLevel){
					currentPlanGraph = node;
					currentLevel++;
					break;
				}
			} 
		}else if (highestLevel == currentLevel){
			return;
		}
		if (!currentPlanGraph.isGoalNonMutex(problem.goal)){
			extend();
		}
	}
	
	public PlanGraph search(){
		if (currentPlanGraph.getLevel() == 0){
			return currentPlanGraph;
		}
		else{
			ArrayList<Literal> goalLiterals = new ArrayList<Literal>();
			ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
			ArrayList<Literal> effectLiterals = new ArrayList<Literal>();
			goalLiterals = expressionToLiterals(problem.goal);
			steps = currentPlanGraph.getAllSteps();
			
			for (PlanGraphStep step: steps){
				for (Literal goalLiteral: goalLiterals){
					for (Literal effectLiteral: expressionToLiterals(step.GetStep().effect)){
						if (effectLiteral.equals(goalLiteral)){
							achieveGoals.add(step);
						}
					}
				}
			}
			
			achieveGoals
		
			
			}
			
		return pg;	
		
	}
	

	
	public void nextPG(PlanGraph pg){
		if (pg.getLevel() == 0){
			parentList.add(pg);
			return;
		}
		parentList.add(pg);
		nextPG(pg.getParent());
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
