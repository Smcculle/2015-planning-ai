package edu.uno.ai.planning;

/**
 * A result contains information about how and why a search succeeded or failed.
 * 
 * @author Stephen G. Ware
 */
public class Result {

	/** The planner that performed the search */
	public final Planner<?> planner;
	
	/** The problem being solved */
	public final Problem problem;
	
	/** Whether or not the search was successful */
	public final boolean success;
	
	/** The plan, or null if the search failed */
	public final Plan solution;
	
	/** The reason why the search failed, or null if the search succeeded */
	public final String reason;
	
	/** The number of nodes visited during the search */
	public final int visited;
	
	/** The number of nodes expanded during the search */
	public final int expanded;
	
	/** The amount of time the search took (in milliseconds) */
	public final long time;
	
	/**
	 * Constructs a new result object.
	 * 
	 * @param planner the planner used
	 * @param problem the problem being solved
	 * @param solution the plan (possibly null)
	 * @param reason the reason for failure (possibly null)
	 * @param visited the number of nodes visited
	 * @param expanded the number of nodes expanded
	 * @param time the amount of time spent searching (in milliseconds)
	 */
	public Result(Planner<?> planner, Problem problem, Plan solution, String reason, int visited, int expanded, long time) {
		this.planner = planner;
		this.problem = problem;
		if(solution == null)
			this.success = false;
		else
			this.success = problem.isSolution(solution);
		this.solution = solution;
		this.reason = reason;
		this.visited = visited;
		this.expanded = expanded;
		this.time = time;
	}
	
	@Override
	public String toString() {
		String str = "[" + planner + " ";
		if(success)
			str += "succeeded";
		else
			str += "failed";
		str += " on " + problem.name + " in " + problem.domain.name + "; ";
		str += visited + " visited, " + expanded + " expanded; ";
		str += getTime();
		if(reason != null)
			str += "; " + reason;
		return str + "]";
	}
	
	/**
	 * Converts the time spent (in milliseconds) to a more human-readable
	 * format.
	 * 
	 * @return a string representing the amount of time
	 */
	public String getTime() {
		int minutes = (int) (time / (1000*60));
		int seconds = (int) (time / 1000) % 60;
		int milliseconds = (int) (time % 1000);
		return String.format("%d:%d:%d", minutes, seconds, milliseconds);
	}
}
