/**
 * imports
 */
package edu.uno.ai.planning.pop;
import edu.uno.ai.planning.logic.Predication;

/**
 * @author Matthew Farmer
 * CausalLink object representation class.
 * Each causal link specifies a pair of steps and a proposition, where the 
 *  proposition is a postcondition of the first operator and a precondition
 *  of the second operator. The first operator is ordered before the second 
 *  operator
 */
public class CausalLink {
	
	public PartialStep previousStep;
	public PartialStep nextStep;
	public Predication label;
	
	public CausalLink(PartialStep previousStep, PartialStep nextStep, Predication label){
		this.previousStep = previousStep;
		this.nextStep = nextStep;
		this.label = label;
	}

}