using Planning.Logic;
using System;
using System.Collections.Generic;

namespace Planning
{
    /**
     * A state completely describes the disposition of all the objects in the
     * world at some particular time.  Note that, by default, a state cannot be
     * modified.  See {@link MutableState} for a state which can be modified.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class State : ICloneable
    {

        /** The set of currently true literals */
        protected readonly List<Literal> literals;

        /**
         * Creates a new state which is a clone of the given state.
         * 
         * @param toClone the state to clone
         */
        public State(State toClone)
        {
            literals = new List<Literal>();
            literals.AddRange(toClone.literals);
        }

        /**
         * Constructs a new, empty state.
         */
        public State()
        {
            this.literals = new List<Literal>();
        }

        /**
         * Tests if a given literal is true in this state.
         * 
         * @param proposition the literal to test
         * @return true if the literal is true, false otherwise
         */
        public bool isTrue(Literal proposition)
        {
            if (proposition is NegatedLiteral)
                return !isTrue(((NegatedLiteral)proposition).argument as Literal);
            else
                return literals.Contains(proposition);
        }

        /**
         * If the precondition of the given step is met, this method returns a new
         * state which has been modified according to the step's effect.
         * 
         * @param step the step to take
         * @return a new state after the effect has been applied
         * @throws IllegalArgumentException if the step's precondition is not met
         */
        public State apply(Step step)
        {
            if (step.precondition.IsTrue(this))
            {
                MutableState state = new MutableState(this);
                step.effect.Impose(state);
                return state;
            }
            else
                throw new ArgumentException("Cannot apply " + step + "; precondition " + step.precondition + " is not met");
        }

        /**
         * Returns a logical expression representing the current state.
         * 
         * @return an expression
         */
        public Expression toExpression()
        {
            return new Conjunction(literals.ToArray()).Simplify();
        }

        public override string ToString()
        {
            return toExpression().ToString();
        }

        public object Clone()
        {
            return new State(this);
        }

        public List<Literal> Literals { get { return new List<Literal>(literals); } }
    }
}