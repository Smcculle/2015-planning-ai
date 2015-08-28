package edu.uno.ai.planning.logic;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;

/**
 * A literal is an atomic expression (i.e. one which cannot be decomposed into
 * smaller expressions) or the negation of such an atomic expression.
 * 
 * @author Stephen G. Ware
 */
public interface Literal extends Expression {
	
	@Override
	public Literal substitute(Substitution substitution);
	
	@Override
	public Literal negate();
	
	@Override
	public default boolean isTestable() {
		return true;
	}
	
	@Override
	public default boolean isTrue(State state) {
		return state.isTrue(this);
	}

	@Override
	public default boolean isImposable() {
		return true;
	}

	@Override
	public default void impose(MutableState state) {
		state.impose(this);
	}
	
	@Override
	public default Expression toCNF() {
		return this;
	}

	@Override
	public default Expression toDNF() {
		return this;
	}
	
	@Override
	public default Expression simplify() {
		return this;
	}
}
