package edu.uno.ai.motionplanning.Planners;

public interface MotionPlanner {
	public long getVisited();
	public long getExpanded();
	public String toResultsString();
	public String getPlannerName();
	public float getSolutionCost();
	public long getFirstSolutionTime();
	public long getSolutionTime();
	public void setNodeLimit(int NodeLimit);
	
}
