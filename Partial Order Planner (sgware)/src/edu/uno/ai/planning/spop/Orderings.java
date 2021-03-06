package edu.uno.ai.planning.spop;

import java.util.Iterator;
import java.util.LinkedList;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;

public class Orderings implements Iterable<Step>, Partial {

	private final class Node implements Partial {
		
		public final Step step;
		public final ImmutableList<Step> children;
		
		private Node(Step step, ImmutableList<Step> children) {
			this.step = step;
			this.children = children;
		}
		
		public Node(Step step) {
			this(step, new ImmutableList<>());
		}
		
		@Override
		public String toString() {
			return toString(Bindings.EMPTY);
		}
		
		@Override
		public String toString(Substitution substitution) {
			return step.toString(substitution);
		}
		
		public Node addEdgeTo(Step child) {
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
	
	@Override
	public String toString() {
		return toString(Bindings.EMPTY);
	}
	
	@Override
	public String toString(Substitution substitution) {
		String str = "ORDERINGS:";
		for(Node before : nodes)
			for(Step after : before.children)
				str += "\n  " + before.toString(substitution) + " < " + after.toString(substitution);
		return str;
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
			result = new Orderings(result.nodes.add(beforeNode));
		}
		Node afterNode = getNode(after);
		if(afterNode == null) {
			afterNode = new Node(after);
			result = new Orderings(result.nodes.add(afterNode));
		}
		// Would this ordering create a cycle?
		if(path(afterNode, before))
			return null;
		// Does this ordering already exist?
		if(path(beforeNode, after))
			return this;		
		beforeNode = beforeNode.addEdgeTo(after);
		result = new Orderings(replace(beforeNode, result.nodes));
		return result;
	}
	
	public boolean allows(Step before, Step middle, Step after) {
		Orderings newOrderings = this;
		if(before != middle)
			newOrderings = newOrderings.add(before, middle);
		if(newOrderings == null)
			return false;
		newOrderings = newOrderings.add(middle, after);
		return newOrderings != null;
	}
	
	private final boolean path(Node from, Step to) {
		if(from.step == to)
			return true;
		ImmutableList<Step> children = from.children;
		while(children.length != 0) {
			if(path(getNode(children.first), to))
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
	
	private final Node findStartNode(ImmutableList<Node> nodes) {
		if(nodes.first.step.isStart())
			return nodes.first;
		else
			return findStartNode(nodes.rest);
	}
	
	private final void dfs(Node node, LinkedList<Node> nodes) {
		if(nodes.contains(node))
			return;
		ImmutableList<Step> children = node.children;
		while(children.length != 0) {
			dfs(getNode(children.first), nodes);
			children = children.rest;
		}
		nodes.add(0, node);
	}
}
