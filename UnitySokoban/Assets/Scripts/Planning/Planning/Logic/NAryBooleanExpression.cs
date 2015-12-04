using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * The parent class of all Boolean expressions with an arbitrary number of
     * arguments.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public abstract class NAryBooleanExpression : ExpressionObject
    {
        /** The Boolean operator used for expressing this kind of expression */
        public string oper;

        /** The expression's arguments */
        public ImmutableArray<Expression> arguments;

        /**
         * Constructs a new N-ary Boolean expression with the given operator and
         * arguments.
         * 
         * @param operator the operator
         * @param arguments the arguments
         * @throws IllegalArgumentException if 0 arguments are provided
         */
        public NAryBooleanExpression(String oper, ImmutableArray<Expression> arguments)
        {
            if (arguments.length == 0)
                throw new ArgumentException("N-ary Boolean expression requires at least 1 argument");
            this.oper = oper;
            this.arguments = arguments;
        }

        /**
         * Constructs a new N-ary Boolean expression with the given operator and
         * arguments.
         * 
         * @param operator the operator
         * @param arguments the arguments
         * @throws IllegalArgumentException if 0 arguments are provided
         */
        public NAryBooleanExpression(String oper, params Expression[] arguments) :
            this(oper, new ImmutableArray<Expression>(arguments))
        {
        }

        public override bool Equals(Formula other, Substitution substitution)
        {
            if (other is NAryBooleanExpression)
            {
                NAryBooleanExpression otherNAB = (NAryBooleanExpression)other;
                if (oper != otherNAB.oper || arguments.length != otherNAB.arguments.length)
                    return false;
                for (int i = 0; i < arguments.length; i++)
                    if (!arguments.get(i).Equals(otherNAB.arguments.get(i), substitution))
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

        /** The expression's hash code */
        private int hashCode = 0;

        public override int GetHashCode()
        {
            if (hashCode == 0)
                hashCode = oper.GetHashCode() + arguments.GetHashCode();
            return hashCode;
        }

        public override string ToString()
        {
            String str = "(" + oper;
            foreach (Expression argument in arguments)
                str += " " + argument;
            return str + ")";
        }

        public override bool IsGround()
        {
            foreach (Expression argument in arguments)
                if (!argument.IsGround())
                    return false;
            return true;
        }

        /**
         * Returns copies of the expression's arguments, but with terms replaced
         * according to some substitution.
         * 
         * @param substitution the substitution
         * @return the arguments with variables replaced
         */
        protected ImmutableArray<Expression> SubstituteArguments(Substitution substitution)
        {
            Expression[] sub = new ExpressionObject[arguments.length];
            for (int i = 0; i < arguments.length; i++)
                sub[i] = arguments.get(i).Substitute(substitution);
            return new ImmutableArray<Expression>(sub);
        }

        /**
         * Given another N-ary Boolean expression, this method unifies each of this
         * expression's arguments with the corresponding argument in the other
         * expression and returns the resulting bindings.
         * 
         * @param other the other expression with which to unify
         * @param bindings the set of bindings to which constraints will be added
         * @return the new bindings (or null if the arguments cannot be unified)
         */
        protected Bindings UnifyArguments(NAryBooleanExpression other, Bindings bindings)
        {
            if (arguments.length != other.arguments.length)
                return null;
            for (int i = 0; i < arguments.length && bindings != null; i++)
                bindings = arguments.get(i).Unify(other.arguments.get(i), bindings);
            return bindings;
        }

        /**
         * Returns copies of the expression's arguments, but negated.
         * 
         * @return the negated arguments
         */
        internal ImmutableArray<Expression> NegateArguments()
        {
            Expression[] neg = new Expression[arguments.length];
            for (int i = 0; i < arguments.length; i++)
                neg[i] = arguments.get(i).Negate();
            return new ImmutableArray<Expression>(neg);
        }

        /**
         * If any of the arguments to this expression are expressions of the same
         * type, their arguments are combined with this expression's arguments and
         * the original expression is removed.
         * 
         * @return the arguments of the flattened expression
         */
        protected Expression[] Flatten()
        {
            
            List<Expression> arguments = new List<Expression>();
            foreach (Expression argument in this.arguments)
            {
                if (GetType().Equals(argument.GetType()))
                    foreach (Expression a in ((NAryBooleanExpression)argument).arguments)
                        arguments.Add(a);
                else
                    arguments.Add(argument);
            }
            return arguments.ToArray();
        }
    }
}