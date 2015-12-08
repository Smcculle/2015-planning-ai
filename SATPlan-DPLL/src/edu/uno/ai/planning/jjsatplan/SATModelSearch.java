package edu.uno.ai.planning.jjsatplan;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.ss.TotalOrderPlan;

import java.util.*;

class SATModelSearch extends Search {

	public SATModelSearch(Problem problem) {
		super(problem);
	}
	
	private int nodesExpanded;
	private int nodesVisited;
	private int limit;
	
	@Override
	public int countVisited() {
		return nodesVisited;
	}

	@Override
	public int countExpanded() {
		return nodesExpanded;
	}

	@Override
	public void setNodeLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public Plan findNextSolution() {
		for (int counter = 1; counter <= 20; counter++) {
			SATEncoding encoding = new SATEncoding(problem);
			SATConjunction cnf = encoding.encode(counter);

//			System.out.println(cnf.toString());

			ArrayList<BooleanVariable> mainList = new ArrayList<>();
			ArrayList<ArrayList<BooleanVariable>> cnfBV = cnf.convert();
			SATProblem satProblem = new SATProblem(cnfBV, mainList);

			List<BooleanVariable> solution =
					SATSolver.getModel(satProblem, new ArrayList<>(), this.limit - (nodesExpanded > nodesVisited ? nodesExpanded : nodesVisited));

			nodesVisited += SATSolver.nodesVisited;
			nodesExpanded += SATSolver.nodesExpanded;

			if((nodesExpanded > limit) || (nodesVisited > limit))
				throw new SearchLimitReachedException();

			if (solution != null) {
//				printString(solution);
				return convertSATPlanResultToSolution((ArrayList<BooleanVariable>) solution, encoding.getEncodingModel());
			}
		}
		return new TotalOrderPlan();
	}

	public Plan convertSATPlanResultToSolution(ArrayList<BooleanVariable> satisfiedModel, Map<String, CNFEncodingModel> encodingModel){
		HashMap<Integer, Step> setOfSteps = new HashMap<>();
		for(BooleanVariable bv : satisfiedModel) {
			if (encodingModel.containsKey(bv.name) && !bv.negation) {
				CNFEncodingModel step = encodingModel.get(bv.name);
				setOfSteps.put(step.time, step.step);
			}
		}
		return getOrderedPlan(setOfSteps);
	}

	public void printString(List<BooleanVariable> model){
		for (BooleanVariable bv : model){
			System.out.println(bv);
		}
	}

	public TotalOrderPlan getOrderedPlan(HashMap<Integer, Step> steps){
		TotalOrderPlan plan = new TotalOrderPlan();
		for (int counter = 0; counter < steps.size(); counter++){
			Step planToAdd = steps.get(new Integer(counter));
			plan = plan.addStep(planToAdd);
		}
		return plan;
	}
}
