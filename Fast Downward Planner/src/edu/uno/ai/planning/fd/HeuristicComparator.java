package edu.uno.ai.planning.fd;

import java.util.Comparator;

public class HeuristicComparator implements Comparator<FDNode> {

	/**
	 * Returns negative if a has the lower heuristic, positive if b has the lower heuristic
	 */
	@Override
    public int compare(FDNode a, FDNode b)
    {
    	if(a.heuristic - b.heuristic > 0)
    		return 1;
    	else if(a.heuristic - b.heuristic < 0)
    		return -1;
    	return 0;
    }
    
    
}
