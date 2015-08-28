package edu.uno.ai.planning.logic;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;

/**
 * A negation is a Boolean expression which represents the opposite of some
 * expression.
 * 
 * @author Stephen G. Ware
 */
public class Negation extends BooleanExpression {

	/** The expression whose opposite this negation represents */
	public final Expression argument;
	
	/**
	 * Constructs a new negation for the given argument.
	 * 
	 * @param argument the expression to be negated
	 */
	public Negation(Expression argument) {
		this.argument = argument;
	}
	
	@Override
	public boolean equals(Formula other, Substitution substitution) {
		if(other instanceof Negation) {
			Negation otherNeg = (Negation) other;
			return argument.equals(otherNeg.argument, substitution);
		}
		return false;
	}
	
	/** The negation's hash code */
	private int hashCode = 0;
	
	@Override
	public int hashCode() {
		if(hashCode == 0)
			hashCode = "not".hashCode() + argument.hashCode();
		return hashCode;
	}
	
	@Override
	public String toString() {
		return "(not " + argument + ")";
	}
	
	@Override
	public int compareTo(Formula other) {
		if(other.equals(argument))
			return 1;
		else if(other instanceof Negation)
			return super.compareTo(other);
		else
			return argument.compareTo(other);
	}

	@Override
	public boolean isGround() {
		return argument.isGround();
	}

	@Override
	public Bindings unify(Formula other, Bindings bindings) {
		if(other instanceof Negation)
			return argument.unify(((Negation) other).argument, bindings);
		else
			return null;
	}

	@Override
	public Expression substitute(Substitution substitution) {
		return new Negation(argument.substitute(substitution));
	}

	@Override
	public boolean isTestable() {
		return argument.negate().isTestable();
	}

	@Override
	public boolean isTrue(State state) {
		return argument.negate().isTrue(state);
	}

	@Override
	public boolean isImposable() {
		return argument.negate().isImposable();
	}

	@Override
	public void impose(MutableState state) {
		argument.negate().impose(state);
	}

	@Override
	public Expression negate() {
		return argument;
	}

	@Override
	public Expression toCNF() {
		return argument.negate().toCNF();
	}

	@Override
	public Expression toDNF() {
		return argument.negate().toDNF();
	}

	@Override
	public Expression simplify() {
		return argument.negate();
	}
}
