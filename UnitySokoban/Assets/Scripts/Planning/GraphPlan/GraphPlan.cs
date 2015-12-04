using GraphPlanProject.Util;
using PlanGraphProject;
using Planning;
using Planning.Logic;
using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GraphPlanProject
{
    public class GraphPlan
    {
        public State CurrentState;

        private PlanGraph _planGraph;
        private Problem _problem;
        private List<Step> _plan;
        private IEnumerator<State> findPlan;

        public GraphPlan(PlanGraph planGraph, Problem problem)
        {
            _planGraph = planGraph;
            _problem = problem;
            CurrentState = null;
        }

        public State NextState()
        {
            if (findPlan == null)
                findPlan = FindPlan().GetEnumerator();

            if (!findPlan.MoveNext())
            {
                findPlan = FindPlan().GetEnumerator();
                return State.Null;
            }
            return findPlan.Current;
        }

        public IEnumerable<State> FindPlan()
        {
            State.PlanGraph.level = _planGraph.CountLevels() - 1;
            yield return State.PlanGraph;

            List<Literal> goal = ConversionUtil.expressionToLiterals(_problem.goal);
            State.Goals.list = ListUtil.CreateGenericList(goal);
            State.Goals.level = _planGraph.CountLevels() - 1;
            yield return State.Goals;

            while (!_planGraph.isLeveledOff())
            {
                int n = _planGraph.CountLevels();

                IEnumerator<State> findPlan2 = FindPlan(goal, n - 1).GetEnumerator();
                State state = State.Null;
                while (findPlan2.MoveNext())
                {
                    state = findPlan2.Current;
                    yield return state;
                    if (state == State.Complete)
                        yield break;
                }

                _planGraph.extend();
                State.Extend.level = _planGraph.CountLevels() - 1;
                yield return State.Extend;
            }
            State.Fail.level = _planGraph.CountLevels() - 1;
            yield return State.Fail;
        }

        private IEnumerable<State> FindPlan(List<Literal> goal, int level)
        {
            if (level == 0)
            {
                State.LevelCheckZero.level = 0;
                yield return State.LevelCheckZero;
                if (ContainsGoalAtRoot(goal))
                {
                    _plan = new List<Step>();
                    yield return State.Complete;
                }
                else
                    yield return null;
            }

            PlanGraphStep[][] setOfSteps = GetAllSetOfSteps(goal, level);
            foreach (PlanGraphStep[] steps in setOfSteps)
            {
                State.NewSteps.list = new List<object>(steps);
                State.NewSteps.level = level;
                yield return State.NewSteps;

                List<Literal> newGoal = GetNewGoal(steps);
                State.NewGoals.list = ListUtil.CreateGenericList(newGoal);
                State.NewGoals.level = level - 1;
                yield return State.NewGoals;

                IEnumerator<State> findPlan3 = FindPlan(newGoal, level - 1).GetEnumerator();
                while (findPlan3.MoveNext())
                {
                    State state = findPlan3.Current;
                    if (state == State.Complete)
                    {
                        foreach (PlanGraphStep step in steps)
                            if (!step.isPersistent())
                                _plan.Add(step.getStep());
                        state.list = ListUtil.CreateGenericList(_plan);
                        yield return state;
                        yield break;
                    }
                    else
                        yield return state;
                }
            }
            yield return State.NextIteration;
        }

        private bool ContainsGoalAtRoot(List<Literal> goal)
        {
            foreach(Literal literal in goal)
            {
                PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
                if (!_planGraph.ExistsAtLevel(pgLiteral, 0))
                    return false;
            }
            return true;
        }

        private PlanGraphStep[][] GetAllSetOfSteps(List<Literal> goal, int level)
        {
            Dictionary<PlanGraphLiteral, List<PlanGraphStep>> stepsWithEffect =
                new Dictionary<PlanGraphLiteral, List<PlanGraphStep>>();
            foreach (Literal literal in goal)
            {
                PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
                foreach (PlanGraphStep step in pgLiteral.getParentNodes())
                {
                    if (_planGraph.existsAtLevel(step, level))
                        if (!stepsWithEffect.ContainsKey(pgLiteral))
                        {
                            List<PlanGraphStep> steps = new List<PlanGraphStep>();
                            steps.Add(step);
                            stepsWithEffect.Add(pgLiteral, steps);
                        }
                        else
                        {
                            stepsWithEffect[pgLiteral].Add(step);
                        }
                }
            }

            List<List<PlanGraphStep>> setOfSteps = new List<List<PlanGraphStep>>();
            List<PlanGraphLiteral> keys = new List<PlanGraphLiteral>(stepsWithEffect.Keys);
            setOfSteps = GetAllSetOfSteps(keys, 0, null, setOfSteps, stepsWithEffect, level);
            PlanGraphStep[][] result = new PlanGraphStep[setOfSteps.Count][];
            for (int i = 0; i < setOfSteps.Count; i++)
                result[i] = setOfSteps[i].ToArray<PlanGraphStep>();
            return result;
        }

        private List<List<PlanGraphStep>> GetAllSetOfSteps(List<PlanGraphLiteral> keys, 
            int v, List<PlanGraphStep> steps, List<List<PlanGraphStep>> setOfSteps,
            Dictionary<PlanGraphLiteral, List<PlanGraphStep>> stepsWithEffect, int level)
        {
            if (keys == null) return null;
            if (steps == null) steps = new List<PlanGraphStep>();

            if (v >= keys.Count)
            {
                if (AllStepsNonMutex(steps, level))
                {
                    List<PlanGraphStep> newSteps = RemoveDuplicates(steps);
                    setOfSteps.Add(newSteps);
                }
                return setOfSteps;
            }

            foreach (PlanGraphStep step in stepsWithEffect[keys[v]])
            {
                steps.Add(step);
                setOfSteps = GetAllSetOfSteps(keys, v + 1, new List<PlanGraphStep>(steps), setOfSteps, stepsWithEffect, level);
                steps.RemoveAt(steps.Count - 1);
            }
            return setOfSteps;
        }

        private List<PlanGraphStep> RemoveDuplicates(List<PlanGraphStep> steps)
        {
            List<PlanGraphStep> newSteps = new List<PlanGraphStep>(steps);
            for (int i = newSteps.Count - 1; i >= 0; i--)
                for (int j = newSteps.Count - 1; j >= 0; j--)
                    if (i != j)
                        if (newSteps[i].Equals(newSteps[j]))
                        {
                            newSteps.RemoveAt(j);
                            break;
                        }
            return newSteps;
        }

        private List<Literal> GetNewGoal(PlanGraphStep[] steps)
        {
            List<Literal> literals = new List<Literal>();
            foreach (PlanGraphStep step in steps)
                foreach (PlanGraphLiteral pgLiteral in step.getParentNodes())
                    if (!literals.Contains(pgLiteral.getLiteral()))
                        literals.Add(pgLiteral.getLiteral());

            return literals;
        }

        private bool AllStepsNonMutex(List<PlanGraphStep> steps, int level)
        {
            foreach (PlanGraphStep step in steps)
                foreach (PlanGraphStep otherStep in steps)
                    if (step != otherStep)
                        if (_planGraph.IsMutexAtLevel(step, otherStep, level))
                            return false;

            return true;
        }

        public class State
        {
            public enum Name { Null, PlanGraph, Goals, LevelCheckZero, Complete, NewSteps, NewGoals, NextIteration, Extend, Fail }
            static public State Null = new State(Name.Null);
            static public State PlanGraph = new State(Name.PlanGraph);
            static public State Goals = new State(Name.Goals);
            static public State LevelCheckZero = new State(Name.LevelCheckZero);
            static public State Complete = new State(Name.Complete);
            static public State NewSteps = new State(Name.NewSteps);
            static public State NewGoals = new State(Name.NewGoals);
            static public State NextIteration = new State(Name.NextIteration);
            static public State Extend = new State(Name.Extend);
            static public State Fail = new State(Name.Fail);

            public Name name;
            public List<Object> list;
            public int level = -1;

            public State(Name name)
            {
                this.name = name;
            }

            public override string ToString()
            {
                return name.ToString();
            }
        }
    }
}
