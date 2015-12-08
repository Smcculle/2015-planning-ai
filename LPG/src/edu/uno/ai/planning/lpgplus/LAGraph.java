package edu.uno.ai.planning.lpgplus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.graphplan.PlanGraph;
import edu.uno.ai.planning.graphplan.PlanGraphLiteral;
import edu.uno.ai.planning.graphplan.PlanGraphStep;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import edu.uno.ai.planning.util.ConversionUtil;

public class LAGraph implements Comparable<LAGraph> {
	
	public List<LANode> laGraph;
	private List<Set<PlanGraphLiteral>> inconsistencies;
	private PriorityQueue<UnsupportedPrecondition> iqueue;
	private static PlanGraph graph;
	public int quality;
	private int maxLevel;
	private List<PlanGraphLiteral> goalLiterals;
	
	public LAGraph(Problem problem, PlanGraph graph) {
		this.graph = graph;
		goalLiterals = new ArrayList<PlanGraphLiteral>();
		laGraph = new LinkedList<LANode>();
		this.iqueue = new PriorityQueue<UnsupportedPrecondition>(inconsistencyComperator());
		addStartAndEndSteps(problem);
		//
		ArrayList<Literal> goals = edu.uno.ai.planning.util.ConversionUtil.expressionToLiterals(problem.goal);
		for(Literal literal : goals)
			goalLiterals.add(graph.getPlanGraphLiteral(literal));
		
		calculateQuality2();
	}
	
	public LAGraph(LAGraph other) {
		this.laGraph = new LinkedList<LANode>(other.laGraph);
		//this.inconsistencies = new PriorityQueue<UnsupportedPrecondition>(inconsistencyComperator());
		this.goalLiterals = other.goalLiterals;
		this.iqueue = new PriorityQueue<UnsupportedPrecondition>(inconsistencyComperator());
		this.calculateQuality2();
	
	}
	
	public void insert(PlanGraphStep step, int level) {
		if(laGraph.get(level).getStep() == null)
			laGraph.get(level).addStep(step);
		else{
			if(level < laGraph.size() && level > 0){
				LANode newNode = new LANode(laGraph.get(level-1), laGraph.get(level), step);
				laGraph.add(level, newNode);
			}
		}
	}
	
	public void insert(int level) {
		if(level < laGraph.size() && level > 0){
			LANode newNode = new LANode(laGraph.get(level-1), laGraph.get(level));
			laGraph.add(level, newNode);
		}
	}
	
	private void addStartAndEndSteps(Problem problem) {
		
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
		
		LANode root = new LANode(null, null);
		LANode last = new LANode(root, null);
		root.setPMap(getPersistentSteps());
		root.next = last;
		root.setGraph(graph);
		root.addStep(start);
		last.addStep(end);
		
		laGraph.add(root);
		laGraph.add(last);
		maxLevel = 2;
		this.insert(1);
	}
	
	/*
	public int calculateQuality() {
		
		int i = 1;
		LANode current = laGraph.get(i);
		do {
			PlanGraphStep step = current.getStep();
			for (PlanGraphLiteral precondition : step.getParentNodes()) {
				if(!current.previous.contains(precondition))
					inconsistencies.offer( new UnsupportedPrecondition(precondition, i));
			}
			
		} while (current.next != null);
		
		return 0;
	}*/
	
	public int calculateQuality2() {
		this.quality = 0;
		List<Set<PlanGraphLiteral>> inconsistencies = new ArrayList<Set<PlanGraphLiteral>>(laGraph.size());
		for (int i = 0; i < laGraph.size(); i++) {
			inconsistencies.add(new HashSet<PlanGraphLiteral>());
		}
		int i = laGraph.size() - 1;
		inconsistencies.get(i).addAll(goalLiterals);
		while(i > 1) {
			for (PlanGraphLiteral goal : inconsistencies.get(i)) {
				if (laGraph.get(i).facts.contains(goal)) {
					inconsistencies.get(i-1).add(goal);
				}
				else {
					this.quality = this.quality + goal.getInitialLevel() + 1;
					for (PlanGraphLiteral precondition : goal.getParentNodes().iterator().next().getParentNodes()){
						inconsistencies.get(i-1).add(precondition);
						iqueue.offer(new UnsupportedPrecondition(goal, i-1));
					}
				}
			}
			i--;
		}
		return this.quality;
	}
	
	public UnsupportedPrecondition chooseInconsistency() {
		for(int i = 0; i < inconsistencies.size(); i++) {
			if(!inconsistencies.get(i).isEmpty())
				return new UnsupportedPrecondition(inconsistencies.get(i).iterator().next(), i);
		}
		return null;
	}
	
	public UnsupportedPrecondition chooseInconsistency2() {
		int counter = 0;
		int lastlevel = 0;
		PlanGraphLiteral next = null;
		for(int i = 0; i < inconsistencies.size(); i++) {
			
			Iterator<PlanGraphLiteral> it = inconsistencies.get(i).iterator();
			while(it.hasNext()){ 
				next = it.next();
				lastlevel = i;
				counter++;
			}
		}
		return new UnsupportedPrecondition(next, lastlevel);
	}
	
	public UnsupportedPrecondition chooseInconsistency3() {
		
		return iqueue.poll();
	}
	
	
	private Map<PlanGraphLiteral, PlanGraphStep> getPersistentSteps(){
		
		HashMap<PlanGraphLiteral, PlanGraphStep> persistentSteps = new HashMap<PlanGraphLiteral, PlanGraphStep>();
		for(PlanGraphStep step : graph.getPersistantSteps())
			persistentSteps.put(step.getChildNodes().get(0), step);
		
		return persistentSteps;
	}
	private Comparator<UnsupportedPrecondition> inconsistencyComperator() {
		return new Comparator<UnsupportedPrecondition>() {

			@Override
			public int compare(UnsupportedPrecondition usp1,
					UnsupportedPrecondition usp2) {
				
				return usp1.getInitialLevel() - usp2.getInitialLevel();
			}
		};
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
	private PlanGraphStep findRemoveChoice(PlanGraphLiteral unsupportedLiteral, int currentLevel) {
		
		if (currentLevel + 2 <= maxLevel) {
			PlanGraphStep nextStep = laGraph.get(currentLevel + 1).getStep();
			/* remove each step present at the next level */
			for (PlanGraphStep stepToRemove : unsupportedLiteral.getChildNodes()){
				if (nextStep.equals(stepToRemove))
					return nextStep;
			}
		}
		return null;
	}
	
	public List<LAGraph> makeNeighborhood(UnsupportedPrecondition inconsistency) {
		List<LAGraph> neighborhood = new ArrayList<LAGraph>();
		
		if(inconsistency == null)
			return null;
					
		int currentLevel = inconsistency.getCurrentLevel();
		PlanGraphLiteral unsupportedLiteral = inconsistency.getUnsupportedPrecondition();
		
		for(PlanGraphStep stepToAdd : findAddChoices(unsupportedLiteral)) {
			int initialLevel = stepToAdd.getInitialLevel();
			// TODO possibly insert != -1 here
			if (initialLevel <= currentLevel) {
				for(int i = 0; i <= currentLevel; i++) {
					LAGraph newLAG = getAddNeighbor(stepToAdd, inconsistency, i);
					neighborhood.add(newLAG);
				}
			}
		}
		
		PlanGraphStep removeChoice = findRemoveChoice(unsupportedLiteral, currentLevel);
		if (removeChoice != null)
			neighborhood.add(getDeleteNeighbor(removeChoice, inconsistency, currentLevel));
		
		return neighborhood;
	
	}

	private LAGraph getDeleteNeighbor(PlanGraphStep removeChoice,
			UnsupportedPrecondition inconsistency, int currentLevel) {
		
		LAGraph neighbor = new LAGraph(this);
		LANode cLevel = neighbor.laGraph.get(currentLevel);
		if (!cLevel.getStep().equals(removeChoice))
			System.out.println("some error.. remove step does not equal");
		else
			neighbor.laGraph.get(currentLevel).removeStep();
		
		return null;
	}

	private LAGraph getAddNeighbor(PlanGraphStep stepToAdd,
			UnsupportedPrecondition inconsistency, int i) {
		
		LAGraph neighbor = new LAGraph(this);
		neighbor.insert(stepToAdd, i);
		neighbor.calculateQuality2();
		
		return neighbor;
	}

	public TotalOrderPlan getPlan() {
		TotalOrderPlan plan = new TotalOrderPlan();
		for(int i = 1; i < laGraph.size(); i++) {
			if (laGraph.get(i).getStep() != null)
				plan = plan.addStep(laGraph.get(i).getStep().getStep());
		}
		return plan;
	}

	@Override
	public int compareTo(LAGraph o) {
		return this.quality - o.quality;
	}

	public void checkFacts() {
		LANode current = laGraph.get(0);
		while(current.next != null){
			Set<PlanGraphLiteral> effects = new HashSet<PlanGraphLiteral>(current.getStep().getChildNodes());
			for (PlanGraphStep persistentStep : current.pSteps) {
				effects.add(persistentStep.getChildNodes().get(0));
			}
			current.facts = effects;
			current = current.next;
		}
	}
	
	
}
