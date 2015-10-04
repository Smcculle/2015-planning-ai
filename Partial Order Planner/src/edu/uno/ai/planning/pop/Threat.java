/**
 * 
 */
package edu.uno.ai.planning.pop;

/**
 * @author Matthew Farmer
 * Implements Flaw interface because Threats and OpenPreconditions
 *  are both Flaws that the plan needs to handle.
 * A Threat is an object that represents the relationship between a 
 *  step or operator that endangers a causal link between two other
 *  steps or operators.
 */
public class Threat implements Flaw {
	CausalLink threatenedLink;
	PartialStep threateningStep;
	
	public Threat(CausalLink threatenedLink, PartialStep threateningStep){
		this.threatenedLink = threatenedLink;
		this.threateningStep = threateningStep;
	}
}
