using System;
using System.Collections.Generic;
using Planning;
using StateSpaceSearchProject;

namespace HeuristicSearchPlannerSGW
{
    public class CompleteSearch : HeuristicSearch
    {
        private StateHeuristic heuristic;

        public CompleteSearch(StateSpaceProblem problem, StateHeuristic heuristic, HeuristicComparator comparator)
            :base(problem, heuristic, comparator)
        {
            this.heuristic = heuristic;
            _currentNode = root;
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
                    queue.push(child);
                if (problem.goal.IsTrue(current.state))
                    return current.plan;
            }
            return null;
        }

        protected override int GetCost(StateSpaceNode child)
        {
            return Convert.ToInt32(heuristic.evaluate(child.state));
        }
    }
}