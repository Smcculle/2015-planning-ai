using PlanGraphSGW;
using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GraphPlanSGW
{
    public class SubgraphRoot : SubgraphNode
    {
        internal int limit = Planner<Search>.NO_NODE_LIMIT;

        internal SubgraphRoot(PlanGraph graph)
            : base(graph)
        {
        }

        internal void setNodeLimit(int limit)
        {
            this.limit = limit;
        }
    }

}
