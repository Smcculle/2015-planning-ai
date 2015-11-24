package edu.uno.ai.planning.hsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * Explores a problem's search space in a best-first search fashion, guided by a heuristic.
 * 
 */
public class HeuristicSearch extends StateSpaceSearch {
	
	/** The queue in which the frontier is stored */
	protected final PriorityQueue<StateSpaceNode> queue;
	
	/** List of steps in the problem */
	private ArrayList<Step> steps = new ArrayList<Step>();
	
	/** The problem as a StateSpaceProblem */
	private StateSpaceProblem prob;
	
	public final static double infinity = Double.POSITIVE_INFINITY;
	
	/**
	 * Creates a new heuristic search process.
	 * 
	 * @param problem the problem to be explored
	 */
	public HeuristicSearch(StateSpaceProblem problem) {
		super(problem);
		this.prob = problem;
		getStepsInProblem(prob.steps);
		HeuristicComparator comparator = new HeuristicComparator();
		queue = new PriorityQueue<StateSpaceNode>(comparator);
		root.heuristic = calculateHeuristic(root.state) + root.plan.size();
		queue.add(root);
	}
	
	
	// Defines the estimated cost from the given state to the goal. Is only an estimated cost, i.e.,
	// it does not include the accumulated cost to the given state, which is added in later.
	public int calculateHeuristic(State state){
		double heuristic = 0;
		int cost = 0;
		
		ArrayList<Literal> literalsInThisState = new ArrayList<Literal>();
		HashMap<Literal,Double> costs = new HashMap<Literal,Double>();
		
		for (Step step: steps){
			for (Literal lit: expressionToLiterals(step.precondition)){
				costs.put(lit, infinity);
			}
		}
		
		for (Step step: steps){
			for (Literal lit: expressionToLiterals(step.effect)){
				costs.put(lit, infinity);
			}
		}
		
		for (Literal lit: expressionToLiterals(state.toExpression())){
			literalsInThisState.add(lit);
			costs.put(lit, (double) 0);
		}
		
		double temp;
		
		for (Step step: steps){
			for (Literal lit: expressionToLiterals(step.effect)){
				temp = 1 + costOfAPrecondition(costs,step.precondition);
				if (costs.get(lit) > temp){
					costs.put(lit, (double) temp);
				}
			}
		}
		
		for (Literal litss: expressionToLiterals(problem.goal)){
			heuristic += costs.get(litss);
		}
		return (int) heuristic;
	}
	
	public StateSpaceNode getRoot(){
		return root;
	}
	
	/**More compact way of getting a preconditions cost.*/
	public double costOfAPrecondition(HashMap<Literal,Double> costs, Expression pre){
		double cost = 0;
		for (Literal lit: expressionToLiterals(pre)){
			cost = cost + costs.get(lit);
		}
		return cost;
	}
	
	//Makes a list of all the steps in the problem.
	public void getStepsInProblem(ImmutableArray<Step> sssteps){
		for (Step step : sssteps)
			steps.add((step));
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
	public Plan findNextSolution() {
		while(!queue.isEmpty()) {
			StateSpaceNode node = queue.poll();
			node.expand();
			if(problem.goal.isTrue(node.state))
				return node.plan;
			for(StateSpaceNode child : node.children){
				child.heuristic = calculateHeuristic(child.state) + child.plan.size(); //heuristic calculated here.
				queue.add(child);
			}
		}
		return null;
	}
}
