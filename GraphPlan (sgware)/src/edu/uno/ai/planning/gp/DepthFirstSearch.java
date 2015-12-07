package edu.uno.ai.planning.gp;

import java.util.Stack;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.pg.PlanGraph;

// for memoization
import java.util.HashMap;
// second attempt for greater speed
import java.util.HashSet;

public class DepthFirstSearch extends Search {

	public final PlanGraph graph;
	private final Stack<SubgraphNode> stack = new Stack<>();
	private final SubgraphRoot root;
	
	// for memoization
	private HashMap<Integer, Integer> memos = new HashMap<Integer, Integer>();
	private HashSet<Integer> memos2 = new HashSet<Integer>();
	// go back to the old plan with strings
	private HashSet<String> memos3 = new HashSet<String>();
	// idk anymore
	private HashMap<Integer, String> memos4 = new HashMap<Integer, String>();
	// same as above btu add parent to me
	private HashMap<Long, String> memos5 = new HashMap<Long, String>();
	
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
		while(!stack.isEmpty()) {
			SubgraphNode node = stack.peek();
			/** Check against the memos. */
/**			int me = node.hashCode();
//			if (node.parent != null) me += node.parent.hashCode();
			int parent;
			if (node.parent == null) parent = 0;
			else parent = node.parent.hashCode();
			*/
//			if (memos.containsKey(me) && memos.containsValue(parent)){
//			if (memos.containsKey(node.hashCode())){
//			if (memos.contains(node.hashCode())){
//				if (memos.get(node.hashCode()) == node.parent.hashCode()){
///				System.out.println("Found.");
/**			int me = node.getMe();
			int parent = node.getParent();
			Integer n = memos.get(me);
			if (n != null && n.equals(parent)){
			*/
			// go back tostrings
			// or not idk
//			if (memos3.contains(node.toString2())){
			
			String m = node.toString2();
			int me = node.getMe();
			int parent = node.getParent();
		//	int me = node.hashCode();
	//		Long me = node.getMe2();
			
			String s = memos4.get(me);
	//		Integer n = memos.get(me);
//			if (true){
//			long me = node.getMe2();
		//	String s = memos5.get(me);
			if (s != null && s.equals(m)){
		//	if (n != null && n.equals(parent)){
//			if (memos4.containsKey(me)){
//			if (memos3.contains(m)){
//			if (memos2.contains(me)){
		//			System.out.println("Node " + me + " and my parent " + parent + " found.");
					
				//	System.out.println("FFOUND");
					stack.pop();
					if (stack.isEmpty()) return null;
					continue;
				
			}
//			System.out.println("nah");
	//	}
	//		System.out.println("Not found: " + m);
			//System.out.println("Node " + me + " and my parent " + parent + " NOT found.");

			///			System.out.println("Not found.");
			SubgraphNode child = node.expand();
			if(child == null)
				stack.pop();
			else
				stack.push(child);
			if(node.level == 0 && problem.isSolution(node.plan))
				return node.plan;
			/** If the node isn't a solution, add it to the memos. */
//			else if (node.level < 4 || node.level < (graph.size() - 5)){
			//else if (node.level < 1 && !problem.isSolution(node.plan)){
			else if (true){
			// idk what's happening
//			else if (node.level <= 3){
//			else if (node.level < (graph.size() - 5)){
	//			int me = node.hashCode();
//				int parent = node.parent.hashCode();
			
		//		if (!memos.containsKey(me)){
			//		System.out.println("Adding.");
//					memos.put(me, null);
			//	}
//				memos.put(me,  parent);
//				memos3.add(node.toString2());
//				memos4.put(node.getMe(), node.parent.toString2());
//				if (node.parent != null) memos4.put(node.getMe(), node.parent.toString2());
				if (node.level < node.plan.size() - 1){
//				if (true){
//				if (node.level < graph.size() - 1){
//				if (node.level < graph.size() - 1){
//				memos4.put(node.getMe(), node.toString2());
//					memos.put(me, parent);
//					memos4.put(me, m);
					memos4.put(node.getMe(), node.toString2());
	//				memos3.add(node.toString2());
//					System.out.println("Placed " + node.toString2());
//					memos2.add(me);
//					memos5.put(me, node.toString2());
				}
	//			else System.out.println("Not adding.");
				
	//			memos2.add(me);
			}
		}
		return null;
	}
}