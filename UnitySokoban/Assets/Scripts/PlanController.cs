using Planning;
using Planning.Logic;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using UnityEngine;
using UnityThread;

public class PlanController : MonoBehaviour
{
    public enum PlanMenuState { Initial, Menu, Domain, Problem, Wait, Computing, Complete }
    public enum ThreadState { Initial, Running, Complete }

    public GameObject PlanMenuPrefab;
    public GameObject PaperPrefab;
    public LevelController levelController;
    public PlanMenuState planMenuState = PlanMenuState.Initial;
    public ThreadState problemThreadState = ThreadState.Initial;
    public ThreadState computeThreadState = ThreadState.Initial;

    private StateSpaceProblem _ssProblem;
    private PlannerThread _hThread;
    private StateSpaceNode _previousNode;
    private bool planButtonActive;
    // Public for Unity Debug
    public string _plannerFunction = null;
    public string _plannerType = null;
    public bool _useNovelty;

    public void ClickPlan()
    {
        planMenuState = PlanMenuState.Menu;
        problemThreadState = ThreadState.Initial;
        computeThreadState = ThreadState.Initial;
        Status.SetText(GetStatus());

        _ssProblem = null;
        _plannerFunction = null;
        _plannerType = null;
        _useNovelty = false;
        levelController.planButton.SetActive(false);
        levelController.PauseGame();
        levelController.DeleteHelpers();
        GameObject planMenu = Instantiate(PlanMenuPrefab);
        planMenu.transform.SetParent(transform);
        PlanMenuController.OnClickCompute += ShowDomainPDDL;
        PlanMenuController.OnClickCancel += FinishWithPaper2;
        GetProblem();
    }

    private void GetProblem()
    {
        problemThreadState = ThreadState.Running;
        Status.SetText(GetStatus());

        LevelState ls = new LevelState(gameObject, levelController.level);
        string pddl = ls.ToPDDL();
        ThreadJob thread = new ProblemThread(pddl, levelController.level, LevelController.domain.text);
        thread.Start();
        thread.OnThreadComplete += ProblemThreadCallback;
        thread.OnThreadAbort += Abort;
    }

    private void ShowDomainPDDL(PlanMenuController planMenu)
    {
        planMenuState = PlanMenuState.Domain;
        Status.SetText(GetStatus());

        PlanMenuController.OnClickCompute -= ShowDomainPDDL;
        _plannerFunction = planMenu.GetPlannerFunction();
        _plannerType = planMenu.GetPlanner();
        _useNovelty = planMenu.UseNovelty();

        GameObject paper = Instantiate(PaperPrefab);
        paper.transform.localPosition += new Vector3(0, 3, 0);
        paper.transform.FindChild("Paper Object").localEulerAngles -= new Vector3(90, 0, 0);
        paper.transform.FindChild("Paper Object").localScale = Vector3.one * 5;
        paper.transform.FindChild("Paper Object").localPosition += new Vector3(0, 0, -10);
        paper.GetComponentInChildren<PaperResize>().SetText(LevelController.domain.text);
        PaperController.OnClickDone += ShowProblemPDDL;

        if (_plannerFunction == "NextState" || _plannerFunction == "PlanGraph")
            if (problemThreadState == ThreadState.Complete)
                GetNextState();
    }

    private void ShowProblemPDDL()
    {
        planMenuState = PlanMenuState.Problem;
        Status.SetText(GetStatus());

        PaperController.OnClickDone -= ShowProblemPDDL;
        LevelState ls = new LevelState(gameObject, levelController.level);
        string pddl = ls.ToPDDL();

        GameObject paper = Instantiate(PaperPrefab);
        paper.transform.localPosition += new Vector3(0, 3, 0);
        paper.transform.FindChild("Paper Object").localEulerAngles -= new Vector3(90, 0, 0);
        paper.transform.FindChild("Paper Object").localScale = Vector3.one * 5;
        paper.transform.FindChild("Paper Object").localPosition += new Vector3(0, 0, -10);
        paper.GetComponentInChildren<PaperResize>().SetText(pddl);
        PaperController.OnClickDone += FinishWithPaper;
    }

    private void FinishWithPaper()
    {
        if (_plannerFunction == "NextState" || _plannerFunction == "PlanGraph")
        {
            planMenuState = PlanMenuState.Wait;
            if (computeThreadState == ThreadState.Complete)
                planMenuState = PlanMenuState.Complete;
        }
        else if (_plannerFunction == "Solution")
        {
            planMenuState = PlanMenuState.Computing;
            StartFindingSolution();
        }
        Status.SetText(GetStatus());

        PaperController.OnClickDone -= FinishWithPaper;
    }

    private void FinishWithPaper2()
    {
        planMenuState = PlanMenuState.Initial;
        problemThreadState = ThreadState.Initial;
        computeThreadState = ThreadState.Initial;

        PlanMenuController.OnClickCancel -= FinishWithPaper2;
        PlanMenuController.OnClickCompute -= ShowDomainPDDL;
        levelController.ResumeGame();
        levelController.planButton.SetActive(true);
    }

    private void ProblemThreadCallback(ThreadJob thread)
    {
        problemThreadState = ThreadState.Complete;
        Status.SetText(GetStatus());

        if (thread is ProblemThread)
        {
            ProblemThread pThread = (ProblemThread)thread;
            _ssProblem = pThread.GetStateSpaceProblem();
            Debug.Log(_ssProblem);
        }
        thread.ResetEventSubscriptions();

        if (_plannerFunction == "NextState")
        {
            if (planMenuState == PlanMenuState.Domain
            || planMenuState == PlanMenuState.Problem
            || planMenuState == PlanMenuState.Wait)
                GetNextState();
        }
        else if (_plannerFunction == "PlanGraph")
            GetNextState();
    }

    private void GetNextState()
    {
        computeThreadState = ThreadState.Running;
        Status.SetText(GetStatus());

        ThreadJob thread = new PlannerThread(_ssProblem, _plannerFunction, _plannerType, _useNovelty);
        thread.Start();
        thread.OnThreadComplete += PlannerCallback;
        thread.OnThreadAbort += Abort;

        if (_plannerFunction == "PlanGraph")
        {
            _hThread = (PlannerThread)thread;
            levelController.pgLevel = 0;
            levelController.pgLevelTimer = 0;
        }
    }

    // Probably just need to use GetNextState (and rename function while you're at it)
    private void StartFindingSolution()
    {
        computeThreadState = ThreadState.Running;
        Status.SetText(GetStatus());

        ThreadJob thread = new PlannerThread(_ssProblem, _plannerFunction, _plannerType, _useNovelty);
        thread.Start();
        thread.OnThreadComplete += PlannerCallback;
        thread.OnThreadAbort += Abort;
        _hThread = (PlannerThread)thread;
    }

    private void Abort(ThreadJob thread)
    {
        thread.ResetEventSubscriptions();
    }

    private void PlannerCallback(ThreadJob thread)
    {
        computeThreadState = ThreadState.Complete;
        if (planMenuState == PlanMenuState.Wait)
            planMenuState = PlanMenuState.Complete;
        if (planMenuState == PlanMenuState.Computing)
            planMenuState = PlanMenuState.Complete;
        Status.SetText(GetStatus());

        _hThread = (PlannerThread)thread;
        thread.ResetEventSubscriptions();
        planButtonActive = true;
    }

    private string GetStatus()
    {
        string result = "";

        if (planMenuState == PlanMenuState.Menu)
            result += "Showing Plan Menu... ";
        else if (planMenuState == PlanMenuState.Domain)
            result += "Showing Domain PDDL... ";
        else if (planMenuState == PlanMenuState.Problem)
            result += "Showing Problem PDDL... ";
        else
            result += "Waiting... ";

        if (problemThreadState == ThreadState.Running)
            result += "Loading Problem... ";

        if (computeThreadState == ThreadState.Running)
            result += "Loading Next Step...";

        if (planMenuState == PlanMenuState.Complete)
            result = "Showing Next Step...";

        return result;
    }

    public PlannerThread GetThread()
    {
        if (planMenuState != PlanMenuState.Complete)
            return null;

        return _hThread;
    }

    public void DeleteThread()
    {
        _hThread = null;
    }

    public bool GetPlannerStateChanged()
    {
        if (planMenuState != PlanMenuState.Computing)
            return false;

        if (_previousNode == _hThread.GetCurrentNode())
            return false;

        return true;
    }

    private void Update()
    {
        if (_plannerFunction != "PlanGraph")
            if (GetPlannerStateChanged())
            {
                _previousNode = _hThread.GetCurrentNode();
                Debug.Log(_hThread.GetCurrentNode());
                levelController.ConvertSSNodeToGhosts(_previousNode);
            }

        if (planButtonActive)
        {
            levelController.planButton.SetActive(true);
            planButtonActive = false;
        }

        if (Input.GetKeyDown(KeyCode.Escape))
            StateSpaceSearchET.ShutThisSuckaDown = true;
    }
}