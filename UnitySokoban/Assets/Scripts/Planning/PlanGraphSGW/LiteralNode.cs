using Planning.Logic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public class LiteralNode : Node
    {

        public Literal literal;
        internal List<StepNode> producers = new List<StepNode>();
        internal List<StepNode> consumers = new List<StepNode>();

        internal LiteralNode(PlanGraph graph, Literal literal)
            : base(graph)
        {
            this.literal = literal;
        }

        public override int GetHashCode()
        {
            return literal.GetHashCode();
        }
        public override string ToString()
        {
            return literal.ToString() + " " + level;
        }

        public override bool setLevel(int level)
        {
            if (base.setLevel(level))
            {
                foreach (StepNode consumer in consumers)
                    consumer.incrementLiteralCount();
                return true;
            }
            else
                return false;
        }

        public IEnumerable<StepNode> getProducers(int level)
        {
            IEnumerator<StepNode> iterator = new NodeIterator<StepNode>(level, producers);
            while (iterator.MoveNext())
                yield return iterator.Current;
        }

        public IEnumerable<StepNode> getConsumers(int level)
        {
            IEnumerator<StepNode> iterator = new NodeIterator<StepNode>(level, consumers);
            while (iterator.MoveNext())
                yield return iterator.Current;
        }
    }
}
