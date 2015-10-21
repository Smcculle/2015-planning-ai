package edu.uno.ai.planning.pg;

import java.util.ArrayList;

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
	protected boolean setLevel(int level) {
		if(super.setLevel(level)) {
			for(StepNode consumer : consumers)
				consumer.incrementLiteralCount();
			return true;
		}
		else
			return false;
	}
}
