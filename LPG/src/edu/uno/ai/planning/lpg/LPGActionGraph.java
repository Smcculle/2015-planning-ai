package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.Collection;
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

public class LPGActionGraph {
	
	/** maximum level for the graph */
	private final int maxLevel; 
	
	/** random number generator */
	private final Random rand;
	
	/** Measure (from 0 to 1) of how much we prefer resolving inconsistencies at earlier levels */
	private static final float inconsistencyWeight = 0.5f; 
	
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
	

	/**
	 * Initializes an empty action graph only containing special actions start, end, and noops of start
	 * @param problem
	 */
	public LPGActionGraph(Problem problem) {
		
		graph = new PlanGraph(problem, true); 
		maxLevel = graph.countLevels() - 1;
		rand = new Random();
		persistentSteps = getPersistentSteps();
		initializeMaps();
		addStartAndEndSteps(problem);
		
	}
	
	//TODO deep copy 
	public LPGActionGraph(LPGActionGraph actionGraph) {
	
		this.maxLevel = actionGraph.maxLevel;
		this.inconsistencyCount = actionGraph.inconsistencyCount;
		this.rand = new Random();
		this.steps = DeepCloneMap.deepClone(actionGraph.steps);
		this.facts = DeepCloneMap.deepClone(actionGraph.facts);
		this.inconsistencies = DeepCloneMap.deepClone(actionGraph.inconsistencies);
	}


	public boolean isSolution() {
		
		boolean foundSolution = true;
		for (List<LPGInconsistency> inconsistencyList : inconsistencies.values()) {
			if (!inconsistencyList.isEmpty()) {
				foundSolution = false;
				break;
			}
		}
		
		return foundSolution;
	}

	public TotalOrderPlan getTotalOrderPlan(TotalOrderPlan plan) {
		
		for (int i = 1; i <= maxLevel; i++) {
			for (PlanGraphStep pgStep : steps.get(i)) {
				System.out.println("Level " + i + " " + pgStep);
				if(!pgStep.isPersistent()) {
					plan = plan.addStep(pgStep.getStep());
				}
			}
		}
		
		return plan;
	}

	public int getInconsistencyCount() {
		
		return inconsistencyCount;
	}
	
	public LPGInconsistency getInconsistency() {
		
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
		else {
			
			/* choose a random number and iterate until we get to that inconsistency */
			int inconsistencyCounter = rand.nextInt(inconsistencyCount);
			
			outer:
			for (List<LPGInconsistency> inconsistencyList: inconsistencies.values()) {
				for (Iterator<LPGInconsistency> it = inconsistencyList.iterator(); it.hasNext();) {
					
					/* if we have iterated through enough inconsistencies, choose this one */
					if (inconsistencyCounter == 0) {
						chosenInconsistency = it.next();
						break outer;
					}
					
					/* otherwise decrease counter and continue iterating */
					else {
					it.next();
					inconsistencyCounter--;
					}
				}
			}
		}
		
		return chosenInconsistency;
	}

	public ArrayList<LPGPlanGraph> getNeighborhood(LPGInconsistency inconsistency) {
		
		ArrayList<LPGPlanGraph> neighborhood = new ArrayList<LPGPlanGraph>(); 
		if (inconsistency instanceof UnsupportedPrecondition) {
			PlanGraphLiteral unsupportedLiteral = ((UnsupportedPrecondition) inconsistency).getUnsupportedPrecondition();
			List<PlanGraphStep> addChoices = unsupportedLiteral.getParentNodes();
			neighborhood.addAll(getAddGraphs(addChoices));
			
		}
		return null;
	}
	
	private ArrayList<LPGPlanGraph> getAddGraphs(List<PlanGraphStep> addChoices){
		
		return null;
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
		addStep(start, 0);
		
	}
	
	/** Adds a step and its effects to the current level and propagates effects until blocked */
	private void addStep(PlanGraphStep stepToAdd, int currentLevel) {
		
		//_addStep(steps.get(currentLevel), facts.get(currentLevel), stepToAdd);
		steps.get(currentLevel).add(stepToAdd);
		facts.get(currentLevel).addAll(stepToAdd.getChildNodes());
		propagateAddStep(stepToAdd, currentLevel);
	}
	
	/** helper method to add steps/facts without calling propagate */
	private void _addStep(Set<PlanGraphStep> currentLevelSteps, PlanGraphStep stepToAdd, int currentLevel) {
		
		currentLevelSteps.add(stepToAdd);
		facts.get(currentLevel).addAll(stepToAdd.getChildNodes());
	}
	
	private void propagateAddStep(PlanGraphStep stepToAdd, int currentLevel) {

		/* add step to next level until we've reached the max */
		if (currentLevel < maxLevel) {
			
			/* propagate if persistent and does not exist in next level if not mutex to any step at next level*/
			Set<PlanGraphStep> nextLevelSteps = steps.get(currentLevel + 1);
			if (stepToAdd.isPersistent() && !nextLevelSteps.contains(stepToAdd)) {
				PlanGraphLevelMutex nextLevelMutex = (PlanGraphLevelMutex)graph.getLevel(currentLevel + 1);
				if (!isMutexWithSteps(nextLevelMutex, nextLevelSteps, stepToAdd)) {
					addStep(stepToAdd, currentLevel + 1);
				}
			}
			
			/* get persistent steps for each effect and propagate those */
			else {
				for (PlanGraphLiteral effect : stepToAdd.getChildNodes()) {
					//System.out.println("Getting persistent step for " + effect);
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
	
	// TODO organize 
	
	private void updateUnsupportedPreconditionInconsistencies(PlanGraph graph, 
			Map<Integer, Set<PlanGraphStep>> steps, 
			Map<Integer, Set<PlanGraphLiteral>> facts, 
			Map<Integer, List<LPGInconsistency>> inconsistencies, int currentLevel) {
		
		List<LPGInconsistency> currentInconsistencies = inconsistencies.get(currentLevel);
		/** check for unsupported preconditions in the next level if we aren't at max level */
		if (currentLevel + 1 <= graph.countLevels()) {
			Set<PlanGraphStep> nextLevelSteps = steps.get(currentLevel + 1);
			Set<PlanGraphLiteral> currentFacts = facts.get(currentLevel);
			for (PlanGraphStep pgStep : nextLevelSteps) {
				for (PlanGraphLiteral pgLiteral : pgStep.getParentNodes()) {
					if (!currentFacts.contains(pgLiteral))
						currentInconsistencies.add( new UnsupportedPrecondition(pgLiteral, currentLevel));
				}
			}
		}
	}
	
	private void removeInvalidMutex(List<LPGInconsistency> currentInconsistencies, PlanGraphStep stepToRemove) {
		
		/** check for other mutex that are no longer valid */
		for (Iterator<LPGInconsistency> iterator = currentInconsistencies.iterator(); iterator.hasNext();) {
			LPGInconsistency lpgInconsistency = (LPGInconsistency) iterator.next();
			if ((lpgInconsistency instanceof MutexRelation) && ((MutexRelation)lpgInconsistency).contains(stepToRemove))
				iterator.remove();
		}
	}
	
	/** Removes a step and any unsupported effects from this level and propagates removal */
	private void removeStep(Map<Integer, Set<PlanGraphStep>> steps, Map<Integer, Set<PlanGraphLiteral>> facts, 
			PlanGraphStep stepToRemove, int currentLevel) {
		
		Set<PlanGraphStep> currentSteps = steps.get(currentLevel);
		Set<PlanGraphLiteral> currentFacts = facts.get(currentLevel);
		/* propagate removal until steps no longer exists*/
		if (currentSteps.contains(stepToRemove)){
			
			currentSteps.remove(stepToRemove);
			for (PlanGraphLiteral pgLiteral : stepToRemove.getChildNodes()) {
				/* check to see if another step at this level has the effect pgLiteral */
				if (Collections.disjoint(currentSteps, pgLiteral.getParentNodes())) {
					currentFacts.remove(pgLiteral);
					if (stepToRemove.isPersistent()) {
						// TODO verify bounds
						removeStep(steps, facts, stepToRemove, currentLevel + 1);
					}
					else {
						PlanGraphStep persistentStep = persistentSteps.get(pgLiteral);
						removeStep(steps, facts, persistentStep, currentLevel + 1);
					}
				}
			}
		}
	}
	
	private void updateInconsistency(
			Map<Integer, Set<PlanGraphStep>> steps,
			Map<Integer, Set<PlanGraphLiteral>> facts,
			Map<Integer, List<LPGInconsistency>> inconsistencies,
			PlanGraph graph, PlanGraphStep newStep, int currentLevel) {
		
		List<LPGInconsistency> currentLevelInconsistencies = inconsistencies.get(currentLevel);
		/** remove any unsupported preconditions that are now supported TODO modify w/ iterator for remove*/
		
		for (Iterator<LPGInconsistency> iterator = currentLevelInconsistencies.iterator(); iterator.hasNext();) {
			LPGInconsistency lpgInconsistency = (LPGInconsistency) iterator.next();
			if (lpgInconsistency instanceof UnsupportedPrecondition) {
				PlanGraphLiteral unsupportedPrecondition = ((UnsupportedPrecondition)lpgInconsistency).getUnsupportedPrecondition();
				if (newStep.getChildNodes().contains(unsupportedPrecondition))
					iterator.remove();
			}
		}
		
		/*
		for (LPGInconsistency lpgInconsistency : currentLevelInconsistencies) {
			if (lpgInconsistency.isUnsupportedPrecondition()) {
				PlanGraphLiteral unsupportedPrecondition = lpgInconsistency.getUnsupportedPrecondition();
				if (newStep.getChildNodes().contains(unsupportedPrecondition))
					currentLevelInconsistencies.remove(lpgInconsistency);
			}
		}*/
		
		PlanGraphLevelMutex pgLevel = (PlanGraphLevelMutex) graph.getLevel(currentLevel);
		
		/* add any new mutex steps at this level */
		for (PlanGraphStep step : steps.get(currentLevel)) {
			if (pgLevel.isMutex(step, newStep))
				currentLevelInconsistencies.add( new MutexRelation(step, newStep, currentLevel));
		}
		
		/* add unsupported preconditions for this step by checking facts of previous level */
		if (currentLevel == 0 )
			System.out.println("Current level is 0 somehow... shouldn't happen");
		List<LPGInconsistency> previousLevelInconsistencies = inconsistencies.get(currentLevel - 1);
		for (PlanGraphLiteral pgLiteral : newStep.getParentNodes()) {
			/* if we are at the first level, fact must be part of initial condition to satisfy */
			if (currentLevel - 1 == 0) {
				// TODO:  Remove later
				if (pgLiteral.getInitialLevel() != 0 )
					System.out.println("Some step added which cannot be supported: " + newStep);
			}
			else if (!facts.get(currentLevel - 1).contains(pgLiteral)) 
				previousLevelInconsistencies.add(new UnsupportedPrecondition(pgLiteral, currentLevel - 1));
		}
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
			
			if (!iterator.hasNext())
				setNextIterator();
			
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
}
