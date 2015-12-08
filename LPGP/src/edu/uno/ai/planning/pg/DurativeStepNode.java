package edu.uno.ai.planning.pg;

import edu.uno.ai.planning.DurativeStep;

public class DurativeStepNode extends StepNode { 
	
	protected DurativeStepNode(PlanGraph graph, DurativeStep step) {
		super(graph, step);
	}
	
	public boolean isDurative(){
		return getStep().isDurative();
	}
	
	public DurativeStep getStep(){
		return (DurativeStep) step;
	}
	
	public int getDuration(){
		return getStep().getDuration();
	}
	
	public boolean isStart(){
		return getStep().isStart();
	}
	
	public boolean isInvariant(){
		return getStep().isInvariant();
	}
	
	public boolean isEnd(){
		return getStep().isEnd();
	}

}
