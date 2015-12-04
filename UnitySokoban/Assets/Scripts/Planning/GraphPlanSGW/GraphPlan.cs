using PlanGraphSGW;
using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GraphPlanSGW
{
    public class GraphPlan : PlanGraphPlanner
    {
        public GraphPlan()
            :base("SGP")
        {
        }

        public override Search makeSearch(PlanGraph graph)
        {
            return new DepthFirstSearch(graph);
        }
    }
}
