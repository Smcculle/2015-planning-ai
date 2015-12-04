using Planning.Logic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning
{
    /**
      * A {@link State} which can be modified.
      * 
      * @author Stephen G. Ware
      * @ported Edward Thomas Garcia
      */
    public class MutableState : State
    {
        /**
         * Returns a new state which is a clone of the given state.
         * 
         * @param toClone the state to clone
         */
        public MutableState(State toClone)
            : base(toClone)
        {
        }

        /**
         * Constructs a new, empty state.
         */
        public MutableState() : base()
        {
        }

        /**
         * Modifies the current state such that the given literal is true.
         * 
         * @param proposition the literal to make true
         */
        public void impose(Literal proposition)
        {
            if (proposition is NegatedLiteral)
            {
                proposition = ((NegatedLiteral)proposition).argument as Literal;
                if (proposition is NegatedLiteral)
                    impose(((NegatedLiteral)proposition).argument as Literal);
                else
                    literals.Remove(proposition);
            }
            else
                literals.Add(proposition);
        }
    }
}
