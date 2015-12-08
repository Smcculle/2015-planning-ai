package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.graphplan.PlanGraph;
import edu.uno.ai.planning.graphplan.PlanGraphLevelMutex;
import edu.uno.ai.planning.graphplan.PlanGraphLiteral;
import edu.uno.ai.planning.graphplan.PlanGraphStep;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import edu.uno.ai.planning.util.ConversionUtil;

public class LPGActionGraph implements Comparable<LPGActionGraph> {
	
	/** maximum level for the graph */
	private int maxLevel; 
	
	private int maxExtend = 2;
	
	/** random number generator */
	private final Random rand;
	
	/** Measure (from 0 to 1) of how much we prefer resolving inconsistencies at earlier levels */
	private static final float INCONSISTENCY_WEIGHT = 0.0f; 
	
	/** PlanGraph for this problem shared by all action graphs. */
	private PlanGraph graph;
	
	/** Contains a mapping for literals to their corresponding persistent steps */
	private static Map<PlanGraphLiteral, PlanGraphStep> persistentSteps;
	
	/** List of steps in the action graph indexed by level */
	private Map<Integer, Set<PlanGraphStep>> steps;
	
	/** List of facts in the action graph indexed by level */
	private Map<Integer, Set<PlanGraphLiteral>> facts; 
	
	/** List of inconsistencies in the action graph indexed by level */
	private Map<Integer, Set<LPGInconsistency>> inconsistencies;
	
	/** number of current inconsistencies */
	private int inconsistencyCount;
	
	/** graph Quality */
	private int graphQuality; 
	

	/**
	 * Initializes an empty action graph only containing special actions start, end, and noops of start
	 * @param problem Problem we are trying to solve
	 * @param graph 
	 */
	public LPGActionGraph(Problem problem, PlanGraph graph) {
		
		this.graph = graph;
		LPGActionGraph.persistentSteps = getPersistentSteps();
		maxLevel = graph.countLevels() - 1;
		rand = new Random();
		initializeMaps();
		addStartAndEndSteps(problem);
		updateUnsupportedPreconditionInconsistencies(maxLevel);
		graphQuality = calculateQuality();
	}
	
	/** Used to copy an action graph */
	public LPGActionGraph(LPGActionGraph actionGraph) {
	
		this.maxLevel = actionGraph.maxLevel;
		this.inconsistencyCount = actionGraph.inconsistencyCount;
		this.rand = new Random();
		this.steps = DeepCloneMap.deepClone(actionGraph.steps);
		this.facts = DeepCloneMap.deepClone(actionGraph.facts);
		this.inconsistencies = DeepCloneMap.deepClone(actionGraph.inconsistencies);
		this.graph = actionGraph.graph;
		this.graphQuality = actionGraph.graphQuality;
	}

	private LPGActionGraph copyActionGraph() {
		return new LPGActionGraph(this);
	}
	
	/** Add empty collections to graph */
	private void initializeMaps(){
		
		steps = new HashMap<Integer, Set<PlanGraphStep>>();
		facts = new HashMap<Integer, Set<PlanGraphLiteral>>();
		inconsistencies = new HashMap<Integer, Set<LPGInconsistency>>();
		
		for(int i = 0; i <= maxLevel; i++) {
			steps.put(i, new HashSet<PlanGraphStep>());
			facts.put(i, new HashSet<PlanGraphLiteral>());
			inconsistencies.put(i, new HashSet<LPGInconsistency>());
		}
		
		/* This level holds only the special action end which has the goals as preconditions */
		steps.put(maxLevel + 1, new HashSet<PlanGraphStep>());
	}
	
	/** Create special steps start and end, connect their parents/children and add to graph */
	private void addStartAndEndSteps(Problem problem){
		
		Expression startEffects = problem.initial.toExpression();
		Expression goal = problem.goal;
		
		/* connect end step to the preconditions (goals) */
		List<PlanGraphLiteral> endParents = new ArrayList<PlanGraphLiteral>();
		for (Literal literal : ConversionUtil.expressionToLiterals(problem.goal)) {
			PlanGraphLiteral pgLiteral = graph.getPlanGraphLiteral(literal);
			endParents.add(pgLiteral);
		}
		
		/* connect start step with effects (initial conditions) */
		List<PlanGraphLiteral> startChildren = new ArrayList<PlanGraphLiteral>();
		for (Literal literal : ConversionUtil.expressionToLiterals(problem.initial.toExpression())) {
			PlanGraphLiteral pgLiteral = graph.getPlanGraphLiteral(literal);
			startChildren.add(pgLiteral);
		}
		PlanGraphStep start = new PlanGraphStep(
				new Step("start", Expression.TRUE, startEffects), new ArrayList<PlanGraphLiteral>(), startChildren);
		
		PlanGraphStep end = new PlanGraphStep(
				new Step("end", goal, Expression.TRUE), endParents, new ArrayList<PlanGraphLiteral>());
		
		steps.get(maxLevel + 1 ).add(end);
		if(maxLevel <= 1)
			addStep(start, 0);
		facts.get(0).addAll(startChildren);
		
		for (PlanGraphLiteral pgLiteral : startChildren) {
			addStep(persistentSteps.get(pgLiteral), 1, false);
		}
		
	}

	public int getInconsistencyCount() {
		
		return inconsistencyCount;
	}
	
	/** Chooses a random integer bounded by inconsistencyCount and returns that inconsistency */ 
	public LPGInconsistency chooseInconsistency() {
		
		LPGInconsistency chosenInconsistency = null; 
		int inconsistencyCounter;
		
		/* rare instances where inconsistency count is 0 */
		if (inconsistencyCount <= 0 ) {
			countInconsistencies();
			if (inconsistencyCount <= 0)
				return null;
		}
			
		/* check to see if we will prefer earlier inconsistencies or not */
		if (rand.nextFloat() <= INCONSISTENCY_WEIGHT) 
			inconsistencyCounter = rand.nextInt((int) Math.ceil(inconsistencyCount*INCONSISTENCY_WEIGHT));
		
		/* choose a random number and iterate until we get to that inconsistency */
		else
			inconsistencyCounter = rand.nextInt(inconsistencyCount);
			
			for(InconsistencyIterator iterator = this.new InconsistencyIterator(); iterator.hasNext();){
				if (inconsistencyCounter > 0 ) {
					iterator.next();
					inconsistencyCounter--;
				}
				else {
					chosenInconsistency = iterator.next();
					break;
				}
			}
			
			if(chosenInconsistency == null){
				chosenInconsistency = countInconsistencies();
			}
			
		return chosenInconsistency;
	}
	
	 
	/** Generates action graphs based on each possible solution to the given inconsistency */
	public List<LPGActionGraph> makeNeighborhood(LPGInconsistency inconsistency) {
		
		List<LPGActionGraph> neighborhood; 
		if (inconsistency instanceof UnsupportedPrecondition) {
			neighborhood = handleUnsupportedPrecondition((UnsupportedPrecondition)inconsistency);
			
		}
		else if (inconsistency instanceof MutexRelation)
			neighborhood = handleMutexRelation((MutexRelation) inconsistency, inconsistency.getCurrentLevel());
		
		else {
			neighborhood = null;
		}
		
		return neighborhood;
	}
	
	/** Returns a list of candidate actionGraphs that solve the current inconsistency */
	private List<LPGActionGraph> handleUnsupportedPrecondition(UnsupportedPrecondition inconsistency) {
		
		List<LPGActionGraph> neighborhood = Collections.synchronizedList(new ArrayList<LPGActionGraph>());
		int currentLevel = inconsistency.getCurrentLevel();
		PlanGraphLiteral unsupportedLiteral = inconsistency.getUnsupportedPrecondition();
		
		/* steps we can add to resolve the USP */
		// List<PlanGraphStep> addChoices = unsupportedLiteral.getParentNodes();
		List<PlanGraphStep> addChoices = findAddChoices(unsupportedLiteral);
		List<Thread> threads = new ArrayList<Thread>();
		for (PlanGraphStep stepToAdd : addChoices) {
			int initialLevel = stepToAdd.getInitialLevel();
			if (initialLevel != -1 && initialLevel <= currentLevel) {
				/* evaluate each level we can add this step to to solve inconsistency at currentLevel */
				for(int i = initialLevel; i <= currentLevel; i++) {
					
					final int index = i;
					Thread t = new Thread( new Runnable() {
					
						@Override
						public void run() {
							LPGActionGraph newAG = getAddNeighbor(stepToAdd, inconsistency, index);
							if (newAG != null){
								neighborhood.add(newAG);
							}
						}
					});
					threads.add(t);
					t.start();
				}
			}
		}
		
		/* block until all threads are done */
		for(Thread t : threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/* steps we can remove to resolve the USP*/
		List<PlanGraphStep> removeChoices = findRemoveChoices(unsupportedLiteral, currentLevel);
		if (removeChoices != null)
			neighborhood.add(getDeleteNeighbor(removeChoices, inconsistency, currentLevel));
		
		return neighborhood;
		
	}
	
	/** Copies the current action graph and returns a new A-graph solving the current inconsistency by 
	 * adding the indicated step */ 
	private LPGActionGraph getAddNeighbor(PlanGraphStep stepToAdd, 
			UnsupportedPrecondition inconsistency, int currentLevel) {
		
		LPGActionGraph neighbor = this.copyActionGraph();
		neighbor.addStep(stepToAdd, currentLevel);
		neighbor.graphQuality = neighbor.calculateQuality();
		return neighbor;
		
	}
	
	/** Copies the current action graph and returns a new A-graph solving the current inconsistency by 
	 * deleting the indicated steps */
	private LPGActionGraph getDeleteNeighbor(List<PlanGraphStep> removeChoices,
			UnsupportedPrecondition inconsistency, int currentLevel) {
		
		LPGActionGraph neighbor = this.copyActionGraph();
		
		for (PlanGraphStep stepToRemove : removeChoices) {
			neighbor.removeEffects(stepToRemove, currentLevel + 1);
			neighbor.removeInvalidMutex(stepToRemove, currentLevel + 1);
		}
		neighbor.removeInconsistency(inconsistency);
		neighbor.updateUnsupportedPreconditionInconsistencies(currentLevel + 1);
		neighbor.graphQuality = neighbor.calculateQuality();
		return neighbor;
	}

	/** Returns each possible non-persistent step that we can add to solve the inconsistency in a list */
	private List<PlanGraphStep> findAddChoices(PlanGraphLiteral unsupportedLiteral) {
		List <PlanGraphStep> addChoices = new ArrayList<PlanGraphStep>();
		
		for (PlanGraphStep pgStep : unsupportedLiteral.getParentNodes()) {
			if (!pgStep.isPersistent())
				addChoices.add(pgStep);
		}
		return addChoices;
	}
	
	/** Returns each possible step we can remove to resolve the unsupportedLiteral at currentLevel */
	private List<PlanGraphStep> findRemoveChoices(PlanGraphLiteral unsupportedLiteral, int currentLevel) {
		
		List<PlanGraphStep> removeChoices = null; 
		if (currentLevel + 1 <= maxLevel) {
			removeChoices = new ArrayList<PlanGraphStep>();
			Set<PlanGraphStep> nextLevelSteps = steps.get(currentLevel + 1);
			/* remove each step present at the next level */
			for (PlanGraphStep stepToRemove : unsupportedLiteral.getChildNodes()){
				if (nextLevelSteps.contains(stepToRemove))
					removeChoices.add(stepToRemove);
			}
		}
		
		return removeChoices;
	}
	
	/** Returns 2 neighbors from deleting each of the 2 mutex steps in the relation 
	 * @param currentLevel */
	private List<LPGActionGraph> handleMutexRelation(MutexRelation inconsistency, int currentLevel) {
		
		List<LPGActionGraph> neighbors = new ArrayList<LPGActionGraph>();
		
		/** add A-graphs resulting from removing each mutex step */
		neighbors.addAll(fixMutexStep(inconsistency.getMutexA(), inconsistency, currentLevel));
		neighbors.addAll(fixMutexStep(inconsistency.getMutexB(), inconsistency, currentLevel));
		
		return neighbors;

	}
	
	/** Returns the action graph (neighbor) by removing a step to resolve the mutex relation */ 
	private List<LPGActionGraph> fixMutexStep(PlanGraphStep stepToRemove, 
			MutexRelation inconsistency, int currentLevel) {

		List<LPGActionGraph> mutexSolutions = new ArrayList<LPGActionGraph>();
		
		/* option 1 - remove */
		LPGActionGraph neighbor1 = this.copyActionGraph();
		neighbor1.removeStep(stepToRemove, inconsistency.getCurrentLevel());
		neighbor1.graphQuality = neighbor1.calculateQuality();
		mutexSolutions.add(neighbor1);
		
		
		/* option 2 - postpone by moving up a level*/
		LPGActionGraph neighbor2 = this.copyActionGraph();
		//neighbor2.extendGraph(currentLevel + 1);
		neighbor2.removeEffects(stepToRemove, inconsistency.getCurrentLevel(), false);
		neighbor2.removeInvalidMutex(stepToRemove, inconsistency.getCurrentLevel());
		
		if (maxLevel <= 8 && maxExtend > 0) {
			neighbor2.extendGraph(currentLevel + 1);
			maxExtend--;
		}
		
		if(currentLevel + 1 <= maxLevel) {
			neighbor2.addStep(stepToRemove, currentLevel + 1);
			neighbor2.graphQuality = neighbor2.calculateQuality();
			mutexSolutions.add(neighbor2); 
		}
		
		/* option 3 - anticipate by moving mutex step back a level
		LPGActionGraph neighbor3 = this.copyActionGraph();
		neighbor3.removeStep(stepToRemove, inconsistency.getCurrentLevel());
		neighbor3.extendGraph(currentLevel);
		neighbor3.addStep(stepToRemove, currentLevel);
		neighbor3.graphQuality = neighbor3.calculateQuality();
		mutexSolutions.add(neighbor3);*/
		
		return mutexSolutions;
	}
	
	
	
	/** Adds a step and its effects to the current level and propagates effects until blocked */
	public void addStep(PlanGraphStep stepToAdd, int currentLevel) {
		addStep(stepToAdd, currentLevel, true);
	}
	
	/** Adds a step and its effects to the current level without propagation*/
	public void addStep(PlanGraphStep stepToAdd, int currentLevel, boolean propagate) {
		
		steps.get(currentLevel).add(stepToAdd);
		facts.get(currentLevel).addAll(stepToAdd.getChildNodes());
		updateInconsistencies(stepToAdd, currentLevel);
		if (propagate == true)
			propagateAddStep(stepToAdd, currentLevel);
	}
	
	/** Adds a step and checks for supported preconditions, 
	 * used when propagating in order to reduce redundant inconsistency checking */
	private void _addStep(PlanGraphStep stepToAdd, int currentLevel) {
		
		steps.get(currentLevel).add(stepToAdd);
		facts.get(currentLevel).addAll(stepToAdd.getChildNodes());
		checkSupportedPreconditions(stepToAdd, currentLevel);
		propagateAddStep(stepToAdd, currentLevel);
	}
	
	/** Propagates effects to later levels until mutex is found */
	private void propagateAddStep(PlanGraphStep stepToAdd, int currentLevel) {

		/* add step to next level until we've reached the max */
		if (currentLevel < maxLevel) {
			
			/* propagate if persistent and does not exist in next level if not mutex to any step at next level*/
			Set<PlanGraphStep> nextLevelSteps = steps.get(currentLevel + 1);
			if (stepToAdd.isPersistent()) { 
				if (!nextLevelSteps.contains(stepToAdd)) {
					PlanGraphLevelMutex nextLevelMutex = (PlanGraphLevelMutex)graph.getLevel(currentLevel + 1);
					if (!isMutexWithSteps(nextLevelMutex, nextLevelSteps, stepToAdd)) {
						_addStep(stepToAdd, currentLevel + 1);
					}
				}
			}
			
			/* get persistent steps for each effect and propagate those */
			else {
				for (PlanGraphLiteral effect : stepToAdd.getChildNodes()) {
					PlanGraphStep persistentStep = persistentSteps.get(effect);
					propagateAddStep(persistentStep, currentLevel);
				}
			}
		}
	}
	
	/** Inserts a new level in the actionGraph at level so that we can anticipate/postpone a mutex relation */
	public void extendGraph(int level) {
		maxLevel++;
		steps.put(maxLevel+1, steps.get(maxLevel));
		if (maxLevel == graph.countLevels()){
			graph.extend();
		}
			
		
		/* start at highest level and move each level up until we get to insert level*/
		for(int i = maxLevel; i > level; i--) {
			steps.put(i, steps.get(i-1));
			facts.put(i, facts.get(i-1));
			inconsistencies.put(i, inconsistencies.get(i-1));
		}
		
		/* add new sets to level */
		// Set<PlanGraphStep> newSteps = new HashSet<PlanGraphStep>();
		steps.put(level, new HashSet<PlanGraphStep>());
		facts.put(level, new HashSet<PlanGraphLiteral>());
		inconsistencies.put(level, new HashSet<LPGInconsistency>());
		
		/* propagate facts from previous level via no-ops
		for (PlanGraphLiteral pgLiteral : facts.get(level-1)) {
			addStep(persistentSteps.get(pgLiteral), level, false);
		}*/
		facts.get(level).addAll(facts.get(level-1));
		 
	}
	/** Checks testStep against the given steps for any mutex relations */
	private boolean isMutexWithSteps(PlanGraphLevelMutex mutexLevel,
			Set<PlanGraphStep> steps, PlanGraphStep testStep) {
		
		boolean foundMutex = false;
		for (PlanGraphStep step : steps) {
			if (mutexLevel.isMutex(step, testStep)) {
				foundMutex = true;
				break;
			}
		}
		
		return foundMutex;
	}
	
	/** Create a mapping of steps to persistent steps */
	private Map<PlanGraphLiteral, PlanGraphStep> getPersistentSteps(){
		
		HashMap<PlanGraphLiteral, PlanGraphStep> persistentSteps = new HashMap<PlanGraphLiteral, PlanGraphStep>();
		for(PlanGraphStep step : graph.getPersistantSteps())
			persistentSteps.put(step.getChildNodes().get(0), step);
		
		return persistentSteps;
	}
	
	/** Check for new unsupported preconditions */
	private void updateUnsupportedPreconditionInconsistencies(int currentLevel) {
		
		Set<LPGInconsistency> currentInconsistencies = inconsistencies.get(currentLevel);
		/** check for unsupported preconditions in the next level */
		if (currentLevel <= maxLevel) {
			Set<PlanGraphStep> nextLevelSteps = steps.get(currentLevel + 1);
			Set<PlanGraphLiteral> currentFacts = facts.get(currentLevel);
			for (PlanGraphStep pgStep : nextLevelSteps) {
				for (PlanGraphLiteral pgLiteral : pgStep.getParentNodes()) {
					if (!currentFacts.contains(pgLiteral)){
						currentInconsistencies.add( new UnsupportedPrecondition(pgLiteral, currentLevel));
						inconsistencyCount++;
					}
				}
			}
		}
	}
	
	/** Check any mutex relations that no longer hold after stepToRemove is removed*/  
	private void removeInvalidMutex(PlanGraphStep stepToRemove, int currentLevel) {
		
		/** check for other mutex that are no longer valid */
		for (Iterator<LPGInconsistency> iterator = inconsistencies.get(currentLevel).iterator(); iterator.hasNext();) {
			LPGInconsistency lpgInconsistency = iterator.next();
			if ((lpgInconsistency instanceof MutexRelation) && ((MutexRelation)lpgInconsistency).contains(stepToRemove)) {
				iterator.remove();
				inconsistencyCount--;
			}
		}
	}
	
	/** Removes a step and updates inconsistencies */
	private void removeStep(PlanGraphStep stepToRemove, int currentLevel) {
		
		removeEffects(stepToRemove, currentLevel);
		removeInvalidMutex(stepToRemove, currentLevel);
		updateUnsupportedPreconditionInconsistencies(currentLevel);
		
	}
	
	/** Removes a step and any unsupported effects from this level and propagates removal */
	private void removeEffects(PlanGraphStep stepToRemove, int currentLevel) {
		
		removeEffects(stepToRemove, currentLevel, true);
	}
	
	/** Removes a step and any unsupported effects from this level and propagates if true removal */
	private void removeEffects(PlanGraphStep stepToRemove, int currentLevel, boolean propagate) {
		
		Set<PlanGraphStep> currentSteps = steps.get(currentLevel);
		Set<PlanGraphLiteral> currentFacts = facts.get(currentLevel);
		/* propagate removal until steps no longer exists*/
		if (currentSteps.contains(stepToRemove)){
			
			currentSteps.remove(stepToRemove);
			for (PlanGraphLiteral pgLiteral : stepToRemove.getChildNodes()) {
				/* check to see if another step at this level has the effect pgLiteral */
				if (Collections.disjoint(currentSteps, pgLiteral.getParentNodes())) {
					currentFacts.remove(pgLiteral);
					if (propagate == true && currentLevel + 1 < maxLevel) {
						if (stepToRemove.isPersistent()) {
							removeStep(stepToRemove, currentLevel + 1);
						}
						else {
							PlanGraphStep persistentStep = persistentSteps.get(pgLiteral);
							removeStep(persistentStep, currentLevel + 1);
						}
					}
				}
			}
		}
	}
	
	/** When adding steps, checks for any USP to add/remove or mutex relations to add */
	private void updateInconsistencies(PlanGraphStep newStep, int currentLevel) {
		
		Set<LPGInconsistency> currentLevelInconsistencies = inconsistencies.get(currentLevel);

		/** remove any unsupported precondition inconsistencies that newStep supports */ 
		checkSupportedPreconditions(newStep, currentLevel, currentLevelInconsistencies);
		
		/** add any new mutex steps */
		checkMutexSteps(newStep, currentLevel, currentLevelInconsistencies);
		
		/** check last level facts for preconditions of newStep */
		checkUnsupportedPreconditions(newStep, currentLevel);
	}

	/** Check and remove any USP inconsistencies newStep now supports */
	private void checkSupportedPreconditions(PlanGraphStep newStep,
			int currentLevel, Set<LPGInconsistency> currentLevelInconsistencies) {
		for (Iterator<LPGInconsistency> iterator = currentLevelInconsistencies.iterator(); iterator.hasNext();) {
			LPGInconsistency lpgInconsistency = iterator.next();
			if (lpgInconsistency instanceof UnsupportedPrecondition) {
				PlanGraphLiteral unsupportedPrecondition = ((UnsupportedPrecondition)lpgInconsistency).getUnsupportedPrecondition();
				if (newStep.getChildNodes().contains(unsupportedPrecondition)) {
					iterator.remove();
					inconsistencyCount--;
				}
			}
		}
	}
	
	/** Check and remove any USP inconsistencies newStep now supports */
	private void checkSupportedPreconditions(PlanGraphStep newStep, int currentLevel) {
		checkSupportedPreconditions(newStep, currentLevel, inconsistencies.get(currentLevel));
	}

	/** Check any mutex relations between newStep and steps at this level */
	private void checkMutexSteps(PlanGraphStep newStep, int currentLevel,
			Set<LPGInconsistency> currentLevelInconsistencies) {
		PlanGraphLevelMutex pgLevel = (PlanGraphLevelMutex) graph.getLevel(currentLevel);
		
		/* add any new mutex steps at this level */
		for (PlanGraphStep step : steps.get(currentLevel)) {
			if (pgLevel.isMutex(step, newStep)){
				currentLevelInconsistencies.add( new MutexRelation(step, newStep, currentLevel));
				inconsistencyCount++;
			}
		}
	}

	/** Checks facts of (currentLevel - 1) for preconditions of newStep, adding any USP found */
	private void checkUnsupportedPreconditions(PlanGraphStep newStep, int currentLevel) {
		/* add unsupported preconditions for this step by checking facts of previous level */
		Set<LPGInconsistency> previousLevelInconsistencies = inconsistencies.get(currentLevel - 1);
		Set<PlanGraphLiteral> previousLevelFacts = facts.get(currentLevel - 1);
		for (PlanGraphLiteral pgLiteral : newStep.getParentNodes()) {
			/* if initial level is 0, satisfied by initial state. No need for inconsistency */
			 if (pgLiteral.getInitialLevel() != 0 && !previousLevelFacts.contains(pgLiteral)) { 
				previousLevelInconsistencies.add(new UnsupportedPrecondition(pgLiteral, currentLevel - 1));
				inconsistencyCount++;
			}
		}
	}
	
	/** Removes inconsistency and decreases count */
	private void removeInconsistency(LPGInconsistency inconsistency){
		int currentLevel = inconsistency.getCurrentLevel();
		removeInconsistency(inconsistency, inconsistencies.get(currentLevel));
	}

	/** Removes inconsistency and decreases count */
	private void removeInconsistency(LPGInconsistency inconsistency, Set<LPGInconsistency> currentInconsistencies){
		currentInconsistencies.remove(inconsistency);
		inconsistencyCount--;
	}
	
	/** Returns a plan from the steps contained in action graph */
	public TotalOrderPlan getTotalOrderPlan(TotalOrderPlan plan) {
		
		for (int i = 1; i <= maxLevel; i++) {
			for (PlanGraphStep pgStep : steps.get(i)) {
				//System.out.println("Level " + i + " " + pgStep);
				if(!pgStep.isPersistent()) {
					plan = plan.addStep(pgStep.getStep());
				}
			}
		}
		
		return plan;
	}
	
	/** Orders steps by initial level able to achieve that step */
	public TotalOrderPlan getOrderedTotalPlan(TotalOrderPlan plan) {
		
		List<PlanGraphStep> stepList = new ArrayList<PlanGraphStep>(Collections.nCopies(maxLevel, null));
		/** Impose ordering on steps based on initial level of each step */
		for (int i = 1; i <= maxLevel; i++) {
			for (PlanGraphStep pgStep : steps.get(i)) {
				if(!pgStep.isPersistent()) {
					int initialLevel = pgStep.getInitialLevel();
					int j = initialLevel;
					if (stepList.get(j) == null)
						stepList.add(j,pgStep);
					else {
						while(j < stepList.size() && stepList.get(j) != null) {
							if(stepList.get(j).getInitialLevel() <= initialLevel)
								j++;
							else
								break;
						}
						stepList.add(j, pgStep);
					}
				}
			}
		}
		for (PlanGraphStep pgStep : stepList) {
			if (pgStep != null) {
				plan = plan.addStep(pgStep.getStep());
			}
		}
		
		return plan;
	}
	
	@Override
	public String toString() {
		return String.format("steps: %s"
				+ "\nfacts: %s"
				+ "\ninconsistencies: %s"
				+ "\ngraph quality=%d", steps, facts, inconsistencies, graphQuality);
	}
	
	/** Checks if a given action graph is a solution.  Verifies that goal conditions are met */
	public boolean isSolution() {
		
		boolean foundSolution = false;
		
		if (inconsistencyCount == 0) {
			foundSolution = true;
			Set<PlanGraphLiteral> lastLevelFacts = facts.get(maxLevel);
			PlanGraphStep endStep = steps.get(maxLevel + 1).iterator().next();
			for (PlanGraphLiteral goalCondition : endStep.getParentNodes()) {
				if (!lastLevelFacts.contains(goalCondition)) {
					inconsistencies.get(maxLevel).add(new UnsupportedPrecondition(goalCondition, maxLevel));
					inconsistencyCount++;
					foundSolution = false;
				}
			}
		}
		
		return foundSolution;
	}
	
	/** Recursively computes plan quality as number of inconsistencies + weighted value for nonpersistent steps
	 * in a level + recursive cost calculation for unsupported preconditions.  
	 * @return integer quality, 0 indicates a solution.  
	 */
	private int calculateQuality() {
		
		int quality = inconsistencyCount;
		
		/* found solution */
		if (inconsistencyCount == 0)
			return 0; 
		
		/* add cost for each unsupported precondition*/
		for(InconsistencyIterator iterator = new InconsistencyIterator(); iterator.hasNext();) {
			LPGInconsistency inconsistency = iterator.next();
			quality += inconsistency.getCurrentLevel();
			if (inconsistency instanceof UnsupportedPrecondition) {
				int currentLevel = inconsistency.getCurrentLevel();
				PlanGraphLiteral unsupportedPrecondition = ((UnsupportedPrecondition) inconsistency).getUnsupportedPrecondition();
				quality += costToSupport(unsupportedPrecondition, currentLevel);
				quality += inconsistency.getInitialLevel();
			}
		}
		/* check steps, penalize levels with no real steps */
		for( int i = 1; i < steps.size() - 1; i++) {
			boolean foundStep = false;
			for(Iterator<PlanGraphStep> it = steps.get(i).iterator(); it.hasNext();) {
				PlanGraphStep next = it.next();
				if(!next.isPersistent()){
					foundStep = true;
					break;
				}
			}
			// increase quality if we have found no real steps 
			if (!foundStep)
				quality++;
		}
		
		return quality;
	}
	
	/** Calculates cost to support precondition as minimum cost of any step that supports this condition*/
	private int costToSupport(PlanGraphLiteral unsupportedPrecondition, int currentLevel) {
		
		int cost = Integer.MAX_VALUE;
		
		/* cost = 0 if part of initial conditions */
		if (currentLevel == 0 && unsupportedPrecondition.getInitialLevel() == 0)
			cost = 0;
		else if(isSupported(unsupportedPrecondition, currentLevel))
			cost = 0;
		else {
			/* cost of supporting this precondition is the cost of the cheapest step that supports it */
			for (PlanGraphStep step : unsupportedPrecondition.getParentNodes()) {
				cost = Math.min(cost, costToSupport(step, currentLevel));
			}
		}
	
			return cost;
	}

	/** Calculates cost to support step as maximum over the cost of all preconditions */
	private int costToSupport(PlanGraphStep step, int currentLevel) {
		
		int cost = 0;
		
		/* make sure we can achieve step */
		if(step.getInitialLevel() != -1 && step.getInitialLevel() <= currentLevel) {
			
			/* cost of achieving a step is the max over the cost of achieving the preconditions */
			for (PlanGraphLiteral precondition : step.getParentNodes()) {
				if (!isSupported(precondition, currentLevel - 1))
					cost = Math.max(cost, costToSupport(precondition, currentLevel - 1) + 1 );
			}
		}
		else
			cost = Integer.MAX_VALUE;
		
		return cost;
	}

	/** Returns whether a literal is supported in the action graph at currentLevel*/
	private boolean isSupported(PlanGraphLiteral literal, int currentLevel) {
		
		/* check facts for support*/
		if (currentLevel > 0)
			return facts.get(currentLevel).contains(literal);
		
		/* if currentLevel = 0, check if literal in initial conditions */
		else
			return literal.getInitialLevel() == 0; 
	}

	/** Counts number of inconsistencies and returns the last inconsistency in the list */
	public LPGInconsistency countInconsistencies() {
		InconsistencyIterator it = new InconsistencyIterator();
		int counter = 0;
		LPGInconsistency inconsistency = null;
		while(it.hasNext()) {
			inconsistency = it.next();
			counter++;
		}
		inconsistencyCount = counter;
		
		return inconsistency;
	}
	
	/** Iterator to traverse inconsistencies*/
	private class InconsistencyIterator implements Iterator<LPGInconsistency> {

		private Iterator<LPGInconsistency> iterator;
		private int index;
		
		public InconsistencyIterator() {
			
			index = 1; 
			setNextIterator();
			if (iterator == null)
				iterator = Collections.emptyIterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public LPGInconsistency next() {
			LPGInconsistency next = iterator.next();
			
			if (!iterator.hasNext()) {
				index++;
				setNextIterator();
			}
			
			return next;
		}
		
		/** Gets an iterator for the next level */
		private void setNextIterator() {
			
			for (int i = index; i < inconsistencies.size(); i++){
				Set<LPGInconsistency> inconsistencyList = inconsistencies.get(i);
				if (!inconsistencyList.isEmpty()) {
					iterator = inconsistencyList.iterator();
					break;
				}
				else
					index++;
			}
		}
	}

	public int getGraphQuality() {
		return graphQuality;
	}
	
	@Override
	public int compareTo(LPGActionGraph o) {
		return this.graphQuality - o.graphQuality;
	}
	
	/** Verifies that goals have been met and there are no inconsistencies.  Returns number of inconsistencies. */
	public int checkGoals() {
		updateUnsupportedPreconditionInconsistencies(maxLevel);
		countInconsistencies();
		graphQuality = calculateQuality();
		return this.inconsistencyCount;
	}
	
	
}
