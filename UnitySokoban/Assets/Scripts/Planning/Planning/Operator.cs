using Planning.Logic;
using Planning.Util;
using System;

namespace Planning
{
    /**
     * An operator is an action template that describes one way to change the world in terms
     * of its precondition (what must be true before the action can be taken) and effect
     * (what becomes true after the action is taken).
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class Operator
    {
        /** The name of the action */
        public readonly string name;

        /** The parameters that provide the specific details for the action */
        public readonly ImmutableArray<Variable> parameters;

        /** What must be true before the action can be taken */
        public readonly Expression precondition;

        /** What becomes true after the action is taken */
        public readonly Expression effect;

        /**
         * Constructs a new action teamplte.
         * 
         * @param name the name of the action
         * @param parameters the parameters that provide specific detail
         * @param precondition what must be true before
         * @param effect what becomes true after
         */
        public Operator(string name, ImmutableArray<Variable> parameters, Expression precondition, Expression effect)
        {
            if (!isDeterministic(effect))
                throw new ArgumentException("Effect nondeterministic");
            this.name = name;
            this.parameters = parameters;
            this.precondition = precondition;
            this.effect = effect;
        }

        /**
         * Constructs a new action template.
         * 
         * @param name the name of the action
         * @param parameters the parameters that provide specific detail
         * @param precondition what must be true before
         * @param effect what becomes true after
         */
        public Operator(string name, Variable[] parameters, Expression precondition, Expression effect) :
            this(name, new ImmutableArray<Variable>(parameters), precondition, effect)
        {
        }

        /**
         * Checks if an effect expression is deterministic (i.e. results in exactly
         * one possible next state).
         * 
         * @param expression the expression to test
         * @return true of the expression is deterministic, false otherwise
         */
        private static bool isDeterministic(Expression expression)
        {
            expression = expression.ToDNF();
            if (!(expression is Disjunction))
			return false;
            Disjunction dnf = (Disjunction)expression;
            if (dnf.arguments.length != 1)
                return false;
            if (!(dnf.arguments.get(0) is Conjunction))
			return false;
            Conjunction clause = (Conjunction)dnf.arguments.get(0);
            foreach (Expression literal in clause.arguments)
                if (!(literal is Literal))
				return false;
            return true;
        }

        /**
         * Creates a ground step (i.e. a specific action) from this action
         * template.
         * 
         * @param substitution provides bindings for each of the operator's parameters
         * @return a step
         */
        public Step makeStep(Substitution substitution)
        {
            String name = "(" + this.name;
            foreach (Variable parameter in parameters)
                name += " " + parameter.substitute(substitution);
            name += ")";
            return new Step(name, precondition.Substitute(substitution), effect.Substitute(substitution));
        }

        public override string ToString()
        {
            String str = "(" + name;
            foreach (Variable parameter in parameters)
                str += " " + parameter;
            return str + ")";
        }
    }

}