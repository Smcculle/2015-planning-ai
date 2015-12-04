using System.Collections.Generic;

namespace Planning.Logic
{
    /**
  * An implementation of the {@link Bindings} data structure based on a hash
  * table, which allows fast lookups but is costly to clone and modify.
  * 
  * @author Jonathan Grimm
  * @ported Edward Thomas Garcia
  */
    public class HashBindings : Bindings
    {
        protected Dictionary<Term, HashVarSet> bindings;

        public HashBindings() {
            this.bindings = new Dictionary<Term, HashVarSet>();
        }

        protected HashBindings(Dictionary<Term, HashVarSet> bindings) {
            this.bindings = bindings;
        }

        /**
         * returns a constant associated with a variable or returns the term entered
         */
        public override Term get(Term term) {
            HashVarSet v;
            if (bindings.ContainsKey(term))
            {
                v = bindings[term];
                Constant c = v.getConstant();
                if (c != null)
                {
                    return c;
                }

            }
            return term;
        }

        /**
	 * Defines two terms as equivalent
	 * returns the current instance if the terms are already equivalent
	 * returns null if making the terms equivalent would create an inconsistent state.
	 * otherwise returns a new instance with added bindings
	 */
    public override Bindings setEqual(Term t1, Term t2) {
            if (t1.type != t2.type)
            {
                return null;
            }
            if (t1.Equals(t2))
            {
                return this;
            }
            if (t1 is Constant && t2 is Constant){
                return EquateConstants((Constant)t1, (Constant)t2);

            }
		else if (t1 is Constant){
                return BindVariableConstant((Variable)t2, (Constant)t1);
            }
		else if (t2 is Constant){
                return BindVariableConstant((Variable)t1, (Constant)t2);
            }
		else {
                return BindVariables((Variable)t1, (Variable)t2);
            }
        }

        /**
         * Defines two terms as definitely not equivalent
         * returns the current instance if the terms are already not equivalent
         * returns null if making the terms non equivalent would create an inconsistent state.
         * otherwise returns a new instance with added bindings
         */
        public override Bindings setNotEqual(Term t1, Term t2) {
            if (t1.Equals(t2))
            {
                return null;
            }
            else if (t1 is Constant && t2 is Constant){
                //This is already assumed no mapping must be made
                return this;
            }
		else if (t1 is Constant){
                return NoBindVariableConstant((Variable)t2, (Constant)t1);
            }
		else if (t2 is Constant){
                return NoBindVariableConstant((Variable)t1, (Constant)t2);
            }
		else {
                return NoBindVariables((Variable)t1, (Variable)t2);
            }
        }

        /**
         * Constants with different names are required to be different by the unique name assumption. 
         */
        protected HashBindings EquateConstants(Constant c1, Constant c2) {
            if (c1.Equals(c2))
            {
                return this;
            }
            else
            {
                return null;
            }
        }

        /**
         * A variable can only be bound to one constant.
         */
        protected HashBindings BindVariableConstant(Variable t1, Constant c) {
            if (bindings.ContainsKey(t1))
            {
                HashVarSet t1Binds = bindings[t1];
                if (t1Binds.getConstant() == c)
                {
                    return this;
                }
                else
                {
                    HashVarSet newt1Binds;
                    if (bindings.ContainsKey(c))
                    {
                        HashVarSet cBinds = bindings[c];
                        newt1Binds = t1Binds.union(cBinds);
                    }
                    else
                    {
                        newt1Binds = t1Binds.setConstant(c);
                    }
                    return useVarSet(newt1Binds);
                }
            }
            else if (bindings.ContainsKey(c))
            {
                HashVarSet cBinds = bindings[c];
                HashVarSet newBinds = cBinds.addCD(t1);
                return useVarSet(newBinds);
            }
            else
            {
                HashVarSet empty = new HashVarSet();
                HashVarSet constant = empty.setConstant(c);
                HashVarSet mapping = constant.addCD((Variable)t1);
                return useVarSet(mapping);
            }

        }

        /**
         * Binding two variables has 4 cases
         *  neither is bound
         *  t1 is bound
         *  t2 is bound
         *  both are bound
         */
        protected HashBindings BindVariables(Variable v1, Variable v2) {
            if (bindings.ContainsKey(v1) && bindings.ContainsKey(v2))
            {
                HashVarSet t1Vars = bindings[v1];
                HashVarSet t2Vars = bindings[v2];
                if (t1Vars == t2Vars)
                {
                    return this;
                }
                else
                {
                    //the union function checks legality we don't have to do it here.
                    HashVarSet newVars = t1Vars.union(t2Vars);
                    return useVarSet(newVars);
                }
            }
            else if (bindings.ContainsKey(v1))
            {
                HashVarSet newVars = bindings[v1].addCD(v2);
                return useVarSet(newVars);
            }
            else if (bindings.ContainsKey(v2))
            {
                HashVarSet newVars = bindings[v2].addCD(v1);
                return useVarSet(newVars);
            }
            else
            {
                HashVarSet empty = new HashVarSet();
                HashVarSet complete = empty.addCD(v1).addCD(v2);
                return useVarSet(complete);
            }
        }

        /**
         * setting a variable not equal to a constant is simple 
         * add that constant to the NCD set
         */
        protected HashBindings NoBindVariableConstant(Variable t1, Constant c) {

            if (bindings.ContainsKey(t1))
            {
                HashVarSet t1Binds = bindings[t1];
                if (t1Binds.getConstant() != c)
                {
                    HashVarSet newt1Binds = t1Binds.addNCD(c);
                    return useVarSet(newt1Binds);
                }
                else
                {
                    return null;
                }
            }
            else
            {
                HashVarSet empty = new HashVarSet();
                HashVarSet ncd = empty.addNCD(c);
                HashVarSet mapping = ncd.addCD((Variable)t1);
                return useVarSet(mapping);
            }

        }

        /**
         * setting a variable not equal to a is more complicated 
         * Everything that t1 is equivalent to, t2 is not equivalent to.
         * Everything that t2 is equivalent to, t1 is not equivalent to.
         */
        protected Bindings NoBindVariables(Variable v1, Variable v2) {
            HashVarSet v1Binds = null;
            HashVarSet v2Binds = null;
            if (bindings.ContainsKey(v1) && bindings.ContainsKey(v2))
            {
                HashVarSet t1Vars = bindings[v1];
                HashVarSet t2Vars = bindings[v2];
                if (t1Vars.getNonCoDefines().Contains(v2))
                {
                    return this;
                }
                if (t1Vars == t2Vars)
                {
                    return null;
                }
                else
                {
                    v1Binds = t1Vars.addNCDs(t2Vars.getCoDefines());
                    v2Binds = t2Vars.addNCDs(t1Vars.getCoDefines());
                }
            }
            else if (bindings.ContainsKey(v1))
            {
                v1Binds = bindings[v1].addNCD(v2);
                v2Binds = new HashVarSet().addCD(v2).addNCD(v1);
            }
            else if (bindings.ContainsKey(v2))
            {
                v1Binds = new HashVarSet().addCD(v1).addNCD(v2);
                v2Binds = bindings[v2].addNCD(v1);
            }
            else
            {
                v1Binds = new HashVarSet().addCD(v1).addNCD(v2);
                v2Binds = new HashVarSet().addCD(v2).addNCD(v1);
            }
            HashBindings v1bound = useVarSet(v1Binds);
            return v1bound.useVarSet(v2Binds);
        }

        /**
         * helper function
         * Copy this hashbindings
         * for each variable in the cd set 
         *    update the lookup hash to point to the new hashvarset
         * for each variable in the ncd set 
         *    make sure it contains everything in the cdset
         * Return the hashbindings with the updated entries
         */
        protected HashBindings useVarSet(HashVarSet newVars) {
            if (newVars != null)
            {
                Dictionary<Term, HashVarSet> newBindings = new Dictionary<Term, HashVarSet>(bindings);
                HashSet<Term> codefines = newVars.getCoDefines();
                foreach (Variable v in codefines)
                {
                    if (!newBindings.ContainsKey(v))
                        newBindings.Add(v, newVars);
                }
                if (newVars.getConstant() != null)
                {
                    newBindings.Add(newVars.getConstant(), newVars);
                }
                HashBindings needNCDupdate = new HashBindings(newBindings);
                foreach (Term t1 in newVars.getNonCoDefines())
                {
                    if (bindings.ContainsKey(t1))
                    {
                        HashVarSet t1Binds = bindings[t1];
                        HashVarSet newT1Binds = t1Binds.addNCDs(codefines);
                        if (newT1Binds != t1Binds)
                        {
                            needNCDupdate = needNCDupdate.useVarSet(newT1Binds);
                        }
                    }
                }
                return needNCDupdate;
            }
            else
            {
                return null;
            }
        }
    }

}