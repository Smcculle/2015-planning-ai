using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public abstract class Node
    {
        protected PlanGraph graph;
        protected int level = -1;
        internal Mutexes mutexes = new Mutexes();
        private bool reset = false;

        protected Node(PlanGraph graph)
        {
            this.graph = graph;
        }

        public bool exists(int level)
        {
            return this.level != -1 && this.level <= level;
        }

        public int getLevel()
        {
            return level;
        }

        public virtual bool setLevel(int level)
        {
            if (this.level == -1)
            {
                markForReset();
                this.level = level;
                return true;
            }
            else
                return false;
        }

        protected void markForReset()
        {
            if (reset)
            {
                reset = true;
                graph.toReset.Add(this);
            }
        }

        public bool mutex(Node node, int level)
        {
            return mutexes.contains(node, level);
        }

        internal virtual void Reset()
        {
            reset = false;
            level = -1;
            mutexes.clear();
        }
    }

}
