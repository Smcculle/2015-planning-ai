using Planning;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace IterativeWidthPlanner
{
    public class IteratedWidthPlanner : Planner<IteratedWidthSearch>
    {
        public IteratedWidthPlanner()
            : base("IW2")
        {
        }

        public override IteratedWidthSearch makeSearch(Problem problem)
        {
            return new IteratedWidthSearch((StateSpaceProblem)problem, 2);
        }
    }
}
