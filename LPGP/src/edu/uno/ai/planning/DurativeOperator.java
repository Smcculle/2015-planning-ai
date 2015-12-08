package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Substitution;
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

	@Override
	public Step makeStep(Substitution substitution) {
		String name = "(" + this.name;
		for(Variable parameter : parameters)
			name += " " + parameter.substitute(substitution);
		name += ")";
//		Enum type = DurativeStep.DurativeType.NON;
		DurativeStep.DurativeType type = DurativeStep.DurativeType.NON;
		if(name.endsWith("-start"))
			type = DurativeStep.DurativeType.START;
		else if(name.endsWith("-inv"))
			type = DurativeStep.DurativeType.INVARIANT;
		else if(name.endsWith("-end"))
			type = DurativeStep.DurativeType.END;
		return new DurativeStep(name, precondition.substitute(substitution), effect.substitute(substitution), duration, type);
	}

}
