using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace StateSpaceSearchProject
{
    public abstract class StateSpaceSearchET : StateSpaceSearch
    {
        public static bool ShutThisSuckaDown = false;

        protected StateSpaceNode _currentNode;

        public StateSpaceSearchET(StateSpaceProblem problem) : base (problem)
        {
        }

        public virtual Dictionary<StateSpaceNode, int> GetNextStates(int depth = 1)
        {
            Dictionary<StateSpaceNode, int> statesCosts = new Dictionary<StateSpaceNode, int>();
            GetNextStateCosts(depth, root, statesCosts);
            return statesCosts;
        }

        protected virtual void GetNextStateCosts(int depth, StateSpaceNode parent, Dictionary<StateSpaceNode, int> statesCosts)
        {
            if (depth <= 0)
                return;

            parent.expand();
            foreach (StateSpaceNode child in parent.children)
            {
                GetNextStateCosts(depth - 1, child, statesCosts);
                statesCosts.Add(child, GetCost(child));
            }
        }

        protected virtual int GetCost(StateSpaceNode child)
        {
            return 0;
        }

        public virtual StateSpaceNode GetCurrentNode()
        {
            return _currentNode;
        }
    }
}