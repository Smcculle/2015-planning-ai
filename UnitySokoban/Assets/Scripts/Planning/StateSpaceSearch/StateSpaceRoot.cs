using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace StateSpaceSearchProject
{
    /**
     * A special {@link edu.uno.ai.planning.ss.StateSpaceNode} that
     * represents the root of the search space and holds a pointer to the search
     * object and the node search limit.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    class StateSpaceRoot : StateSpaceNode
    {
        /** The state space search object */
        internal StateSpaceSearch search;

        /** The maximum number of nodes which may be visited during search (initially no limit) */
        internal int limit = -1;//Planner.NO_NODE_LIMIT;

        /**
         * Constructs a new root node.
         * 
         * @param search the state space search for which this node is the root
         */
        public StateSpaceRoot(StateSpaceSearch search)
            : base(search.problem.initial)
        {
            this.search = search;
        }

        /**
         * Sets the maximum number of nodes that may be visited during search.
         * 
         * @param limit the limit to set
         */
        public void setNodeLimit(int limit)
        {
            this.limit = limit;
        }
    }

}
