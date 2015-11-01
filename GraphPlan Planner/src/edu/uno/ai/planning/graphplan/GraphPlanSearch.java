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
	
	HashMap<Integer, Integer> positionInMasterLists = new HashMap<Integer, Integer>();
	
	ArrayList<PlanGraphStep> aPermutationOfSteps = new ArrayList<PlanGraphStep>();
	
	ArrayList<ArrayList<PlanGraphStep>> listOfPermutations = new ArrayList<ArrayList<PlanGraphStep>>();
	
	Set<Set<PlanGraphStep>> pgSteps;
	
	HashMap<Integer, Set<Set<PlanGraphStep>>> possibleStepSets = new HashMap<Integer, Set<Set<PlanGraphStep>>>();
	
	HashMap<Integer, Set<Set<PlanGraphLiteral>>> possibleLiteralSets = new HashMap<Integer, Set<Set<PlanGraphLiteral>>>();
	
	Set<Set<PlanGraphLiteral>> pgLiterals;
	
	Boolean moreSolutions = true;
			
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
		TotalOrderPlan solution = null;
		System.out.println(pg);
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
			
			finished = goalReached(nodes.get(currentLevel).getLiterals());
			if (finished == true){
				return;
			}
			if (currentLevel == 0){
				recalculateLevel();
				while (currentLevel > 0){
					continueDown();
				}
				finished = goalReached(nodes.get(currentLevel).getLiterals());
				if (finished == true){
					return;
				}
				while (finished == false){
					recalculateLevel();
					
					if (currentLevel == currentMaxLevel){
						pg.extend();
						currentMaxLevel = pg.countLevels() - 1;
						counter.clear();
						for (int i = 0; i <= currentMaxLevel; i++){
							counter.add(0);
						}
						createGoalNode();
						while (currentLevel > 0){
							createNewNode();
						}
						System.out.println("herrooo");
					}
					while (currentLevel > 0){
						continueDown();
					}
					finished = goalReached(nodes.get(currentLevel).getLiterals());
					if (finished == true){
						return;
					}
				}
			}
//			if (currentLevel == 0 && !pg.isLeveledOff() && finished == false && moreSolutions){
//				currentLevel++;
//				recalculateLevel();
//				finished = goalReached(nodes.get(currentLevel).getLiterals());
//			}
//					counter++;
//					currentLevel = counter;
//					if (currentLevel == currentMaxLevel){
//						pg.extend();
//						currentMaxLevel++;
//						createGoalNode();
//					}
//				}
//			}
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
		Iterator<Step> x = solution.iterator();
		while(x.hasNext()){
			System.out.println(x.next());
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
		stepListsByLevel.put(currentLevel,tempList);
	}
	
	public void createMasterStepListByLevel(int level){
		ArrayList<PlanGraphStep> tempList = new ArrayList<PlanGraphStep>();
		Set<PlanGraphStep> deleteRepeats = new HashSet<PlanGraphStep>();
		
		for (PlanGraphLiteral findAGoal: allPermutationsLiterals.get(currentLevel).get(positionInMasterLists.get(currentLevel))){
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
			for (Literal effect: expressionToLiterals(step.getStep().precondition)){
				for (PlanGraphLiteral literal: pg.getAllPossiblePlanGraphEffects()){
					if (literal.existsAtLevel(currentLevel)){
						if(effect.equals(literal.getLiteral())){
							System.out.println(effect + "!!");
							System.out.println(step + "!!!");
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
		
		positionInMasterLists.put(currentLevel,counter.get(currentLevel));
		
		steps = allPermutationsSteps.get(currentLevel).get(positionInMasterLists.get(currentLevel));
		
//		for (int i = 1; i <allPermutationsSteps.size(); i++){
//			System.out.println(allPermutationsSteps.get(i));
//		}	
		
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
		
		int globalLitCount = counter.get(currentLevel);
		globalLitCount++;
		counter.set(currentLevel, globalLitCount);
		
		positionInMasterLists.put(currentLevel,counter.get(currentLevel));
		literals = allPermutationsLiterals.get(currentLevel).get(positionInMasterLists.get(currentLevel));
		
		
		if (currentLevel == 0){
			ArrayList<PlanGraphStep> nullList = new ArrayList<PlanGraphStep>();
			defineNode(nullList,literals,currentLevel);
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

//		int globalStepCount = counter.get(currentLevel);
//		counter.set(currentLevel, globalStepCount);
//		positionInMasterLists.put(currentLevel,counter.get(currentLevel));
		
		steps = allPermutationsSteps.get(currentLevel).get(positionInMasterLists.get(currentLevel));
		
		defineNode(steps,literals,currentLevel);
		}
		
		extendedNodes++;
		visitedNodes++;
		System.out.println(nodes.get(currentLevel).getLiterals() + " Level: " + currentLevel);
		System.out.println(nodes.get(currentLevel).getSteps() + "Steps Level: " + currentLevel);
//		System.exit(0);
	}
	
	
	/**
	 * Finds the first level going up from the current level that has a list of permutations that is still untried.
	 * Reaching the max level is a failure, since this method is only called from failure cases. 
	 */
	public void recalculateLevel(){
		int globalLitCount = counter.get(currentLevel);
		globalLitCount++;
		counter.set(currentLevel, globalLitCount);
		positionInMasterLists.put(currentLevel,counter.get(currentLevel));
		
		if (currentLevel == 0){
			if (allPermutationsLiterals.get(currentLevel).size() == positionInMasterLists.get(currentLevel)){
				currentLevel++;
			
				recalculateLevel();
	
				return;
			}
			ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
	
			literals = allPermutationsLiterals.get(currentLevel).get(positionInMasterLists.get(currentLevel));
			defineNodeLiterals(literals);	
			return;
		}
		
		if (allPermutationsSteps.get(currentLevel).size() == positionInMasterLists.get(currentLevel)){
			if (allPermutationsLiterals.get(currentLevel).size() == positionInMasterLists.get(currentLevel)){
				currentLevel++;
				
				recalculateLevel();
				
				return;
			}
			
			ArrayList<PlanGraphLiteral> literals = new ArrayList<PlanGraphLiteral>();
			literals = allPermutationsLiterals.get(currentLevel).get(positionInMasterLists.get(currentLevel));
			
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
			positionInMasterLists.put(currentLevel,counter.get(currentLevel));
		
			steps = allPermutationsSteps.get(currentLevel).get(positionInMasterLists.get(currentLevel));
		
			defineNode(steps,literals,currentLevel);
		}
		
		ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
		int globalStepCount = counter.get(currentLevel);
		counter.set(currentLevel, globalStepCount);
		positionInMasterLists.put(currentLevel,counter.get(currentLevel));
		
		steps = allPermutationsSteps.get(currentLevel).get(positionInMasterLists.get(currentLevel));
		defineNodeSteps(steps);
//		System.exit(0);
	}
	
	
	public void continueDown(){
//		
		
		if ((currentLevel > 0) &&(currentLevel != currentMaxLevel)){ 
			System.out.println("repeatforever");
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
		
		positionInMasterLists.put(currentLevel,counter.get(currentLevel));
		literals = allPermutationsLiterals.get(currentLevel).get(positionInMasterLists.get(currentLevel));
		
		if (currentLevel == 0){
			ArrayList<PlanGraphStep> nullList = new ArrayList<PlanGraphStep>();
			defineNode(nullList,literals,currentLevel);
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
			positionInMasterLists.put(currentLevel,counter.get(currentLevel));
	
			steps = allPermutationsSteps.get(currentLevel).get(positionInMasterLists.get(currentLevel));
		
			defineNode(steps,literals,currentLevel);
			}
		}
	}
	
	public ArrayList<PlanGraphLiteral> checkForFailure(ArrayList<PlanGraphLiteral> list){	
		ArrayList<PlanGraphLiteral> aList = new ArrayList<PlanGraphLiteral>();
		
		for (int i = 0; i < aList.size(); i++){
			for (int j = i +1; j < aList.size(); j++){
				if (isMutex(aList.get(i), aList.get(j))){
					aList.remove(i);
					checkForFailure(aList);
				}
			}
		}
		return aList;
	}
	
	public ArrayList<PlanGraphStep> checkMutexSteps(ArrayList<PlanGraphStep> list){	
		ArrayList<PlanGraphStep> changedList = new ArrayList<PlanGraphStep>();
		changedList.addAll(list);
		
		for (int i = 0; i < changedList.size(); i++){
			for (int j = i +1; j < changedList.size(); j++){
				if (isMutex(changedList.get(i), changedList.get(j))){
					changedList.remove(i);
					checkMutexSteps(changedList);
				}
			}
		}
		
		return changedList;
		
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
