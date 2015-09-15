package edu.uno.ai.planning.logic;


/**
 * Terms represent objects in the world.  Terms do not have a truth value.
 * 
 * @author Stephen G. Ware
 */
public abstract class Term implements Formula {

	/** A term's type specifies which kind of values it can have */
	public final String type;
	
	/** The name of the term */
	public final String name;
	
	/** The hash code for this term */
	private final int hashCode;
	
	/**
	 * Constructs a new term with a given type and name.
	 * 
	 * @param type the type of the term
	 * @param name the name of the term
	 */
	public Term(String type, String name) {
		this.type = type;
		this.name = name;
		this.hashCode = type.hashCode() * name.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Formula)
			return equals((Formula) other, Substitution.EMPTY);
		else
			return false;
	}
	
	@Override
	public boolean equals(Formula other, Substitution substitution) {
		if(other instanceof Term) {
			Term me = substitute(substitution);
			Term otherTerm = (Term) other.substitute(substitution);
			return me.getClass() == otherTerm.getClass() && type.equals(otherTerm.type) && name.equals(otherTerm.name);
		}
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public abstract Term substitute(Substitution substitution);
	
	@Override
	public Bindings unify(Formula other, Bindings bindings) {
		if(other instanceof Term)
			return bindings.setEqual(this, (Term) other);
		else
			return null;
	}
}
