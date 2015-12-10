package edu.uno.ai.planning.lpgplus;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.graphplan.PlanGraph;
import edu.uno.ai.planning.ss.TotalOrderPlan;

public class LASearch extends Search {
	
	private final static Random rand = new Random();
	/** The number of times we will restart if not finding a solution */
	private final static int restarts = 50;
	
	/** Updates noise factor after this number of steps has elapsed */ 
	private final static int NOISE_WINDOW = 25;
	
	/** Default value for noise factor */ 
	private final static double DEFAULT_NOISE_FACTOR = 0.1;

	/** The problem being solved */
	private final Problem problem;
	
	/** Complete plan graph for the problem */
	private PlanGraph graph;
	
	/** empty graph for copying */
	private LAGraph laGraph;
	
	/** Total number of expanded nodes */
	private int expanded;
	
	/** Total number of visited nodes */
	private int visited;
	
	/** noise factor increases or decreases based on variance of # of inconsistencies 
	 * in the last NOISE_WINDOW number of steps */
	private double noiseFactor;
	
	/** The search limit on visited nodes (-1 if no limit) */
	private int limit = -1;
	
	public LASearch(Problem problem) {
		super(problem);
		this.problem = problem;
		noiseFactor = DEFAULT_NOISE_FACTOR;
		graph = new PlanGraph(problem, true);
		graph.extend();
		laGraph = new LAGraph(problem, graph);
		
	}
	

	/**
	 * Creates an empty LPGPlanGraph and iterates on that graph to find a solution up to 
	 * maxSteps, restarting if no solution is found until maxRestarts is reached
	 *  
	 * @param maxSteps Max number of steps for each search
	 * @param maxRestarts Max times the search will restart from empty LPGPlanGraph 
	 * @return A totally ordered plan representing a solution or null if none is found.  
	 * 
	 * 
	 */
	private Plan findPlan(int maxSteps, int maxRestarts)
	{
		TotalOrderPlan plan = null;
		
		outer:
		for (int i = 0; i < maxRestarts + 1; i++) {
			LAGraph lag = new LAGraph(laGraph);
			
			for(int j = 0; j < maxSteps; j++){
				if(isSolution(lag)){
					plan = getPlan(lag);
					if (problem.isSolution(plan))
						break outer;
					else
						lag.checkFacts();
				}
				
				UnsupportedPrecondition inconsistency = lag.chooseInconsistency3();
				
				if( (j) % NOISE_WINDOW == 0) 
					this.noiseFactor = Math.min(0.85, noiseFactor*1.25);
								
				List<LAGraph> neighborhood = lag.makeNeighborhood(inconsistency);
				
				if(neighborhood != null && neighborhood.size() > 0) {
					lag = chooseNewActionGraph(neighborhood, lag);
					expanded += neighborhood.size();
					visited++;
					if(visited >= limit)
						throw new SearchLimitReachedException();
				}
				
			}
		}
		
		return plan;
	}
	
	private boolean isSolution(LAGraph lag) {
		return lag.quality==0;
	}
	
	private TotalOrderPlan getPlan(LAGraph lag) {
		return lag.getPlan();
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
	private LAGraph chooseNewActionGraph(List<LAGraph> neighborhood, LAGraph lag) {
		
		Collections.sort(neighborhood);
		int currentQuality = lag.quality;
		int count = 0;
		
		/* count the neighbors with better quality.  They are sorted, so we can break after finding the cutoff */
		for (LAGraph neighbor: neighborhood) {
			if (neighbor.quality == 0) {
					/* found solution - return it */
					return neighbor;
			}
			if (neighbor.quality < currentQuality)
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
			
			if ( rand.nextDouble() < noiseFactor) {
				/* choosing randomly between all options, instead of between the better options as above */
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
}

