using System;
using System.Collections.Generic;

namespace Planning.Logic
{
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
      *
      * @author Jonathan Grimm
      * @ported Edward Thoams Garcia
      */
    public class HashVarSet : ICloneable
    {
        HashSet<Term> coDefines;
        HashSet<Term> nonCoDefines;
        Constant groundName;

        public HashVarSet()
        {
            coDefines = new HashSet<Term>();
            nonCoDefines = new HashSet<Term>();
            groundName = null;
        }

        /**
         * helper functions to avoid casts
         * because this is only defined in a branch that is cloneable the exception should never occur.
         * If it does it is rethrown as a runtime exception so the programmer can evaluate the problem.
         * @return
         */
        protected HashVarSet typedClone()
        {
            try
            {
                HashVarSet copy = new HashVarSet();
                copy.coDefines = this.coDefines;
                copy.nonCoDefines = this.nonCoDefines;
                copy.groundName = this.groundName;
                return copy;
            }
            catch (Exception ce)
            {
                throw new Exception(ce.ToString());
            }
        }

        public object Clone()
        {
            return typedClone();
        }

        /**
         * set the constant this varset is equivalent to.
         * Because of the distinct world assumption there can only be one.
         */
        public HashVarSet setConstant(Constant c)
        {
            if (this.groundName == null && !nonCoDefines.Contains(c))
            {
                HashVarSet copy = this.typedClone();
                copy.groundName = c;
                return copy;
            }
            else
            {
                return null;
            }

        }

        /**
         * adds a variable to the cd set.  It must be a variable as a constant would be handled with the method above.
         */
    public HashVarSet addCD(Variable v)
        {
            if (coDefines.Contains(v))
            {
                return this;
            }
            else
            {
                HashVarSet copy = this.typedClone();
                copy.coDefines = new HashSet<Term>(coDefines);
                copy.coDefines.Add(v);
                return copy;
            }
        }

        /**
         * adds a term to the ncd set
         * There could be many constants in this set.
         */
    public HashVarSet addNCD(Term t)
        {
            HashVarSet copy = this.typedClone();
            copy.nonCoDefines = new HashSet<Term>(nonCoDefines);
            copy.nonCoDefines.Add(t);
            return copy;
        }

        /**
         * Adds a set of terms to the cd set.  If done one at a time with the above method that would create numerous copies.
         */
    public HashVarSet addCDs(HashSet<Term> sv)
        {
            HashSet<Term> newCoDefines = new HashSet<Term>(coDefines);
            newCoDefines.UnionWith(sv);
            if (newCoDefines.Count == coDefines.Count)
            {
                return this;
            }
            else
            {
                HashVarSet copy = this.typedClone();
                copy.coDefines = newCoDefines;
                return copy;
            }
        }

        /**
         * Adds a set of terms to the ncd set.  If done one at a time with the above method that would create numerous copies.
         * Odd notation to allow a set of variables to be passed. 
         */
    public HashVarSet addNCDs(HashSet<Term> st)
        {
            HashSet<Term> newNonCoDefines = new HashSet<Term>(nonCoDefines);
            newNonCoDefines.UnionWith(st);
            if (newNonCoDefines.Count == this.nonCoDefines.Count)
            {
                return this;
            }
            else
            {
                HashVarSet copy = this.typedClone();
                copy.nonCoDefines = newNonCoDefines;
                return copy;
            }
        }

        /**
         * returns the union of this HashVarSet with another
         * returns null if this is not legal
         */
        public HashVarSet union(HashVarSet t2Vars)
        {
            if (canUnion(t2Vars))
            {
                HashVarSet copy = this.typedClone();
                if (copy.groundName == null)
                {
                    copy.groundName = t2Vars.groundName;
                }
                copy.coDefines.UnionWith(t2Vars.coDefines);
                copy.nonCoDefines.UnionWith(t2Vars.nonCoDefines);
                return copy;
            }
            else
            {
                return null;
            }
        }

        /**
         * checks to see if the union of this varset with another is legal
         */
        public bool canUnion(HashVarSet t2Vars)
        {
            HashSet<Term> t1co = new HashSet<Term>();
            foreach (Term term in coDefines) t1co.Add(term);
            HashSet<Term> t2co = new HashSet<Term>();
            foreach (Term term in t2Vars.coDefines) t2co.Add(term);

            if (groundName != null && groundName.Equals(t2Vars.groundName))
            {
                return false;
            }
            if (groundName != null && t2Vars.nonCoDefines.Contains(groundName))
            {
                return false;
            }
            if (t2Vars.groundName != null && nonCoDefines.Contains(t2Vars.groundName))
            {
                return false;
            }
            if (t1co.Overlaps(t2Vars.nonCoDefines))
            {
                return false;
            }
            if (t2co.Overlaps(nonCoDefines))
            {
                return false;
            }
            return true;
        }

        /**
         * returns the constant this varset is equivalent to if any 
         */
        public Constant getConstant()
        {
            return groundName;
        }

        /**
         * returns the codefines as immutable
         * an exception would be thrown if modified
         */
        public HashSet<Term> getCoDefines()
        {
            return new HashSet<Term>(coDefines);
        }

        /**
         * returns the noncodefines as immutable
         * an exception would be thrown if modified
         */
        public HashSet<Term> getNonCoDefines()
        {
            return new HashSet<Term>(nonCoDefines);
        }
    }
}