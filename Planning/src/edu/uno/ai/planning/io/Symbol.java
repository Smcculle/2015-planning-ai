package edu.uno.ai.planning.io;

/**
 * A symbol is a string of characters.
 * 
 * @author Stephen G. Ware
 */
public class Symbol extends Node {

	/** The value of the symbol */
	public final String value;
	
	/**
	 * Constructs a new symbol.
	 * 
	 * @param value the value
	 * @param next the next symbol in this list (if any)
	 */
	Symbol(String value, Node next) {
		super(next);
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
