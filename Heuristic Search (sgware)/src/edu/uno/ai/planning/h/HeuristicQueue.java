package edu.uno.ai.planning.h;

import java.util.Comparator;
import java.util.PriorityQueue;

import edu.uno.ai.planning.ss.StateSpaceNode;

public class HeuristicQueue {
	
	private final class Node {
		
		public final StateSpaceNode state;
		public final double heuristic;
		
		Node(StateSpaceNode state) {
			this.state = state;
			this.heuristic = HeuristicQueue.this.heuristic.estimate(state.state);
		}
	}
	
	public final Heuristic heuristic;
	private final PriorityQueue<Node> queue;
	
	public HeuristicQueue(Heuristic heuristic, HeuristicComparator comparator) {
		this.heuristic = heuristic;
		this.queue = new PriorityQueue<>(new Comparator<Node>(){
			@Override
			public int compare(Node n1, Node n2) {
				double comparison = comparator.compare(n1.state.plan, n1.state.state, n1.heuristic, n2.state.plan, n2.state.state, n2.heuristic);
				if(comparison == 0)
					return 0;
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
	
	public void push(StateSpaceNode node) {
		queue.add(new Node(node));
	}
	
	public StateSpaceNode peek() {
		return queue.peek().state;
	}
	
	public StateSpaceNode pop() {
		return queue.poll().state;
	}
}
