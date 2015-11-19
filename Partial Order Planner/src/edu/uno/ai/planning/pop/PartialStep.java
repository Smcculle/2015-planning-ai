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
public class PartialStep{

	
	/** The name of the action */
	public final String name;
	
	public ImmutableArray<Term> partialBinds;

		
	/** What must be true before the action can be taken */
	public final Expression precondition;
	
	/** What becomes true after the action is taken */
	public final Expression effect;

	public PartialStep(Operator operator) {
		
		if(!isDeterministic(operator.effect))
			throw new IllegalArgumentException("Effect nondeterministic");
		
		HashSubstitution subs = new HashSubstitution();

		Variable[] temp = new Variable[operator.parameters.length];

		for (int i=0; i < operator.parameters.length; i++) {
			temp[i] = operator.parameters.get(i).makeUnique();
			subs.set(operator.parameters.get(i), temp[i]);			
		}
		
		this.name = operator.name;
		this.precondition = operator.precondition.substitute(subs);
		this.effect = operator.effect.substitute(subs);
		this.partialBinds = new ImmutableArray<Term>(temp);
	}

	public PartialStep(String name, Variable[] parameters,
			Expression precondition, Expression effect) {

		this.partialBinds = new ImmutableArray<Term>(parameters);
		this.precondition = precondition;
		this.name = name;
		this.effect = effect;
		
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
	
	private static final boolean isDeterministic(Expression expression) {
		expression = expression.toDNF();
		if(!(expression instanceof Disjunction))
			return false;
		Disjunction dnf = (Disjunction) expression;
		if(dnf.arguments.length != 1)
			return false;
		if(!(dnf.arguments.get(0) instanceof Conjunction))
			return false;
		Conjunction clause = (Conjunction) dnf.arguments.get(0);
		for(Expression literal : clause.arguments)
			if(!(literal instanceof Literal))
				return false;
		return true;
	}

	/**
	 * Creates a ground step (i.e. a specific action) from this action
	 * template.
	 *
	 * @param substitution provides bindings for each of the operator's parameters
	 * @return a step
	 */
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