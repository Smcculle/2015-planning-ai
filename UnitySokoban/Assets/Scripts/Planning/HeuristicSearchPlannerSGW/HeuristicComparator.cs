using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace HeuristicSearchPlannerSGW
{
    public abstract class HeuristicComparator : IComparer<HeuristicNode>
    {
        public abstract int Compare(Plan p1, double h1, Plan p2, double h2);

        public int Compare(HeuristicNode n1, HeuristicNode n2)
        {
            return Compare(n1.state.plan, n1.heuristic, n2.state.plan, n2.heuristic);
        }
    }

}
