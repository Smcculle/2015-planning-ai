package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * A planning domain is a reusable set of action templates called
 * {@link Operator}s that describes all the ways the state of the world can
 * change.
 * 
 * @author Stephen G. Ware
 */
public class Domain {

	/** The name of the domain */
	public final String name;
	
	/** A set of objects that must exist in for problems in this domain */
	public final ImmutableArray<Constant> constants;
	
	/** A set of action templates */
	public final ImmutableArray<Operator> operators;
	
	/**
	 * Constructs a new domain.
	 * 
	 * @param name the name of the domain
	 * @param constants a set of objects that must exist in for problems in this domain
	 * @param operators a set of action templates
	 */
	public Domain(String name, ImmutableArray<Constant> constants, ImmutableArray<Operator> operators) {
		this.name = name;
		this.constants = constants;
		this.operators = operators;
	}
	
	/**
	 * Constructs a new domain.
	 * 
	 * @param name the name of the domain
	 * @param constants a set of objects that must exist in for problems in this domain
	 * @param operators a set of action templates
	 */
	public Domain(String name, Constant[] constants, Operator...operators) {
		this(name, new ImmutableArray<>(constants), new ImmutableArray<>(operators));
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return "[" + name + ": " + operators.length + " operators]";
	}
}
