﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * The parent class of all logical formulas.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public interface Formula : IComparable<Formula>
    {

        //public default int compareTo(Formula other)
        //{
        //    return toString().compareTo(other.toString());
        //}

        /**
         * Tests whether two logical formulas are the same under a given
         * substitution.
         * 
         * @param other a logical formula to be compared to this formula
         * @param substitution a substitution
         * @return true if the formulas are the same, false otherwise
         */
        bool Equals(Formula other, Substitution substitution);

        /**
         * Tests whether or not the formula contains variables.
         * 
         * @return true if the formula contains no variables, false otherwise
         */
        bool IsGround();

        /**
         * Returns a new formula whose terms have been replaced by their values
         * from a given substitution.
         * 
         * @param substitution the substitution
         * @return a formula with variables replaced
         */
        Formula Substitute(Substitution substitution);

        /**
         * Unifies this logical formula with another by adding constraints to a
         * given set of bindings.
         * 
         * @param other the formula to be unified with this one
         * @param bindings the bindings to which constraints will be added
         * @return a new set of bindings with constraints added if the formulas can be unified, null if they cannot be unified
         */
        Bindings Unify(Formula other, Bindings bindings);
    }
}