package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;

/**
 * A {@link State} which can be modified.
 * 
 * @author Stephen G. Ware
 */
public class MutableState extends State {

	/**
	 * Returns a new state which is a clone of the given state.
	 * 
	 * @param toClone the state to clone
	 */
	public MutableState(State toClone) {
		super(toClone);
	}
	
	/**
	 * Constructs a new, empty state.
	 */
	public MutableState() {
		super();
	}
	
	/**
	 * Modifies the current state such that the given literal is true.
	 * 
	 * @param proposition the literal to make true
	 */
	public void impose(Literal proposition) {
		if(proposition instanceof NegatedLiteral) {
			proposition = ((NegatedLiteral) proposition).argument;
			if(proposition instanceof NegatedLiteral)
				impose(((NegatedLiteral) proposition).argument);
			else
				literals.remove(proposition);
		}
		else
			literals.add(proposition);
	}
}
