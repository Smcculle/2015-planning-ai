using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace StateSpaceSearchProject
{
    /**
     * Represents a search space whose
     * {@link edu.uno.ai.planning.ss.StateSpaceNode nodes} are states
     * and whose edges are steps.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public abstract class StateSpaceSearch : Search
    {
        /** The root node of the search space (i.e. a plan with 0 steps) */
        public readonly StateSpaceNode root;

        /** The search limit on visited nodes (-1 if no limit) */
        int limit = -1;

        /**
         * Creates a state space search for a given problem.
         * 
         * @param problem the problem whose state space will be searched
         */
        public StateSpaceSearch(StateSpaceProblem problem)
            : base(problem)
        {
            this.problem = problem;
            this.root = new StateSpaceRoot(this);
        }

        public override int countVisited()
        {
            return root.countVisited();
        }

        public override int countExpanded()
        {
            return root.countExpanded();
        }

        public override void setNodeLimit(int limit)
        {
            ((StateSpaceRoot)root).setNodeLimit(limit);
        }
    }

}
