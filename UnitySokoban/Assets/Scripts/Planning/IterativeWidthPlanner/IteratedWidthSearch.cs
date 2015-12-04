using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;

namespace IterativeWidthPlanner
{
    public class IteratedWidthSearch : StateSpaceSearch
    {
        public readonly int maxWidth;
        private readonly Queue<StateSpaceNode> queue = new Queue<StateSpaceNode>();

        public IteratedWidthSearch(StateSpaceProblem problem, int maxWidth)
            :base(problem)
        {
            this.maxWidth = maxWidth;
            queue.Enqueue(root);
        }

        public override Plan findNextSolution()
        {
            while (queue.Count != 0)
            {
                StateSpaceNode node = queue.Dequeue();
                node.expand();
                foreach (StateSpaceNode child in node.children)
                    if (!prune(child))
                        queue.Enqueue(child);
                if (problem.goal.IsTrue(node.state))
                    return node.plan;
            }
            return null;
        }

        private bool prune(StateSpaceNode node) {
            for (int i = 1; i <= maxWidth; i++)
                if (Novelty.hasNovelty((StateSpaceProblem)problem, node, i))
                    return false;
            return true;
        }
    }
}
