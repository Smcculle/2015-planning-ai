package edu.uno.ai.planning.fd;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import edu.uno.ai.planning.util.ImmutableArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;

public class FDSearch extends Search{ 
	
	/** The multi-valued planning task being solved */
	public final MPTProblem problem;

	/** The root node of the search space (i.e. a plan with 0 steps) */
	public final FDNode root;

	/** The search limit on visited nodes (-1 if no limit) */
	int limit = -1;

	/** The causal graph */
	public DirectedGraph<StateVariable, WeightedEdge> CG;

	/** The frontier */
	protected final PriorityQueue<FDNode> queue;
	
	/**
	 * Creates a FD search for a given problem.
	 * 
	 * @param problem the problem to be searched
	 */
	public FDSearch(MPTProblem problem) {
		super(problem);
		this.problem = problem;
		if(Utilities.currentlyHandles(problem)){
			buildDomainTransitionGraphs();
			buildCausalGraph();
			//System.out.println("Original causal graph:");
			//printGraph(CG);
			pruneCausalGraph();	
			//System.out.println("Acyclic causal graph:");
			//printGraph(CG);
		}
		queue = new PriorityQueue<FDNode>(new HeuristicComparator());
		this.root = new FDRoot(this);
		root.heuristic = Double.POSITIVE_INFINITY;
		queue.add(root);
		
		ArrayList<StateVariable> restrictedVarSet = new ArrayList<StateVariable>();
	
	}
	
	private boolean currentlyHanldes(MPTProblem problem2) {
		// TODO Auto-generated method stub
		return false;
	}

	// Build domain transition graphs for each variable
	public void buildDomainTransitionGraphs(){
		for(StateVariable v : problem.mptDomain.variables){
			v.buildDomainTransitionGraph(problem.mptSteps);
		}
	}

	// Build causal graph
	public void buildCausalGraph(){
		// Create an empty graph and add a node for each state variable in the problem
		CG = new DefaultDirectedGraph<StateVariable, WeightedEdge>(WeightedEdge.class);
		for(StateVariable var : problem.mptDomain.variables){
			CG.addVertex(var);
		}
		// For each variable var
		for(StateVariable var : problem.mptDomain.variables){
			// look through the edges in its domain transition graph
			for(LabelledEdge e : var.DTG.edgeSet()){
				// If there is a precondition on variable v, add an edge from v to var
				for(Assignment a : (ArrayList<Assignment>)e.label){
					if(!CG.containsEdge(a.variable, var))
						CG.addEdge(a.variable, var, new WeightedEdge(0));
					// Also increment the weight of the edge, which we'll use later when pruning the graph
					CG.getEdge(a.variable, var).weight++;
				}
			}			
		}
		// Look through all the operators in the problem
		for(MPTStep operator : problem.mptSteps){
			// For every pair of variables in affectedVariables, add a double edge 
			List<List<StateVariable>> powerset = Utilities.powerset(operator.affectedVariables);
			for(List<StateVariable> subset : powerset){
				if(subset.size()==2){
					if(!CG.containsEdge(subset.get(0), subset.get(1)))
						CG.addEdge(subset.get(0), subset.get(1), new WeightedEdge(0));
					if(!CG.containsEdge(subset.get(1), subset.get(0)))
						CG.addEdge(subset.get(1), subset.get(0), new WeightedEdge(0));
					// Increment the weights of both edges
					CG.getEdge(subset.get(0), subset.get(1)).weight++;
					CG.getEdge(subset.get(1), subset.get(0)).weight++;
				}
			}
		}
	}
	/**
	 * Collects ancestors of the given variable in the graph: This function explodes and so is not being used.
	 * @param CG
	 * @param var
	 * @param ancestors
	 */
	private static final void collectVariableAncestors(DirectedGraph<StateVariable, WeightedEdge> CG, StateVariable var, ArrayList<StateVariable> ancestors, int depth){
		if(!ancestors.contains(var))
			ancestors.add(var);
		for(WeightedEdge e : CG.incomingEdgesOf(var)){
			collectVariableAncestors(CG, CG.getEdgeSource(e), ancestors, depth+1);
		}
	}

	/**
	 * Prune the causal graph to remove cycles
	 */
	public void pruneCausalGraph(){
		// Compute the strongly connected components of the causal graph. 
		List<Set<StateVariable>> stronglyConnected = (new StrongConnectivityInspector(CG)).stronglyConnectedSets();

		// For each component, 
		for(Set<StateVariable> component : stronglyConnected){
			ArrayList<StateVariable> sortedComponent = new ArrayList<StateVariable>();
			sortedComponent.addAll(component);
			// Sort the variables in component according to the weights of their incoming edges in CG
			Collections.sort(sortedComponent, new Comparator<StateVariable>(){
				// Returns positive if v1 has the lower weight, negative if v2 has the lower weight
				public int compare(StateVariable v1, StateVariable v2){
					int weight1 = 0;
					int weight2 = 0;
					for(WeightedEdge e : CG.incomingEdgesOf(v1))
						weight1 += e.weight;
					for(WeightedEdge e : CG.incomingEdgesOf(v2))
						weight2 += e.weight;
					return weight2 - weight1;
				}
			});

			while(!sortedComponent.isEmpty()){
				// Pick the lowest weight variable. Remove each of its incoming edges to other nodes in this component
				StateVariable v = sortedComponent.get(0);
				ArrayList<WeightedEdge> edgesToRemove = new ArrayList<WeightedEdge>();
				for(WeightedEdge e : CG.incomingEdgesOf(v)){
					if(sortedComponent.contains(CG.getEdgeSource(e)))
						edgesToRemove.add(e);
				}
				for(WeightedEdge e : edgesToRemove)
					CG.removeEdge(e);
				sortedComponent.remove(v);
			}
		}
	}
	
	/**
	 * Search
	 */
	@Override
	public Plan findNextSolution() {
		if(!Utilities.currentlyHandles(problem))
			return root.plan;

		while(!queue.isEmpty()){
			FDNode node = queue.poll();
/*			System.out.println("===================================================================");
			System.out.println("Plan: ");
			for(Step step : node.plan){
				System.out.print((MPTStep)step);
			}
			System.out.println("Heuristic: "+node.heuristic);
*/			
			if(node.state.isTrue(problem.goalAssignments))
				return translatePlan(node.plan);
			node.expand();
			for(FDNode child : node.children){
				ArrayList<Double> relaxedPlanSolutions = new ArrayList<Double>(); 
				// For each goal variable 
				for(Assignment goal : problem.goalAssignments){
					// Solve the relaxed problem to change that variable from its current value to the goal value
					ArrayList<StateVariable> restrictedVarSet = new ArrayList<StateVariable>();
					restrictedVarSet.add(goal.variable);
					for(WeightedEdge e : CG.incomingEdgesOf(goal.variable)){
						restrictedVarSet.add(CG.getEdgeSource(e));
					}
					relaxedPlanSolutions.add(solve_relaxed_plan(goal, child.state, restrictedVarSet));
				}
				double child_heuristic = 0.0;
				for(Double relaxedSolution : relaxedPlanSolutions){
					child_heuristic += relaxedSolution;
				}
				child.heuristic = child_heuristic; // but later use deferred heuristic evaluation
				queue.add(child);
			}
		}
		return null;
	}
	
	private TotalOrderPlan translatePlan(TotalOrderMPTPlan plan) {
		MPTTranslator translator = new MPTTranslator(problem);
		TotalOrderPlan newPlan = new TotalOrderPlan();
		for(Step mptStep : plan){
			for(Step step : translator.steps){
				if(step.name.equals(((MPTStep)mptStep).name)){
					newPlan = newPlan.addStep(step);
					break;
				}
			}
		}
		return newPlan;
	}

	/**
	 * Estimates the cost of changing the goal variable from its value in the given state to each other value
	 * @param problem
	 * @param state
	 * @param variable
	 * @return the plan to change the variable accordingly
	 */
	public double solve_relaxed_plan(Assignment goal, MPTState state, ArrayList<StateVariable> restrictedVarSet){
		TotalOrderMPTPlan plan = new TotalOrderMPTPlan();

		/** Useful vars */
		StateVariable variable = goal.variable;
		Atom current_value = state.getValue(variable);
		DirectedGraph<Atom, LabelledEdge> DTG = variable.DTG;

		/** Terminate */
		if(!restrictedVarSet.contains(variable))
			return 0.0;
		if(state.getValue(variable).equals(goal.value))
			return 0.0;
		
		/** Initiate */
		HashMap<Atom, Double> nodeCosts = new HashMap<Atom, Double>();
		HashMap<Atom, TotalOrderMPTPlan> nodePlans = new HashMap<Atom, TotalOrderMPTPlan>();
		HashMap<LabelledEdge, Double> edgeCosts = new HashMap<LabelledEdge, Double>();
		ArrayList<Atom> frontier = new ArrayList<Atom>();
		frontier.add(current_value);
		ArrayList<Atom> unreachedNodes = new ArrayList<Atom>();		
		for(Atom node : DTG.vertexSet()){
			unreachedNodes.add(node); // Add all nodes to unreached
			nodeCosts.put(node, Double.POSITIVE_INFINITY); // Set all node costs to infinity
		}
		nodeCosts.put(current_value, 0.0); // Set cost of current node to 0
		nodePlans.put(current_value, new TotalOrderMPTPlan()); // Create an empty plan to achieve it

		/** Iterate */
		// While some nodes are unreached
		boolean onlyUnreachableLeft = false;
		while(!onlyUnreachableLeft && !unreachedNodes.isEmpty()){
			// Pick the lowest cost unreached node on the frontier 
			double lowestCost = Double.POSITIVE_INFINITY;
			Atom d1 = null;
			for(Atom node : frontier){
				if(nodeCosts.get(node) < lowestCost && unreachedNodes.contains(node)){
					lowestCost = nodeCosts.get(node);
					d1 = node;
				}
			}
			if(d1==null) onlyUnreachableLeft = true;
			else{
				// Remove the node from the unreached list.
				unreachedNodes.remove(d1);
				// For each outgoing edge from d1 to another unreached node, d2:
				for(LabelledEdge edge : DTG.outgoingEdgesOf(d1)){
					Atom d2 = DTG.getEdgeTarget(edge);
					if(unreachedNodes.contains(d2)){
						frontier.add(d2);
						double sumConditionCosts = 0.0;
						TotalOrderMPTPlan planToSolveConditions = new TotalOrderMPTPlan();
						for(Assignment condition : (ArrayList<Assignment>)edge.label){
							MutableMPTState mutableState = new MutableMPTState(state);
							mutableState.impose(new Assignment(variable, d1));
							sumConditionCosts += solve_relaxed_plan(condition, mutableState, restrictedVarSet);
						}
						edgeCosts.put(edge, 1 + sumConditionCosts);
						// If taking this edge is shorter than my current path, update this node's cost and plan
						if(nodeCosts.get(d1) + edgeCosts.get(edge) < nodeCosts.get(d2)){
							nodeCosts.put(d2, nodeCosts.get(d1) + edgeCosts.get(edge));
							TotalOrderMPTPlan updatedNodePlan = nodePlans.get(d1);
							for(Step step : planToSolveConditions){
								updatedNodePlan.addStep((MPTStep)step);
							}
							updatedNodePlan.addStep(edge.step);
							nodePlans.put(d2, updatedNodePlan);
						}
					}
				}
			}
		}
		
		//return nodePlans.get(goal.value);
		return nodeCosts.get(goal.value);
	}
	
	/** 
	 * Print a graph
	 * @param graph
	 */
	public <V,E> void printGraph(DirectedGraph<V,E> graph){
		for(V v : graph.vertexSet()){
			System.out.println("Vertex: "+v+"... incoming edges: "+graph.inDegreeOf(v));
			for(E e : graph.outgoingEdgesOf(v)){
				System.out.println(" - outgoing edge to "+graph.getEdgeTarget(e));
			}
		}
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
		((FDRoot) root).setNodeLimit(limit);
	}

}
