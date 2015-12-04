using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;

namespace HeuristicSearchPlannerSGW
{
    public abstract class HeuristicSearch : StateSpaceSearchET
    {
        public static readonly HeuristicComparator A_STAR = new AStar();
        public static readonly HeuristicComparator GREEDY = new Greedy();
        protected readonly HeuristicQueue queue;

        public HeuristicSearch(StateSpaceProblem problem, StateHeuristic heuristic, HeuristicComparator comparator)
            : base(problem)
        {
            this.queue = new HeuristicQueue(heuristic, comparator);
            this.queue.push(root);
        }

        class AStar : HeuristicComparator
        {
            public override int Compare(Plan p1, double h1, Plan p2, double h2)
            {
                double comparison = (p1.Size() + h1) - (p2.Size() + h2);
                if (comparison == 0)
                    return GREEDY.Compare(p1, h1, p2, h2);
                else
                    return Convert.ToInt32(comparison);
            }
        }

        class Greedy : HeuristicComparator
        {
            public override int Compare(Plan p1, double h1, Plan p2, double h2)
            {
                return Convert.ToInt32(h1 - h2);
            }
        }
    }
}
