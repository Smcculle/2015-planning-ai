package edu.uno.ai.motionplanning.Planners;

public interface MotionPlanner {
	public long getVisited();
	public long getExpanded();
	public String toResultsString();
	public float getSolutionCost();
	public long getFirstSolutionTime();
	public long getSolutionTime();
}
