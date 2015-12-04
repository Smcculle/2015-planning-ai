using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public class Level
    {
        public int number;
        public Level previous;
        protected PlanGraph graph;

        internal Level(PlanGraph graph, int number)
        {
            this.graph = graph;
            this.number = number;
            this.previous = number == 0 ? null : graph.getLevel(number - 1);
        }

        internal void computeMutexes()
        {

        }
    }
}
