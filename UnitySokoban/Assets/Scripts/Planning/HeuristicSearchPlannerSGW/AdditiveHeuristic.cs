using Planning.Logic;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace HeuristicSearchPlannerSGW
{
    public class AdditiveHeuristic : HSPHeuristic
    {
        public AdditiveHeuristic(StateSpaceProblem problem)
            : base(problem)
        {
        }

        protected override int cost(Expression expression)
        {
            if (expression is Literal)
                return cost((Literal)expression);
            else if (expression is Conjunction)
            {
                int total = 0;
                foreach (Expression argument in ((Conjunction)expression).arguments)
                {
                    int argCost = cost(argument);
                    if (argCost == INFINITY)
                        return INFINITY;
                    else
                        total += argCost;
                }
                return total;
            }
            else
                throw new InvalidOperationException(expression.GetType() + " not supported.");
        }
    }

}
