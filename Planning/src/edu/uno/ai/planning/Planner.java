package edu.uno.ai.planning;

import java.util.HashMap;

/**
 * A planner is an algorithm which solves a planning problem by finding a plan.
 * 
 * @author Stephen G. Ware
 * @param <S> the kind of search done by this planner
 */
public abstract class Planner<S extends Search> {

	/** Represents no limit to the number of nodes that can be searched */
	public static final int NO_NODE_LIMIT = -1;
	
	/** Represents no limit to the amount of time which can be spent searching */
	public static final long NO_TIME_LIMIT = -1;
	
	/** The name of the planner */
	public final String name;
	
	/** Keeps track of all current searchers being done by this planner */
	private final HashMap<Problem, S> searches = new HashMap<>();
	
	/**
	 * Constructs a new planned with a given name.
	 * 
	 * @param name the name of the planner
	 */
	public Planner(String name) {
		this.name = name;
	}
	
	/**
	 * Given some problem to be solved, this method constructs the appropriate
	 * kind of {@link Search} to solve it based on this planner.
	 * 
	 * @param problem the problem to be solved
	 * @return a search object for solving this problem
	 */
	protected abstract S makeSearch(Problem problem);
	
	/**
	 * Returns the next solution to the given problem.  This method is
	 * equivalent to
	 * <code>findSolution(problem, NO_NODE_LIMIT, NO_TIME_LIMIT)</code>.
	 * 
	 * @param problem the problem for which to find a solution
	 * @return a result object describing the result of the search
	 */
	public final Result findSolutuion(Problem problem) {
		return findSolutuion(problem, NO_NODE_LIMIT, NO_TIME_LIMIT);
	}
	
	/**
	 * Returns the next solution to the given problem, limiting the search
	 * in space and time.
	 * 
	 * @param problem the problem for which to find a solution
	 * @param nodeLimit the maximum number of nodes the planner may visit during the search
	 * @param timeLimit the maximum amount of time (in milliseconds) that planner may spend searching for a solution
	 * @return a result object describing the result of the search
	 */
	public final Result findSolutuion(Problem problem, int nodeLimit, long timeLimit) {
		S space = searches.get(problem);
		if(space == null) {
			space = makeSearch(problem);
			searches.put(problem, space);
		}
		int visitedBefore = space.countVisited();
		int expandedBefore = space.countExpanded();
		space.setNodeLimit(nodeLimit);
		PlanningThread thread = new PlanningThread(space);
		if(timeLimit == NO_TIME_LIMIT)
			timeLimit = 0;
		long start = System.currentTimeMillis();
		thread.start();
		try {
			thread.join(timeLimit);
			if(thread.solution == null && thread.reason == null)
				thread.reason = "time limit reached";
		}
		catch(InterruptedException ex) {
			thread.reason = "planning thread interrupted";
		}
		long time = System.currentTimeMillis() - start;
		return new Result(this, problem, thread.solution, thread.reason, space.countVisited() - visitedBefore, space.countExpanded() - expandedBefore, time);
	}
	
	/**
	 * A thread to run the planning process.  Planning is done on a separate
	 * thread so that a time limit can be imposed.
	 * 
	 * @author Stephen G. Ware
	 */
	private final class PlanningThread extends Thread {
		
		/** The search space to be explored */
		final S space;
		
		/** The solution found, if any */
		Plan solution = null;
		
		/** The reason the planner failed, if any */
		String reason = null;
		
		/**
		 * Constructs a new planning thread for a given search space.
		 * 
		 * @param space the search space to be explored
		 */
		public PlanningThread(S space) {
			this.space = space;
		}

		@Override
		@SuppressWarnings("unused")
		public void run() {
			Object[] extra = null;
			try {
				extra = new Object[512];
				solution = space.findNextSolution();
				if(solution == null)
					reason = "no solution exists";
			}
			catch(SearchLimitReachedException ex) {
				reason = "search limit reached";
			}
			catch(OutOfMemoryError ex) {
				extra = null;
				System.gc();
				reason = "out of memory";
			}
			catch(Exception ex) {
				ex.printStackTrace();
				reason = ex.getClass().getSimpleName() + ": " + ex.getMessage();
			}
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
