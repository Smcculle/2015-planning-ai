package edu.uno.ai.planning.pg;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uno.ai.planning.logic.Literal;

public class LiteralNode extends Node {

	public final Literal literal;
	protected final ArrayList<StepNode> producers = new ArrayList<>();
	protected final ArrayList<StepNode> consumers = new ArrayList<>();

	protected LiteralNode(PlanGraph graph, Literal literal) {
		super(graph);
		this.literal = literal;
	}
	
	@Override
	public String toString() {
		return literal.toString();
	}
	
	@Override
	protected boolean setLevel(int level) {
		if(super.setLevel(level)) {
			for(StepNode consumer : consumers)
				consumer.incrementLiteralCount();
			return true;
		}
		else
			return false;
	}
	
	public Iterable<StepNode> getProducers(int level) {
		return new Iterable<StepNode>() {
			@Override
			public Iterator<StepNode> iterator() {
				return new NodeIterator<>(level - 1, producers);
			}
		};
	}
	
	public Iterable<StepNode> getConsumers(int level) {
		return new Iterable<StepNode>() {
			@Override
			public Iterator<StepNode> iterator() {
				return new NodeIterator<>(level, consumers);
			}
		};
	}
	
	public boolean mutex(LiteralNode other, int level) {
		if(level < this.level)
			throw new IllegalArgumentException(literal + " does not exist at level " + level + ".");
		if(level < other.level)
			throw new IllegalArgumentException(other + " does not exist at level " + level + ".");
		return false;
	}
}
