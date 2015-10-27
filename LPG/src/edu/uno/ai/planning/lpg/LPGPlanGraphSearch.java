package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.HashSet;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.ss.TotalOrderPlan;

public class LPGPlanGraphSearch extends Search {
	
	/** The problem being solved */
	public final Problem problem;
	
	/** Updates noise factor after this number of steps has elapsed */ 
	private final static int NOISE_WINDOW = 50;
	
	/** noise factor updated if new variance differs by more than the threshold */
	private final static double VARIANCE_THRESHOLD = 0.5;
	
	/** Default value for noise factor */ 
	private final static double DEFAULT_NOISE_FACTOR = 0.1;
	
	private int nodesExpanded;

	private int nodesVisited;
	
	private int steps; 
	
	private int restarts;
	
	/** noise factor increases or decreases based on variance of # of inconsistencies 
	 * in the last NOISE_WINDOW number of steps */
	private double noiseFactor;
	
	/** compare lastVariance with current variance after NOISE_WINDOW steps */
	private double lastVariance;
	
	/** last noiseWindow number of inconsistencies recorded for variance calculation*/
	private int[] numInconsistency;

	/** The search limit on visited nodes (-1 if no limit) */
	int limit = -1;
	
	public LPGPlanGraphSearch(Problem problem) {
		super(problem);
		this.problem = problem;
		numInconsistency = new int[NOISE_WINDOW];
		noiseFactor = DEFAULT_NOISE_FACTOR;
	}
	
	/**
	 * Creates an empty LPGPlanGraph and iterates on that graph to find a solution up to 
	 * maxSteps, restarting if no solution is found until maxRestarts is reached
	 *  
	 * @param maxSteps Max number of steps for each search
	 * @param maxRestarts Max times the search will restart from empty LPGPlanGraph 
	 * @return A totally ordered plan representing a solution or null if none is found.  
	 * 
	 * TODO:  implement needed methods 
	 */
	private Plan findPlan(int maxSteps, int maxRestarts)
	{
		TotalOrderPlan plan = null;
		
		outer:
		for (int i = 0; i < maxRestarts; i++) {
			LPGPlanGraph actionGraph = new LPGPlanGraph(problem);
			for(int j = 0; j < maxSteps; j++){
				if(actionGraph.isSolution()){
					plan = actionGraph.getTotalOrderPlan();
					break outer;
				}
				
				PlanGraphStep inconsistency = actionGraph.getInconsistency();
				numInconsistency[i % NOISE_WINDOW] = actionGraph.getInconsistencyCount();
				if(i % NOISE_WINDOW == 0)
					updateNoiseFactor();
				
				ArrayList<LPGPlanGraph> neighborhood = actionGraph.getNeighborhood(inconsistency);
				double[] graphQuality = evaluateNeighborhood(neighborhood);
				actionGraph = chooseNewActionGraph(neighborhood, graphQuality, actionGraph);
				
			}
		}
		return plan;
	}
	
	/**
	 * Chooses a new action graph from the neighborhood based on graphQuality.  Chooses one with quality
	 * that is not worse than the current graph; if there are multiple graphs that are not worse, 
	 * chooses one randomly.  If every graph in the neighborhood is worse, uses noise parameter to decide
	 * between choosing a random graph or the best available.  
	 * 
	 * @param neighborhood Potential new LPGPlanGraphs to choose between
	 * @param graphQuality Quality of each LPGPlanGraph in consideration
	 * @param actionGraph The current LPGPlanGraph that is being replaced
	 * @return A new actionGraph chosen from the neighborhood.
	 * 
	 *   TODO:  All
	 */
	private LPGPlanGraph chooseNewActionGraph(
			ArrayList<LPGPlanGraph> neighborhood, double[] graphQuality,
			LPGPlanGraph actionGraph) {
		
		return null;
	}

	/**
	 * Estimates the quality for each LPGPlanGraph in the neighborhood
	 * 
	 * @param neighborhood The list of new LPGPlanGraphs in consideration
	 * 
	 * @return an array of values based on the number of inconsistencies, estimated search steps, 
	 * and overall action cost.   
	 * 
	 * TODO:  All 
	 */
	private double[] evaluateNeighborhood(ArrayList<LPGPlanGraph> neighborhood) {
		double [] graphQuality = new double[neighborhood.size()];
		
		for (int i = 0; i < neighborhood.size(); i++) {
			graphQuality[i] = calculateQuality(neighborhood.get(i));
						
		}
		
		return null;
	}
	
	/**
	 * Calculates quality for the given graph through relaxed plan.  
	 * 
	 * @param graph The LPGPlanGraph to calculate the quality for
	 * @return Quality of the graph based on number of inconsistencies, estimated search steps, and 
	 * cost of actions.  
	 * 
	 * TODO:  All
	 */
	private double calculateQuality(LPGPlanGraph graph){
		calculateRelaxedPlan(graph);
		return 0;
	}
	
	/**
	 * Returns a set of actions and a number estimating a minimal set of actions required 
	 * to achieve a set of goal facts to be used in heuristic estimation.
	 * 
	 *   TODO:  All
	 * @param graph 
	 */
	private void calculateRelaxedPlan(LPGPlanGraph graph){
		
	}

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
		return findPlan(1000, 10);
	}
	
	/**
	 * Increases noise factor if current variance is not significantly different 
	 * than lastVariance since the last noiseWindow number of steps, or sets it to default.
	 * 
	 * TODO: getVariance
	 */
	private void updateNoiseFactor(){
		double variance = getVariance();
		
		if( (variance - lastVariance) < VARIANCE_THRESHOLD )
			this.noiseFactor *= 1.25;
		else
			this.noiseFactor = DEFAULT_NOISE_FACTOR;
		
		this.lastVariance = variance;
	}
	
	/** Calculates and returns variance of numInconsistency */
	private double getVariance(){
		return 0;
	}

}
