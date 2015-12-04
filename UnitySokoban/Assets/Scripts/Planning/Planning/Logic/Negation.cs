using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
 * A negation is a Boolean expression which represents the opposite of some
 * expression.
 * 
 * @author Stephen G. Ware
 * @ported Edward Thomas Garcia
 */
    public class Negation : BooleanExpression
    {
        /** The expression whose opposite this negation represents */
        public readonly Expression argument;

        /**
         * Constructs a new negation for the given argument.
         * 
         * @param argument the expression to be negated
         */
        public Negation(Expression argument)
        {
            this.argument = argument;
        }

        public override bool Equals(object obj)
        {
            if (obj is Formula)
                return Equals((Formula)obj, Substitution.EMPTY);
            else
                return false;
        }

        public override bool Equals(Formula other, Substitution substitution)
        {
            if (other is Negation)
            {
                Negation otherNeg = (Negation)other;
                return argument.Equals(otherNeg.argument, substitution);
            }
            return false;
        }

        /** The negation's hash code */
        private int hashCode = 0;

        public override int GetHashCode()
        {
            if (hashCode == 0)
                hashCode = "not".GetHashCode() + argument.GetHashCode();
            return hashCode;
        }

        public override string ToString()
        {
            return "(not " + argument + ")";
        }

        public override int CompareTo(Formula other)
        {
            if (other.Equals(argument))
                return 1;
            else if (other is Negation)
                return base.CompareTo(other);
            else
                return argument.CompareTo(other);
        }

        public override bool IsGround()
        {
            return argument.IsGround();
        }

        public override Bindings Unify(Formula other, Bindings bindings)
        {
            if (other is Negation)
                return argument.Unify(((Negation)other).argument, bindings);
            else
                return null;
        }

        public override Expression Substitute(Substitution substitution)
        {
            return new Negation(argument.Substitute(substitution));
        }

        public override bool IsTestable()
        {
            return argument.Negate().IsTestable();
        }

        public override bool IsTrue(State state)
        {
            return argument.Negate().IsTrue(state);
        }


        public override bool IsImposable()
        {
            return argument.Negate().IsImposable();
        }

        public override void Impose(MutableState state)
        {
            argument.Negate().Impose(state);
        }

        public override Expression Negate()
        {
            return argument;
        }

        public override Expression ToCNF()
        {
            return argument.Negate().ToCNF();
        }

        public override Expression ToDNF()
        {
            return argument.Negate().ToDNF();
        }

        public override Expression Simplify()
        {
            return argument.Negate();
        }
    }
}
