package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Planner;

/**
 * A special {@link edu.uno.ai.planning.pop.PartialOrderNode} that
 * represents the root of the search space and holds a pointer to the search
 * object and the node search limit.
 * 
 * @author Stephen G. Ware
 */
class PartialOrderRoot extends PartialOrderNode {

	/** The state space search object */
	final PartialOrderSearch search;
	
	/** The maximum number of nodes which may be visited during search (initially no limit) */
	int limit = Planner.NO_NODE_LIMIT;
	
	/**
	 * Constructs a new root node.
	 * 
	 * @param search the state space search for which this node is the root
	 */
	PartialOrderRoot(PartialOrderSearch search) {
		super(search.problem);
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
