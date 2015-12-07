package edu.uno.ai.motionplanning;

public class MotionResults {
	public long getVisited() {
		return visited;
	}
	public void setVisited(long visited) {
		this.visited = visited;
	}
	public long getExpanded() {
		return expanded;
	}
	public void setExpanded(long expanded) {
		this.expanded = expanded;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public double getSolutionCost() {
		return solutionCost;
	}
	public void setSolutionCost(double solutionCost) {
		this.solutionCost = solutionCost;
	}
	
	public long visited;
	public long expanded;
	public long time;
	public double solutionCost;
	

}
