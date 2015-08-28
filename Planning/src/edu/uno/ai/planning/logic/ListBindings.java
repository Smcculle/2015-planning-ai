package edu.uno.ai.planning.logic;

/**
 * An implementation of the {@link Bindings} data structure based on a linked
 * list, which is easy to clone and modify but provides slower lookups.
 * 
 * @author Your Name
 */
public class ListBindings implements Bindings {

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
