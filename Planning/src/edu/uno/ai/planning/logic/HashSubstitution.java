package edu.uno.ai.planning.logic;

import java.util.HashMap;

/**
 * A substitution which uses a {@link java.util.HashMap} to keep track of
 * substitutions.
 * 
 * @author Stephen G. Ware
 */
public class HashSubstitution implements Substitution, Cloneable {

	/** The map used to keep track of substitutions */
	private final HashMap<Term, Term> subs;
	
	/**
	 * Constructs a new hash substitution which is a clone of the given
	 * substitution.
	 * 
	 * @param toClone the subsitution to clone
	 */
	protected HashSubstitution(HashSubstitution toClone) {
		this.subs = toClone.subs;
	}
	
	/**
	 * Constructs a new, empty hash substitution.
	 */
	public HashSubstitution() {
		this.subs = new HashMap<>();
	}
	
	@Override
	public Term get(Term term) {
		Term value = subs.get(term);
		if(value == null)
			return term;
		else
			return value;
	}
	
	/**
	 * Specifies that a given term should be replaced with another term.
	 * 
	 * @param term the term to be replaced
	 * @param substitute the term that will replace it
	 */
	public void set(Term term, Term substitute) {
		subs.put(term, substitute);
	}
	
	@Override
	public HashSubstitution clone() {
		return new HashSubstitution(this);
	}
}
