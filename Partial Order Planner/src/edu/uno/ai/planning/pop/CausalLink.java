/**
 * imports
 */
package edu.uno.ai.planning.pop;
import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.logic.Predication;

/**
 * @author Matthew Farmer
 * CausalLink object representation class.
 * Each causal link specifies a pair of steps and a proposition, where the 
 *  proposition is a postcondition of the first step and a precondition of the
 *  second step. The first step is ordered before the second step
 */
public class CausalLink {
	
	public Operator previousStep;
	public Operator nextStep;
	public Predication label;
	
	public CausalLink(Operator previousStep, Operator nextStep, Predication label){
		this.previousStep = previousStep;
		this.nextStep = nextStep;
		this.label = label;
	}

}