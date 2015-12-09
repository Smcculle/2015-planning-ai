package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.logic.Variable;

public class Invariant {
	public ArrayList<Variable> parameters;
	public ArrayList<Atom> atoms;
	public ArrayList<Variable> countedVariables;
	
	public Invariant(ArrayList<Variable> parameters, ArrayList<Atom> atoms){
		this.parameters = parameters;
		this.atoms = atoms;
		
		countedVariables = new ArrayList<Variable>();
		for(Atom atom : atoms){
			for(Term term : atom.terms){
				if(term instanceof Variable && !parameters.contains(term) && !countedVariables.contains(term))
					countedVariables.add((Variable)term);
			}
		}
	}
	
	/**
	 * Checks if this invariant candidate covers some atom.
	 * @param candidate
	 * @param p
	 * @return
	 */
	public boolean covers(Atom b){
		for(Atom a : this.atoms){
			if(a.covers(b, this.parameters))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the list of predicates in this candidate's atoms
	 */
	public ArrayList<String> getPredicates(){
		ArrayList<String> predicates = new ArrayList<String>();
		for(Atom atom : atoms){
			predicates.add(atom.predicate);
		}
		return predicates;
	}
	
	/**
	 * Checks if the two invariants are redundant, i.e. if one dominates the other. Returns 1 if this is dominant, -1 if the other is 
	 * dominant, or 0 of they're not redundant.
	 * @param other
	 * @return
	 */
	public int checkRedundancy(Invariant other){
		if(this.dominates(other))
			return 1;
		else if(other.dominates(this))
			return -1;
		else 
			return 0;
	}
	
	/**
	 * Returns true if this candidate dominates the other. 
	 * One candidate dominates the other if, for each atom in the other, there is an atom in this candidate which has the same predicate and 
	 * number of terms, and its terms contains parameters, constants, and counted variables in the exact same places 
	 * 
	 * --> is this correct? And/or is it poorly titled?
	 * 
	 * @param other
	 * @return
	 */
	public boolean dominates(Invariant other){
		for(Atom otherAtom : other.atoms){
			boolean otherAtomMatched = false;
			for(Atom thisAtom : this.atoms){
				boolean thisAtomEqual = true;
				if(thisAtom.predicate.equals(otherAtom.predicate) && thisAtom.terms.length==otherAtom.terms.length){
					for(Term thisTerm : thisAtom.terms){
						if( (thisTerm instanceof Constant || otherAtom.terms.get(thisAtom.terms.indexOf(thisTerm)) instanceof Constant) 
							&& !thisTerm.equals(otherAtom.terms.get(thisAtom.terms.indexOf(thisTerm))))
							thisAtomEqual = false;
						else if(this.parameters.contains(thisTerm) && !other.parameters.contains(otherAtom.terms.get(thisAtom.terms.indexOf(thisTerm))))
							thisAtomEqual = false;
						else if(!this.parameters.contains(thisTerm) && other.parameters.contains(otherAtom.terms.get(thisAtom.terms.indexOf(thisTerm))))
							thisAtomEqual = false;
					}
				} else thisAtomEqual = false;
				if(thisAtomEqual){
					otherAtomMatched = true;
				}
			}
			if(!otherAtomMatched){
				return false; // couldn't find a match for one of the atoms
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Invariant))
			return false;
		Invariant other = (Invariant)o;
		if(!parameters.equals(other.parameters))
			return false;
		if(!atoms.equals(other.atoms))
			return false;
		return true;
	}

	public String toString(){
		String result = "";
		for(int i=0; i<this.parameters.size(); i++){
			result += this.parameters.get(i).name;
			if(i<this.parameters.size()-1)
				result += ",";
		}
		result += "{";
		for(int i=0; i<this.atoms.size(); i++){
			result += this.atoms.get(i);
			if(i<this.atoms.size()-1)
				result += " + ";
		}
		result += "}";
		return result;
	}
}
