package edu.uno.ai.planning.lpg;

import edu.uno.ai.planning.graphplan.PlanGraphStep;

/**
 * Represents a pair of steps that are mutually exclusive at currentLevel 
 * in an action graph.  
 *
 * @author Shane McCulley
 */
/**
 *
 * @author Shane McCulley
 */
public class MutexRelation implements LPGInconsistency {

	/** level this inconsistency appears in the action graph */
	private final int currentLevel;
	
	/** First of the two mutex steps */
	private final PlanGraphStep mutexStepA;
	
	/** Second of the two mutex steps */
	private final PlanGraphStep mutexStepB;
	
	/** Creates a new MutexRelation at currentLevel of action graph */
	public MutexRelation(PlanGraphStep mutexStepA, PlanGraphStep mutexStepB, int currentLevel) {
		this.mutexStepA = mutexStepA;
		this.mutexStepB = mutexStepB;
		this.currentLevel = currentLevel;
	}
	
	/** Returns the first mutex step */
	public PlanGraphStep getMutexA(){
		return this.mutexStepA;
	}
	
	/** Returns the second mutex step */
	public PlanGraphStep getMutexB(){
		return this.mutexStepB;
	}
	
	@Override
	public int getCurrentLevel() {
		return currentLevel;
	}
	
	/** Returns true if PlanGraphStep is one of the two mutex steps */
	public boolean contains(PlanGraphStep step){
		return step.equals(mutexStepA) || step.equals(mutexStepB);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentLevel;
		result = prime * result
				+ ((mutexStepA == null && mutexStepB == null) ? 
						0 : mutexStepA.hashCode() + mutexStepB.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MutexRelation))
			return false;
		MutexRelation other = (MutexRelation) obj;
		if (currentLevel != other.currentLevel)
			return false; 
		if (mutexStepA.equals(other.mutexStepA))
			return mutexStepB.equals(other.mutexStepB);
		if (mutexStepA.equals(other.mutexStepB)) { 
			return mutexStepB.equals(other.mutexStepA);
		}
		else return false; 
	}
	
	@Override
	public String toString() {
		return String.format("%s=%s@%d", mutexStepA.toString(), mutexStepB.toString(), this.currentLevel);
	}
	
}
