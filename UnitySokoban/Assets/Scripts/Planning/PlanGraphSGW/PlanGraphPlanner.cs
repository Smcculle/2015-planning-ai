using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public abstract class PlanGraphPlanner : Planner<PlanGraphSearch>
    {
        public PlanGraphPlanner(String name)
            : base(name)
        {
        }

        public override PlanGraphSearch makeSearch(Problem problem)
        {
            return new PlanGraphSearch(this, problem);
        }

        public abstract Search makeSearch(PlanGraph graph);
    }
}
