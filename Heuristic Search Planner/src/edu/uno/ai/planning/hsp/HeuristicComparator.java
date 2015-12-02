package edu.uno.ai.planning.hsp;

import java.util.Comparator;

public class HeuristicComparator implements Comparator<StateSpaceNode>{

	    @Override
	    public int compare(StateSpaceNode x, StateSpaceNode y)
	    {
	    	return x.heuristic - y.heuristic;
	    }
	    
	    
}


