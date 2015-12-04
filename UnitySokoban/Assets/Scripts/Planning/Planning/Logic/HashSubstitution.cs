using System;
using System.Collections.Generic;

namespace Planning.Logic
{
    /**
     * A substitution which uses a {@link java.util.HashMap} to keep track of
     * substitutions.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class HashSubstitution : Substitution, ICloneable
    {

        /** The map used to keep track of substitutions */
        private readonly Dictionary<Term, Term> subs;

        /**
         * Constructs a new hash substitution which is a clone of the given
         * substitution.
         * 
         * @param toClone the subsitution to clone
         */
        protected HashSubstitution(HashSubstitution toClone)
        {
            this.subs = toClone.subs;
        }

        /**
         * Constructs a new, empty hash substitution.
         */
        public HashSubstitution()
        {
            subs = new Dictionary<Term, Term>();
        }

        public override Term get(Term term)
        {
            Term value = subs[term];
            if (value == null)
                return term;
            else
                return value;
        }

        /**
         * Specifies that a given term should be replaced with another term.
         * 
         * @param term the term to be replaced
         * @param substitute the term that will replace it
         */
        public void set(Term term, Term substitute)
        {
            subs.Remove(term);
            subs.Add(term, substitute);
        }

        public object Clone()
        {
            return new HashSubstitution(this);
        }
    }

}