package edu.uno.ai.planning.logic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A container to hold
 * 1 constant
 * a set of known codefines
 * a set of known non-codefines
 * 
 * This class is implemented as immutable.
 * 
 * implementation detail
 * a variable is mapped as a codefine of itself so that
 * when t1=t2 they can share a varset where the codefines set is {t1,t2}
 * @author jgrimm
 *
 *
 */
public class HashVarSet implements Cloneable {
	HashSet<Variable> coDefines;
	HashSet<Term> nonCoDefines;
	Constant groundName;

	public HashVarSet() {
		coDefines = new HashSet<Variable>();
		nonCoDefines = new HashSet<Term>();
		groundName = null;
	}

	/**
	 * helper functions to avoid casts
	 * because this is only defined in a branch that is cloneable the exception should never occur.
	 * If it does it is rethrown as a runtime exception so the programmer can evaluate the problem.
	 * @return
	 */
	protected HashVarSet typedClone(){
		try {
			HashVarSet copy = (HashVarSet) super.clone();
			copy.coDefines = this.coDefines;
			copy.nonCoDefines = this.nonCoDefines;
			copy.groundName = this.groundName;
			return copy;
		} catch (CloneNotSupportedException ce) {
			throw new RuntimeException(ce);
		}
	}
	
	@Override
	public Object clone() {
		return typedClone();
	}

	/**
	 * set the constant this varset is equivalent to.
	 * Because of the distinct world assumption there can only be one.
	 */
	public HashVarSet setConstant(Constant c) {
		if (this.groundName == null && !nonCoDefines.contains(c)) {
			HashVarSet copy = this.typedClone();
			copy.groundName = c;
			return copy;
		} else {
			return null;
		}

	}

	/**
	 * adds a variable to the cd set.  It must be a variable as a constant would be handled with the method above.
	 */
	@SuppressWarnings("unchecked")
	public HashVarSet addCD(Variable v) {
		if ( coDefines.contains(v)){
			return this;
		}
		else {
			HashVarSet copy = this.typedClone();
			copy.coDefines=(HashSet<Variable>)this.coDefines.clone();
			copy.coDefines.add(v);
			return copy;
		}
	}
	
	/**
	 * adds a term to the ncd set
	 * There could be many constants in this set.
	 */
	@SuppressWarnings("unchecked")
	public HashVarSet addNCD(Term t) {
		HashVarSet copy = this.typedClone();
		copy.nonCoDefines=(HashSet<Term>)this.nonCoDefines.clone();
		copy.nonCoDefines.add(t);
		return copy;
	}

	/**
	 * Adds a set of terms to the ncd set.  If done one at a time with the above method that would create numerous copies.
	 * Odd notation to allow a set of variables to be passed. 
	 */
	@SuppressWarnings("unchecked")
	public HashVarSet addNCDs(Set<? extends Term> sv) {
		HashVarSet copy = this.typedClone();
		copy.nonCoDefines=(HashSet<Term>)this.nonCoDefines.clone();
		copy.nonCoDefines.addAll(sv);
		return copy;
	}
	
	/**
	 * returns the union of this HashVarSet with another
	 * returns null if this is not legal
	 */
	public HashVarSet union(HashVarSet t2Vars){
		if (canUnion(t2Vars)){
			HashVarSet copy=this.typedClone();
			if (copy.groundName==null){
				copy.groundName=t2Vars.groundName;
			}
			copy.coDefines.addAll(t2Vars.coDefines);
			copy.nonCoDefines.addAll(t2Vars.nonCoDefines);
			return copy;
		}
		else {
			return null;
		}
	}

	/**
	 * checks to see if the union of this varset with another is legal
	 */
	public boolean canUnion(HashVarSet t2Vars){
		if (groundName!=null && groundName.equals(t2Vars.groundName)){
			return false;
		}
		if (groundName!=null && t2Vars.nonCoDefines.contains(groundName)){
			return false;
		}
		if (t2Vars.groundName!=null && nonCoDefines.contains(t2Vars.groundName)){
			return false;
		}
		if(!Collections.disjoint(coDefines, t2Vars.nonCoDefines)){
			return false;
		}
		if(!Collections.disjoint(t2Vars.coDefines, nonCoDefines)){
			return false;
		}
		
		return true;
	}

	/**
	 * returns the constant this varset is equivalent to if any 
	 */
	public Constant getConstant() {
		return groundName;
	}
	
	/**
	 * returns the codefines as immutable
	 * an exception would be thrown if modified
	 */
	public Set<Variable> getCoDefines(){
		return Collections.unmodifiableSet(coDefines);
	}
	
	/**
	 * returns the noncodefines as immutable
	 * an exception would be thrown if modified
	 */
	public Set<Term> getNonCoDefines(){
		return Collections.unmodifiableSet(nonCoDefines);
	}
}
