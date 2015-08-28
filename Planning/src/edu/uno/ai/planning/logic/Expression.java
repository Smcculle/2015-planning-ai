package edu.uno.ai.planning.logic;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;

/**
 * An expression is any logical formula with a truth value.
 * 
 * @author Stephen G. Ware
 */
public interface Expression extends Formula {
	
	@Override
	public Expression substitute(Substitution substitution);
	
	/**
	 * Returns true if this expression can be tested (i.e. can be a goal or a
	 * precondition.
	 * 
	 * @return true if the formula can be tested, false otherwise
	 */
	public boolean isTestable();
	
	/**
	 * Tests whether or not this expression is true in the given state.
	 * 
	 * @param state the state in which the expression may be true or false
	 * @return true if the expression is true, false otherwise
	 */
	public boolean isTrue(State state);
	
	/**
	 * Returns true if this expression can be imposed (i.e. can be in the
	 * initial state or can be an effect).  An expression must be deterministic
	 * to be impossible.
	 * 
	 * @return true if the formula can be imposed, false otherwise
	 */
	public boolean isImposable();
	
	/**
	 * Makes this expression true in the given state.
	 * 
	 * @param state the state to modify
	 */
	public void impose(MutableState state);
	
	/**
	 * Returns the negation (opposite) of this expression.
	 * 
	 * @return the negation
	 */
	public Expression negate();
	
	/**
	 * Converts this expression to conjunctive normal form.
	 * 
	 * @return a new expression equivalent to this one but in conjunctive normal form
	 */
	public Expression toCNF();
	
	/**
	 * Converts this expression to disjunctive normal form.
	 * 
	 * @return a new expression equivalent to this one but in conjunctive normal form
	 */
	public Expression toDNF();
	
	/**
	 * Simplifies this expression if possible.
	 * 
	 * @return a new, simpler expression equivalent to this one
	 */
	public Expression simplify();
}
