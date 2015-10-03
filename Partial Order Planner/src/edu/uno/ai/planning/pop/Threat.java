/**
 * 
 */
package edu.uno.ai.planning.pop;
import edu.uno.ai.planning.Operator;

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
	Operator threateningOperator;
	
	public Threat(CausalLink threatenedLink, Operator threateningOperator){
		this.threatenedLink = threatenedLink;
		this.threateningOperator = threateningOperator;
	}
}
