using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * An expression is any logical formula with a truth value.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */

    public interface Expression : Formula
    {
        new Expression Substitute(Substitution substitution);

        /**
         * Returns true if this expression can be tested (i.e. can be a goal or a
         * precondition.
         * 
         * @return true if the formula can be tested, false otherwise
         */
        bool IsTestable();

        /**
         * Tests whether or not this expression is true in the given state.
         * 
         * @param state the state in which the expression may be true or false
         * @return true if the expression is true, false otherwise
         */
        bool IsTrue(State state);

        /**
         * Returns true if this expression can be imposed (i.e. can be in the
         * initial state or can be an effect).  An expression must be deterministic
         * to be impossible.
         * 
         * @return true if the formula can be imposed, false otherwise
         */
        bool IsImposable();

        /**
         * Makes this expression true in the given state.
         * 
         * @param state the state to modify
         */
        void Impose(MutableState state);

        /**
         * Returns the negation (opposite) of this expression.
         * 
         * @return the negation
         */
        Expression Negate();

        /**
         * Converts this expression to conjunctive normal form.
         * 
         * @return a new expression equivalent to this one but in conjunctive normal form
         */
        Expression ToCNF();

        /**
         * Converts this expression to disjunctive normal form.
         * 
         * @return a new expression equivalent to this one but in conjunctive normal form
         */
        Expression ToDNF();

        /**
         * Simplifies this expression if possible.
         * 
         * @return a new, simpler expression equivalent to this one
         */
        Expression Simplify();
    }
}
