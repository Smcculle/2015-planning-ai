using PlanGraphSGW;
using Planning;
using Planning.Logic;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FastForward
{
    public class FastForwardHeuristic : PlanGraph
    {

        private List<Literal> goalLiterals;

        public FastForwardHeuristic(StateSpaceProblem problem)
            : base(problem, false)              //false means do not evaluate mutexes
        {
            this.goalLiterals = Util.expressionToLiterals(problem.goal);
        }

        public int hValue(State state)
        {
            initialize(state);
            while (!goalAchieved() && !hasLeveledOff())
            {
                extend();
            }
            if (!goalAchieved())
            {
                return int.MaxValue;
            }

            int hValue = 0;
            List<Literal>[] levelGoals = new List<Literal>[Size()];
            for (int i = 0; i < levelGoals.Length; i++)
            {
                levelGoals[i] = new List<Literal>();
            }

            //start at end level
            int currentLevel = Size() - 1;
            //add all goals to level goals of end level to initialize traceback
            levelGoals[currentLevel].AddRange(goalLiterals);

            while (currentLevel > 0)
            {
                foreach (Literal goal in levelGoals[currentLevel])
                {
                    //for each goal in level i, if goal exists in i-1, add goal to be achieved in i-1
                    if (literalMap[goal].exists(currentLevel - 1))
                    {
                        levelGoals[currentLevel - 1].Add(goal);
                        //if goal does not exist in i-1, find action that produces goal
                        //and add its preconditions as goals to be achieved in i-1
                    }
                    else
                    {
                        hValue++;
                        var enumerator = literalMap[goal].getProducers(currentLevel).GetEnumerator();
                        while (enumerator.MoveNext())
                            foreach (LiteralNode precondition in enumerator.Current.getPreconditions(currentLevel - 1))
                            {
                                levelGoals[currentLevel - 1].Add(precondition.literal);
                            }
                    }
                }
                currentLevel--;
            } //stop at first level
            return hValue;
        }
    }
}
