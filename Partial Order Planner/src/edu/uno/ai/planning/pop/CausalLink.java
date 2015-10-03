/**
 * imports
 */
package edu.uno.ai.planning.pop;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.Step;

/**
 * @author Matthew Farmer
 * CausalLink object representation class.
 * Each causal link specifies a pair of steps and a proposition, where the 
 *  proposition is a postcondition of the first step and a precondition of the
 *  second step. The first step is ordered before the second step
 */
public class CausalLink {
	
	public Step previousStep;
	public Step nextStep;
	public Literal label;
	
	public CausalLink(Step previousStep, Step nextStep, Literal label){
		this.previousStep = previousStep;
		this.nextStep = nextStep;
		this.label = label;
	}

}