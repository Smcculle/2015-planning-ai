package edu.uno.ai.planning.graphplan;

import java.util.*;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.ss.TotalOrderPlan;

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
		TotalOrderPlan solution = null;
		System.out.println(pg);
		
		if (firstCall == true)
		{
			finished = checkGoalInInitial();
			firstCall = false;
			createGoalNode();
//			goalReached(nodes.get(currentLevel).getLiterals());
//			System.out.println(nodes.get(currentLevel).getLiterals());

		} 
			
		searchAux();

		if (finished == true){
			solution = createTotalOrderPlan();
			return solution;
		}

//		repeatPreviousLevel();
//		if (finished = false){
//	
//			
//			repeatPreviousLevel();
//		}

		return solution;
	}
	
	public void searchAux(){

		while (currentLevel > 0){
			createNewNode();
			finished = goalReached(nodes.get(currentLevel).getLiterals());
			if (currentLevel == 0){
				if (!pg.isLeveledOff() && finished == false){
					pg.extend();
					currentMaxLevel++;
					createGoalNode();
				}
			}
		}
	}
	
//	public void navigateGraph(){
//
//		int tempCurrentLevel = 0;
//		goalReached(nodes.get(currentLevel).getLiterals());
//		if (currentMaxLevel == 0){
//			goalReached(nodes.get(currentLevel).getLiterals());
//			if (finished == false){
//				pg.extend();
//				currentMaxLevel++;
//				currentLevel = currentMaxLevel;
//				createGoalNode();
//			}
//		}
//		if (currentLevel == 0) {
//			if (finished == false){
//				if (!pg.isLeveledOff()){
//					pg.extend();
//					currentMaxLevel++;
//					currentLevel = currentMaxLevel;
//					createGoalNode();
//				}
//				
//				tempCurrentLevel = tempCurrentLevel + 1;
//		
//			}
//		}
//		if (currentLevel == 0){
//			if (finished == false){
//				
//			}
//		}
//	}
	
	//Create TotalOrderPlan which contains correct steps for solution
	public TotalOrderPlan createTotalOrderPlan(){
		TotalOrderPlan solution = new TotalOrderPlan();
		
		for (int i = 0; i <= currentMaxLevel; i++){
			for (PlanGraphStep step: nodes.get(i).getSteps()){
				solution = solution.addStep(step.getStep());
			}
		}
		Iterator<Step> x = solution.iterator();
		while(x.hasNext()){
			System.out.println(x.next());
		}
		return solution;
		
	}
	
	
	/**
	 * Creates a node based upon the goal literals
	 */
	
	public void createGoalNode(){
		nodes.clear();
		currentLevel = currentMaxLevel;
		ArrayList<PlanGraphLiteral> tempGoalList = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		Set<PlanGraphStep> deleteRepeatedSteps = new HashSet<PlanGraphStep>();
		for (Literal lit: expressionToLiterals(problem.goal)){
			tempGoalList.add(pg.getPlanGraphLiteral(lit));
		}
	

		ArrayList<PlanGraphStep> newSteps = new ArrayList<PlanGraphStep>();
		for (PlanGraphStep x: pg.getAllPossiblePlanGraphSteps()){
			if (x.existsAtLevel(currentLevel)){
				newSteps.add(x);
			}
		}
		
//		for (PlanGraph x: newSteps){
//			expressionToLiterals(x.getStep().effect)
//		}
		
		System.out.println(newSteps);
		
		
//		
//				for (PlanGraphLiteral y: tempGoalList){
//					System.out.println(y);;
//					for (Literal z: ){
//						if (z == y){
//							
//						}
//					}	
//				}
			
		
	
		

		for (PlanGraphLiteral goal: tempGoalList){
			for (PlanGraphStep step: goal.getParentNodes()){
				deleteRepeatedSteps.add(step);
				if (!step.existsAtLevel(currentLevel)){
					deleteRepeatedSteps.remove(step);
				}
			}
		}
		
		tempSteps.addAll(deleteRepeatedSteps);
		defineNode(checkMutexSteps(tempSteps), tempGoalList, currentMaxLevel);
		System.out.println(nodes.get(currentMaxLevel).getLiterals());
		System.out.println(nodes.get(currentMaxLevel).getSteps());

		extendedNodes++;
		visitedNodes++;
	}
	
	public void createNewNode(){
		GraphPlanNode tempNode = nodes.get(currentLevel);
		currentLevel--;
		ArrayList<PlanGraphLiteral> tempLiterals = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
		Set<PlanGraphStep> deleteRepeatedSteps = new HashSet<PlanGraphStep>();
		
		for (PlanGraphStep step: tempNode.getSteps()){
			for (PlanGraphLiteral lit: step.getParentNodes()){
				deleteRepeats.add(lit);
				if (!lit.existsAtLevel(currentLevel)){
					deleteRepeats.remove(lit);
				}
			}
		}
		
		
		tempLiterals.addAll(deleteRepeats);
		tempLiterals = checkForFailure(tempLiterals);
		
		for (PlanGraphLiteral goal: tempLiterals){
			for (PlanGraphStep step: goal.getParentNodes()){
				deleteRepeatedSteps.add(step);
				if (!step.existsAtLevel(currentLevel)){
					deleteRepeatedSteps.remove(step);
				}
			}
		}
		
		tempSteps.addAll(deleteRepeatedSteps);

		defineNode(checkMutexSteps(tempSteps),tempLiterals,currentLevel);
		extendedNodes++;
		visitedNodes++;
		System.out.println(nodes.get(currentLevel).getLiterals() + " Level: " + currentLevel);
		System.out.println(nodes.get(currentLevel).getSteps());
	}
	
	public ArrayList<PlanGraphLiteral> checkForFailure(ArrayList<PlanGraphLiteral> list){	
		for (int i = 0; i < list.size(); i++){
			for (int j = i +1; j < list.size(); j++){
				if (isMutex(list.get(i), list.get(j))){
					list.remove(i);
					checkForFailure(list);
				}
			}
		}
		
		return list;
		
	}
	
	public ArrayList<PlanGraphStep> checkMutexSteps(ArrayList<PlanGraphStep> list){	
		for (int i = 0; i < list.size(); i++){
			for (int j = i +1; j < list.size(); j++){
				if (isMutex(list.get(i), list.get(j))){
//					System.out.println(list.get(i));
					list.remove(i);
					checkMutexSteps(list);
				}
			}
		}
		
		return list;
		
	}
	
	public ArrayList<PlanGraphStep> getStepsFromAllPreconditions(ArrayList<PlanGraphLiteral> lits){
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		for (PlanGraphLiteral l : lits){
			steps.addAll(getStepsFromPrecondition(l));
		}
		
//		Set<PlanGraphStep> stepSet = new HashSet<PlanGraphStep>();
//		stepSet.addAll(steps);
//		steps.clear();
//		steps.addAll(stepSet);
		
//		steps = checkMutexSteps(steps);
		return steps;
	}
	
	public ArrayList<PlanGraphStep> getStepsFromPrecondition(PlanGraphLiteral p){
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
			if (isStepApplicablePrecon(s, p)) steps.add(s);
		}
		return steps;
	}

	public boolean isStepApplicablePrecon(PlanGraphStep step, PlanGraphLiteral literal){
			return ( (literal.getLiteral().equals(step.getStep().effect)) );
	}

	
	public void repeatPreviousLevel(){
		currentLevel++;
		GraphPlanNode tempNode = nodes.get(currentLevel);
		ArrayList<PlanGraphLiteral> tempLiterals = new ArrayList<PlanGraphLiteral>();
		ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
		Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
		
		
		for (PlanGraphStep step: tempNode.getSteps()){
			for (PlanGraphLiteral lit: step.getParentNodes()){
				deleteRepeats.add(lit);
			}
		}
		
		tempLiterals.addAll(nodes.get(currentLevel).getLiterals());
		
		for (PlanGraphLiteral goal: tempLiterals){
			for (PlanGraphStep step: goal.getParentNodes()){
				
					tempSteps.add(step);
		
			}
		}
		

		currentLevel--;
		goalReached(tempLiterals);
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
		
		for (Literal lit : initialLiterals){
			if (!nonPGLiterals.contains(lit)) return false;
		}
		return true;
		
	}
	
	
	public boolean checkGoalInInitial(){
		
		ArrayList<Literal> initialLiterals = new ArrayList<Literal>();
		initialLiterals = expressionToLiterals(problem.initial.toExpression());
		
		for (Literal lit: expressionToLiterals(problem.goal)){
			if (!initialLiterals.contains(lit)) return false;
		}
		return true;
		
	}
	
	public boolean isMutex(PlanGraphLiteral lit1, PlanGraphLiteral lit2){
		PlanGraphLiteral litNegation = new PlanGraphLiteral((new NegatedLiteral(lit1.getLiteral())).negate());
		if (lit2.equals(litNegation)){
//			System.out.println(lit2.toString() +  " is mutex with " + lit1.toString() + " because its negation is " + litNegation.toString());
			return true;
		}
//		System.out.println(lit2.toString() +  " isn't mutex with " + lit1.toString() + " because its negation is " + litNegation.toString());
		return false;
	}

	public boolean isMutex(PlanGraphStep step1, PlanGraphStep step2){

//		System.out.println("Checking " + step1.toString() + " versus " + step2.toString());
		for (PlanGraphLiteral lit1: step1.getParentNodes()){
			for (PlanGraphLiteral lit2: step2.getParentNodes()){
				if (isMutex(lit1, lit2)) return true;
				if (isMutex(lit2, lit1)) return true;
			}
		}
		
		for (PlanGraphLiteral lit1: step1.getChildNodes()){
			for (PlanGraphLiteral lit2: step2.getChildNodes()){
				if (isMutex(lit1, lit2)) return true;
				if (isMutex(lit2, lit1)) return true;
			}
		}
		
//		System.out.println(step1.toString() + " and " + step2.toString() + " are not mutex");
		return false;

	}
	
	public boolean isStepApplicable(PlanGraphStep step, ArrayList<PlanGraphLiteral> lits){
		for (PlanGraphLiteral literal1 : lits){	// for each literal in the specified level's list	
			for (PlanGraphLiteral literal2 : step.getChildNodes()){
				if (isMutex(literal1, literal2)) return false;
			}
		}
		return true;
	}
	
//	public ArrayList<PlanGraphStep> dealWithMutex(ArrayList<PlanGraphStep> tempSteps){
//
//		PlanGraphLevel mutex = pg.getLevel(currentMaxLevel);
//		ArrayList<PlanGraphStep> nonMutex = new ArrayList<PlanGraphStep>();
//		Set<PlanGraphStep> tempNonMutex = new HashSet<PlanGraphStep>();
//		ArrayList<PlanGraphStep> tempMutex = new ArrayList<PlanGraphStep>();
//		
//		for (int i = 0; i < tempSteps.size(); i++) {
//			for (int j = i+1; j < tempSteps.size(); j++) {
//				if (((PlanGraphLevelMutex)mutex).isMutex(tempSteps.get(i),tempSteps.get(j))){
//					tempMutex.add(tempSteps.get(j));
//					tempNonMutex.add(tempSteps.get(i));
//					break;
//				} else {
//					tempNonMutex.add(tempSteps.get(i));
//					if ((j == (tempSteps.size() -1)) && (!(tempMutex.contains(tempSteps.get(j)))) ){
//						tempNonMutex.add(tempSteps.get(j));
//					}
//				}
//			}
//		}	
//		nonMutex.addAll(tempNonMutex);
//		mutexLists.put(currentLevel,tempMutex);
//		nonMutex.removeAll(tempMutex);
//		
////		System.out.println(mutexLists);
////		System.out.println(tempNonMutex + "Non Mutex steps");
//		return nonMutex;
//	}
	
	
	/**
	 * Create a GraphPlanNode which models the steps and literals at a certain level of the PlanGraph.
	 */
	
	public void defineNode(ArrayList<PlanGraphStep> steps, ArrayList<PlanGraphLiteral> literals, int level){
		GraphPlanNode node = new GraphPlanNode();
		for (PlanGraphStep step: steps ){
			node.addSteps(step);
		}
		
		for (PlanGraphLiteral literal: literals ){
			node.addLiterals(literal);
		}
		
		node.setLevel(level);
		nodes.put(currentLevel,node);

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
