using UnityEngine;
using System.Collections;
using UnityThread;
using System.IO;
using Planning;
using Planning.IO;
using StateSpaceSearchProject;

public class ProblemThread : ThreadJob
{
    private string _pddl;
    private Level _level;
    private string _domain;
    private StateSpaceProblem _ssProblem;

    public ProblemThread(string pddl, Level level, string domain)
    {
        _pddl = pddl;
        _level = level;
        _domain = domain;
    }

    protected override void ThreadFunction()
    {
        Problem problem = PDDLReader.GetProblem(_domain, _pddl);
        _ssProblem = new StateSpaceProblem(problem);

        base.ThreadFunction();
    }

    public StateSpaceProblem GetStateSpaceProblem()
    {
        return _ssProblem;
    }
}
