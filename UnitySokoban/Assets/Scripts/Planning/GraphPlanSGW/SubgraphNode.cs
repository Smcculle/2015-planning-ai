using PlanGraphSGW;
using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GraphPlanSGW
{
    public class SubgraphNode
    {

        private static readonly TotalOrderPlan EMPTY = new TotalOrderPlan();

        public readonly SubgraphNode parent;
        public readonly TotalOrderPlan plan;
        public readonly int level;
        public readonly ImmutableList<LiteralNode> goals;
        internal int descendants = 0;
        private readonly StepPermutationIterator permutations;

        internal SubgraphNode(PlanGraph graph)
        {
            this.parent = null;
            this.plan = EMPTY;
            this.level = graph.Size() - 1;
            this.goals = toList<LiteralNode>(graph.goals);
            this.permutations = new StepPermutationIterator(level, goals);
        }

        internal SubgraphNode(SubgraphNode parent, TotalOrderPlan plan, int level, ImmutableList<LiteralNode> goals)
        {
            this.parent = parent;
            this.plan = plan;
            this.level = level;
            this.goals = goals;
            this.permutations = new StepPermutationIterator(level, goals);
            SubgraphNode ancestor = parent;
            while (ancestor != null)
            {
                ancestor.descendants++;
                ancestor = ancestor.parent;
            }
        }

        private static ImmutableList<T> toList<T>(IEnumerable<T> collection)
        {
            ImmutableList<T> list = new ImmutableList<T>();
            foreach (T obj in collection)
                list = list.add(obj);
            return list;
        }

        public override string ToString()
        {
            String str;
            if (parent == null)
                str = "Subgraph:";
            else
                str = parent.ToString();
            str += "\n  Level " + level + " goals:";
            foreach (LiteralNode literal in goals)
                str += " " + literal.literal;
            return str;
        }

        public SubgraphRoot getRoot()
        {
            SubgraphNode node = this;
            while (node.parent != null)
                node = node.parent;
            return (SubgraphRoot)node;
        }

        public SubgraphNode expand()
        {
            // If this would violate the search limit, throw an exception.
            SubgraphRoot root = getRoot();
            if (root.limit == root.descendants)
                throw new TimeoutException();
            // Loop until we generate a child node or run out of permutations.
            while (true)
            {
                // If this node has no more children, return null.
                if (!permutations.MoveNext())
                    return null;
                // Get the next permutation of steps.
                ImmutableList<StepNode> steps = permutations.Current;
                // My child's plan is my plan plus all non-persistence steps.
                TotalOrderPlan childPlan = this.plan;
                foreach (StepNode stepNode in steps)
                    if (!stepNode.persistence)
                        childPlan = childPlan.add(stepNode.step);
                // My child's level is one level earlier than mine.
                int childLevel = level - 1;
                // My child's goals are the preconditions of the steps at my level.
                ImmutableList<LiteralNode> childGoals = new ImmutableList<LiteralNode>();
                foreach (StepNode stepNode in steps)
                    foreach (LiteralNode precondition in stepNode.getPreconditions(level))
                        if (!childGoals.contains(precondition))
                            childGoals = childGoals.add(precondition);
                // If any of the child's goals are mutex, try the next permutation.
                // Otherwise, return the child node.
                if (!anyMutex(childGoals, childLevel))
                    return new SubgraphNode(this, childPlan, childLevel, childGoals);
            }
        }

        private static bool anyMutex(ImmutableList<LiteralNode> literals, int level)
        {
            ImmutableList<LiteralNode> first = literals;
            while (first.length > 1)
            {
                ImmutableList<LiteralNode> second = first.rest;
                while (second.length > 0)
                {
                    if (first.first.mutex(second.first, level))
                        return true;
                    second = second.rest;
                }
                first = first.rest;
            }
            return false;
        }
    }
}
