package edu.uno.ai.motionplanning.Planners;
import edu.uno.ai.motionplanning.*;

public interface MotionPlanner {
	public long getVisited();
	public long getExpanded();
	public String toResultsString();
	public String getPlannerName();
	public float getSolutionCost();
	public long getFirstSolutionTime();
	public long getSolutionTime();
	public void setNodeLimit(int NodeLimit);
	public void run();
	public MotionResults getResult();
	
}
