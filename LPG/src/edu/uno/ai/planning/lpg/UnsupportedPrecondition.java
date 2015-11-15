package edu.uno.ai.planning.lpg;

import edu.uno.ai.planning.graphplan.PlanGraphLiteral;

/**
 * Represents a precondition for a step at (currentLevel + 1) that is not true 
 * at currentLevel.  
 * 
 * @author Shane McCulley
 */
public class UnsupportedPrecondition implements LPGInconsistency {

	/** level this inconsistency appears in the action graph */
	private final int currentLevel;
	
	/** PlanGraphLiteral that has no support in the action graph at currentLevel */
	private final PlanGraphLiteral unsupportedPrecondition;
	
	/** Creates a new UnsupportedPrecondition */ 
	public UnsupportedPrecondition(PlanGraphLiteral unsupportedPrecondition, int currentLevel) {
		this.currentLevel = currentLevel;
		this.unsupportedPrecondition = unsupportedPrecondition;
	}
	
	/** Returns unsupported precondition */
	public PlanGraphLiteral getUnsupportedPrecondition(){
		return unsupportedPrecondition;
	}
	
	/** Get current level of inconsistency */
	@Override
	public int getCurrentLevel() {
		return currentLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentLevel;
		result = prime
				* result
				+ ((unsupportedPrecondition == null) ? 0
						: unsupportedPrecondition.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UnsupportedPrecondition))
			return false;
		UnsupportedPrecondition other = (UnsupportedPrecondition) obj;
		if (currentLevel != other.currentLevel)
			return false;
		if (unsupportedPrecondition == null) {
			if (other.unsupportedPrecondition != null)
				return false;
		} else if (!unsupportedPrecondition
				.equals(other.unsupportedPrecondition))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s@%d", unsupportedPrecondition.toString(), this.currentLevel);
	}
	
	

}
