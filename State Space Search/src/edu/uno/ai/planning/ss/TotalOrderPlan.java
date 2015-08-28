package edu.uno.ai.planning.ss;

import java.util.Iterator;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.util.ArrayIterator;

/**
 * A plan which specifies exactly what order steps should be executed in.  Note
 * that this data structure is immutable.  The {@link #addStep(Step)} method
 * returns a new plan with the given step added without modifying the plan on
 * which that method was called.
 * 
 * @author Stephen G. Ware
 */
public class TotalOrderPlan implements Plan {

	/** The rest of the steps in the plan */
	private final TotalOrderPlan first;
	
	/** The last step in the plan */
	private final Step last;
	
	/** The number of steps in the plan */
	private final int size;
	
	/**
	 * Constructs a new plan with a given rest of steps and last step.
	 * 
	 * @param first the rest of the plan
	 * @param last the last step
	 */
	private TotalOrderPlan(TotalOrderPlan first, Step last) {
		this.first = first;
		this.last = last;
		this.size = first.size + 1;
	}
	
	/**
	 * Constructs a new plan with 0 steps.
	 */
	public TotalOrderPlan() {
		this.first = null;
		this.last = null;
		this.size = 0;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public Iterator<Step> iterator() {
		Step[] steps = new Step[size];
		TotalOrderPlan current = this;
		for(int i = size - 1; i >= 0; i--) {
			steps[i] = current.last;
			current = current.first;
		}
		return new ArrayIterator<Step>(steps);
	}
	
	/**
	 * Returns a new plan with the given step added at the end.
	 * 
	 * @param step the next step to take
	 * @return a new plan whose last step is the given step
	 */
	public TotalOrderPlan addStep(Step step) {
		return new TotalOrderPlan(this, step);
	}
}
