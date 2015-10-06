package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Problem;

public class PlanSpaceRoot extends PlanSpaceNode {
	
	int limit = -1;
	
	PlanSpaceRoot(Problem problem) {
		super(problem);
	}
	
	void setNodeLimit(int limit) {
		this.limit = limit;
	}
}
