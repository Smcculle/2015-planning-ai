package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Expression;

public class DurativeStep extends Step {  

	private int duration = Integer.MIN_VALUE;
	private DurativeType type;
	
	public enum DurativeType { START, INVARIANT, END };
	
	public DurativeStep(String name, Expression precondition, Expression effect, int newDuration, DurativeType newType) {
		super(name, precondition, effect);
		this.duration = newDuration;
		this.type = newType;
	}
	
	public boolean isDurative(){
		return this.duration != Integer.MIN_VALUE;
	}
	
	public int getDuration(){
		return this.duration;
	}
	
	public boolean isStart(){
		return type == DurativeType.START;
	}
	
	public boolean isInvariant(){
		return type == DurativeType.INVARIANT;
	}
	
	public boolean isEnd(){
		return type == DurativeType.END;
	}
	
	@Override
	public String toString(){
		return super.toString() + " Duration: " + getDuration(); 
	}
	
}
