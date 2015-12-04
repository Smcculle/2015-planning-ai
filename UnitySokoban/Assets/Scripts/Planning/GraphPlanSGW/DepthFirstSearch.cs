using PlanGraphSGW;
using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GraphPlanSGW
{
    public class DepthFirstSearch : Search
    {
        public readonly PlanGraph graph;
        private readonly Stack<SubgraphNode> stack = new Stack<SubgraphNode>();
        private readonly SubgraphRoot root;

        public DepthFirstSearch(PlanGraph graph)
            : base(graph.problem)
        {
            this.graph = graph;
            this.root = new SubgraphRoot(graph);
            stack.Push(root);
        }

        public override int countVisited()
        {
            return root.descendants;
        }

        public override int countExpanded()
        {
            return root.descendants;
        }

        public override void setNodeLimit(int limit)
        {
            root.setNodeLimit(limit);
        }

        public override Plan findNextSolution()
        {
            while (stack.Count != 0)
            {
                SubgraphNode node = stack.Peek();
                SubgraphNode child = node.expand();
                if (child == null)
                    stack.Pop();
                else
                    stack.Push(child);
                if (node.level == 0 && problem.isSolution(node.plan))
                    return node.plan;
            }
            return null;
        }
    }
}
