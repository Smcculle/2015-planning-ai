package edu.uno.ai.planning.pop;

import java.util.*;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.util.*;

/**
 * This class represents a step which has its parameters replaced with unique variables which are used to bind in he bindings object
 * @author dpeabody
 *
 */
public class PartialStep extends Operator{

	public ImmutableArray<Term> partialBinds;

	public PartialStep(String name, Variable[] parameters,
			Expression precondition, Expression effect) {
		super(name, parameters, precondition, effect);
		Variable[] temp = new Variable[parameters.length];
		for(int i=0; i<parameters.length;i++){
			temp[i] = parameters[i].makeUnique();
		}
		this.partialBinds = new ImmutableArray<Term>(temp);
	}

	public ArrayList<Expression> effects() {
		ArrayList<Expression> result = new ArrayList<Expression>();

		if (this.effect instanceof Literal) {
			result.add(this.effect);
		}
		else if (this.effect instanceof Conjunction) {
			Conjunction effects = (Conjunction)this.effect;

			for(Expression expression : effects.arguments) {
				result.add(expression);
			}
		}

		return result;
	}

	public boolean isEnd() {
		boolean hasEndName = this.name.equalsIgnoreCase("End");
		boolean preconditionEqualsEffect = this.precondition.equals(this.effect);

		return hasEndName && preconditionEqualsEffect;
	}

	public boolean isStart() {
		boolean hasStartName = this.name.equalsIgnoreCase("Start");
		boolean hasNoPrecondition = this.precondition == null;

		return hasStartName && hasNoPrecondition;
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
		for(Term parameter : partialBinds)
			name += " " + parameter.substitute(substitution);
		name += ")";
		return new Step(name, precondition.substitute(substitution), effect.substitute(substitution));
	}

	@Override
	public String toString() {
		String str = "(" + name;
		for(Term parameter : partialBinds)
			str += " "  + parameter;
		return str + ")";
	}
}