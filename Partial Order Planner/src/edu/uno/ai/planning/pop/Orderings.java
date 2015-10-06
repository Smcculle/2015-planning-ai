package edu.uno.ai.planning.pop;

import java.util.Iterator;
import java.util.LinkedList;

import edu.uno.ai.planning.util.ImmutableList;

public class Orderings implements Iterable<Step> {

	private final class Node {
		
		public final Step step;
		public final ImmutableList<Node> children;
		
		private Node(Step step, ImmutableList<Node> children) {
			this.step = step;
			this.children = children;
		}
		
		public Node(Step step) {
			this(step, new ImmutableList<>());
		}
		
		public Node addEdgeTo(Node child) {
			return new Node(step, children.add(child));
		}
	}
	
	private final ImmutableList<Node> nodes;
	
	private Orderings(ImmutableList<Node> nodes) {
		this.nodes = nodes;
	}
	
	Orderings() {
		this(new ImmutableList<Node>());
	}
	
	private final Node getNode(Step step) {
		ImmutableList<Node> current = nodes;
		while(current != null && current.length != 0) {
			if(current.first.step == step)
				return current.first;
			current = current.rest;
		}
		return null;
	}
	
	public Orderings add(Step before, Step after) {
		Orderings result = this;
		Node beforeNode = getNode(before);
		if(beforeNode == null) {
			beforeNode = new Node(before);
			result = new Orderings(nodes.add(beforeNode));
		}
		Node afterNode = getNode(after);
		if(afterNode == null) {
			afterNode = new Node(after);
			result = new Orderings(nodes.add(afterNode));
		}
		if(path(afterNode, beforeNode))
			return null;
		beforeNode = beforeNode.addEdgeTo(afterNode);
		result = new Orderings(replace(beforeNode, result.nodes));
		return result;
	}
	
	private static final boolean path(Node from, Node to) {
		if(from.step == to.step)
			return true;
		ImmutableList<Node> children = from.children;
		while(children.length != 0) {
			if(path(children.first, to))
				return true;
			children = children.rest;
		}
		return false;
	}
	
	private static final ImmutableList<Node> replace(Node node, ImmutableList<Node> nodes) {
		if(nodes.first.step == node.step)
			return nodes.rest.add(node);
		else
			return replace(node, nodes.rest).add(nodes.first);
	}

	@Override
	public Iterator<Step> iterator() {
		return topologicalSort().iterator();
	}
	
	private final LinkedList<Step> topologicalSort() {
		LinkedList<Node> nodes = new LinkedList<>();
		dfs(findStartNode(this.nodes), nodes);
		LinkedList<Step> steps = new LinkedList<>();
		for(Node node : nodes)
			steps.add(node.step);
		return steps;
	}
	
	private static final Node findStartNode(ImmutableList<Node> nodes) {
		if(nodes.first.step.isStart())
			return nodes.first;
		else
			return findStartNode(nodes.rest);
	}
	
	private static final void dfs(Node node, LinkedList<Node> nodes) {
		if(nodes.contains(node))
			return;
		ImmutableList<Node> children = node.children;
		while(children.length != 0) {
			dfs(children.first, nodes);
			children = children.rest;
		}
		nodes.add(0, node);
	}
}
