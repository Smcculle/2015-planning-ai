using Planning;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FastForward
{
    public class FastForwardPlanner : Planner<FastForwardSearch> {

        public FastForwardPlanner()
            :base("FF")
        {
        }

        public override FastForwardSearch makeSearch(Problem problem)
        {
            return new FastForwardSearch(new StateSpaceProblem(problem));
        }
    }

}
