using Planning;
using Planning.Logic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public class StepNode : Node
    {
        public readonly Step step;
        public readonly bool persistence;
        internal readonly List<LiteralNode> preconditions = new List<LiteralNode>();
        internal readonly List<LiteralNode> effects = new List<LiteralNode>();
        private int literalCount = 0;

        public StepNode(PlanGraph graph, Step step)
            : base(graph)
        {
            this.step = step;
            this.persistence = false;
        }

        public StepNode(PlanGraph graph, Literal literal)
            : base(graph)
        {
            this.step = new Step("(persist " + literal + ")", literal, literal);
            this.persistence = true;
        }

        public override int GetHashCode()
        {
            return step.GetHashCode();
        }

        public override string ToString()
        {
            return step.ToString() + " " + level;
        }

        public void incrementLiteralCount()
        {
            markForReset();
            literalCount++;
            if (literalCount == preconditions.Count)
                graph.nextSteps.Add(this);
        }

        public override bool setLevel(int level)
        {
            if (base.setLevel(level))
            {
                foreach (LiteralNode effect in effects)
                    effect.setLevel(level);
                return true;
            }
            else
                return false;
        }

        internal override void Reset()
        {
            base.Reset();
            literalCount = 0;
        }

        public IEnumerable<LiteralNode> getPreconditions(int level)
        {
            IEnumerator<LiteralNode> iterator = new NodeIterator<LiteralNode>(level, preconditions);
            while (iterator.MoveNext())
                yield return iterator.Current;
        }

        public IEnumerable<LiteralNode> getEffects(int level)
        {
            IEnumerator<LiteralNode> iterator = new NodeIterator<LiteralNode>(level, effects);
            while (iterator.MoveNext())
                yield return iterator.Current;
        }
    }
}
