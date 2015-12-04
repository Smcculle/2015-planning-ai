using StateSpaceSearchProject;

namespace HeuristicSearchPlannerSGW
{
    public class HeuristicNode
    {
        private static int nextID = 0;

        int id = nextID++;
        public readonly StateSpaceNode state;
	    public readonly double heuristic;

        internal HeuristicNode(StateSpaceNode state, double heuristic)
        {
            this.state = state;
            this.heuristic = heuristic;
        }
    }
}