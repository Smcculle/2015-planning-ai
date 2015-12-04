using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;

namespace BreadthFirstSearch
{
    public class BFSPlanner : StateSpaceSearchET
    {
        protected readonly Queue<StateSpaceNode> queue = new Queue<StateSpaceNode>();

        public BFSPlanner(StateSpaceProblem problem)
            : base(problem)
        {
            queue.Enqueue(root);
            _currentNode = root;
        }

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
                    queue.Enqueue(child);
            }
            return null;
        }
    }
}
