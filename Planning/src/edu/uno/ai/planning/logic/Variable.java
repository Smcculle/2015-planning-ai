package edu.uno.ai.planning.logic;

import java.util.HashSet;

/**
 * A variable represents a placeholder for a constant whose value is not yet
 * known.
 * 
 * @author Stephen G. Ware
 */
public class Variable extends Term {

	/** The next ID number to be used when creating a unique term */
	private static int nextID = 0;
	
	/** Stores the names of all variables */
	private static HashSet<String> names = new HashSet<>();
	
	/**
	 * Constructs a new variable with a given type and name.
	 * 
	 * @param type the type of the variable
	 * @param name the name of the variable
	 */
	public Variable(String type, String name) {
		super(type, name);
		names.add(name);
	}
	
	@Override
	public String toString() {
		return "?" + name;
	}

	@Override
	public boolean isGround() {
		return false;
	}

	/**
	 * Creates a new variable whose name is similar to this one's but which is
	 * guaranteed to be unique.
	 * 
	 * @return a unique variable with a similar name to this variable's name
	 */
	public Variable makeUnique() {
		String name;
		do {
			name = this.name + "-" + nextID++;
		} while(!names.contains(name));
		return new Variable(type, name);
	}

	@Override
	public Term substitute(Substitution substitution) {
		return substitution.get(this);
	}
}
