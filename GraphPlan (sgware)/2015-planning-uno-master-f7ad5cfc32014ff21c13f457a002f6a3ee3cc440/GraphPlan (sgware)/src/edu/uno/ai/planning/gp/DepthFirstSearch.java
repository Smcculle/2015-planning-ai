package edu.uno.ai.planning.gp;

import java.util.Stack;


// for memoization
import java.util.HashMap;
import java.util.ArrayList;


import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.PlanGraph;

public class DepthFirstSearch extends Search {

	public final PlanGraph graph;
	private final Stack<SubgraphNode> stack = new Stack<>();
	private final SubgraphRoot root;
	
	// for memoization
	private HashMap<SubgraphNode, Boolean> memos = new HashMap<>();
	
	// trying with string for key
	private HashMap<String, SubgraphNode> memos2 = new HashMap<>();
	
	private ArrayList<SubgraphNode> nodetemps = new ArrayList<SubgraphNode>();
	
	public DepthFirstSearch(PlanGraph graph) {
		super(graph.problem);
		this.graph = graph;
		this.root = new SubgraphRoot(graph);
		stack.push(root);
	}

	@Override
	public int countVisited() {
		return root.descendants;
	}

	@Override
	public int countExpanded() {
		return root.descendants;
	}

	@Override
	public void setNodeLimit(int limit) {
		root.setNodeLimit(limit);
	}

	@Override
	public Plan findNextSolution() {
		//SubgraphNode temp;
		loop:
		while(!stack.isEmpty()) {
			SubgraphNode node = stack.peek();
			/** Lookup current node in the previously computed hash map. If it's already been searched, continue. */
			/**
///			System.out.print("Checking... ");
///			System.out.print(" " + node.toString2() + " ");
			if (memos2.containsKey(node.toString2())){
///				System.out.println("String found: " + node.toString2());
///				System.out.print("found.\n");
				stack.pop();
///				continue loop;
			}			
///			System.out.print("not found.\n");
///			System.out.println("String not found: " + node.toString2());
			 */ 
			SubgraphNode child = node.expand();

			if(child == null)
				stack.pop();
			else
				stack.push(child);
			if(node.level == 0 && problem.isSolution(node.plan)){
				return node.plan;
			}
			else{
				/** If it wasn't a solution, add it to the hash map, if it isn't there already. */
				/**
				String str = node.toString2();
				if (!memos2.containsKey(str)){
					memos2.put(str, node);
///					System.out.println("Placed.");
				}
///				else System.out.println("Not placed.");
				 */ 			

			}
		}
		return null;
	}
}