using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * A disjunction is a Boolean expression that is true when any of its disjuncts
     * are true.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class Disjunction : NAryBooleanExpression
    {
        /** The Boolean operator used for expressing disjunctions */
        public static readonly string DISJUNCTION_OPERATOR = "or";

        /**
         * Constructs a new disjunction with a given set of disjuncts.
         * 
         * @param arguments the disjuncts
         */
        public Disjunction(ImmutableArray<Expression> arguments) :
            base(DISJUNCTION_OPERATOR, arguments)
        {
        }

        /**
         * Constructs a new disjunction with a given set of disjuncts.
         * 
         * @param arguments the disjuncts
         */
        public Disjunction(params Expression[] arguments) :
            base(DISJUNCTION_OPERATOR, arguments)
        {
        }

        public override Expression Substitute(Substitution substitution)
        {
            return new Disjunction(SubstituteArguments(substitution));
        }

        public override Bindings Unify(Formula other, Bindings bindings)
        {
            if (other is Disjunction)
			return UnifyArguments((NAryBooleanExpression)other, bindings);
		else
			return null;
        }

        public override bool IsTestable()
        {
            return true;
        }

        public override bool IsTrue(State state)
        {
            foreach (Expression argument in arguments)
                if (argument.IsTrue(state))
                    return true;
            return false;
        }

        public override bool IsImposable()
        {
            return false;
        }

        public override void Impose(MutableState state)
        {
            throw new ArgumentException("Disjunction cannot be imposed");
        }

        public override Expression Negate()
        {
            return new Conjunction(NegateArguments());
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
            if (arguments.length == 1)
                return arguments.get(0);
            else
                return new Disjunction(Flatten());
        }
    }
}
