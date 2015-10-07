package edu.uno.ai.planning.logic;

/**
 * A data structure which tracks constraints on the values of variables.
 * Note that this data structure is immutable.  All methods which add
 * constraints return a clone of the bindings object which obeys the new
 * constraints (or null if the constraints are impossible to satisfy).
 * 
 * @author Stephen G. Ware
 */
public interface Bindings extends Substitution {
	
	/** An empty set of bindings */
	public static final Bindings EMPTY = new ListBindings();
	
	/**
	 * Constrains two terms to be the same, if possible.
	 * 
	 * @param t1 the first term
	 * @param t2 the second term
	 * @return a new set of bindings if the constraint is legal, null if the constraint is illegal
	 */
	public abstract Bindings setEqual(Term t1, Term t2);
	
	/**
	 * Constrains two terms to be different, if possible.
	 * 
	 * @param t1 the first term
	 * @param t2 the second term
	 * @return a new set of bindings if the constraint is legal, null if the constraint is illegal
	 */
	public abstract Bindings setNotEqual(Term t1, Term t2);
}
