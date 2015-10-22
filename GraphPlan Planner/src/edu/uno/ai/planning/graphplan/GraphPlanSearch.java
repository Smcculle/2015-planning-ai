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
	
	private HashMap<Integer, ArrayList<PlanGraphStep>> mutexLists = new HashMap<Integer, ArrayList<PlanGraphStep>>();
	
	private int extendedNodes;
	
	private int visitedNodes;
	
	private int currentMaxLevel = -1;

	private int currentLevel = -1;
	
	private boolean finished = false;
	
	int limit = -1;
	
	//Used to determine if we are creating the highest level node. 
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
		System.out.println(pg);
		
		if (firstCall == true)
		{
			createGoalNode();
			firstCall = false;

		} 
		
		while (currentLevel > 0){
			createNewNode();
			
			
		}
		
		if (finished = false){
			whi
			currentLevel++;
			createNewNode();
		}
		
		
		return x;
	}
	
	
	/**
	 * Creates a node based upon the goal literals
	 */
	
	public void createGoalNode(){
		currentLevel = currentMaxLevel;
		ArrayList<PlanGraphLiteral> tempGoalList = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		
//		System.out.println(expressionToLiterals(problem.goal));
		
		for (Literal lit: expressionToLiterals(problem.goal)){
//			for (PlanGraphLiteral possibleGoal: pg.getAllPossiblePlanGraphEffects()){
//				if (possibleGoal.existsAtLevel(currentLevel)){
//					if (lit.equals(possibleGoal.getLiteral())){
//						
//					}
//				}
				tempGoalList.add(pg.getPlanGraphLiteral(lit));
//			}
		}
	
		for (PlanGraphLiteral goal: tempGoalList){
			for (PlanGraphStep step: goal.getParentNodes()){
				tempSteps.add(step);
			}
		}
		
		defineNode(dealWithMutex(tempSteps), tempGoalList,currentMaxLevel);
		System.out.println(nodes.get(currentMaxLevel).getLiterals());
		System.out.println(nodes.get(currentLevel).getSteps());
		extendedNodes++;
		visitedNodes++;
	}
	
	public void createNewNode(){
		GraphPlanNode tempNode = nodes.get(currentLevel);
		ArrayList<PlanGraphLiteral> tempLiterals = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
		
		for (PlanGraphStep step: tempNode.getSteps()){
			for (PlanGraphLiteral lit: step.getParentNodes()){
				System.out.println(lit + "!!");
				deleteRepeats.add(lit);
			}
		}
		tempLiterals.addAll(deleteRepeats);
		
		for (PlanGraphLiteral goal: tempLiterals){
			for (PlanGraphStep step: goal.getParentNodes()){
			tempSteps.add(step);
			}
		}
		
		goalReached(tempLiterals);
		currentLevel--;
		defineNode(dealWithMutex(tempSteps),tempLiterals,currentLevel);
		extendedNodes++;
		visitedNodes++;
		System.out.println(nodes.get(currentLevel).getLiterals() + " Level: " + currentLevel);
		System.out.println(nodes.get(currentLevel).getSteps());
	}
	
	public void repeatPreviousLevel(){
		GraphPlanNode tempNode = nodes.get(currentLevel);
		ArrayList<PlanGraphLiteral> tempLiterals = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
		
		for (PlanGraphStep step: tempNode.getSteps()){
			for (PlanGraphLiteral lit: step.getParentNodes()){
				System.out.println(lit + "!!");
				deleteRepeats.add(lit);
			}
		}
		tempLiterals.addAll(deleteRepeats);
		
		for (PlanGraphLiteral goal: tempLiterals){
			for (PlanGraphStep step: goal.getParentNodes()){
			tempSteps.add(step);
			}
		}
		
		goalReached(tempLiterals);
		currentLevel--;
		defineNode(dealWithMutex(mutexLists.get(currentLevel)),tempLiterals,currentLevel);
		extendedNodes++;
		visitedNodes++;
	}
	
	public boolean goalReached(ArrayList<PlanGraphLiteral> tempLiterals){
		ArrayList<Literal> nonPGLiterals = new ArrayList<Literal>();
		ArrayList<Literal> initialLiterals = new ArrayList<Literal>();
		initialLiterals = expressionToLiterals(problem.initial.toExpression());
		
		for(PlanGraphLiteral lit:tempLiterals){
			nonPGLiterals.add(lit.getLiteral());
		}
		
		if (nonPGLiterals.equals(initialLiterals)){
			finished = true;
		} else {
			finished = false;
		}
			
		return finished;
	}
	
	public ArrayList<PlanGraphStep> dealWithMutex(ArrayList<PlanGraphStep> tempSteps){
		PlanGraphLevel mutex = pg.getLevel(currentMaxLevel);
		ArrayList<PlanGraphStep> tempNonMutex = new ArrayList<PlanGraphStep>();
		ArrayList<PlanGraphStep> tempMutex = new ArrayList<PlanGraphStep>();
		
		for (int i = 0; i < tempSteps.size(); i++) {
			for (int j = i+1; j < tempSteps.size(); j++) {
				if (((PlanGraphLevelMutex)mutex).isMutex(tempSteps.get(i),tempSteps.get(j))){
					tempMutex.add(tempSteps.get(j));
					tempNonMutex.add(tempSteps.get(i));
					break;
				} else {
					tempNonMutex.add(tempSteps.get(i));
					if ((j == (tempSteps.size() -1)) && (!(tempMutex.contains(tempSteps.get(j)))) ){
						tempNonMutex.add(tempSteps.get(j));
					}
				}
			}
		}	
		
		mutexLists.put(currentLevel,tempMutex);
		tempNonMutex.removeAll(tempMutex);
		
//		System.out.println(mutexLists);
//		System.out.println(tempNonMutex + "Non Mutex steps");
		return tempNonMutex;
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
