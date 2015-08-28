package edu.uno.ai.planning.logic;

/**
 * A substitution specifies that certain terms should be replaced by other
 * terms.
 * 
 * @author Stephen G. Ware
 */
public interface Substitution {

	/**
	 * Returns the term which should be used in place of the given term.  If a
	 * term has no substitution, this method simply returns the term itself.
	 * 
	 * @param term a term
	 * @return the term to use instead of the given term
	 */
	public Term get(Term term);
	
	/** An empty substitution */
	public static final Substitution EMPTY = new Substitution() {

		@Override
		public Term get(Term term) {
			return term;
		}
	};
}
