package edu.uno.ai.planning.h;

import java.util.Comparator;
import java.util.PriorityQueue;

import edu.uno.ai.planning.ss.StateSpaceNode;

public class HeuristicQueue {
	
	public final StateHeuristic heuristic;
	private final PriorityQueue<HeuristicNode> queue;
	
	public HeuristicQueue(StateHeuristic heuristic, HeuristicComparator comparator) {
		this.heuristic = heuristic;
		this.queue = new PriorityQueue<>(new Comparator<HeuristicNode>(){
			@Override
			public int compare(HeuristicNode n1, HeuristicNode n2) {
				double comparison = comparator.compare(n1, n2);
				if(comparison == 0)
					return n1.id - n2.id;
				else if(comparison < 0)
					return -1;
				else
					return 1;
			}
		});
	}
	
	public void clear() {
		queue.clear();
	}
	
	public int size() {
		return queue.size();
	}
	
	public HeuristicNode push(StateSpaceNode node) {
		HeuristicNode n = new HeuristicNode(node, heuristic.evaluate(node.state));
		queue.add(n);
		return n;
	}
	
	public HeuristicNode peek() {
		return queue.peek();
	}
	
	public double hPeek() {
		return queue.peek().heuristic;
	}
	
	public StateSpaceNode pop() {
		return queue.poll().state;
	}
}
