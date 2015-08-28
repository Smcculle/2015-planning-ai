package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Expression;

/**
 * A step is a single ground action in a plan which changes the world's state.
 * 
 * @author Stephen G. Ware
 */
public class Step implements Comparable<Step> {

	/** The name of the step */
	public final String name;
	
	/** What must be true before the step can be taken */
	public final Expression precondition;
	
	/** What becomes true after the step has been taken */
	public final Expression effect;
	
	/**
	 * Constructs a new step.
	 * 
	 * @param name the name of the step
	 * @param precondition the precondition (must be ground)
	 * @param effect the effect (must be ground)
	 * @throws IllegalArgumentException if either the precodition or effect are not ground
	 */
	public Step(String name, Expression precondition, Expression effect) {
		if(!precondition.isGround())
			throw new IllegalArgumentException("Precondition not ground");
		if(!effect.isGround())
			throw new IllegalArgumentException("Effect not ground");
		this.name = name;
		this.precondition = precondition;
		this.effect = effect;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int compareTo(Step other) {
		return name.compareTo(other.name);
	}
}
