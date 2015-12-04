using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    class Mutexes
    {
        public static readonly int ALWAYS = -1;
        private readonly Dictionary<Node, int> nodes = new Dictionary<Node, int>();

        public void add(Node node, int level)
        {
            if (!nodes.ContainsKey(node))
                nodes[node] = level;
            else if (nodes[node] == ALWAYS)
                return;
            else if (nodes[node] < level)
                nodes[node] = level;
        }

        public bool contains(Node node, int level)
        {
            if (!node.exists(level))
                throw new ArgumentException(node + " does not exist at level " + level + ".");
            if (!nodes.ContainsKey(node))
                return false;
            else if (nodes[node] == ALWAYS)
                return true;
            else
                return level <= nodes[node];
        }

        public void clear()
        {
            nodes.Clear();
        }
    }

}
