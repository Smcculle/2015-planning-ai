using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
    * A substitution specifies that certain terms should be replaced by other
    * terms.
    * 
    * @author Stephen G. Ware
    */
    public abstract class Substitution
    {
        /**
         * Returns the term which should be used in place of the given term.  If a
         * term has no substitution, this method simply returns the term itself.
         * 
         * @param term a term
         * @return the term to use instead of the given term
         */
        public abstract Term get(Term term);

        /** An empty substitution */
        static public Substitution EMPTY = new Empty();

        /** A one-time use empty substitution class */
        class Empty : Substitution
        {
            public override Term get(Term term)
            {
                return term;
            }
        }
    }
}
