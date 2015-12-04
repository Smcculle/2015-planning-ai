using PlanGraphSGW;
using StateSpaceSearchProject;
using UnityThread;

public class PlanGraphThread : ThreadJob
{
    private PlanGraph _planGraph;
    private StateSpaceProblem _problem;
    public LiteralNode[] literals;

    public PlanGraphThread(StateSpaceProblem problem)
    {
        _problem = problem;
        _planGraph = new PlanGraph(problem, false);
    }

    protected override void ThreadFunction()
    {
        literals = null;
        while (!_planGraph.goalAchieved() && !_planGraph.hasLeveledOff())
            _planGraph.extend();
        if (!_planGraph.goalAchieved())
        {
            Status.SetText("Goal not Achieved...");
            return;
        }
        literals = _planGraph.literals;
    }
}