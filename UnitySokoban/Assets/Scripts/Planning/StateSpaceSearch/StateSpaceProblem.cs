using Planning;
using Planning.Logic;
using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace StateSpaceSearchProject
{
    /**
     * A subclass of {@link edu.uno.ai.planning.Problem} that
     * propositionalizes all the {@link #steps} that could possibly occur in a
     * plan. 
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class StateSpaceProblem : Problem
    {

        /** Every possible step that could be taken in a solution to this problem */
        public readonly ImmutableArray<Step> steps;

        /** Every possible literal that could be true or false in a given state */
        public readonly ImmutableArray<Literal> literals;

        /**
         * Constructs a new state space problem from a general planning problem.
         * 
         * @param problem the planning problem
         */
        public StateSpaceProblem(Problem problem) :
            base(problem.name, problem.domain, problem.objects, problem.initial, problem.goal)
        {
            steps = collectSteps(problem);
            literals = collectLiterals(problem, steps);
        }

        /**
         * Returns an array of every possible ground step.
         * 
         * @param problem the problem whose steps will be created
         * @return an array of every possible step
         */
        private static ImmutableArray<Step> collectSteps(Problem problem)
        {
            List<Step> steps = new List<Step>();
            foreach (Operator oper in problem.domain.operators)
                collectSteps(problem, oper, new HashSubstitution(), 0, steps);
            return new ImmutableArray<Step>(steps.ToArray());
        }

        /**
         * A recursive helper method for {@link #collectSteps(Problem)} which
         * creates all the steps that can be created for a given operator.
         * 
         * @param problem the problem whose steps will be created
         * @param operator the operator whose steps will be created
         * @param substitution maps the operator's parameters to constants
         * @param paramIndex the index of the current operator parameter being considered
         * @param steps a collection of ground steps
         */
        private static void collectSteps(Problem problem, Operator oper, HashSubstitution substitution, int paramIndex, List<Step> steps)
        {
            if (paramIndex == oper.parameters.length)
                steps.Add(oper.makeStep(substitution));
            else
            {
                Variable parameter = oper.parameters.get(paramIndex);
                foreach (Constant obj in problem.getObjectsByType(parameter.type))
                {
                    substitution.set(parameter, obj);
                    collectSteps(problem, oper, substitution, paramIndex + 1, steps);
                }
            }
        }

        /**
         * Returns an array of every possible ground literal.
         * 
         * @param problem the planning problem
         * @param steps the list of all ground steps
         * @return an array of every possible literal
         */
        private static ImmutableArray<Literal> collectLiterals(Problem problem, IEnumerable<Step> steps)
        {
            List<Literal> literals = new List<Literal>();
            collectLiterals(problem.initial.toExpression(), literals);
            collectLiterals(problem.goal, literals);
            foreach (Step step in steps)
            {
                collectLiterals(step.precondition, literals);
                collectLiterals(step.effect, literals);
            }
            return new ImmutableArray<Literal>(literals.ToArray());
        }

        /**
         * A recursive helper method for {@link #collectLiterals(Problem, Iterable)}
         * which creates all the literals that can be true in a given state for a
         * problem.
         * 
         * @param expression the expression from which all literals should be extracted
         * @param literals the collection of literals to which all literals will be added
         */
        private static void collectLiterals(Expression expression, ICollection<Literal> literals)
        {
            if (expression is Literal)
            {
                if (!literals.Contains((Literal)expression))
                    literals.Add((Literal)expression);
            }
            else if (expression is Conjunction)
                foreach (Expression argument in ((Conjunction)expression).arguments)
                    collectLiterals(argument, literals);
            else
                throw new InvalidOperationException(expression.GetType() + " not supported.");
        }
    }
}
