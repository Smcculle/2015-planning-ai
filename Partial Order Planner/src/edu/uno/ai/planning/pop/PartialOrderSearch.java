package edu.uno.ai.planning.pop;

import java.util.Comparator;
import java.util.PriorityQueue;

import edu.uno.ai.planning.logic.*;

import edu.uno.ai.planning.pop.*;
import edu.uno.ai.planning.util.ImmutableArray;
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
	
	/** The root node of the search space (null plan?) */
	public final PartialOrderNode root;
	
	private PriorityQueue<PartialOrderNode> pQueue;
	
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
		this.root = new PartialOrderRoot(this);
		this.pQueue = new PriorityQueue<PartialOrderNode>(20,new NodeComparator());
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
			
			if(workingNode.flaws.length == 0)
				return workingNode.orderings.topologicalSort();//this may need to be bound with the bindings found in the node
			//Get a flaw and check it out
			boolean workableFlaw = false;
			Flaw currentFlaw;
			int i = 0;
			//this will grab the flaws and check them out we're looking for either an openCondition which we handle right away
			//or a threat which we check and see if it is a definite threat
			while(!workableFlaw && i < workingNode.flaws.length){
				//get the flaw to look at
				currentFlaw = workingNode.flaws.get(i);
				//if it's an open condition good we can continue and work on that
				if(currentFlaw instanceof OpenCondition){
					workableFlaw = true;
				}
				else{
					//otherwise the flaw is a threat
					//we need to check and see if the threat is definite
					Threat currentThreat = (Threat) currentFlaw;
					Expression effects = currentThreat.threateningOperator.effect;
					if(effects instanceof Literal){
						boolean dealWithThreat = effects.isGround();
						if(dealWithThreat){
							//do promotion/demotion here
						}
					}
					else{
						ImmutableArray<Expression> arguments = ((Conjunction) effects).arguments;
						for(int j=0; j< arguments.length; j++){
							boolean dealWithThreat = arguments.get(j).isGround();
							if(dealWithThreat){
								//if this threats predication matches the negation  of the causal link's predecation
								Expression threatenedPredicate = currentThreat.threatenedLink.label;
								if(threatenedPredicate.isGround() && threatenedPredicate.equals(arguments.get(j).negate())){
									//deal with promotion//demotion
									break; //break from for loop because we found the threatened link and fixed it
								}
							}
						}
					}	
				}	
			}
		}
		return null;
	}

	@Override
	public int countVisited() {
		return root.countVisited();
	}

	@Override
	public int countExpanded() {
		return root.countExpanded();
	}

	@Override
	public void setNodeLimit(int limit) {
		((StateSpaceRoot) root).setNodeLimit(limit);
	}

	@Override
	//This is the bread and butter it is the method that is called to actually solve the problem
	public Plan findNextSolution() {
		// this.pop(problem.steps(), new(DAG), new(set of Casual Links), new Bindings, new Threats))
		return null;
	}
	
	
	private class NodeComparator implements Comparator<PartialOrderNode>{

		@Override
		public int compare(PartialOrderNode o1, PartialOrderNode o2) {
			return Integer.compare(o1.flaws.count() + o1.steps.length, o2.flaws.count() + o1.steps.length);
		}
		
	}
	
}
