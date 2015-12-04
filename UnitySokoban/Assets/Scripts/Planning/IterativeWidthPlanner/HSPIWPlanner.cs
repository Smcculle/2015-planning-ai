using HeuristicSearchPlannerSGW;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;
using StateSpaceSearchProject;

namespace IterativeWidthPlanner
{
    public class HSPIWPlanner : HeuristicSearchPlanner
    {
        public HSPIWPlanner() { }

        public override HeuristicSearch makeSearch(Problem problem)
        {
            StateHeuristic heuristic = new AdditiveHeuristic((StateSpaceProblem)problem);
            return new HSPIWSearch((StateSpaceProblem)problem, heuristic, HeuristicSearch.A_STAR);
        }
    }
}
