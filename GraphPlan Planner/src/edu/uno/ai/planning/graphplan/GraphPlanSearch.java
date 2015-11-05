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
import com.google.common.collect.Sets;

public class GraphPlanSearch extends Search{
	
	/** PlanGraph used in solving the problem **/
	private PlanGraph pg;
	
	/** Actual problem to solve. **/
	public final Problem problem;
	
	//HashMap used to define a node at each level. A node contains a list of literals and list of levels.
	private HashMap<Integer, GraphPlanNode> nodes = new HashMap<Integer, GraphPlanNode>();
	
	private HashMap<Integer, ArrayList<PlanGraphStep>> stepListsByLevel = new HashMap<Integer, ArrayList<PlanGraphStep>>();
	
	private HashMap<Integer, ArrayList<PlanGraphLiteral>> literalListsByLevel = new HashMap<Integer, ArrayList<PlanGraphLiteral>>();
	
	private ArrayList<PlanGraphLiteral> goalList = new ArrayList<PlanGraphLiteral>();
	
	private int extendedNodes;
	
	private int visitedNodes;
	
	private int currentMaxLevel = -1;

	private int currentLevel = -1;
	
	//when going up, this is used to continue going up, so that the loop doesn't just repeat the same level everytime.
	private ArrayList<Integer> counter = new ArrayList<Integer>();
	
	private boolean finished = false;
	
	int limit = -1;
	
	//Used to determine if we are creating the highest level node. 
	boolean firstCall = true;
	
	HashMap<Integer, ArrayList<ArrayList<PlanGraphStep>>> allPermutationsSteps = new HashMap<Integer, ArrayList<ArrayList<PlanGraphStep>>>();
	
	HashMap<Integer, ArrayList<ArrayList<PlanGraphLiteral>>> allPermutationsLiterals =  new HashMap<Integer, ArrayList<ArrayList<PlanGraphLiteral>>>();
	
	HashMap<Integer, Integer> positionInLiteralLists = new HashMap<Integer, Integer>();
	
	HashMap<Integer, Integer> positionInStepLists = new HashMap<Integer, Integer>();
	
	ArrayList<PlanGraphStep> aPermutationOfSteps = new ArrayList<PlanGraphStep>();
	
	ArrayList<ArrayList<PlanGraphStep>> listOfPermutations = new ArrayList<ArrayList<PlanGraphStep>>();
	
	Set<Set<PlanGraphStep>> pgSteps;
	
	HashMap<Integer, Set<Set<PlanGraphStep>>> possibleStepSets = new HashMap<Integer, Set<Set<PlanGraphStep>>>();
	
	HashMap<Integer, Set<Set<PlanGraphLiteral>>> possibleLiteralSets = new HashMap<Integer, Set<Set<PlanGraphLiteral>>>();
	
	Set<Set<PlanGraphLiteral>> pgLiterals;
			
	public GraphPlanSearch(Problem problem){
		super(problem);
		this.problem = problem;
		this.extendedNodes = 0;
		this.visitedNodes = 0;
		pg = new PlanGraph(this.problem, true);
		currentMaxLevel = pg.countLevels() - 1;
		for (int i = 0; i <= currentMaxLevel; i++){
			counter.add(0);
		}
	}
	
	public Plan search(){
		TotalOrderPlan solution = new TotalOrderPlan();
		
		if (firstCall == true)
		{
			finished = checkGoalInInitial();
			firstCall = false;
			createGoalNode();
		} 
			
		searchAux();

		for (int i =0; i<nodes.size(); i++){
			System.out.println(nodes.get(i).getSteps()  + "Level " + nodes.get(i).getLevel() + " Steps");
			System.out.println(nodes.get(i).getLiterals() + "Level " + nodes.get(i).getLevel() + " Literals");
		}
		
		if (finished == true){
			solution = createTotalOrderPlan();
			return solution;
		}
		return solution;
	}
	
	
	public void searchAux(){
		while (currentLevel > 0){
			createNewNode();
		}
		finished = goalReached(nodes.get(currentLevel).getLiterals());	
		if (finished == true){
			return;
		}
		
		while (finished == false){
			recalculateLevel();
			while (currentLevel > 0){
				createNewNode();
			}	
//			System.out.println("\r\n" + "special text " );
//			for (int i =0; i< nodes.size(); i++){
//				System.out.println(nodes.get(i).getSteps()  + "Level " + nodes.get(i).getLevel() + " Steps");
//				System.out.println(nodes.get(i).getLiterals() + "Level " + nodes.get(i).getLevel() + " Literals");
//			}
			
			finished = goalReached(nodes.get(currentLevel).getLiterals());

		}	
	}
		
	
	//Create TotalOrderPlan which contains correct steps for solution
	public TotalOrderPlan createTotalOrderPlan(){
		TotalOrderPlan solution = new TotalOrderPlan();
		
		for (int i = 0; i <= currentMaxLevel; i++){
			for (PlanGraphStep step: nodes.get(i).getSteps()){
				solution = solution.addStep(step.getStep());
			}
		}
		return solution;
	}
	
	public void createMasterStepsForGoals(int level){
		Set<PlanGraphStep> deleteRepeatedSteps = new HashSet<PlanGraphStep>();
		ArrayList<PlanGraphStep> tempList = new ArrayList<PlanGraphStep>();
		
		for (PlanGraphLiteral findAGoal: goalList){
			for (PlanGraphStep step: pg.getAllPossiblePlanGraphSteps()){
				if (step.existsAtLevel(currentLevel)){
					for (Literal effect: expressionToLiterals(step.getStep().effect)){
						if (effect.equals(findAGoal.getLiteral())){
							deleteRepeatedSteps.add(step);
						}
					}
				}
			}}
		
		tempList.addAll(deleteRepeatedSteps);
//		PlanGraphLevel lev = pg.getLevel(currentLevel);
//		System.out.println(tempList);
//		System.out.println(checkMutexSteps(tempList));
//		System.out.println(((PlanGraphLevelMutex)lev).isMutex(tempList.get(0),tempList.get(1)));
//		for (PlanGraphStep step: tempList){
//		
//		}
//		System.exit(0);
		
		stepListsByLevel.put(currentLevel,tempList);
	}
	
	public void createMasterStepListByLevel(int level){
		ArrayList<PlanGraphStep> tempList = new ArrayList<PlanGraphStep>();
		Set<PlanGraphStep> deleteRepeats = new HashSet<PlanGraphStep>();
	
		for (PlanGraphLiteral findAGoal: literalListsByLevel.get(currentLevel)){
			for (PlanGraphStep step: pg.getAllPossiblePlanGraphSteps()){
				if (step.existsAtLevel(currentLevel)){
					for (Literal effect: expressionToLiterals(step.getStep().effect)){
						if (effect.equals(findAGoal.getLiteral())){
							deleteRepeats.add(step);
						}
					}
				}
			}}
		
		tempList.addAll(deleteRepeats);
	
		stepListsByLevel.put(currentLevel,tempList);
	}
	
	
	public void createMasterLiteralListByLevel(int level){
		ArrayList<PlanGraphLiteral> tempList = new ArrayList<PlanGraphLiteral>();
		Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
		for (PlanGraphStep step: nodes.get(currentLevel).getSteps()){
			for (Literal pre: expressionToLiterals(step.getStep().precondition)){
				for (PlanGraphLiteral literal: pg.getAllPossiblePlanGraphEffects()){
					if (literal.existsAtLevel(currentLevel)){
						if(pre.equals(literal.getLiteral())){
							deleteRepeats.add(literal);
						}
					}
				}
			}
		}
		tempList.addAll(deleteRepeats);
		currentLevel--;
		literalListsByLevel.put(currentLevel, tempList);
	}
	
	/**
	 * Creates a node based upon the goal literals
	 */
	public void createGoalNode(){
		nodes.clear();
		goalList.clear();
		currentLevel = currentMaxLevel;
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		for (Literal lit: expressionToLiterals(problem.goal)){
			goalList.add(pg.getPlanGraphLiteral(lit));
		}
		createMasterStepsForGoals(currentLevel);	
		Set<PlanGraphStep> temp = new LinkedHashSet<PlanGraphStep>();
		temp.addAll(stepListsByLevel.get(currentLevel));
		pgSteps = Sets.powerSet(temp);
		
		ArrayList<PlanGraphStep> x = new ArrayList<PlanGraphStep>();
		ArrayList<ArrayList<PlanGraphStep>> xy = new ArrayList<ArrayList<PlanGraphStep>>(); 
		for (Set<PlanGraphStep> set: pgSteps){
			for (PlanGraphStep step: set){
				x.add(step);
			}
			xy.add(new ArrayList<PlanGraphStep>(x));
			x.clear();
		}
		allPermutationsSteps.put(currentLevel,new ArrayList<ArrayList<PlanGraphStep>>(xy));
		
		int globalcount = counter.get(currentLevel);
		globalcount++;
		counter.set(currentLevel, globalcount);
		
		positionInLiteralLists.put(currentLevel,counter.get(currentLevel));
		positionInStepLists.put(currentLevel,counter.get(currentLevel));
		
		steps = allPermutationsSteps.get(currentLevel).get(positionInStepLists.get(currentLevel));
		
		defineNode(steps, goalList, currentLevel);
		System.out.println("\r\n"+ nodes.get(currentLevel).getLiterals());
		System.out.println(nodes.get(currentLevel).getSteps());
		extendedNodes++;
		visitedNodes++;
	}
			
	/**
	 * Creates a GraphPlanNode for the levels that do not include the goal literal.s
	 */
	
	public void createNewNode(){
		createMasterLiteralListByLevel(currentLevel);
	
		ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
		Set<Set<PlanGraphLiteral>> setOfLiterals;
		Set<PlanGraphLiteral> temp = new LinkedHashSet<PlanGraphLiteral>();
		temp.addAll(literalListsByLevel.get(currentLevel));
		
		if (currentLevel == 0){
			ArrayList<PlanGraphStep> nullList = new ArrayList<PlanGraphStep>();
			defineNode(nullList,literalListsByLevel.get(currentLevel),currentLevel);
		}else{
		
		createMasterStepListByLevel(currentLevel);
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		Set<Set<PlanGraphStep>> setOfSets;
		Set<PlanGraphStep> tempSteps = new LinkedHashSet<PlanGraphStep>();
		tempSteps.addAll(stepListsByLevel.get(currentLevel));

		setOfSets = Sets.powerSet(tempSteps);
		
		possibleStepSets.put(currentLevel, setOfSets);
		ArrayList<PlanGraphStep> s = new ArrayList<PlanGraphStep>();
		ArrayList<ArrayList<PlanGraphStep>> sy = new ArrayList<ArrayList<PlanGraphStep>>(); 
		for (Set<PlanGraphStep> set: possibleStepSets.get(currentLevel)){
			for (PlanGraphStep step: set){
				s.add(step);
			}
			sy.add(new ArrayList<PlanGraphStep>(s));
			s.clear();
		}
		allPermutationsSteps.put(currentLevel, new ArrayList<ArrayList<PlanGraphStep>>(sy));

		
		int globalStepCount = 1;
		counter.set(currentLevel, globalStepCount);
		
		positionInStepLists.put(currentLevel,counter.get(currentLevel));
		steps = allPermutationsSteps.get(currentLevel).get(positionInStepLists.get(currentLevel));
		
		defineNode(steps,literalListsByLevel.get(currentLevel),currentLevel);
		}
		
		extendedNodes++;
		visitedNodes++;
//		System.out.println(nodes.get(currentLevel).getLiterals() + " Level: " + currentLevel);
//		System.out.println(nodes.get(currentLevel).getSteps() + "Steps Level: " + currentLevel);
//		
		

	}
	
	
	/**
	 * Finds the first level going up from the current level that has a list of permutations that is still untried.
	 * Reaching the max level is a failure, since this method is only called from failure cases. 
	 */
	public void recalculateLevel(){
		if (currentLevel == 0){
//			if (allPermutationsLiterals.get(currentLevel).size() <= positionInLiteralLists.get(currentLevel)){
				
//			}
//			ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
//	
//			literals = allPermutationsLiterals.get(currentLevel).get(positionInLiteralLists.get(currentLevel));
//			defineNodeLiterals(literals);	
			currentLevel++;
			recalculateLevel();
			return;
		}
		
		int globalLitCount = counter.get(currentLevel);
		globalLitCount++;
		counter.set(currentLevel, globalLitCount);
		positionInLiteralLists.put(currentLevel,counter.get(currentLevel));
		positionInStepLists.put(currentLevel,counter.get(currentLevel));
		
	
		if (allPermutationsSteps.get(currentLevel).size() <= positionInStepLists.get(currentLevel)){
//			if (allPermutationsLiterals.get(currentLevel).size() <= positionInLiteralLists.get(currentLevel)){
				currentLevel++;
			
				if (currentLevel >  currentMaxLevel){
					currentLevel = currentMaxLevel;
					pg.extend();
					currentMaxLevel = pg.countLevels() - 1;
					counter.clear();
					for (int i = 0; i <= currentMaxLevel; i++){
						counter.add(0);
					}
					createGoalNode();
				
//					while (currentLevel > 0){
//						createNewNode();
//					}
					
					
					return;
				}
				recalculateLevel();
				
				return;
		}
		
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();		
		steps = allPermutationsSteps.get(currentLevel).get(positionInStepLists.get(currentLevel));
		defineNodeSteps(steps);
		
	}
	
	
	public void continueDown(){
	
		if ((currentLevel > 0)){ 
	
		createMasterLiteralListByLevel(currentLevel);
		
		ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
		Set<Set<PlanGraphLiteral>> setOfLiterals;
		Set<PlanGraphLiteral> temp = new LinkedHashSet<PlanGraphLiteral>();
		temp.addAll(literalListsByLevel.get(currentLevel));
		setOfLiterals = Sets.powerSet(temp);
	
		possibleLiteralSets.put(currentLevel, setOfLiterals);

		
		ArrayList<PlanGraphLiteral> x = new ArrayList<PlanGraphLiteral>();
		ArrayList<ArrayList<PlanGraphLiteral>> xy = new ArrayList<ArrayList<PlanGraphLiteral>>(); 
		for (Set<PlanGraphLiteral> set: setOfLiterals){
			for (PlanGraphLiteral step: set){
				x.add(step);
			}
			xy.add(new ArrayList<PlanGraphLiteral>(x));
			x.clear();
		}
		allPermutationsLiterals.put(currentLevel, new ArrayList<ArrayList<PlanGraphLiteral>>(xy));
	
		int globalLitCount = 1;
		counter.set(currentLevel, globalLitCount);
		
		positionInLiteralLists.put(currentLevel,counter.get(currentLevel));
		positionInStepLists.put(currentLevel,counter.get(currentLevel));
		literals = allPermutationsLiterals.get(currentLevel).get(positionInLiteralLists.get(currentLevel));
		
		if (currentLevel == 0){
			ArrayList<PlanGraphStep> nullList = new ArrayList<PlanGraphStep>();
			defineNode(nullList,literalListsByLevel.get(currentLevel),currentLevel);
		}else{
		
			createMasterStepListByLevel(currentLevel);
		
			ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
			Set<Set<PlanGraphStep>> setOfSets;
			Set<PlanGraphStep> tempSteps = new LinkedHashSet<PlanGraphStep>();
			tempSteps.addAll(stepListsByLevel.get(currentLevel));
			setOfSets = Sets.powerSet(tempSteps);
	
			possibleStepSets.put(currentLevel, setOfSets);
			ArrayList<PlanGraphStep> s = new ArrayList<PlanGraphStep>();
			ArrayList<ArrayList<PlanGraphStep>> sy = new ArrayList<ArrayList<PlanGraphStep>>(); 
			for (Set<PlanGraphStep> set: possibleStepSets.get(currentLevel)){
				for (PlanGraphStep step: set){
					s.add(step);
				}
				sy.add(new ArrayList<PlanGraphStep>(s));
				s.clear();
			}
		
			allPermutationsSteps.put(currentLevel, new ArrayList<ArrayList<PlanGraphStep>>(sy));
			positionInStepLists.put(currentLevel,counter.get(currentLevel));
	
			steps = allPermutationsSteps.get(currentLevel).get(positionInStepLists.get(currentLevel));
		
			defineNode(steps,literalListsByLevel.get(currentLevel),currentLevel);
			}
		}
	}
	
	/**Compares a list of preconditions to a list of literals, to see if the preconditions contain the literals.
	* returns false if there is a missing literal, true if all literals necessary for the step exist.
	*/
	public boolean checkPreConditionsBetweenLevels(ArrayList<PlanGraphLiteral> lits){
	
		ArrayList<Expression> regSteps = new ArrayList<Expression>();
		ArrayList<Literal> preConditionLiterals = new ArrayList<Literal>();
		ArrayList<Literal> pLiterals = new ArrayList<Literal>();
		
		for (PlanGraphStep step: nodes.get(currentLevel + 1).getSteps()){
			regSteps.add(step.getStep().precondition);
		}
		
		for(Expression reg: regSteps){
			for (Literal l: expressionToLiterals(reg)){
				preConditionLiterals.add(l);
			}
		}
		
		for (PlanGraphLiteral lit: lits){
			pLiterals.add(lit.getLiteral());
		}

		return pLiterals.containsAll(preConditionLiterals);
	}
	
	/** Check if the given list of literals equals the list of initial literals of the problem */
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
		
		if (!problem.isSolution(createTotalOrderPlan())){
			return false;
		}
		
	
		
		return true;
		
	}

	/** Possible function to check if there is a solution before any searching is done. */
	public boolean checkGoalInInitial(){
		ArrayList<Literal> initialLiterals = new ArrayList<Literal>();
		initialLiterals = expressionToLiterals(problem.initial.toExpression());
		for (Literal lit: expressionToLiterals(problem.goal)){
			if (!initialLiterals.contains(lit)) return false;
		}
		return true;
	}	
	
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
		nodes.put(level,node);
	}
	
	/** Change the steps of the node at the current level. */
	public void defineNodeSteps(ArrayList<PlanGraphStep> steps){
		nodes.get(currentLevel).clearSteps();
		
		for (PlanGraphStep step: steps ){
			nodes.get(currentLevel).addSteps(step);
		}
	}
	
	/** Change the literals of the node at the current level.*/
	public void defineNodeLiterals(ArrayList<PlanGraphLiteral> literals){
		nodes.get(currentLevel).clearLiterals();
		
		for (PlanGraphLiteral literal: literals ){
			nodes.get(currentLevel).addLiterals(literal);
		}
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
	/**Increased each time there is a new GraphPlanNode.*/
	public int countVisited() {
		return this.visitedNodes;
	}

	@Override
	/**Increased each time there is a new GraphPlanNode, since we only travel down the node that was just created.*/
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
