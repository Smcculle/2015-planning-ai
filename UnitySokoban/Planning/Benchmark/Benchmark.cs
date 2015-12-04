using BreadthFirstSearch;
using FastForward;
using GraphPlanSGW;
using HeuristicSearchPlannerSGW;
using IterativeWidthPlanner;
using PlanGraphSGW;
using Planning;
using Planning.IO;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace Benchmark
{
    class Benchmark
    {
        static void Main(string[] args)
        {
            string domainFile = "Sokoban_domain.txt";
            string problemFile = "Sokoban_Problem_0.txt";
            string domainString = ReadFile(domainFile);
            string problemString = ReadFile(problemFile);
            Problem problem = PDDLReader.GetProblem(domainString, problemString);
            StateSpaceProblem ssProblem = new StateSpaceProblem(problem);

            //HSPIWPlanner hsp = new HSPIWPlanner();
            //HeuristicSearch iw = hsp.makeSearch(ssProblem);
            //Plan plan = iw.findNextSolution();

            //FastForwardSearch ff = new FastForwardSearch(ssProblem);
            //Plan plan = ff.findNextSolution();
            //FastForwardSearch ff = new FFIWPlanner(ssProblem);
            //Plan plan = ff.findNextSolution();


            HeuristicSearchPlanner hsp = new HeuristicSearchPlanner();
            HeuristicSearch hspSearch = hsp.makeSearch(ssProblem);
            //var nextStates = hspSearch.GetNextStates();
            Plan plan = hspSearch.findNextSolution();
            ////HSPlanner hsp = new HSPlanner(ssProblem);
            //Plan plan = hsp.findNextSolution();
        }

        private static string ReadFile(string filename)
        {
            using (StreamReader streamReader = new StreamReader(filename))
                return streamReader.ReadToEnd();
        }
    }
}
