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
    public class GraphPlanNoYield
    {
        private PlanGraph _planGraph;
        private Problem _problem;

        public GraphPlanNoYield(PlanGraph planGraph, Problem problem)
        {
            _planGraph = planGraph;
            _problem = problem;
        }

        public List<Step> FindPlan()
        {
            List<Literal> goal = ConversionUtil.expressionToLiterals(_problem.goal);
            List<Step> plan;
            while (!_planGraph.isLeveledOff())
            {
                int n = _planGraph.CountLevels();
                plan = FindPlan(goal, n - 1);
                if (plan != null) return plan;
                _planGraph.extend();
            }
            return null;
        }

        private List<Step> FindPlan(List<Literal> goal, int level)
        {
            if (level == 0)
                if (ContainsGoalAtRoot(goal))
                    return new List<Step>();
                else
                    return null;

            PlanGraphStep[][] setOfSteps = GetAllSetOfSteps(goal, level);
            foreach (PlanGraphStep[] steps in setOfSteps)
            {
                List<Literal> newGoal = GetNewGoal(steps);
                List<Step> plan = FindPlan(newGoal, level - 1);
                if (plan != null)
                {
                    foreach (PlanGraphStep step in steps)
                        if (!step.isPersistent())
                            plan.Add(step.getStep());
                    return plan;
                }
            }
            return null;
        }

        private bool ContainsGoalAtRoot(List<Literal> goal)
        {
            foreach (Literal literal in goal)
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
    }
}
