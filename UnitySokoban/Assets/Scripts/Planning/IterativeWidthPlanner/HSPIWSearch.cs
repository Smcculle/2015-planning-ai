using HeuristicSearchPlannerSGW;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;
using StateSpaceSearchProject;

namespace IterativeWidthPlanner
{
    public class HSPIWSearch : CompleteSearch
    {
        public HSPIWSearch(StateSpaceProblem problem, StateHeuristic heuristic, HeuristicComparator comparator)
            : base(problem, heuristic, comparator)
        {
        }

        public override Plan findNextSolution()
        {
            while (queue.size() > 0)
            {
                if (ShutThisSuckaDown)
                {
                    ShutThisSuckaDown = false;
                    return null;
                }
                StateSpaceNode current = queue.pop();
                _currentNode = current;
                current.expand();
                foreach (StateSpaceNode child in current.children)
                    if (!prune(child))
                        queue.push(child);
                if (problem.goal.IsTrue(current.state))
                    return current.plan;
            }
            return null;
        }

        private bool prune(StateSpaceNode node)
        {
            for (int i = 1; i <= 2; i++)
                if (Novelty.hasNovelty((StateSpaceProblem)problem, node, i))
                    return false;
            return true;
        }
    }
}
