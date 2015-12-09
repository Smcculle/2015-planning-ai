package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * An Atom is a Predication but with some useful functions for invariant synthesis
 * @author Rachel Farrell
 */
public class Atom extends Predication{

	public Atom(String s, ImmutableArray<Term> terms){
		super(s, terms);
	}
	
	/** Checks if this atom covers another atom, given a list of parameters of this atom
	 * @param parameters - list of candidate's parameters
	 * @param atom - atom to check
	 * @return
	 */	
	public boolean covers(Atom atom, ArrayList<Variable> parameters){
		// If their predicate is not the same or they have a different number of terms, these cannot become equal
		if(!this.predicate.equals(atom.predicate) || this.terms.length != atom.terms.length)
			return false;
		// Loop through each term
		for(int i=0; i<this.terms.length; i++){
			Term a_term = this.terms.get(i);
			Term b_term = atom.terms.get(i);
			// If a's term is a parameter or a constant, then fail if b's term is not equal to it
			if ((parameters.contains(a_term) || a_term instanceof Constant) && !a_term.equals(b_term))
				return false;
		}
		return true;
	}
	
	
	/** Checks if this atom covers a grounded atom, given a list of parameters and objects they are mapped to
	 * @param atom - atom to check
	 * @param parameters - list of the invariant's parameters
	 * @param objects - list of objects mapped to the parameters
	 */
	public boolean coversGroundedAtom(Atom atom, ArrayList<Variable> parameters, ArrayList<Term> objects){
		if(!this.predicate.equals(atom.predicate) || this.terms.length != atom.terms.length)
			return false;
		// Loop through each term
		for(int i=0; i<this.terms.length; i++){
			// If it's a constant
			if(this.terms.get(i) instanceof Constant){
				// Return false if the atom's term doesn't equal it
				if(!atom.terms.get(i).equals(this.terms.get(i)))
					return false;
			// If it's a variable and contained in parameters
			} else if (parameters.contains(this.terms.get(i))){
				// Return false if the atom's term doesn't equal the corresponding mapped object
				if(!atom.terms.get(i).equals(objects.get(parameters.indexOf(this.terms.get(i)))))
					return false;
			// If it's a variable and not contained in the parameters
			} else {
				// Allow everything else. I think.
			}
		}
		
		return true;
	}
	
	/** ........don't think I need this..............
	 * Checks if this (grounded) atom is reachable, given a list of modifiable fluent predicates
	 * @param modifiableFluentPredicates
	 * @return
	 */
	public boolean isReachable(ArrayList<Atom> modifiableFluentPredicates){
		for(Atom atom : modifiableFluentPredicates){
			if(atom.covers(this, new ArrayList<Variable>())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Renames some of the variables in this atom to match the given candidate
	 * @param candidate
	 * @param bindings
	 * @return a new atom with variables renamed
	 */
	public Atom renameToMatch(Invariant candidate, Bindings bindings){
		Term[] terms = new Term[this.terms.length];
		// First find the matching atom from the candidate
		for(Atom atom : candidate.atoms){
			if(atom.predicate==this.predicate && atom.terms.length==this.terms.length){
				// Then rename this atom's terms to match its parameters, leaving the other terms intact
				for(int i=0; i<terms.length; i++){
					if(candidate.parameters.contains(atom.terms.get(i))){
						if(bindings.setEqual(atom.terms.get(i), this.terms.get(i)) != null){
							terms[i] = atom.terms.get(i);
						} else return null;
					}
					else
						terms[i] = this.terms.get(i);
				}
				return new Atom(this.predicate, new ImmutableArray<Term>(terms));
			}
		}
		return null;
	}
	
	@Override
	public NegatedLiteral negate() {
		return new NegatedLiteral(this);
	}
	
	@Override
	public int hashCode() {	//didn't help, but whatever
	    int hash = 7;
	    hash = 71 * hash + this.predicate.hashCode();
	    hash = 71 * hash + this.terms.hashCode();
	    return hash;
	}
	
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Atom){
			Atom otherAtom = (Atom)other;
			if(!this.predicate.equals(otherAtom.predicate) || (this.terms.length != otherAtom.terms.length))
				return false;
			for(int i=0; i<this.terms.length; i++){
				if(!this.terms.get(i).equals(otherAtom.terms.get(i)))
					return false;
			}
			return true;
		}
		return false;
	}	
}
