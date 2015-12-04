using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;
using StateSpaceSearchProject;

namespace HeuristicSearchPlannerSGW
{
    public class HeuristicSearchPlanner : HeuristicPlanner
    {
        public HeuristicSearchPlanner()
            :base("SHSP")
        {
        }

        public override HeuristicSearch makeSearch(Problem problem)
        {
            StateHeuristic heuristic = new AdditiveHeuristic((StateSpaceProblem)problem);
            return new CompleteSearch((StateSpaceProblem)problem, heuristic, HeuristicSearch.A_STAR);
        }
    }
}
