package edu.uno.ai.planning.pg;

import java.util.ArrayList;

import edu.uno.ai.planning.Step;

public class StepNode extends Node {

	public final Step step;
	protected final ArrayList<LiteralNode> preconditions = new ArrayList<>();
	protected final ArrayList<LiteralNode> effects = new ArrayList<>();
	private int literalCount = 0;
	
	protected StepNode(PlanGraph graph, Step step) {
		super(graph);
		this.step = step;
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
}
