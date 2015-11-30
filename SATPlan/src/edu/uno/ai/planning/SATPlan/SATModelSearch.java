package edu.uno.ai.planning.SATPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import edu.uno.ai.planning.util.ImmutableArray;

class SATModelSearch extends Search {

	/** The Partial Order problem being solved */
	public ArrayList<BooleanVariable> satisfiableModel;

	protected ISATSolver satSolver;
	
	public SATModelSearch(Problem problem, ISATSolver satSolver) {
		super(problem);
		this.satSolver = satSolver;
		//this.satisfiableModel = SATPlan.Solve(problem);
	}
	
	private int nodesExpanded;

	private int nodesVisited;
	
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
		//root.setNodeLimit(limit);
	}

	@Override
	public Plan findNextSolution() {
		return Solve(problem);
	}
	
	public Plan Solve(Problem problem){
		int maxTimeForSAT = 3;
		
		ArrayList<BooleanVariable> result = new ArrayList<BooleanVariable>();
		
		ImmutableArray<Step> allSteps = (new StateSpaceProblem(problem)).steps;
//		ImmutableArray<Operator> allLiters = (new StateSpaceProblem(problem))

//		System.out.println("All constants are ");
//		for(Operator constant : allLiters){
//			System.out.println(constant);
//		}


		
		CNFEncoding encoding = new CNFEncoding(satSolver);
		
		ArrayList<ArrayList<BooleanVariable>> cnf;
		
		for (int counter = 1; counter <= maxTimeForSAT ; counter++){
			cnf = encoding.encode(problem.initial.toExpression(), allSteps, problem.goal, counter);

			System.out.println(encoding.cnfToString(cnf));

			ArrayList<BooleanVariable> mainList = new ArrayList<BooleanVariable>();
			
			SATProblem satProblem = new SATProblem(cnf, mainList);
			
			List<BooleanVariable> solution = satSolver.getModel(satProblem);
			nodesVisited = satSolver.countVisited();
			nodesExpanded = satSolver.countExpanded();
			
			if(solution != null){
				// System.out.println("\nSolution is:");
				for(BooleanVariable BV : solution){
					if(BV.value == Boolean.TRUE){
//						if (!BooleanVariable.containsEqualBooleanVariable(result, BV)){
							result.add(BV);
							System.out.println((BV.negation? " not " : "") + BV.name + " = " + BV.value);
//						}
					}
				}
				break;
			}else {}
				// System.out.println("\nNo solution");
		}//End of for statement
		System.out.println("Initial condition is " + problem.initial);
		System.out.println("Goal condition is " + problem.goal);
		if (result.isEmpty()) return null;
		else return convertSATPlanResultToSolution(result, encoding.getEncodingModel());
	}//End of Solve

	public Plan convertSATPlanResultToSolution(ArrayList<BooleanVariable> satisfiedModel, Map<String, CNFEncodingModel> encodingModel){
		System.out.println("Converting the variables back to the plan");
		TotalOrderPlan plan = new TotalOrderPlan();

//		Step setp = encodingModel.get("(moveToTable a b) - 0").step;
//		plan = plan.addStep(setp);
		for(BooleanVariable bv : satisfiedModel)
			if (encodingModel.containsKey(bv.name) && !bv.negation){
				Step step = encodingModel.get(bv.name).step;
				System.out.println("Pre condition is " + step.precondition);
				plan = plan.addStep(step);
				System.out.println(step + " at time " + encodingModel.get(bv.name).time);
				System.out.println("Effect is " + step.effect);
			}
		return plan;
	}
	
}
