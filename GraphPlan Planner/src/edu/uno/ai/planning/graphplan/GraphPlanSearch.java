package edu.uno.ai.planning.graphplan;

import java.util.*;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;

public class GraphPlanSearch extends Search{
	
	/** PlanGraph used in solving the problem **/
	private PlanGraph pg;
	
	/** Actual problem to solve. **/
	public final Problem problem;
	
	private HashMap<Integer, GraphPlanNode> nodes = new HashMap<Integer, GraphPlanNode>();

	private int extendedNodes;
	
	private int visitedNodes;
	
	private int currentMaxLevel = -1;

	private int currentLevel = -1;
	
	int limit = -1;
	
	
	boolean firstCall = true;
	
	public GraphPlanSearch(Problem problem){
		super(problem);
		this.problem = problem;
		this.extendedNodes = 0;
		this.visitedNodes = 0;
		pg = new PlanGraph(this.problem, true);
		currentMaxLevel = pg.countLevels() - 1;
	
	}
	
	public Plan search(){
		Plan x = null;
		
		if (firstCall == true)
		{
			createGoalNode();
			firstCall = false;

		} 
			
		createNewNode();
		
		return x;
	}
	
	
	/**
	 * Creates a node based upon the goal literals
	 */
	
	public void createGoalNode(){
		currentLevel = currentMaxLevel;
		
		ArrayList<PlanGraphLiteral> tempGoalList = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		for (Literal lit: expressionToLiterals(problem.goal)){
			tempGoalList.add(pg.getPlanGraphLiteral(lit));
		}
	
		
		
		for (PlanGraphLiteral goal: tempGoalList){
			for (PlanGraphStep step: goal.getParentNodes()){
			tempSteps.add(step);
			}
		}



		defineNode(tempSteps, tempGoalList,currentMaxLevel);
		System.out.println(nodes.get(currentMaxLevel).getLiterals());
		
		
		extendedNodes++;
		visitedNodes++;
	}
	
	public void createNewNode(){
		GraphPlanNode tempNode = null;
		tempNode = nodes.get(currentLevel);
		ArrayList<PlanGraphLiteral> tempLiterals = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
		
		System.out.println(tempNode.getSteps());
		for (PlanGraphStep step: tempNode.getSteps()){
			for (PlanGraphLiteral lit: step.getParentNodes()){
				deleteRepeats.add(lit);
				
			}
		}
		tempLiterals.addAll(deleteRepeats);
		
		for (PlanGraphLiteral goal: tempLiterals){
			for (PlanGraphStep step: goal.getParentNodes()){
			
			tempSteps.add(step);
			}
		}
		
		currentLevel--;
		defineNode(tempSteps,tempLiterals,currentLevel);
		System.out.println(tempLiterals);
		System.out.println(tempSteps);
		
	}
	
	/**
	 * Create a GraphPlanNode which models the steps and literals at a certain level of the PlanGraph.
	 */
	
	public GraphPlanNode defineNode(ArrayList<PlanGraphStep> steps, ArrayList<PlanGraphLiteral> literals, int level){
		
		GraphPlanNode node = new GraphPlanNode();
		
		for (PlanGraphStep step: steps ){
			node.addSteps(step);
		}
		
		for (PlanGraphLiteral literal: literals ){
			node.addLiterals(literal);
		}
		
		node.setLevel(level);
		
		nodes.put(currentLevel,node);
		
		return node;
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

	
	
	@Override
	public int countVisited() {
		return this.visitedNodes;
	}

	@Override
	public int countExpanded() {
		return this.extendedNodes;
	}

	@Override
	public void setNodeLimit(int limit) {
		this.limit = limit;
	}
	
	@Override
	//Function used to actually solve problem.
	public Plan findNextSolution() {
		return search();
	}
	

}
