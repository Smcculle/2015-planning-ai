package edu.uno.ai.planning;

/**
 * A search is the process of exploring some space while looking for solutions
 * to a problem.  There are many different kinds of search spaces.  Note that
 * if a search space contains any solutions it probably contains many
 * solutions, so this class provides ways to get one solution after another.
 * 
 * @author Stephen G. Ware
 */
public abstract class Search {

	/** The problem being solved */
	public final Problem problem;
	
	/**
	 * Constructs a new search for the given problem.
	 * 
	 * @param problem the problem to be solved
	 */
	public Search(Problem problem) {
		this.problem = problem;
	}
	
	/**
	 * Returns the total number of nodes that have been visited in this search
	 * space since it was created.
	 * 
	 * @return the number of visited nodes
	 */
	public abstract int countVisited();
	
	/**
	 * Returns the total number of nodes that have been expanded in this
	 * search since it was created.
	 * 
	 * @return the number of expanded ndoes
	 */
	public abstract int countExpanded();
	
	/**
	 * Imposes a limit on the number of nodes that can be visited.  During the
	 * search process, if more than this number of nodes are visited, the
	 * search will throw an exception.  Note that this limit disregards the
	 * number of nodes visited so far.  For example, if this method is called
	 * with a limit to 10, the next search that occurs can visit as many as
	 * 10 nodes (no matter how many have been visited by past searches).
	 * 
	 * @param limit the maximum number of nodes to search
	 */
	public abstract void setNodeLimit(int limit);
	
	/**
	 * Searches the space until the next solution is found.
	 * 
	 * @return the solution found
	 */
	public abstract Plan findNextSolution();
}
