package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class DurativeOperator extends Operator {
	
	/** Action Duration **/
	private final int duration;

	public DurativeOperator(String newName, Integer newDuration, ImmutableArray<Variable> newParameters, Expression newPrecondition, Expression newEffect) {
		super(newName, newParameters, newPrecondition, newEffect);
		duration = newDuration;
	}
	
	public DurativeOperator(String newName, Integer newDuration, Variable[] newParameters, Expression newPrecondition, Expression newEffect){
		super(newName, newParameters, newPrecondition, newEffect);
		duration = newDuration;
	}
	
	public int getDuration(){
		return this.duration;
	}

}
