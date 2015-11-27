package edu.uno.ai.planning.lpg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

//import javax.swing.JOptionPane;



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
	private final int maxLevel; 
	
	/** random number generator */
	private final Random rand;
	
	/** Measure (from 0 to 1) of how much we prefer resolving inconsistencies at earlier levels */
	private static final float inconsistencyWeight = 0.35f; 
	
	/** PlanGraph for this problem shared by all action graphs.  Graph is not modified*/
	private static PlanGraph graph;
	
	/** Contains a mapping for literals to their corresponding persistent steps */
	private static Map<PlanGraphLiteral, PlanGraphStep> persistentSteps;
	
	/** List of steps in the action graph indexed by level */
	private Map<Integer, Set<PlanGraphStep>> steps;
	
	/** List of facts in the action graph indexed by level */
	private Map<Integer, Set<PlanGraphLiteral>> facts; 
	
	/** List of inconsistencies in the action graph indexed by level */
	private Map<Integer, List<LPGInconsistency>> inconsistencies;
	
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
		
		LPGActionGraph.graph = graph;
		LPGActionGraph.persistentSteps = getPersistentSteps();
		maxLevel = graph.countLevels() - 1;
		rand = new Random();
		initializeMaps();
		addStartAndEndSteps(problem);
		updateUnsupportedPreconditionInconsistencies(maxLevel);
		graphQuality = calculateQuality();
	}
	
	/** Used to copy an action graph */
	private LPGActionGraph(LPGActionGraph actionGraph) {
	
		this.maxLevel = actionGraph.maxLevel;
		this.inconsistencyCount = actionGraph.inconsistencyCount;
		this.rand = new Random();
		this.steps = DeepCloneMap.deepClone(actionGraph.steps);
		this.facts = DeepCloneMap.deepClone(actionGraph.facts);
		this.inconsistencies = DeepCloneMap.deepClone(actionGraph.inconsistencies);
	}

	private LPGActionGraph copyActionGraph() {
		return new LPGActionGraph(this);
	}
	
	private void initializeMaps(){
		
		steps = new HashMap<Integer, Set<PlanGraphStep>>();
		facts = new HashMap<Integer, Set<PlanGraphLiteral>>();
		inconsistencies = new HashMap<Integer, List<LPGInconsistency>>();
		
		for(int i = 0; i <= maxLevel; i++) {
			steps.put(i, new HashSet<PlanGraphStep>());
			facts.put(i, new HashSet<PlanGraphLiteral>());
			inconsistencies.put(i, new ArrayList<LPGInconsistency>());
		}
		
		/* This level holds only the special action end which has the goals as preconditions */
		steps.put(maxLevel + 1, new HashSet<PlanGraphStep>());
	}
	
	private void addStartAndEndSteps(Problem problem){
		
		Expression startEffects = problem.initial.toExpression();
		Expression goal = problem.goal;
		
		/* connect end step to the preconditions (goals) */
		List<PlanGraphLiteral> endParents = new ArrayList<PlanGraphLiteral>();
		for (Literal literal : ConversionUtil.expressionToLiterals(problem.goal)) {
			PlanGraphLiteral pgLiteral = graph.getPlanGraphLiteral(literal);
			//inconsistencies.get(maxLevel).add(new LPGInconsistency(pgLiteral, maxLevel));
			endParents.add(pgLiteral);
		}
		
		/* connect start step with effects (initial conditions) */
		List<PlanGraphLiteral> startChildren = new ArrayList<PlanGraphLiteral>();
		for (Literal literal : ConversionUtil.expressionToLiterals(problem.initial.toExpression())) {
			PlanGraphLiteral pgLiteral = graph.getPlanGraphLiteral(literal);
			//inconsistencies.get(maxLevel).add(new LPGInconsistency(pgLiteral, maxLevel));
			startChildren.add(pgLiteral);
		}
		PlanGraphStep start = new PlanGraphStep(
				new Step("start", Expression.TRUE, startEffects), new ArrayList<PlanGraphLiteral>(), startChildren);
		
		PlanGraphStep end = new PlanGraphStep(
				new Step("end", goal, Expression.TRUE), endParents, new ArrayList<PlanGraphLiteral>());
		
		steps.get(maxLevel + 1 ).add(end);
		if(maxLevel <= 1)
			addStep(start, 0);
		
	}


	public int getInconsistencyCount() {
		
		return inconsistencyCount;
	}
	
	public LPGInconsistency chooseInconsistency() {
		
		LPGInconsistency chosenInconsistency = null; 
		
		// TODO remove this, for debugging
		float nf = rand.nextFloat();
		/* check to see if we will prefer earlier inconsistencies or not */
		if (nf <= inconsistencyWeight) {
			for (int i = 1; i < inconsistencies.size(); i++) {
				List<LPGInconsistency> inconsistenciesAtLevel = inconsistencies.get(i);
				if (!inconsistenciesAtLevel.isEmpty()) {
					chosenInconsistency = inconsistenciesAtLevel.get(rand.nextInt(inconsistenciesAtLevel.size()));
					break;
				}
			}
		} 
		
		/* choose a random number and iterate until we get to that inconsistency */
		else {
			
			int inconsistencyCounter = rand.nextInt(inconsistencyCount);
			
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
			neighborhood = handleMutexRelation((MutexRelation) inconsistency);
		else {
			//System.err.printf("Invalid inconsistency type");
			//if (inconsistency == null)
				//System.err.println(" - Inconsistency = null for some reason");
			//else
				//System.err.println(" class = " + inconsistency.getClass());
			neighborhood = null;
		}
		
		return neighborhood;
	}
	
	private List<LPGActionGraph> handleUnsupportedPrecondition(UnsupportedPrecondition inconsistency) {
		
		List<LPGActionGraph> neighborhood = new ArrayList<LPGActionGraph>();
		int currentLevel = inconsistency.getCurrentLevel();
		PlanGraphLiteral unsupportedLiteral = inconsistency.getUnsupportedPrecondition();
		
		/* steps we can add to resolve the USP */
		List<PlanGraphStep> addChoices = unsupportedLiteral.getParentNodes();
		//System.err.println("# choices = " + addChoices.size());
		List<Thread> threads = new ArrayList<Thread>();
		for (PlanGraphStep stepToAdd : addChoices) {
			int initialLevel = stepToAdd.getInitialLevel();
			if (initialLevel != -1 && initialLevel <= currentLevel) {
				Thread t = new Thread( new Runnable() {

					@Override
					public void run() {
						neighborhood.add(getAddNeighbor(stepToAdd, inconsistency, currentLevel));
					}
				});
				threads.add(t);
				t.start();
			}
		}
		
		for(Thread t : threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/* steps we can remove to resolve the USP*/
		List<PlanGraphStep> removeChoices = findRemoveChoices(unsupportedLiteral, currentLevel);
		if (removeChoices != null)
			neighborhood.add(getDeleteNeighbor(removeChoices, inconsistency, currentLevel));
		
		//System.err.println("Neighborhood size = " + neighborhood.size());
		
		return neighborhood;
		
	}
	
	/** Copies the current action graph and returns a new A-graph solving the current inconsistency by 
	 * adding the indicated step */ 
	private LPGActionGraph getAddNeighbor(PlanGraphStep stepToAdd, 
			UnsupportedPrecondition inconsistency, int currentLevel) {
		
		LPGActionGraph neighbor = this.copyActionGraph();
		neighbor.addStep(stepToAdd, currentLevel);
		//neighbor.removeInconsistency(inconsistency);
		neighbor.graphQuality = neighbor.calculateQuality();
		return neighbor;
	}
	
	/** Copies the current action graph and returns a new A-graph solving the current inconsistency by 
	 * deleting the indicated steps */
	private LPGActionGraph getDeleteNeighbor(List<PlanGraphStep> removeChoices,
			UnsupportedPrecondition inconsistency, int currentLevel) {
		
		LPGActionGraph neighbor = this.copyActionGraph();
		//if (!removeChoices.isEmpty()) System.err.println("+1 remove choices");
		
		for (PlanGraphStep stepToRemove : removeChoices) {
			neighbor.removeEffects(stepToRemove, currentLevel + 1);
			neighbor.removeInvalidMutex(stepToRemove, currentLevel + 1);
		}
		neighbor.removeInconsistency(inconsistency);
		neighbor.updateUnsupportedPreconditionInconsistencies(currentLevel);
		neighbor.graphQuality = neighbor.calculateQuality();
		
		return neighbor;
	}

	/** Returns a list of steps to remove to resolve the unsupportedLiteral at currentLevel */
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
	
	/** Returns 2 neighbors from deleting each of the 2 mutex steps in the relation */
	private List<LPGActionGraph> handleMutexRelation(MutexRelation inconsistency) {
		
		List<LPGActionGraph> neighbors = new ArrayList<LPGActionGraph>();
		
		/** add A-graphs resulting from removing each mutex step */
		neighbors.add(fixMutexStep(inconsistency.getMutexA(), inconsistency));
		neighbors.add(fixMutexStep(inconsistency.getMutexB(), inconsistency));
		
		return neighbors;

	}
	
	/** Returns the action graph (neighbor) by removing a step to resolve the mutex relation */ 
	private LPGActionGraph fixMutexStep(PlanGraphStep stepToRemove, MutexRelation inconsistency) {

		LPGActionGraph neighbor = this.copyActionGraph();
		neighbor.removeStep(stepToRemove, inconsistency.getCurrentLevel());
		//neighbor.removeInconsistency(inconsistency);
		
		return neighbor;
	}
	
	
	
	/** Adds a step and its effects to the current level and propagates effects until blocked */
	public void addStep(PlanGraphStep stepToAdd, int currentLevel) {
		
		//_addStep(steps.get(currentLevel), facts.get(currentLevel), stepToAdd);
		steps.get(currentLevel).add(stepToAdd);
		//System.out.printf("Adding %s, steps now %s\n", stepToAdd, steps);
		facts.get(currentLevel).addAll(stepToAdd.getChildNodes());
		//System.out.printf("Adding %s, facts now %s\n", stepToAdd.getChildNodes(), facts);
		updateInconsistencies(stepToAdd, currentLevel);
		propagateAddStep(stepToAdd, currentLevel);
	}
	
	/** Adds a step and checks for supported preconditions, 
	 * used when propagating in order to reduce redundant inconsistency checking */
	private void _addStep(PlanGraphStep stepToAdd, int currentLevel) {
		
		steps.get(currentLevel).add(stepToAdd);
		//System.out.printf("Adding %s, steps now %s\n", stepToAdd, steps);
		facts.get(currentLevel).addAll(stepToAdd.getChildNodes());
		//System.out.printf("Adding %s, facts now %s\n", stepToAdd.getChildNodes(), facts);
		checkSupportedPreconditions(stepToAdd, currentLevel);
		propagateAddStep(stepToAdd, currentLevel);
	}
	
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
	
	private Map<PlanGraphLiteral, PlanGraphStep> getPersistentSteps(){
		
		HashMap<PlanGraphLiteral, PlanGraphStep> persistentSteps = new HashMap<PlanGraphLiteral, PlanGraphStep>();
		for(PlanGraphStep step : graph.getPersistantSteps())
			persistentSteps.put(step.getChildNodes().get(0), step);
		
		return persistentSteps;
	}
	
	private void updateUnsupportedPreconditionInconsistencies(int currentLevel) {
		
		List<LPGInconsistency> currentInconsistencies = inconsistencies.get(currentLevel);
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
	
	private void removeInvalidMutex(PlanGraphStep stepToRemove, int currentLevel) {
		
		/** check for other mutex that are no longer valid */
		for (Iterator<LPGInconsistency> iterator = inconsistencies.get(currentLevel).iterator(); iterator.hasNext();) {
			LPGInconsistency lpgInconsistency = iterator.next();
			if ((lpgInconsistency instanceof MutexRelation) && ((MutexRelation)lpgInconsistency).contains(stepToRemove)) {
				//JOptionPane.showMessageDialog(null, "Removing " + lpgInconsistency + ".. Before" + inconsistencies);
				iterator.remove();
				inconsistencyCount--;
				//JOptionPane.showMessageDialog(null, "After remove " + lpgInconsistency + " ==" + inconsistencies);
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
		
		Set<PlanGraphStep> currentSteps = steps.get(currentLevel);
		Set<PlanGraphLiteral> currentFacts = facts.get(currentLevel);
		/* propagate removal until steps no longer exists*/
		if (currentSteps.contains(stepToRemove)){
			
			currentSteps.remove(stepToRemove);
			for (PlanGraphLiteral pgLiteral : stepToRemove.getChildNodes()) {
				/* check to see if another step at this level has the effect pgLiteral */
				if (Collections.disjoint(currentSteps, pgLiteral.getParentNodes())) {
					currentFacts.remove(pgLiteral);
					if (currentLevel + 1 < maxLevel) {
						if (stepToRemove.isPersistent()) {
							// TODO verify bounds
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
		
		List<LPGInconsistency> currentLevelInconsistencies = inconsistencies.get(currentLevel);

		/** remove any unsupported precondition inconsistencies that newStep supports */ 
		checkSupportedPreconditions(newStep, currentLevel, currentLevelInconsistencies);
		
		/** add any new mutex steps */
		checkMutexSteps(newStep, currentLevel, currentLevelInconsistencies);
		
		/** check last level facts for preconditions of newStep */
		checkUnsupportedPreconditions(newStep, currentLevel);
	}

	/** Check and remove any USP inconsistencies newStep now supports */
	private void checkSupportedPreconditions(PlanGraphStep newStep,
			int currentLevel, List<LPGInconsistency> currentLevelInconsistencies) {
		for (Iterator<LPGInconsistency> iterator = currentLevelInconsistencies.iterator(); iterator.hasNext();) {
			LPGInconsistency lpgInconsistency = iterator.next();
			if (lpgInconsistency instanceof UnsupportedPrecondition) {
				PlanGraphLiteral unsupportedPrecondition = ((UnsupportedPrecondition)lpgInconsistency).getUnsupportedPrecondition();
				if (newStep.getChildNodes().contains(unsupportedPrecondition)) {
					//System.out.printf("Removing %s at lvl %d since supported by %s\n", unsupportedPrecondition, 
						//	currentLevel, newStep);
					//JOptionPane.showMessageDialog(null, "B4 rem " + lpgInconsistency + " " + inconsistencies);
					iterator.remove();
					inconsistencyCount--;
					//JOptionPane.showMessageDialog(null, "After rem " + lpgInconsistency + " " + inconsistencies);
					
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
			List<LPGInconsistency> currentLevelInconsistencies) {
		PlanGraphLevelMutex pgLevel = (PlanGraphLevelMutex) graph.getLevel(currentLevel);
		
		/* add any new mutex steps at this level */
		for (PlanGraphStep step : steps.get(currentLevel)) {
			if (pgLevel.isMutex(step, newStep)){
				currentLevelInconsistencies.add( new MutexRelation(step, newStep, currentLevel));
				//System.out.printf("Adding mutex step bt %s=%s lvl %d\n", step, newStep, currentLevel );
				inconsistencyCount++;
			}
		}
	}

	/** Checks facts of (currentLevel - 1) for preconditions of newStep, adding any USP found */
	private void checkUnsupportedPreconditions(PlanGraphStep newStep, int currentLevel) {
		/* add unsupported preconditions for this step by checking facts of previous level */
		if (currentLevel == 0 )
			System.out.println("Current level is 0 somehow... shouldn't happen");
		List<LPGInconsistency> previousLevelInconsistencies = inconsistencies.get(currentLevel - 1);
		Set<PlanGraphLiteral> previousLevelFacts = facts.get(currentLevel - 1);
		for (PlanGraphLiteral pgLiteral : newStep.getParentNodes()) {
			/* if we are at the first level, fact must be part of initial condition to satisfy */
			if (currentLevel - 1 == 0) {
				// TODO:  Remove later
				if (pgLiteral.getInitialLevel() != 0 )
					System.out.println("Some step added which cannot be supported: " + newStep +
							" at level " + currentLevel);
			}
			else if (!previousLevelFacts.contains(pgLiteral)) { 
				previousLevelInconsistencies.add(new UnsupportedPrecondition(pgLiteral, currentLevel - 1));
				inconsistencyCount++;
				//System.out.printf("Adding USP %s for step %s at lvl %d\n", pgLiteral, newStep, currentLevel - 1);
			}
		}
	}
	
	public void removeInconsistency(LPGInconsistency inconsistency){
		int currentLevel = inconsistency.getCurrentLevel();
		removeInconsistency(inconsistency, inconsistencies.get(currentLevel));
	}
	
	public void removeInconsistency(LPGInconsistency inconsistency, List<LPGInconsistency> currentInconsistencies){

		//System.out.println("CLI = " + currentInconsistencies + " and trying to remove " + inconsistency);
		//System.out.println("Does current contain? " + currentInconsistencies.contains(inconsistency));
		//JOptionPane.showMessageDialog(null, "Before" + inconsistencies);
		currentInconsistencies.remove(inconsistency);
		//JOptionPane.showMessageDialog(null, "After" + inconsistencies);
		//System.out.printf("Removing %s, inconsistencies now %s\n", inconsistency, inconsistencies);
		inconsistencyCount--;
	}
	
	public Map<Integer, List<LPGInconsistency>> getInconsistencies() {
		return inconsistencies;
	}
	
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
	
	@Override
	public String toString() {
		return String.format("steps: %s"
				+ "\nfacts: %s"
				+ "\ninconsistencies: %s\n", steps, facts, inconsistencies);
	}
	
	public boolean isSolution() {
		
		
		boolean foundSolution = false;
		
		// TODO .. verify
		if (inconsistencyCount == 0) {
			foundSolution = true;
			Set<PlanGraphLiteral> lastLevelFacts = facts.get(maxLevel);
			//System.out.println("||||||||||||||||||||Steps = " + steps);
			PlanGraphStep endStep = steps.get(maxLevel + 1).iterator().next();
			//System.out.printf("\tSteps = %s\n\tfacts = %s\n,\tinconsistencies = %s and endStep = %s, last facts = %s", 
					//steps, facts, inconsistencies, endStep, lastLevelFacts);
			for (PlanGraphLiteral goalCondition : endStep.getParentNodes()) {
				if (!lastLevelFacts.contains(goalCondition)) {
					//System.out.println("Adding unmet goal condition " + goalCondition + ", inc= " + inconsistencies);
					inconsistencies.get(maxLevel).add(new UnsupportedPrecondition(goalCondition, maxLevel));
					inconsistencyCount++;
					foundSolution = false;
				}
			}
		}
		
		return foundSolution;
	}
	public boolean isSolution2() {
		
		boolean foundSolution = true;
		for (List<LPGInconsistency> inconsistencyList : inconsistencies.values()) {
			if (!inconsistencyList.isEmpty()) {
				foundSolution = false;
				break;
			}
		}
		
		if(foundSolution == true){
			if(inconsistencyCount != 0) {
				//System.out.println("True but inconsistencies not 0?");
				InconsistencyIterator iterator = this.new InconsistencyIterator();
				//System.out.println();
			}
		}
		
		return foundSolution;
	}
	
	private int calculateQuality() {
		
		int quality = inconsistencyCount;
		for(InconsistencyIterator iterator = new InconsistencyIterator(); iterator.hasNext();) {
			LPGInconsistency inconsistency = iterator.next();
			if (inconsistency instanceof UnsupportedPrecondition) {
				int currentLevel = inconsistency.getCurrentLevel();
				PlanGraphLiteral unsupportedPrecondition = ((UnsupportedPrecondition) inconsistency).getUnsupportedPrecondition();
				quality += costToSupport(unsupportedPrecondition, currentLevel);
			}
		}
		
		return quality;
	}
	
	
	private int costToSupport(PlanGraphLiteral unsupportedPrecondition, int currentLevel) {
		
		int cost = Integer.MAX_VALUE;
		
		/* cost = 0 if part of initial conditions */
		if (unsupportedPrecondition.getInitialLevel() == 0)
			cost = 0;
		else {
			/* cost of supporting this precondition is the cost of the cheapest step that supports it */
			for (PlanGraphStep step : unsupportedPrecondition.getParentNodes()) {
				cost = Math.min(cost, costToSupport(step, currentLevel) + 1);
			}
		}
	
			return cost;
	}


	private int costToSupport(PlanGraphStep step, int currentLevel) {
		
		int cost = 0;
		
		/* make sure we can achieve step by this level */
		if(step.getInitialLevel() <= currentLevel) {
			
			/* cost of achieving a step is the max over the cost of achieving the preconditions */
			for (PlanGraphLiteral precondition : step.getParentNodes()) {
				if (!isSupported(precondition, currentLevel - 1))
					cost = Math.max(cost, costToSupport(precondition, currentLevel - 1));
			}
		}
		else
			cost = Integer.MAX_VALUE;
		
		return cost;
	}

	private boolean isSupported(PlanGraphLiteral literal, int currentLevel) {
		return facts.get(currentLevel).contains(literal);
	}

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
				List<LPGInconsistency> inconsistencyList = inconsistencies.get(i);
				if (!inconsistencyList.isEmpty()) {
					iterator = inconsistencyList.iterator();
					break;
				}
				else
					index++;
			}
		}
	}

	@Override
	public int compareTo(LPGActionGraph o) {
		return this.graphQuality - o.graphQuality;
	}
	
	public static Problem getCake(){
		
		Problem cakeProb = null;
		try {
			 cakeProb = new Benchmark("cake", "cake_test").getProblem();
			 //rocketProb = new Benchmark("rocket", "rocket_test").getProblem();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cakeProb;
	}
	
	public static void main(String[] args) throws IOException {
		
		Benchmark bm = TestSuite.BENCHMARKS[19];
		Problem p = bm.getProblem();
		
		long z = System.nanoTime();
		PlanGraph pg = new PlanGraph(p, true);
		System.out.println("pg = " + (System.nanoTime() - z ) / 1_000_000 );
		LPGActionGraph g = new LPGActionGraph(p, pg);
		System.out.println(" g = " + (System.nanoTime() - z ) / 1_000_000 );
		System.out.println(g);
		List<PlanGraphStep> zz = pg.getAllPossiblePlanGraphSteps();
		System.out.println("zz = " + (System.nanoTime() - z ) / 1_000_000 );
		System.out.println(zz.get(21).getParentNodes());
		System.out.println(zz.get(86).getParentNodes());
		System.out.println("get 2 parents " + (System.nanoTime() - z ) / 1_000_000 );
		System.out.println(zz.get(21).getChildNodes());
		/*
		LPGInconsistency i = g.inconsistencies.get(2).get(0);
		System.out.println("\tChoosing inconsistency " + i);
		UnsupportedPrecondition u = (UnsupportedPrecondition)i; 
		List<PlanGraphStep> choices = u.getUnsupportedPrecondition().getParentNodes();
		System.out.println(choices);
		PlanGraphStep step = choices.get(0);
		System.out.println(step);
		int currentLevel = 2;
		g.addStep(step, currentLevel);
		g.inconsistencies.get(currentLevel).remove(i);
		g.updateInconsistencies(step, currentLevel);
		System.out.println(g);
		
		i = g.inconsistencies.get(1).get(0);
		System.out.println("\tChoosing inconsistency " + i);
		u = (UnsupportedPrecondition)i; 
		choices = u.getUnsupportedPrecondition().getParentNodes();
		System.out.println(choices);
		step = choices.get(0);
		System.out.println(step);
		currentLevel = 1;
		g.addStep(step, currentLevel);
		g.inconsistencies.get(currentLevel).remove(i);
		g.updateInconsistencies(step, currentLevel);
		System.out.println(g);*/
	}
}
