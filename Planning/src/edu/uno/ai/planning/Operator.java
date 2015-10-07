package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * An operator is an action template that describes one way to change the world in terms
 * of its precondition (what must be true before the action can be taken) and effect
 * (what becomes true after the action is taken).
 * 
 * @author Stephen G. Ware
 */
public class Operator {

	/** The name of the action */
	public final String name;
	
	/** The parameters that provide the specific details for the action */
	public final ImmutableArray<Variable> parameters;
	
	/** What must be true before the action can be taken */
	public final Expression precondition;
	
	/** What becomes true after the action is taken */
	public final Expression effect;
	
	/**
	 * Constructs a new action teamplte.
	 * 
	 * @param name the name of the action
	 * @param parameters the parameters that provide specific detail
	 * @param precondition what must be true before
	 * @param effect what becomes true after
	 */
	public Operator(String name, ImmutableArray<Variable> parameters, Expression precondition, Expression effect) {
		if(!isDeterministic(effect))
			throw new IllegalArgumentException("Effect nondeterministic");
		this.name = name;
		this.parameters = parameters;
		this.precondition = precondition;
		this.effect = effect;
	}
	
	/**
	 * Constructs a new action template.
	 * 
	 * @param name the name of the action
	 * @param parameters the parameters that provide specific detail
	 * @param precondition what must be true before
	 * @param effect what becomes true after
	 */
	public Operator(String name, Variable[] parameters, Expression precondition, Expression effect) {
		this(name, new ImmutableArray<>(parameters), precondition, effect);
	}
	
	/**
	 * Checks if an effect expression is deterministic (i.e. results in exactly
	 * one possible next state).
	 * 
	 * @param expression the expression to test
	 * @return true of the expression is deterministic, false otherwise
	 */
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
		for(Variable parameter : parameters)
			name += " " + parameter.substitute(substitution);
		name += ")";
		return new Step(name, precondition.substitute(substitution), effect.substitute(substitution));
	}
	
	@Override
	public String toString() {
		String str = "(" + name;
		for(Variable parameter : parameters)
			str += " "  + parameter;
		return str + ")";
	}
}
