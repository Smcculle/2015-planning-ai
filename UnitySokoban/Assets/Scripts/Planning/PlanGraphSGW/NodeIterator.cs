using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public class NodeIterator<N> : IEnumerator<N> where N : Node
    {
        private readonly int level;
        private readonly IEnumerator<N> nodes;
        private N current;

        public N Current { get { return current; } }
        object IEnumerator.Current { get { return current; } }

        public NodeIterator(int level, IEnumerable<N> nodes)
        {
            this.level = level;
            this.nodes = nodes.GetEnumerator();
        }

        public void Dispose()
        {
            nodes.Dispose();
        }

        public bool MoveNext()
        {
            while (nodes.MoveNext())
            {
                current = nodes.Current;
                if (current.getLevel() != -1 && current.getLevel() <= level)
                    return true;
            }
            return false;
        }

        public void Reset()
        {
            nodes.Reset();
        }
    }
}
