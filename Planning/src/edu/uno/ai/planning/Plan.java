package edu.uno.ai.planning;

/**
 * A plan is a sequence of step for achieving a goal.
 * 
 * @author Stephen G. Ware
 */
public interface Plan extends Iterable<Step> {
	
	/**
	 * Returns the number of steps in the plan.
	 * 
	 * @return the number of steps
	 */
	public int size();
}
