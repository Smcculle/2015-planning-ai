package edu.uno.ai.planning.fd;

import java.util.ArrayList;
import java.util.HashSet;

import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;

public class MPTState implements Cloneable{

	/** The set of current variable assignments */
	protected final HashSet<Assignment> assignments;
	
	/**
	 * Creates a new state which is a clone of the given state.
	 * 
	 * @param toClone the state to clone
	 */
	@SuppressWarnings("unchecked")
	MPTState(MPTState toClone) {
		this.assignments = (HashSet<Assignment>) toClone.assignments.clone();
	}
	
	/**
	 * Constructs a new, empty state.
	 */
	public MPTState() {
		this.assignments = new HashSet<>();
	}
	
	/**
	 * Constructs a new state with the given list of initial assignments
	 */
	public MPTState(ArrayList<Assignment> initialAssignments){
		this.assignments = new HashSet<>(initialAssignments);
	}
	
	
	/**
	 * Returns the value of the given variable in this state. 
	 * @return the value, or null if it couldn't find a matching variable
	 */
	public Atom getValue(StateVariable variable){
		for(Assignment assignment : assignments){
			if(assignment.variable.equals(variable)){
				return assignment.value;
			}
		}
		return null;
	}

	/**
	 * Tests if a given list of propositions is true in this state.
	 * 
	 * @param propositions the list of assignments to test
	 * @return true if the assignments are all true, false otherwise
	 */
	public boolean isTrue(ArrayList<Assignment> propositions) {
		for(Assignment proposition : propositions){
			boolean propTrue = false;
			for(Assignment assignment : assignments){
				if(assignment.equals(proposition))
					propTrue = true;
			}
			if(!propTrue){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * If the preconditions of the given step are met, this method returns a new
	 * state which has been modified according to the step's effects.
	 * 
	 * @param step the step to take
	 * @return a new state after the effects have been applied
	 * @throws IllegalArgumentException if the step's preconditions are not met
	 */
	public MPTState apply(MPTStep step) {
		if(this.isTrue(step.precondition)) {
			MutableMPTState state = new MutableMPTState(this);
			state.impose(step.effect);
			return state;
		}
		else
			throw new IllegalArgumentException("Cannot apply " + step + "; preconditions are not met");
	}
	
	/**
	 * Returns a logical expression representing the current state.
	 * 
	 * @return an expression
	 */
	public Expression toExpression() {
		return new Conjunction(assignments.toArray(new Assignment[assignments.size()])).simplify();
	}
	
	@Override
	public String toString() {
		return toExpression().toString();
	}
	
	@Override
	public MPTState clone() {
		return new MPTState(this);
	}

}
