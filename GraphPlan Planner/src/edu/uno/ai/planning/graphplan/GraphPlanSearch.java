package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;

public class GraphPlanSearch extends Search{
	
	/** PlanGraph used in solving the problem **/
	PlanGraph pg;
	
	/** Actual problem to solve. **/
	public final Problem problem;
	
	private ArrayList<GraphPlanNode> nodes = new ArrayList<GraphPlanNode>();
	
	private int extendedNodes;
	
	private int visitedNodes;
	
	int limit = -1;
	
	public GraphPlanSearch(Problem problem){
		super(problem);
		this.problem = problem;
		this.extendedNodes = 0;
		this.visitedNodes = 0;
		
		pg = new PlanGraph(this.problem, true);
	}
	
	public Plan search(){
		Plan x = null;
		return x;
	}
	
	
	/**
	 * Create a GraphPlanNode which models the steps and literals at a certain level of the PlanGraph.
	 */
	
	public GraphPlanNode createNode(ArrayList<PlanGraphStep> steps, ArrayList<PlanGraphLiteral> literals, int level){
		
		GraphPlanNode node = new GraphPlanNode();
		
		for (PlanGraphStep step: steps ){
			node.addSteps(step);
		}
		
		for (PlanGraphLiteral literal: literals ){
			node.addLiterals(literal);
		}
		
		node.setLevel(level);
		
		nodes.add(node);
		
		return node;
	}
	
	
	@Override
	public int countVisited() {
		return this.visitedNodes;
	}

	@Override
	public int countExpanded() {
		return this.extendedNodes;
	}

	@Override
	public void setNodeLimit(int limit) {
		this.limit = limit;
	}
	
	@Override
	//Function used to actually solve problem.
	public Plan findNextSolution() {
		return search();
	}
	

}
