/**
 * 
 */
package edu.uno.ai.planning.pop;
import edu.uno.ai.planning.Step;

/**
 * @author Matthew Farmer
 * Extends Flaw interface because Threats and open preconditions
 *  are both Flaws that the plan needs to handle.
 * A Threat is an object that represents a step that endangers a
 *  causal link between two other steps.
 */
public class Threat implements Flaw {
	CausalLink threatenedLink;
	Step threateningStep;
	
	public Threat(CausalLink threatenedLink, Step threateningStep){
		this.threatenedLink = threatenedLink;
		this.threateningStep = threateningStep;
	}
	
}
