package edu.uno.ai.planning.pg;

import edu.uno.ai.planning.logic.Literal;

public class Level {

	public final int number;
	public final Level previous;
	protected final PlanGraph graph;
	
	protected Level(PlanGraph graph, int number) {
		this.graph = graph;
		this.number = number;
		this.previous = number == 0 ? null : graph.getLevel(number - 1);
	}
	
	final void computeMutexes() {
		
	}
}
