using PlanGraphSGW;
using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace GraphPlanSGW
{
    class StepPermutationIterator : IEnumerator<ImmutableList<StepNode>>
    {

        private int level;
        private ImmutableList<LiteralNode> goals;
        private StepNode[][] groups;
        private int[] indices;
        private ImmutableList<StepNode> current;

        public ImmutableList<StepNode> Current { get { return current; } }
        object IEnumerator.Current { get { return current; } }

        internal StepPermutationIterator(int level, ImmutableList<LiteralNode> goals)
        {
            this.level = level;
            this.goals = goals;
            this.groups = new StepNode[goals.length][];
            for (int i = 0; i < groups.Length; i++)
            {
                groups[i] = makeGroup(goals.first);
                goals = goals.rest;
            }
            this.indices = new int[groups.Length];
        }

        private StepNode[] makeGroup(LiteralNode goal)
        {
            List<StepNode> list = new List<StepNode>();
            foreach (StepNode producer in goal.getProducers(level))
            {
                if (producer.persistence)
                    list.Add(producer); // @ 0 if possible
                else
                    list.Add(producer);
            }
            return list.ToArray();
        }

        private static bool allPersistence(ImmutableList<StepNode> steps)
        {
            if (steps.length == 0)
                return true;
            else if (!steps.first.persistence)
                return false;
            else
                return allPersistence(steps.rest);
        }

        private ImmutableList<StepNode> findNext()
        {
            return findNext(0, new ImmutableList<StepNode>());
        }

        private ImmutableList<StepNode> findNext(int index, ImmutableList<StepNode> steps)
        {
            if (index == groups.Length)
            {
                if (steps.Equals(current))
                    return null;
                else
                    return steps;
            }
            else
            {
                do
                {
                    StepNode step = groups[index][indices[index]];
                    if (canAdd(step, steps))
                    {
                        ImmutableList<StepNode> result = findNext(index + 1, add(step, steps));
                        if (result != null)
                            return result;
                    }
                    indices[index]++;
                } while (indices[index] < groups[index].Length);
                indices[index] = 0;
                return null;
            }
        }

        private bool canAdd(StepNode step, ImmutableList<StepNode> steps)
        {
            if (steps.length == 0)
                return true;
            else if (step.mutex(steps.first, level))
                return false;
            else
                return canAdd(step, steps.rest);
        }

        private static ImmutableList<StepNode> add(StepNode step, ImmutableList<StepNode> steps)
        {
            if (steps.contains(step))
                return steps;
            else
                return steps.add(step);
        }

        public override string ToString()
        {
            String str = "Step Permutation at Level " + level + ":";
            if (current == null)
                str += "\n  none";
            else
            {
                IEnumerator<LiteralNode> goals = this.goals.GetEnumerator();
                for (int i = 0; i < groups.Length; i++)
                {
                    goals.MoveNext();
                    str += "\n  " + goals.Current + " via " + groups[i][indices[i]];
                }
            }
            return str;
        }

        public void Dispose()
        {
            level = -1;
            goals = null;
            groups = null;
            indices = null;
            current = null;
        }

        public bool MoveNext()
        {
            current = findNext();
            if (current != null && allPersistence(current))
                current = findNext();

            if (current == null)
                return false;
            else
                return true;
        }

        public void Reset()
        {
            current = null;
            ImmutableList<LiteralNode> goals = this.goals;
            this.groups = new StepNode[goals.length][];
            for (int i = 0; i < groups.Length; i++)
            {
                groups[i] = makeGroup(goals.first);
                goals = goals.rest;
            }
            this.indices = new int[groups.Length];
            ImmutableList<StepNode> next = new ImmutableList<StepNode>();
            for (int i = 0; i < groups.Length; i++)
            {
                if (groups[i].Length == 0)
                    return;
                else
                    next = next.add(groups[i][0]);
            }
        }
    }

}
