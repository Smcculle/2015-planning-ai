package edu.uno.ai.planning.logic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HashVarSet implements Cloneable {
	HashSet<Variable> coDefines;
	HashSet<Term> nonCoDefines;
	Constant groundName;

	public HashVarSet() {
		coDefines = new HashSet<Variable>();
		nonCoDefines = new HashSet<Term>();
		groundName = null;
	}

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
	
	public Object clone() {
		return typedClone();
	}

	public Constant getConstant() {
		return groundName;
	}

	public HashVarSet setConstant(Constant c) {
		if (this.groundName == null && !nonCoDefines.contains(c)) {
			HashVarSet copy = this.typedClone();
			copy.groundName = c;
			return copy;
		} else {
			return null;
		}

	}

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
	@SuppressWarnings("unchecked")
	public HashVarSet addNCD(Term t) {
		HashVarSet copy = this.typedClone();
		copy.nonCoDefines=(HashSet<Term>)this.nonCoDefines.clone();
		copy.nonCoDefines.add(t);
		return copy;
	}

	@SuppressWarnings("unchecked")
	public HashVarSet addNCDs(Set<Variable> sv) {
		HashVarSet copy = this.typedClone();
		copy.nonCoDefines=(HashSet<Term>)this.nonCoDefines.clone();
		copy.nonCoDefines.addAll(sv);
		return copy;
	}
	
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
	
	public Set<Variable> getCoDefines(){
		return Collections.unmodifiableSet(coDefines);
	}

}
