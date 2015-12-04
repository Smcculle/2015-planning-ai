using Planning;
using StateSpaceSearchProject;

namespace HeuristicSearchPlannerSGW
{
    public abstract class HeuristicPlanner : Planner<HeuristicSearch>
    {

        public HeuristicPlanner(string name)
            :base(name)
        {
        }
    }

}