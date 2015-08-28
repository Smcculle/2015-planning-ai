package edu.uno.ai.planning.logic;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;

/**
 * The negation of a literal is a literal.
 * 
 * @author Stephen G. Ware
 */
public class NegatedLiteral extends Negation implements Literal {

	/** The literal being negated */
	public final Literal argument;
	
	/**
	 * Constructs a new negated literal.
	 * 
	 * @param argument the literal to be negated
	 */
	public NegatedLiteral(Literal argument) {
		super(argument);
		this.argument = argument;
	}

	@Override
	public Literal substitute(Substitution substitution) {
		return new NegatedLiteral(argument.substitute(substitution));
	}
	
	@Override
	public boolean isTestable() {
		return true;
	}
	
	@Override
	public boolean isTrue(State state) {
		return state.isTrue(this);
	}

	@Override
	public boolean isImposable() {
		return true;
	}

	@Override
	public void impose(MutableState state) {
		state.impose(this);
	}
	
	@Override
	public Literal negate() {
		return argument;
	}
	
	@Override
	public Expression toCNF() {
		return this;
	}

	@Override
	public Expression toDNF() {
		return this;
	}
	
	@Override
	public Expression simplify() {
		return this;
	}
}
