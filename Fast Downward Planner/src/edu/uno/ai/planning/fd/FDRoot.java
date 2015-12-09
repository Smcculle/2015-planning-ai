package edu.uno.ai.planning.fd;

import edu.uno.ai.planning.Planner;
//import edu.uno.ai.planning.ss.StateSpaceSearch;

public class FDRoot extends FDNode {

	/** The FD search object */
	final FDSearch search;
	
	/** The maximum number of nodes which may be visited during search (initially no limit) */
	int limit = Planner.NO_NODE_LIMIT;
	
	/**
	 * Constructs a new root node.
	 * 
	 * @param search the state space search for which this node is the root
	 */
	FDRoot(FDSearch search) {
		super(new MPTState(search.problem.initialAssignments));
		this.search = search;
	}
	
	/**
	 * Sets the maximum number of nodes that may be visited during search.
	 * 
	 * @param limit the limit to set
	 */
	void setNodeLimit(int limit) {
		this.limit = limit;
	}

}
