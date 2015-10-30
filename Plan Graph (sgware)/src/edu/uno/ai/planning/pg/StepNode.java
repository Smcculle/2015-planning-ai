package edu.uno.ai.planning.pg;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Literal;

public class StepNode extends Node {

	public final Step step;
	public final boolean persistence;
	protected final ArrayList<LiteralNode> preconditions = new ArrayList<>();
	protected final ArrayList<LiteralNode> effects = new ArrayList<>();
	private int literalCount = 0;
	
	protected StepNode(PlanGraph graph, Step step) {
		super(graph);
		this.step = step;
		this.persistence = false;
	}
	
	protected StepNode(PlanGraph graph, Literal literal) {
		super(graph);
		this.step = new Step("(persist " + literal + ")", literal, literal);
		this.persistence = true;
	}
	
	@Override
	public int hashCode() {
		return step.hashCode();
	}
	
	@Override
	public String toString() {
		return step.toString();
	}
	
	protected void incrementLiteralCount() {
		markForReset();
		literalCount++;
		if(literalCount == preconditions.size())
			graph.nextSteps.add(this);
	}
	
	@Override
	protected boolean setLevel(int level) {
		if(super.setLevel(level)) {
			for(LiteralNode effect : effects)
				effect.setLevel(level);
			return true;
		}
		else
			return false;
	}
	
	@Override
	protected void reset() {
		super.reset();
		literalCount = 0;
	}
	
	public Iterable<LiteralNode> getPreconditions(int level) {
		return new Iterable<LiteralNode>() {
			@Override
			public Iterator<LiteralNode> iterator() {
				return new NodeIterator<>(level - 1, preconditions);
			}
		};
	}
	
	public Iterable<LiteralNode> getEffects(int level) {
		return new Iterable<LiteralNode>() {
			@Override
			public Iterator<LiteralNode> iterator() {
				return new NodeIterator<>(level, effects);
			}
		};
	}
}
