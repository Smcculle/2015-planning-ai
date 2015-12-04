using Planning;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace HeuristicSearchPlannerSGW
{
    public abstract class StateHeuristic : Heuristic<State> {

        public readonly StateSpaceProblem problem;

        public StateHeuristic(StateSpaceProblem problem)
        {
            this.problem = problem;
        }

        public abstract int evaluate(State current);
    }

}
