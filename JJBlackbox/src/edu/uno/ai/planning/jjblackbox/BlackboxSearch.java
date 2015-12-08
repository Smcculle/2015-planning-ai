package edu.uno.ai.planning.jjblackbox;


import edu.uno.ai.planning.*;
import edu.uno.ai.planning.jjsatplan.BooleanVariable;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.jjsatplan.*;

import java.util.*;

public class BlackboxSearch extends Search {
    public Problem problem;
    protected int limit = 0;
    protected int nodeVisited = 0;
    protected int nodeExpanded = 0;

    public BlackboxSearch(Problem problem) {
        super(problem);
        this.problem = problem;
    }

    @Override
    public int countVisited() {
        return nodeVisited;
    }

    @Override
    public int countExpanded() {
        return nodeExpanded;
    }

    @Override
    public void setNodeLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public Plan findNextSolution() {

        StateSpaceProblem problem1 =  new edu.uno.ai.planning.ss.StateSpaceProblem(problem);
        PlanGraph planGraph = new PlanGraph(problem1, true);
        planGraph.initialize(problem1.initial);

        List<BooleanVariable> satisfiedModel;
        while(!planGraph.goalAchieved())
            planGraph.extend();
        do {
            PlanGraphEncoding encoding = new PlanGraphEncoding();
            SATConjunction conjunction = encoding.encode(planGraph);
            
            ArrayList<BooleanVariable> mainList = new ArrayList<>();
            SATProblem satProblem = new SATProblem(conjunction.convert(), mainList);

            satisfiedModel = SATSolver.getModel(satProblem, new ArrayList<>(), limit - (this.nodeVisited > this.nodeExpanded ? this.nodeVisited : this.nodeExpanded));
            this.nodeVisited += SATSolver.nodesVisited;
            this.nodeExpanded += SATSolver.nodesExpanded;

            if (this.nodeVisited > this.limit || this.nodeExpanded > this.limit)
                throw new SearchLimitReachedException();

            if (satisfiedModel != null) {
//                printString(satisfiedModel);
                return convertSATPlanResultToSolution(satisfiedModel, encoding.stepEncodings);
            }
            planGraph.extend();
        }while(true);
    }

    public Plan convertSATPlanResultToSolution(List<BooleanVariable> satisfiedModel, Map<String, PlanGraphEncodingModel> encodingModel){
        HashMap<Integer, List<Step>> setOfSteps = new HashMap<>();
        for(BooleanVariable bv : satisfiedModel) {
            if (encodingModel.containsKey(bv.name) && !bv.negation) {
                PlanGraphEncodingModel step = encodingModel.get(bv.name);
                if (setOfSteps.containsKey(step.time)){
                    List<Step> setOfStepsAtThisLevel = setOfSteps.get(step.time);
                    setOfStepsAtThisLevel.add(step.step);
                    setOfSteps.put(step.time, setOfStepsAtThisLevel);
                }
                else{
                    List<Step> setOfStepsAtThisLevel = new ArrayList<>();
                    setOfStepsAtThisLevel.add(step.step);
                    setOfSteps.put(step.time, setOfStepsAtThisLevel);
                }
            }
        }
        return getOrderedPlan(setOfSteps);
    }

    public void printString(List<BooleanVariable> model){
        for (BooleanVariable bv : model){
            if (bv.value && (bv.toString().contains("load") || bv.toString().contains("unload") || bv.toString().contains("fly")))
                System.out.println(bv);
        }
    }

    public ListPlan getOrderedPlan(HashMap<Integer, List<Step>> steps){
        ArrayList<Step> stepsList = new ArrayList<Step>();
        for (int counter = 1; counter <= steps.size(); counter++){
            List<Step> planStepToAdd = steps.get(new Integer(counter));
            if(planStepToAdd != null)
                for(Step step : planStepToAdd)
                    if (!stepsList.contains(step))
                        stepsList.add(step);
        }
        return new ListPlan(stepsList);
    }

    @SuppressWarnings("serial")
	class ListPlan extends ArrayList<Step> implements Plan {
        public ListPlan(Collection<Step> steps) {
            super(steps);
        }
    }
}