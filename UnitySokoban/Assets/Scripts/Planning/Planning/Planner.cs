using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning
{
    /**
 * A planner is an algorithm which solves a planning problem by finding a plan.
 * 
 * @author Stephen G. Ware
 * @param <S> the kind of search done by this planner
 */
    public abstract class Planner<S> where S : Search
    {
        /** Represents no limit to the number of nodes that can be searched */
        public static readonly int NO_NODE_LIMIT = -1;

        /** Represents no limit to the amount of time which can be spent searching */
        public static readonly long NO_TIME_LIMIT = -1;

        /** The name of the planner */
        public readonly String name;

        /** Keeps track of all current searchers being done by this planner */
        private readonly Dictionary<Problem, S> searches = new Dictionary<Problem, S>();

        /**
         * Constructs a new planned with a given name.
         * 
         * @param name the name of the planner
         */
        public Planner(String name)
        {
            this.name = name;
        }

        /**
         * Given some problem to be solved, this method constructs the appropriate
         * kind of {@link Search} to solve it based on this planner.
         * 
         * @param problem the problem to be solved
         * @return a search object for solving this problem
         */
        public abstract S makeSearch(Problem problem);
    }
}
