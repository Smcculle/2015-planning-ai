package edu.uno.ai.planning.logic;

/**
 * An implementation of the {@link Bindings} data structure based on a hash
 * table, which allows fast lookups but is costly to clone and modify.
 * 
 * @author Your Name
 */
public class HashBindings implements Bindings {

	@Override
	public Term get(Term term) {
		return term;
	}

	@Override
	public Bindings setEqual(Term t1, Term t2) {
		return this;
	}

	@Override
	public Bindings setNotEqual(Term t1, Term t2) {
		return this;
	}
}
