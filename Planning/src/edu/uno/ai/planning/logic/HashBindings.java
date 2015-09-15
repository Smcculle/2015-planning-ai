package edu.uno.ai.planning.logic;

import java.util.HashMap;

/**
 * An implementation of the {@link Bindings} data structure based on a hash
 * table, which allows fast lookups but is costly to clone and modify.
 * 
 * @author Jonathan Grimm
 */
public class HashBindings implements Bindings {
	protected HashMap<Term, HashVarSet> bindings;

	public HashBindings() {
		this.bindings = new HashMap<Term, HashVarSet>();
	}

	protected HashBindings(HashMap<Term, HashVarSet> bindings) {
		this.bindings = bindings;
	}

	@Override
	public Term get(Term term) {
		HashVarSet v;
		if (bindings.containsKey(term)) {
			v = bindings.get(term);
			Constant c = v.getConstant();
			if (c != null) {
				return c;
			}

		}
		return term;
	}

	@Override
	public Bindings setEqual(Term t1, Term t2) {
		if (t1 instanceof Constant && t2 instanceof Constant){
			return EquateConstants((Constant)t1, (Constant)t2);
			
		}
		else if (t1 instanceof Constant){
			return BindVariableConstant((Variable)t2,(Constant)t1);
		}
		else if (t2 instanceof Constant){
			return BindVariableConstant((Variable)t1,(Constant)t2);
		}
		else {
			return BindVariables((Variable)t1,(Variable)t2);
		}
	}
	@Override
	public Bindings setNotEqual(Term t1, Term t2) {
		if (t1.equals(t2)){
			return null;
		}
		else if (t1 instanceof Constant && t2 instanceof Constant){
			//This is already assumed no mapping must be made
			return this;
		}
		else if (t1 instanceof Constant){
			return NoBindVariableConstant((Variable)t2,(Constant)t1);
		}
		else if (t2 instanceof Constant){
			return NoBindVariableConstant((Variable)t1,(Constant)t2);
		}
		else {
			return NoBindVariables((Variable)t1,(Variable)t2);
		}		
	}
	
	protected HashBindings EquateConstants(Constant c1, Constant c2) {
		if (c1.equals(c2)) {
			return this;
		} else {
			return null;
		}
	}

	protected HashBindings BindVariableConstant(Variable t1, Constant c) {

		if (bindings.containsKey(t1)) {
			HashVarSet t1Binds = bindings.get(t1);
			if (t1Binds.getConstant()==c){
				return this;
			}
			else {
				HashVarSet newt1Binds = t1Binds.setConstant(c);
				if (newt1Binds != null) {
					return useVarSet(newt1Binds);
				} else {
					return null;
				}
			}
		} else {
			HashVarSet empty = new HashVarSet();
			HashVarSet constant = empty.setConstant(c);
			HashVarSet mapping = constant.addCD((Variable) t1);
			return useVarSet(mapping);
		}

	}

	
	protected HashBindings BindVariables(Variable v1, Variable v2) {
		if (bindings.containsKey(v1) && bindings.containsKey(v2)) {
			HashVarSet t1Vars = bindings.get(v1);
			HashVarSet t2Vars = bindings.get(v2);
			if (t1Vars == t2Vars) {
				return this;
			} else if (t1Vars.canUnion(t2Vars)) {
				HashVarSet newVars = t1Vars.union(t2Vars);
				return useVarSet(newVars);
			} else {
				return null;
			}
		} else if (bindings.containsKey(v1)) {
			HashVarSet newVars = bindings.get(v1).addCD(v2);
			return useVarSet(newVars);
		} else if (bindings.containsKey(v2)) {
			HashVarSet newVars = bindings.get(v2).addCD(v1);
			return useVarSet(newVars);
		} else {
			HashVarSet empty = new HashVarSet();
			HashVarSet complete = empty.addCD(v1).addCD(v2);
			return useVarSet(complete);
		}
	}

	protected HashBindings useVarSet(HashVarSet newVars) {
		if (newVars!=null){
			@SuppressWarnings("unchecked")
			HashMap<Term, HashVarSet> newBindings = (HashMap<Term, HashVarSet>) bindings.clone();
			for (Variable v : newVars.getCoDefines()) {
				newBindings.put(v, newVars);
			}
			return new HashBindings(newBindings);
		}
		else {
			return this;
		}
	}

	protected HashBindings NoBindVariableConstant(Variable t1, Constant c) {

		if (bindings.containsKey(t1)) {
			HashVarSet t1Binds = bindings.get(t1);
			if (t1Binds.getConstant()!=c){
				HashVarSet newt1Binds = t1Binds.addNCD(c);
				return useVarSet(newt1Binds);
			} else {
				return null;
			}
		} else {
			HashVarSet empty = new HashVarSet();
			HashVarSet ncd = empty.addNCD(c);
			HashVarSet mapping = ncd.addCD((Variable) t1);
			return useVarSet(mapping);
		}

	}

	protected Bindings NoBindVariables(Variable v1, Variable v2) {
		HashVarSet v1Binds=null;
		HashVarSet v2Binds=null;
		if (bindings.containsKey(v1) && bindings.containsKey(v2)) {
			HashVarSet t1Vars = bindings.get(v1);
			HashVarSet t2Vars = bindings.get(v2);
			if (t1Vars.getNonCoDefines().contains(v2)){
				return this;
			}
			if (t1Vars == t2Vars) {
				return null;
			}
			else {
				v1Binds=t1Vars.addNCDs(t2Vars.getCoDefines());
				v2Binds=t2Vars.addNCDs(t1Vars.getCoDefines());
			}
		} else if (bindings.containsKey(v1)) {
			v1Binds = bindings.get(v1).addNCD(v2);
			v2Binds= new HashVarSet().addCD(v2).addNCD(v1);
		} else if (bindings.containsKey(v2)) {
			v1Binds =new HashVarSet().addCD(v1).addNCD(v2); 
			v2Binds= bindings.get(v2).addNCD(v1);
		} else {
			v1Binds =new HashVarSet().addCD(v1).addNCD(v2); 
			v2Binds= new HashVarSet().addCD(v2).addNCD(v1);
		}
		HashBindings v1bound=useVarSet(v1Binds);
		return v1bound.useVarSet(v2Binds);
	}

}
