using FastForward;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Planning;

namespace IterativeWidthPlanner
{
    public class FFIWPlanner : FastForwardSearch
    {
        public FFIWPlanner(StateSpaceProblem problem) : base(problem)
        {
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
                    if (prune(stateNode))
                        continue;

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
                        if (prune(stateNode))
                            continue;

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

        private bool prune(StateSpaceNode node)
        {
            for (int i = 1; i <= 2; i++)
                if (Novelty.hasNovelty((StateSpaceProblem)problem, node, i))
                    return false;
            return true;
        }
    }
}
