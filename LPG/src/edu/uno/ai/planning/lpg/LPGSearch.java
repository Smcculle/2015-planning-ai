package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.graphplan.PlanGraph;
import edu.uno.ai.planning.ss.TotalOrderPlan;

public class LPGSearch extends Search {
	
	private final static Random rand = new Random();
	/** The number of times we will restart if not finding a solution */
	private final static int restarts = 300;
	
	/** Updates noise factor after this number of steps has elapsed */ 
	private final static int NOISE_WINDOW = 50;
	
	/** noise factor updated if new variance differs by more than the threshold */
	private final static double VARIANCE_THRESHOLD = 0.25;
	
	/** Default value for noise factor */ 
	private final static double DEFAULT_NOISE_FACTOR = 0.1;

	/** The problem being solved */
	private final Problem problem;
	
	/** Complete plan graph for the problem */
	private final PlanGraph graph;
	
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
		graph = new PlanGraph(problem, true);
		graph.extend();
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
			if(i+1 % 100 == 0)
				graph.extend();
			
			actionGraph = new LPGActionGraph(problem, graph);
			
			for(int j = 0; j < maxSteps; j++){
				if(actionGraph.isSolution()){
					plan = actionGraph.getTotalOrderPlan(plan);
					TotalOrderPlan plan2 = actionGraph.getOrderedTotalPlan(new TotalOrderPlan());
					if (problem.isSolution(plan))
						break outer;
					else if(problem.isSolution(plan2)) {
						plan = plan2;
						break outer;
					}
					else {
						actionGraph = new LPGActionGraph(problem, graph);
					}
				}
				int ic = actionGraph.getInconsistencyCount();
				if (ic == 0) {
					System.out.println(actionGraph.getInconsistencies());
					throw new RuntimeException();
				}

				LPGInconsistency inconsistency = actionGraph.chooseInconsistency();
				numInconsistency[j % NOISE_WINDOW] = actionGraph.getInconsistencyCount();
				if( (j-1) % NOISE_WINDOW == 0)
					updateNoiseFactor();
				
				List<LPGActionGraph> neighborhood = actionGraph.makeNeighborhood(inconsistency);
				
				if(neighborhood != null) {
					actionGraph = chooseNewActionGraph(neighborhood);
					expanded += neighborhood.size();
					visited++;
				}
				
			}
		}
		
		return plan;
	}
	
	/**
	 * Chooses a new action graph from the neighborhood based on graphQuality.  Chooses one with quality
	 * that is not worse (does not increase # of inconsistencies) than the current graph; if there are multiple 
	 * graphs that are not worse, choose one randomly.  If every graph in the neighborhood is worse, uses noise 
	 * parameter to decide between choosing a random graph or the best available.  
	 * 
	 * @param neighborhood Potential new LPGPlanGraphs to choose between
	 * @param graphQuality Quality of each LPGPlanGraph in consideration
	 * @return A new actionGraph chosen from the neighborhood.
	 * 
	 */
	private LPGActionGraph chooseNewActionGraph(List<LPGActionGraph> neighborhood) {
		
		Collections.sort(neighborhood);
		int currentQuality = actionGraph.getGraphQuality();
		int count = 0;
		
		/* count the neighbors with better quality.  They are sorted, so we can break after finding the cutoff */
		for (LPGActionGraph neighbor: neighborhood) {
			if (neighbor.getGraphQuality() == 0) {
					//System.out.println("quality 0");
					neighbor.checkGoals();
			}
			if (neighbor.getGraphQuality() < currentQuality)
				count++;
			else
				break;
		}

		/* if there is only one A-graph that is better, return that */ 
		if( count == 1)
			return neighborhood.get(0);
		
		/* otherwise randomly pick one of the better options */
		else if ( count > 1 ) {
			int next = rand.nextInt(count);
			return neighborhood.get(next);
		}
		
		/* otherwise pick any graph with probability noiseFactor or the best (index 0) with probability 1-noiseFactor */
		else {
			double random = rand.nextDouble();
			
			if ( random < noiseFactor) {
				/* choosing randomly between all options, instead of between the first count good options as above */
				int next = rand.nextInt(neighborhood.size());
				return neighborhood.get(next);
			}
			else 
				/* return best plan, even though they are all worse than current*/
				return neighborhood.get(0);
		}
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
	 * noiseFactor is always increasing until set to default, so it has range [default, 1].
	 * 
	 */
	private void updateNoiseFactor(){
		double variance = Statistics.calculateVariance(numInconsistency);
		
		/* increase if variance is not changing, ceiling of 1 as it is a probability */
		if( (variance - lastVariance) < VARIANCE_THRESHOLD )
			this.noiseFactor = Math.min(1, noiseFactor*1.25);
		else
			this.noiseFactor = DEFAULT_NOISE_FACTOR;
		
		this.lastVariance = variance;
	}
	
}

