using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;
using BreadthFirstSearch;

namespace IterativeWidthPlanner
{
    public class BFSIWPlanner : BFSPlanner
    {
        public BFSIWPlanner(StateSpaceProblem problem) : base(problem) { }

        public override Plan findNextSolution()
        {
            while (queue.Count > 0)
            {
                if (ShutThisSuckaDown)
                {
                    ShutThisSuckaDown = false;
                    return null;
                }
                StateSpaceNode node = queue.Dequeue();
                _currentNode = node;
                node.expand();
                if (problem.goal.IsTrue(node.state))
                    return node.plan;
                foreach (StateSpaceNode child in node.children)
                    if (!prune(child))
                        queue.Enqueue(child);
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
