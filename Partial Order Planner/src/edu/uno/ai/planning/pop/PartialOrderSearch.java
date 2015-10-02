package edu.uno.ai.planning.pop;

import java.util.PriorityQueue;

import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.pop.PartialOrderNode; 
import edu.uno.ai.planning.pop.PartialOrderProblem;
import edu.uno.ai.planning.pop.PartialOrderRoot;
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
	
	private PriorityQueue<PartialOrderNode> queue;
	
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
		while(!this.queue.isEmpty()){
			PartialOrderNode workingNode = this.queue.poll(); // get the node to work on next
			
			if(workingNode.agenda.length == 0 && workingNode.threats.length == 0)
				return workingNode.orderings.topologicalSort();//this may need to be bound with the bindings found in the node
			//check for definite threats now and handle one of those first
			Literal openCondition = workingNode.agenda.get(0);
			for(Step step : workingNode.steps){
				if(step.effect instanceof Literal){
					if(step.effect.equals(openCondition, workingNode.binds))
						//add to the queue a new node where the steps are the same, but the step we just used has a new ordering 
						//the agenda has the openCondition removed, but maybe new threats added, the binds are bigger cause we unified I think
				}		//and there is a new casual link between the openCondition and the step
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
	
}
