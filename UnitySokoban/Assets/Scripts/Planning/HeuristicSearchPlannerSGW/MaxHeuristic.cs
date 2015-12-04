using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning.Logic;

namespace HeuristicSearchPlannerSGW
{
    public class MaxHeuristic : HSPHeuristic
    {
        public MaxHeuristic(StateSpaceProblem problem)
            : base(problem)
        {
        }

        protected override int cost(Expression expression)
        {
            if (expression is Literal)
                return cost((Literal)expression);
            else if (expression is Conjunction)
            {
                int max = 0;
                foreach (Expression argument in ((Conjunction)expression).arguments)
                {
                    int argCost = cost(argument);
                    if (argCost == INFINITY)
                        return INFINITY;
                    else
                        max = Math.Max(max, argCost);
                }
                return max;
            }
            else
                throw new InvalidOperationException(expression.GetType() + " not supported.");
        }
    }

}
