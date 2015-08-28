package edu.uno.ai.planning.logic;

/**
 * A constant is a term which represents one specific object in the world.
 * 
 * @author Stephen G. Ware
 */
public class Constant extends Term {

	/**
	 * Constructs a new constant with the given type and name.
	 * 
	 * @param type the type of the constant
	 * @param name the name of the constant
	 */
	public Constant(String type, String name) {
		super(type, name);
	}	

	@Override
	public boolean isGround() {
		return true;
	}

	@Override
	public Constant substitute(Substitution substitution) {
		return this;
	}
}
