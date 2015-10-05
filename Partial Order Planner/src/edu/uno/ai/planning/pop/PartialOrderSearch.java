package edu.uno.ai.planning.pop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.util.ImmutableArray;
import edu.uno.ai.planning.util.ImmutableList;
import edu.uno.ai.planning.*;

/**
 * Represents a search space whose
 * {@link edu.uno.ai.planning.pop.PartialOrderNodes nodes} are plans
 * and whose edges are orderings?
 * 
 * @author
 */
public class PartialOrderSearch extends Search {
	
	/** The Partial Order problem being solved */
	public final PartialOrderProblem problem;
	
	
	private PriorityQueue<PartialOrderNode> pQueue;
	
	private int nodesExpanded;
	
	private int nodesVisited;
	
	/** The search limit on visited nodes (-1 if no limit) */
	int limit = -1;
	
	
	
	/**
	 * Creates a partial order search for a given problem.
	 * 
	 * @param problem the problem whose  space will be searched
	 */
	public PartialOrderSearch(PartialOrderProblem problem) {
		super(problem);
		this.problem = problem;
		this.pQueue = new PriorityQueue<PartialOrderNode>(20,new NodeComparator());
		this.nodesExpanded = 0;
		this.nodesVisited = 0;
	}
	
	/**
	 * This the the actual implementation of the POP algorithm. This method will recursively call itself till 
	 * either a correct, problem solving plan is found, it fails to find a plan, or it runs out of search space.
	 * We will not be implementing separation and thus we need to track the threats so we can resolve them later.
	 * 
	 * @param A A list or set or something of the steps involved in this plan so far
	 * @param O A directed acyclic graph whose nodes are the steps in the plan and whose edges are the orderings 
	 * @param L Some collection of the casual links between the steps in the plan
	 * @param B Set of bindings used to ground the variables in the plan
	 * @param T Set of threats to casual links in the plan
	 * @return 
	 */
	private Plan pop(){
		while(!this.pQueue.isEmpty()){
			PartialOrderNode workingNode = this.pQueue.poll(); // get the node to work on next
			
			if(workingNode.flaws.length == 0){
				return workingNode.orderings.topologicalSort();//this may need to be bound with the bindings found in the node
			}
			else{
				handleFlaw(workingNode);
			}
		}
		return null; //wat
	}
	
	//assume that there is at least one flaw to work on
	private void handleFlaw(PartialOrderNode workingNode){
		//flaw we are currently checking out
		Flaw currentFlaw;
		//this will grab the flaws and check them out we're looking for either an openCondition which we handle right away
		//or a threat which we check and see if it is a definite threat
		for(int i=0; i < workingNode.flaws.length; i++){
			//get the flaw to look at
			currentFlaw = workingNode.flaws.get(i);
			//if it's an open condition good we can continue and work on that
			if(currentFlaw instanceof OpenCondition){
				handleOpenCondition((OpenCondition) currentFlaw, workingNode);
				break;
			}
			else{
				//otherwise the flaw is a threat
				boolean found = findAndHandleThreat(currentFlaw, workingNode);
				if(found){break;}
			}	
		}
	}
	
	private boolean findAndHandleThreat(Flaw currentFlaw, PartialOrderNode workingNode){
		//we need to check and see if the threat is grounded and links to the causal link's label
		Threat currentThreat = (Threat) currentFlaw;
		Expression effects = currentThreat.threateningStep.effect;
		if(effects instanceof Literal){ //if there is only one effect
			Predication threatendCondition = currentThreat.threatenedLink.label;
			boolean dealWithThreat = ((Predication)effects).equals(threatendCondition.negate(), workingNode.binds);
			if(dealWithThreat){
				handleThreat((Threat) currentFlaw, workingNode); //work on it
				return true;
			}
		}
		else{ //else there's a bunch of threats, check which one threatens the label
			ImmutableArray<Expression> arguments = ((Conjunction) effects).arguments;
			
			for(int j=0; j< arguments.length; j++){
				Predication threatenedPredicate = (Predication)currentThreat.threatenedLink.label;
				boolean dealWithThreat = ((Predication)arguments.get(j)).equals(threatenedPredicate.negate(), workingNode.binds);
				if(dealWithThreat){
					handleThreat((Threat) currentFlaw, workingNode);//may want to pass an index so we don't have to look again
					return true;
				}
			}
		}
		return false;
	}
	
	private void handleOpenCondition(OpenCondition o, PartialOrderNode workingNode){
		Predication predicatetToMatch = (Predication) o.literal();
		//loop through and find all operators that satisfies the open precondtion
		ImmutableArray<Operator> operatorsToCheck = problem.domain.operators;
		for(int i=0;i < operatorsToCheck.length; i++){
			boolean foundMatch = false;
			if (operatorsToCheck.get(i).effect instanceof Literal){
				foundMatch = predicatetToMatch.equals((Predication) operatorsToCheck.get(i).effect, workingNode.binds);
			}
			else{
				ImmutableArray<Expression> arguments = ((Conjunction) operatorsToCheck.get(i).effect).arguments;
				for(int j=0; j< arguments.length; j++){
					if(predicatetToMatch.equals((Predication) arguments.get(j), workingNode.binds)){
						foundMatch = true;
					}
				}
			}
			if(foundMatch){
				//create partial step and nodes and shit
			}
		}
		
		//loop thorugh all of the existing partial steps to see if one  
		ImmutableList<PartialStep> stepsToLoopThrough = workingNode.steps;
		for(PartialStep step: stepsToLoopThrough){
			boolean foundMatch = false;
			if(step.effect instanceof Literal){
				foundMatch = predicatetToMatch.equals((Predication) step.effect, workingNode.binds); 
			}
			else{
				ImmutableArray<Expression> arguments = ((Conjunction) step.effect).arguments;
				for(int j=0; j< arguments.length; j++){
					if(predicatetToMatch.equals((Predication) arguments.get(j), workingNode.binds)){
						foundMatch = true;
					}
				}
			}
			if(foundMatch){
				//create partial step and nodes and shit
			}
		}
		//checking existing partialSteps
	}
	
	private void handleThreat(Threat t, PartialOrderNode workingNode){
		
		//promotion
		try{
			POPGraph newGraph = workingNode.orderings.promote(t.threateningStep,t.threatenedLink.previousStep);
			ArrayList<Flaw> newFlaws = workingNode.flaws.clone();
			newFlaws.remove(t);
			Flaw[] flaws;
			ImmutableArray<Flaw> newestFlaws = new ImmutableArray<Flaw>(newFlaws.toArray(flaws));
			PartialOrderNode newNode = new PartialOrderNode(workingNode.steps, newGraph, workingNode.causalLinks, workingNode.binds, newestFlaws);
			this.pQueue.add(newNode);
		}
		catch(DirectedAcyclicGraph.CycleFoundException e){
			
		}
	
		
		
		//demotion
		
	}
	
	

	@Override
	public int countVisited() {
		return this.nodesVisited;
	}

	@Override
	public int countExpanded() {
		return this.nodesExpanded;
	}

	@Override
	public void setNodeLimit(int limit) {
		this.limit = limit;
	}

	@Override
	//This is the bread and butter it is the method that is called to actually solve the problem
	public Plan findNextSolution() {
		return pop();
	}
	
	
	private class NodeComparator implements Comparator<PartialOrderNode>{

		@Override
		public int compare(PartialOrderNode o1, PartialOrderNode o2) {
			return Integer.compare(o1.flaws.length + o1.steps.length, o2.flaws.length + o1.steps.length);
		}
		
	}
	
}
