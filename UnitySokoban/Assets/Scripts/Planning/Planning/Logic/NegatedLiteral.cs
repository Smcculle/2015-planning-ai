using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
  * The negation of a literal is a literal.
  * 
  * @author Stephen G. Ware
  * @ported Edward Thomas Garcia
  */
    public class NegatedLiteral : Negation, Literal
    {
        /** The literal being negated */
        //public readonly Literal argument;

        /**
         * Constructs a new negated literal.
         * 
         * @param argument the literal to be negated
         */
        public NegatedLiteral(Literal argument) :
           base(argument)
        {
        }

        public override Expression Substitute(Substitution substitution)
        {
            return new NegatedLiteral(argument.Substitute(substitution) as Literal);
        }

        public override bool IsTestable()
        {
            return true;
        }

        public override bool IsTrue(State state)
        {
            return state.isTrue(this);
        }

        public override bool IsImposable()
        {
            return true;
        }

        public override void Impose(MutableState state)
        {
            state.impose(this);
        }

        public override Expression Negate()
        {
            return argument;
        }

        public override Expression ToCNF()
        {
            return NormalForms.toCNF(this);
        }

        public override Expression ToDNF()
        {
            return NormalForms.toDNF(this);
        }

        public override Expression Simplify()
        {
            return this;
        }

        Literal Literal.Substitute(Substitution substitution)
        {
            return new NegatedLiteral(argument.Substitute(substitution) as Literal);
        }

        Literal Literal.Negate()
        {
            return argument as Literal;
        }
    }
}
