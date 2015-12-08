package edu.uno.ai.planning.h;

import edu.uno.ai.planning.ss.StateSpaceNode;

public class HeuristicNode {

	private static int nextID = 0;
	
	final int id = nextID++;
	public final StateSpaceNode state;
	public final double heuristic;
	
	HeuristicNode(StateSpaceNode state, double heuristic) {
		this.state = state;
		this.heuristic = heuristic;
	}
}
