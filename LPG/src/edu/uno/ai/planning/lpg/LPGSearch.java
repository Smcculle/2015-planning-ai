package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.ss.TotalOrderPlan;

public class LPGSearch extends Search {
	
	
	/** The number of times we will restart if not finding a solution */
	private final static int restarts = 4;
	
	/** Updates noise factor after this number of steps has elapsed */ 
	private final static int NOISE_WINDOW = 50;
	
	/** noise factor updated if new variance differs by more than the threshold */
	private final static double VARIANCE_THRESHOLD = 0.5;
	
	/** Default value for noise factor */ 
	private final static double DEFAULT_NOISE_FACTOR = 0.1;

	/** The problem being solved */
	private final Problem problem;
	
	/** The subgraph we are working on */
	private LPGActionGraph actionGraph;
	
	/** Total number of expanded nodes */
	private int expanded;
	
	/** Total number of visited nodes */
	private int visited;
	
	/** noise factor increases or decreases based on variance of # of inconsistencies 
	 * in the last NOISE_WINDOW number of steps */
	private double noiseFactor;
	
	/** compare lastVariance with current variance after NOISE_WINDOW steps */
	private double lastVariance;
	
	/** last noiseWindow number of inconsistencies recorded for variance calculation*/
	private int[] numInconsistency;

	/** The search limit on visited nodes (-1 if no limit) */
	private int limit = -1;
	
	public LPGSearch(Problem problem) {
		super(problem);
		this.problem = problem;
		numInconsistency = new int[NOISE_WINDOW];
		noiseFactor = DEFAULT_NOISE_FACTOR;
		actionGraph = new LPGActionGraph(problem);
	}
	
	/**
	 * Creates an empty LPGPlanGraph and iterates on that graph to find a solution up to 
	 * maxSteps, restarting if no solution is found until maxRestarts is reached
	 *  
	 * @param maxSteps Max number of steps for each search
	 * @param maxRestarts Max times the search will restart from empty LPGPlanGraph 
	 * @return A totally ordered plan representing a solution or null if none is found.  
	 * 
	 * TODO:  implement needed methods, make private
	 */
	public Plan findPlan(int maxSteps, int maxRestarts)
	{
		TotalOrderPlan plan = new TotalOrderPlan();
		
		outer:
		for (int i = 0; i < maxRestarts; i++) {
			actionGraph = new LPGActionGraph(problem);
			for(int j = 0; j < maxSteps; j++){
				if(actionGraph.isSolution()){
					plan = actionGraph.getTotalOrderPlan(plan);
					break outer;
				}
				
				LPGInconsistency inconsistency = actionGraph.chooseInconsistency();
				numInconsistency[i % NOISE_WINDOW] = actionGraph.getInconsistencyCount();
				if(i % NOISE_WINDOW == 0)
					updateNoiseFactor();
				
				List<LPGActionGraph> neighborhood = actionGraph.makeNeighborhood(inconsistency);
				double[] graphQuality = {0.0}; // = evaluateNeighborhood(neighborhood);
				actionGraph = chooseNewActionGraph(neighborhood, graphQuality);
				
				expanded += neighborhood.size();
				visited++;
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
	private LPGActionGraph chooseNewActionGraph(List<LPGActionGraph> neighborhood, double[] graphQuality) {
		
		Random rand = new Random();
		int next = rand.nextInt(neighborhood.size());
		
		return neighborhood.get(next);
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
		return visited;
	}

	@Override
	public int countExpanded() {
		return expanded;
	}

	@Override
	public void setNodeLimit(int limit) {
		this.limit = limit;

	}

	@Override
	public Plan findNextSolution() {
		return findPlan(limit/restarts, restarts);
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

