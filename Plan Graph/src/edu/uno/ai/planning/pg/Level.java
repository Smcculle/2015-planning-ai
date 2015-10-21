package edu.uno.ai.planning.pg;

public class Level {

	public final int number;
	protected final PlanGraph graph;
	
	protected Level(PlanGraph graph, int number) {
		this.graph = graph;
		this.number = number;
	}
}
