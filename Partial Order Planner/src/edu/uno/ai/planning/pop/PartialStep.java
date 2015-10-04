package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * This class represents a step which has its parameters replaced with unique variables which are used to bind int he bindings object
 * @author dpeabody
 *
 */
public class PartialStep extends Operator{
	
	public ImmutableArray<Variable> partialBinds;

	public PartialStep(String name, Variable[] parameters,
			Expression precondition, Expression effect) {
		super(name, parameters, precondition, effect);
		Variable[] temp = new Variable[parameters.length];
		for(int i=0; i<parameters.length;i++){
			temp[i] = parameters[i].makeUnique();
		}
		this.partialBinds = new ImmutableArray<Variable>(temp);
	}
	
	/**
	 * Creates a ground step (i.e. a specific action) from this action
	 * template.
	 * 
	 * @param substitution provides bindings for each of the operator's parameters
	 * @return a step
	 */
	@Override
	public Step makeStep(Substitution substitution) {
		String name = "(" + this.name;
		for(Variable parameter : partialBinds)
			name += " " + parameter.substitute(substitution);
		name += ")";
		return new Step(name, precondition.substitute(substitution), effect.substitute(substitution));
	}
	
	@Override
	public String toString() {
		String str = "(" + name;
		for(Variable parameter : partialBinds)
			str += " "  + parameter;
		return str + ")";
	}
	
	
}