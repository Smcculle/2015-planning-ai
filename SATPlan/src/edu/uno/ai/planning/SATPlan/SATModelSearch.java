package edu.uno.ai.planning.SATPlan;

import java.util.ArrayList;
import java.util.List;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * Represents a search space whose
 * {@link edu.uno.ai.planning.pop.PartialOrderNodes nodes} are plans
 * and whose edges are orderings?
 *
 * @author
 */
public class SATModelSearch extends Search {

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
		ArrayList<BooleanVariable> result = Solve(this.problem);	
		
		
		if (result.isEmpty())
			return null;
		return null;
	}
	
	public ArrayList<BooleanVariable> Solve(Problem problem){
		int maxTimeForSAT = 4;
		
		ArrayList<BooleanVariable> result = new ArrayList<BooleanVariable>();
		
		ImmutableArray<Step> allSteps = (new StateSpaceProblem(problem)).steps;
		
		CNFEncoding encoding = new CNFEncoding(satSolver);
		
		ArrayList<ArrayList<BooleanVariable>> cnf = 
				new ArrayList<ArrayList<BooleanVariable>>();
		
		for (int counter = 1; counter <= maxTimeForSAT ; counter++){	
			cnf = encoding.encode(
					problem.initial.toExpression(), allSteps, problem.goal, counter);					
		
			ArrayList<BooleanVariable> mainList = new ArrayList<BooleanVariable>();
			
			SATProblem problemo = new SATProblem(cnf, mainList);
			
			List<BooleanVariable> solution = satSolver.getModel(problemo);
			nodesVisited = satSolver.countVisited();
			nodesExpanded = satSolver.countExpanded();
			
			if(solution != null){
				// System.out.println("\nSolution is:");
				for(BooleanVariable BV : solution){
					if(BV.value == Boolean.TRUE){
						if (!BooleanVariable.containsEqualBooleanVariable(result, BV)){
							result.add(BV);
							System.out.println((BV.negation? " not " : "") + BV.name + " = " + BV.value);
						}
					}
				}			
				break;
			}else {}
				// System.out.println("\nNo solution");
		}//End of for statement
		return result;
	}//End of Solve
	
}
