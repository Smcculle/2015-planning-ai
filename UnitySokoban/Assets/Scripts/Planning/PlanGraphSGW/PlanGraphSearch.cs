using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public class PlanGraphSearch : Search
    {
        private readonly PlanGraphPlanner planner;
        private readonly PlanGraph graph;
        private Search search = null;
        private int limit = Planner<Search>.NO_NODE_LIMIT;
        private int visited = 0;
        private int expanded = 0;

        public PlanGraphSearch(PlanGraphPlanner planner, Problem problem)
            : base(problem)
        {
            this.planner = planner;
            this.graph = new PlanGraph(problem, true);
            this.graph.initialize(problem.initial);
        }

        public override int countVisited()
        {
            if (search == null)
                return visited;
            else
                return visited + search.countVisited();
        }

        public override int countExpanded()
        {
            if (search == null)
                return expanded;
            else
                return expanded + search.countExpanded();
        }

        public override void setNodeLimit(int limit)
        {
            this.limit = limit;
        }

        public override Plan findNextSolution()
        {
            while (!graph.goalAchieved() && !graph.hasLeveledOff())
                graph.extend();
            while (true)
            {
                if (search == null)
                {
                    search = planner.makeSearch(graph);
                    search.setNodeLimit(limit);
                }
                Plan plan = search.findNextSolution();
                if (plan == null)
                {
                    visited += search.countVisited();
                    expanded += search.countExpanded();
                    if (limit != Planner<Search>.NO_NODE_LIMIT)
                        limit -= search.countVisited();
                    search = null;
                    graph.extend();
                }
                else
                    return plan;
            }
        }
    }
}
