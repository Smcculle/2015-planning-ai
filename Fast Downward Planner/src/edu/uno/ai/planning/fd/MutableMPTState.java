package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.fd.Assignment;

public class MutableMPTState extends MPTState {

	/**
	 * Returns a new state which is a clone of the given state.
	 * 
	 * @param toClone the state to clone
	 */
	public MutableMPTState(MPTState toClone) {
		super(toClone);
	}
	
	/**
	 * Constructs a new, empty state.
	 */
	public MutableMPTState() {
		super();
	}
	
	/**
	 * Modifies the current state such that the given assignment is true.
	 * @param proposition
	 */
	public void impose(Assignment proposition){
		for(Assignment assignment : assignments){
			if(assignment.variable.equals(proposition.variable)){
				assignments.remove(assignment);
				assignments.add(proposition);
				return;
			}
		}
		assignments.add(proposition);
	}

	/**
	 * Modifies the current state such that the given list of assignments are true.
	 * 
	 * @param proposition the literal to make true
	 */
	public void impose(ArrayList<Assignment> propositions) {
		for(Assignment proposition : propositions){
			impose(proposition);
		}
	}

}
