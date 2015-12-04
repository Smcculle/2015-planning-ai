using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
 * Represents a single statement in function-free predicate logic.
 * 
 * @author Stephen G. Ware
 */
    public class Predication : ExpressionObject, Literal
    {

        /** The predicate (i.e. relationship) applies to the terms */
        public readonly string predicate;

        /** The terms to which the predicate applies */
        public readonly ImmutableArray<Term> terms;

        /**
         * Constructs a new predication with a given predicate and set of terms.
         * 
         * @param predicate the predicate
         * @param terms the terms
         */
        public Predication(String predicate, ImmutableArray<Term> terms)
        {
            this.predicate = predicate;
            this.terms = terms;
        }

        /**
         * Constructs a new predication with a given predicate and set of terms.
         * 
         * @param predicate the predicate
         * @param terms the terms
         */
        public Predication(String predicate, params Term[] terms) :
            this(predicate, new ImmutableArray<Term>(terms))
        {
        }

        public override bool Equals(Formula other, Substitution substitution)
        {
            if (other is Predication)
            {
                Predication otherPred = (Predication)other;
                if (!predicate.Equals(otherPred.predicate) || terms.length != otherPred.terms.length)
                    return false;
                for (int i = 0; i < terms.length; i++)
                    if (!terms.get(i).Equals(otherPred.terms.get(i), substitution))
                        return false;
                return true;
            }
            return false;
        }

        public override bool Equals(object obj)
        {
            if (obj is Formula)
                return Equals(obj as Formula, Substitution.EMPTY);
            else
                return false;
        }

        /** The predication's hash code */
        private int hashCode = 0;

        public override int GetHashCode()
        {
            if (hashCode == 0)
                hashCode = predicate.GetHashCode() + terms.GetHashCode();
            return hashCode;
        }

        public override string ToString()
        {
            String str = "(" + predicate;
            foreach (Term term in terms)
                str += " " + term;
            return str + ")";
        }

        public override int CompareTo(Formula other)
        {
            if (other is Negation)
                return -1 * other.CompareTo(this);
            else
                return base.CompareTo(other);
        }

        public override bool IsGround()
        {
            foreach (Term term in terms)
                if (!term.IsGround())
                    return false;
            return true;
        }

        /**
         * Returns a copy of the set of terms, but after each has been substituted
         * according to a given substitution.
         * 
         * @param substitution the substitution
         * @return the terms, replaced by their substitutions
         */
        protected ImmutableArray<Term> substituteTerms(Substitution substitution)
        {
            Term[] sub = new Term[terms.length];
            for (int i = 0; i < terms.length; i++)
                sub[i] = terms.get(i).substitute(substitution);
            return new ImmutableArray<Term>(sub);
        }

        public override Expression Substitute(Substitution substitution)
        {
            return new Predication(predicate, substituteTerms(substitution));
        }

        public override Bindings Unify(Formula other, Bindings bindings)
        {
            if (other is Predication)
            {
                Predication otherProp = (Predication)other;
                if (terms.length != otherProp.terms.length)
                    return null;
                for (int i = 0; i < terms.length && bindings != null; i++)
                    bindings = terms.get(i).Unify(otherProp.terms.get(i), bindings);
                return bindings;
            }
            return null;
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
            return new NegatedLiteral(this);
        }

        public override Expression ToCNF()
        {
            return NormalForms.toCNF(this);
        }

        public override Expression ToDNF()
        {
            return NormalForms.toDNF(this);
        }

        Literal Literal.Substitute(Substitution substitution)
        {
            return new Predication(predicate, substituteTerms(substitution));
        }

        Literal Literal.Negate()
        {
            return new NegatedLiteral(this);
        }

        public override Expression Simplify()
        {
            throw new NotImplementedException();
        }
    }
}