package edu.uno.ai.planning.spop;

import edu.uno.ai.planning.Problem;

public class PlanSpaceRoot extends PlanSpaceNode {
	
	public final Problem problem;
	int limit = -1;
	
	PlanSpaceRoot(Problem problem) {
		super(problem);
		this.problem = problem;
	}
	
	void setNodeLimit(int limit) {
		this.limit = limit;
	}
}
