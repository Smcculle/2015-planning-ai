package edu.uno.ai.planning.fd;

import java.util.Iterator;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.util.ArrayIterator;

public class TotalOrderMPTPlan implements Plan{

	/** The rest of the steps in the plan */
	private final TotalOrderMPTPlan first;
	
	/** The last step in the plan */
	private final MPTStep last;
	
	/** The number of steps in the plan */
	private final int size;
	
	/**
	 * Constructs a new plan with a given rest of steps and last step.
	 * 
	 * @param first the rest of the plan
	 * @param last the last step
	 */
	private TotalOrderMPTPlan(TotalOrderMPTPlan first, MPTStep last) {
		this.first = first;
		this.last = last;
		this.size = first.size + 1;
	}
	
	/**
	 * Constructs a new plan with 0 steps.
	 */
	public TotalOrderMPTPlan() {
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
		MPTStep[] steps = new MPTStep[size];	// MPTStep...
		TotalOrderMPTPlan current = this;
		for(int i = size - 1; i >= 0; i--) {
			steps[i] = current.last;
			current = current.first;
		}
		return new ArrayIterator<Step>(steps);	// ...Step
	}
	
	/**
	 * Returns a new plan with the given step added at the end.
	 * 
	 * @param step the next step to take
	 * @return a new plan whose last step is the given step
	 */
	public TotalOrderMPTPlan addStep(MPTStep step) {
		return new TotalOrderMPTPlan(this, step);
	}


}
