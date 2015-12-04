using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;

namespace FastForward
{
    public class FastForwardSearch : StateSpaceSearchET
    {

        private int visited = 0;
        private int expanded = 0;
        private int nodeLimit = Planner<Search>.NO_NODE_LIMIT;

        public FastForwardSearch(StateSpaceProblem problem)
            : base(problem)
        {
        }

        public override int countVisited()
        {
            return visited;
        }


        public override int countExpanded()
        {
            return expanded;
        }

        public override void setNodeLimit(int limit)
        {
            nodeLimit = limit;

        }

        public override Plan findNextSolution()
        {
            StateSpaceNode bestNode = root;
            int hValue = int.MaxValue;
            int nextValue;
            List<StateSpaceNode> nextLevel = new List<StateSpaceNode>(1);
            nextLevel.Add(root);

            List<StateSpaceNode> helpful = new List<StateSpaceNode>();
            List<StateSpaceNode> unhelpful = new List<StateSpaceNode>();

            bool foundBetter;
            while (nextLevel.Count != 0)
            {
                foundBetter = false;
                helpful.Clear();
                unhelpful.Clear();
                //separate helpful states from unhelpful states
                foreach (StateSpaceNode stateNode in nextLevel)
                {
                    Util.separateHelpful(problem, stateNode, helpful, unhelpful);
                }
                //evaluate helpful actions
                foreach (StateSpaceNode stateNode in helpful)
                {
                    if (ShutThisSuckaDown)
                    {
                        ShutThisSuckaDown = false;
                        return null;
                    }
                    nextValue = getHeuristicValue(stateNode);
                    if (problem.isSolution(stateNode.plan))
                    {
                        return stateNode.plan;
                    }
                    else
                    {
                        if (nextValue < hValue)
                        {
                            hValue = nextValue;
                            bestNode = stateNode;
                            foundBetter = true;
                        }
                    }
                }
                //if helpful actions yield nothing, weighted A* search on everything
                if (!foundBetter)
                {
                    foreach (StateSpaceNode stateNode in unhelpful)
                    {
                        if (ShutThisSuckaDown)
                        {
                            ShutThisSuckaDown = false;
                            return null;
                        }
                        nextValue = getHeuristicValue(stateNode);
                        if (problem.isSolution(stateNode.plan))
                        {
                            return stateNode.plan;
                        }
                        else
                        {
                            if (nextValue < hValue)
                            {
                                hValue = nextValue;
                                bestNode = stateNode;
                                foundBetter = true;
                            }
                        }
                    }
                }
                //build next layer, enforcing breadth-first search if no better node was found
                if (!foundBetter)
                {
                    nextLevel = makeNextLevel(helpful, unhelpful);
                }
                else
                {
                    nextLevel = makeNextLevel(bestNode);
                }

            }
            return null;
        }

        protected int getHeuristicValue(StateSpaceNode stateNode)
        {
            _currentNode = stateNode;
            visited++;
            if (visited >= nodeLimit && nodeLimit != -1)
            {
                throw new TimeoutException();
            }
            return new FastForwardHeuristic((StateSpaceProblem)problem).hValue(stateNode.state);
        }

        protected override int GetCost(StateSpaceNode node)
        {
            return new FastForwardHeuristic((StateSpaceProblem)problem).hValue(node.state);
        }

        protected List<StateSpaceNode> makeNextLevel(StateSpaceNode parent)
        {
            List<StateSpaceNode> nextLevel = new List<StateSpaceNode>();
            parent.expand();
            expanded++;
            foreach (StateSpaceNode child in parent.children)
            {
                nextLevel.Add(child);
            }
            return nextLevel;
        }

        protected List<StateSpaceNode> makeNextLevel(params List<StateSpaceNode>[] lists)
        {
            List<StateSpaceNode> nextLevel = new List<StateSpaceNode>();
            foreach (List<StateSpaceNode> list in lists)
            {
                foreach (StateSpaceNode parent in list)
                {
                    parent.expand();
                    expanded++;
                    foreach (StateSpaceNode child in parent.children)
                    {
                        nextLevel.Add(child);
                    }
                }
            }
            return nextLevel;
        }
    }
}
