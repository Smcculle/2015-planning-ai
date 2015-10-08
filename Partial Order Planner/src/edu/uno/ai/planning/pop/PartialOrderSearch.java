package edu.uno.ai.planning.pop;

import java.util.*;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.ss.*;
import edu.uno.ai.planning.util.*;

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
		this.pQueue.add(new PartialOrderNode(problem));
		this.nodesExpanded = 1;
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
		TotalOrderPlan plan = null;

		while(!this.pQueue.isEmpty()){
			PartialOrderNode workingNode = this.pQueue.poll(); // get the node to work on next
			if(workingNode.flaws.length == 0){
				plan = workingNode.toTotalOrderPlan();
				break;
			}
			else{
				handleFlaw(workingNode);
			}
			this.nodesVisited++;
		}
		return plan;
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
		Threat threat = (Threat)currentFlaw;

		for (Expression effect : new LiteralCollector(threat.threateningStep.effect)) {
			NegatedLiteral negatedPredicate = threat.threatenedLink.label.negate();

			if (effect.equals(negatedPredicate, workingNode.binds)) {
				handleThreat(threat, workingNode);
				return true;
			}
		}
		return false;
	}

	private void handleOpenCondition(OpenCondition o, PartialOrderNode workingNode){
		Literal predicateToMatch = o.literal();

		//loop through all of the existing partial steps to see if one satisfies this open precondition
		ImmutableList<PartialStep> stepsToLoopThrough = workingNode.steps;

		for(PartialStep step: stepsToLoopThrough) {
			//if the step is not the end step from the null plan
			if(step != workingNode.endStep) {
				for (Expression effect : new LiteralCollector(step.effect)) {
					if (predicateToMatch.unify(effect, workingNode.binds) != null) {
						useStepToSatisfyOpenPrecondition(predicateToMatch, (Literal)effect, step, workingNode, o);
					}
				}
			}
		}

		//loop through and find all operators that satisfies the open precondition
		for (Operator operator : problem.domain.operators) {
			for (Expression effect : new LiteralCollector(operator.effect)) {
				if (predicateToMatch.unify(operator.effect, workingNode.binds) != null) {
					addStepToSatisfyOpenPrecondition(operator, workingNode, o, predicateToMatch);
				}
			}
		}
	}

	private void addStepToSatisfyOpenPrecondition(Operator satisfyingOperator, PartialOrderNode workingNode, OpenCondition o, Literal satisfiedPredication){
			PartialStep newStep = new PartialStep(satisfyingOperator);
			Bindings newNodeBindings = workingNode.binds;
			POPGraph newOrderings = workingNode.orderings.addStep(newStep);
			if(newOrderings != null){
				newOrderings = newOrderings.addEdge(workingNode.startStep, newStep);
				if(newOrderings != null){
					newOrderings = newOrderings.addEdge(newStep, workingNode.endStep);
					if(newOrderings != null){
						newOrderings = newOrderings.addEdge(newStep,o.step());
					}
				}
			}
			if(newOrderings != null){
			
				
				for(Expression effect: newStep.effects()){
					newNodeBindings = satisfiedPredication.unify(effect,workingNode.binds);
					if(newNodeBindings != null){
						break; //only one effect of new partial step will satisfy it
					}
				}
				CausalLink newLink = new CausalLink(newStep,o.step(),(Predication)satisfiedPredication);
				ImmutableList<CausalLink> newCausalLinkList = workingNode.causalLinks.add(newLink);
				ArrayList<Flaw> newFlawSet = workingNode.flaws.clone();
				newFlawSet.remove((Flaw)o);
				ImmutableArray<Flaw> allFlaws = newFlawsFromAddingStep(newFlawSet,newStep,workingNode, newNodeBindings);
				ImmutableList<PartialStep> allSteps = workingNode.steps.add(newStep);
				PartialOrderNode newNode = new PartialOrderNode(allSteps,newOrderings,newCausalLinkList,(ListBindings) newNodeBindings,allFlaws, workingNode.startStep, workingNode.endStep);
				this.pQueue.add(newNode);
				this.nodesExpanded++;
			}
		
	}

	private void useStepToSatisfyOpenPrecondition(Literal satisfiedPredication, Literal satisfyingPredication, PartialStep satisfyingStep, PartialOrderNode workingNode, OpenCondition o){
		Bindings newNodeBindings = satisfiedPredication.unify(satisfyingPredication,workingNode.binds);
		System.out.println(satisfyingStep.name);
		System.out.println(o.step().name);
		POPGraph newOrderings = workingNode.orderings.promote(satisfyingStep, o.step());
		if(newOrderings != null){
			//shit unified correctly, now lets make the causal links
			CausalLink newLink = new CausalLink(satisfyingStep,o.step(),(Predication)satisfiedPredication);
			ArrayList<Flaw> newFlawSet = workingNode.flaws.clone();
			newFlawSet.remove((Flaw)o);
			//add any new flaws created from making the new causal link
			ImmutableArray<Flaw> newFlawsIncluded = newFlawsFromCausalLink(newFlawSet,workingNode,satisfiedPredication,newNodeBindings,newLink);
			ImmutableList<CausalLink> newCausalLinkList = workingNode.causalLinks.add(newLink);
			PartialOrderNode newNode = new PartialOrderNode(workingNode.steps,newOrderings,newCausalLinkList,(ListBindings) newNodeBindings,newFlawsIncluded, workingNode.startStep, workingNode.endStep);
			this.pQueue.add(newNode);
			this.nodesExpanded++;
		}

	}

	private ImmutableArray<Flaw> newFlawsFromCausalLink(ArrayList<Flaw> newFlawSet, PartialOrderNode workingNode, Literal predicateToMatch, Bindings newNodeBindings, CausalLink newLink) {
		//loop through all of the existing partial steps to see if one satisfies this open precondition
		ImmutableList<PartialStep> stepsToLoopThrough = workingNode.steps;

		for (PartialStep step : stepsToLoopThrough) {
			//if the step is not the end step from the null plan
			if (step != workingNode.endStep) {
				for (Expression effect : new LiteralCollector(step.effect)) {
					if (predicateToMatch.unify(effect.negate(), newNodeBindings) != null) {
						newFlawSet.add(new Threat(newLink, step));
						break;
					}
				}
			}
		}

		return new ImmutableArray<Flaw>(newFlawSet, Flaw.class);
	}

	private ImmutableArray<Flaw> newFlawsFromAddingStep(ArrayList<Flaw> oldFlaws, PartialStep newestStep, PartialOrderNode workingNode, Bindings bindings) {
		ArrayList<Flaw> newFlaws = new ArrayList<Flaw>(oldFlaws);

		for(CausalLink causalLink : workingNode.causalLinks) {
			for(Expression effect : new LiteralCollector(newestStep.effect)) {
				Bindings unification = effect.unify(causalLink.label.negate(), bindings);

				if (unification != null) {
					// was unified and is a threat
					newFlaws.add(new Threat(causalLink, newestStep));
				}
				// else, not a threat
			}
		}

		for (Expression precondition : new LiteralCollector(newestStep.precondition)) {
			newFlaws.add(new OpenCondition((Literal)precondition, newestStep));
		}

		return new ImmutableArray<Flaw>(newFlaws, Flaw.class);
	}


	private void handleThreat(Threat t, PartialOrderNode workingNode){

		//promotion

			//add the new ordering to the DAG, gets a new instance
			POPGraph newGraph = workingNode.orderings.promote(t.threateningStep,t.threatenedLink.previousStep);
			if(newGraph != null){
				//this is a copy of the flaws list we will be removing this flaw as we handled it
				ArrayList<Flaw> newFlaws = workingNode.flaws.clone();
				newFlaws.remove(t);
				Flaw[] flaws = new Flaw[newFlaws.size()];//empty array to give a type the array returned from toArray()
				ImmutableArray<Flaw> newestFlaws = new ImmutableArray<Flaw>(newFlaws.toArray(flaws));
				//make a new node to put into the queue
				PartialOrderNode newNode = new PartialOrderNode(workingNode.steps, newGraph, workingNode.causalLinks, workingNode.binds, newestFlaws, workingNode.startStep, workingNode.endStep);
				this.pQueue.add(newNode);
				this.nodesExpanded++;
			}


		//demotion
			//add the new ordering to the DAG, gets a new instance
			POPGraph newGraph2 = workingNode.orderings.demote(t.threateningStep,t.threatenedLink.nextStep);
			if(newGraph2 != null){
				//this is a copy of the flaws list we will be removing this flaw as we handled it
				ArrayList<Flaw> newFlaws = workingNode.flaws.clone();
				newFlaws.remove(t);
				Flaw[] flaws = new Flaw[newFlaws.size()];//empty array to give a type the array returned from toArray()
				ImmutableArray<Flaw> newestFlaws = new ImmutableArray<Flaw>(newFlaws.toArray(flaws));
				//make a new node to put into the queue
				PartialOrderNode newNode = new PartialOrderNode(workingNode.steps, newGraph2, workingNode.causalLinks, workingNode.binds, newestFlaws, workingNode.startStep, workingNode.endStep);
				this.pQueue.add(newNode);
				this.nodesExpanded++;
			}



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
