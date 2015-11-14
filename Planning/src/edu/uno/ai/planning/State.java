package edu.uno.ai.planning;

import java.util.HashSet;

import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;

/**
 * A state completely describes the disposition of all the objects in the
 * world at some particular time.  Note that, by default, a state cannot be
 * modified.  See {@link MutableState} for a state which can be modified.
 * 
 * @author Stephen G. Ware
 */
public class State implements Cloneable {

	/** The set of currently true literals */
	protected final HashSet<Literal> literals;
	
	/**
	 * Creates a new state which is a clone of the given state.
	 * 
	 * @param toClone the state to clone
	 */
	@SuppressWarnings("unchecked")
	State(State toClone) {
		this.literals = (HashSet<Literal>) toClone.literals.clone();
	}
	
	/**
	 * Constructs a new, empty state.
	 */
	public State() {
		this.literals = new HashSet<>();
	}
	
	/**
	 * Tests if a given literal is true in this state.
	 * 
	 * @param proposition the literal to test
	 * @return true if the literal is true, false otherwise
	 */
	public boolean isTrue(Literal proposition) {
		if(proposition instanceof NegatedLiteral)
			return !isTrue(((NegatedLiteral) proposition).argument);
		else
			return literals.contains(proposition);
	}
	
	/**
	 * If the precondition of the given step is met, this method returns a new
	 * state which has been modified according to the step's effect.
	 * 
	 * @param step the step to take
	 * @return a new state after the effect has been applied
	 * @throws IllegalArgumentException if the step's precondition is not met
	 */
	public State apply(Step step) {
		if(step.precondition.isTrue(this)) {
			MutableState state = new MutableState(this);
			step.effect.impose(state);
			return state;
		}
		else
			throw new IllegalArgumentException("Cannot apply " + step + "; precondition " + step.precondition + " is not met");
	}
	
	/**
	 * Returns a logical expression representing the current state.
	 * 
	 * @return an expression
	 */
	public Expression toExpression() {
		return new Conjunction(literals.toArray(new Literal[literals.size()])).simplify();
	}
	
	@Override
	public String toString() {
		return toExpression().toString();
	}
	
	@Override
	public State clone() {
		return new State(this);
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((literals == null) ? 0 : literals.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		State other = (State) obj;
//		if (literals == null) {
//			if (other.literals != null)
//				return false;
//		} else if (!literals.equals(other.literals))
//			return false;
//		return true;
//	}
	
	
	
}
