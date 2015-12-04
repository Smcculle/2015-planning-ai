using UnityEngine;
using System.Collections;
using UnityThread;
using StateSpaceSearchProject;
using System.Collections.Generic;
using System;
using Planning;
using FastForward;
using HeuristicSearchPlannerSGW;
using BreadthFirstSearch;
using IterativeWidthPlanner;
using PlanGraphSGW;

public class PlannerThread : ThreadJob
{
    private StateSpaceProblem _ssProblem;
    private StateSpaceSearchET _planner;
    private Dictionary<StateSpaceNode, int> _result;
    private Plan _plan;
    private string _plannerFunction;
    private PlanGraph _planGraph;

    public PlannerThread(StateSpaceProblem ssProblem, string plannerFunction, string plannerType, bool useNovelty)
    {
        _ssProblem = ssProblem;
        _plannerFunction = plannerFunction;

        switch (plannerType)
        {
            case "BFS":
                if (useNovelty)
                    _planner = new BFSIWPlanner(_ssProblem);
                else
                    _planner = new BFSPlanner(_ssProblem);
                break;
            case "HSP":
                if (useNovelty)
                {
                    HeuristicSearchPlanner hsp = new HSPIWPlanner();
                    _planner = hsp.makeSearch(_ssProblem);
                }
                else
                {
                    HeuristicSearchPlanner hsp = new HeuristicSearchPlanner();
                    _planner = hsp.makeSearch(_ssProblem);
                }
                break;
            case "FF":
                if (useNovelty)
                    _planner = new FFIWPlanner(_ssProblem);
                else
                    _planner = new FastForwardSearch(_ssProblem);
                break;
        }
    }

    protected override void ThreadFunction()
    {
        if (_plannerFunction == "NextState")
            _result = _planner.GetNextStates();
        else if (_plannerFunction == "PlanGraph")
        {
            _planGraph = new PlanGraph(_ssProblem);
            _planGraph.initialize(_ssProblem.initial);
            while (!_planGraph.goalAchieved() && !_planGraph.hasLeveledOff())
                _planGraph.extend();
        }
        else
            _plan = _planner.findNextSolution();
        base.ThreadFunction();
    }

    public Dictionary<StateSpaceNode, int> GetResult()
    {
        return _result;
    }

    public Plan GetPlan()
    {
        return _plan;
    }

    public PlanGraph GetPlanGraph()
    {
        return _planGraph;
    }

    public StateSpaceNode GetCurrentNode()
    {
        return _planner.GetCurrentNode();
    }

    private Level ConvertToLevel(StateSpaceNode node)
    {
        throw new NotImplementedException();
    }
}
