using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * A conjunction is a Boolean expression that is true when all of its conjuncts
     * are true.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class Conjunction : NAryBooleanExpression
    {
        /** The Boolean operator used for expressing conjunctions */
        public static readonly string CONJUNCTION_OPERATOR = "and";

        /**
	     * Constructs a new conjunction with a given set of conjuncts.
	     * 
	     * @param arguments the conjuncts
	     */
        public Conjunction(ImmutableArray<Expression> arguments) :
            base(CONJUNCTION_OPERATOR, arguments)
        {
        }

        /**
         * Constructs a new conjunction with a given set of conjuncts.
         * 
         * @param arguments the conjuncts
         */
        public Conjunction(params Expression[] arguments) :
            base(CONJUNCTION_OPERATOR, arguments)
        {
        }

        public override Expression Substitute(Substitution substitution)
        {
            return new Conjunction(SubstituteArguments(substitution));
        }

        public override Bindings Unify(Formula other, Bindings bindings)
        {
            if (other is Conjunction)
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
                if (!argument.IsTrue(state))
                    return false;
            return true;
        }

        public override bool IsImposable()
        {
            return true;
        }

        public override void Impose(MutableState state)
        {
            foreach (Expression argument in arguments)
                argument.Impose(state);
        }

        public override Expression Negate()
        {
            return new Disjunction(NegateArguments());
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
                return new Conjunction(Flatten());
        }
    }
}
