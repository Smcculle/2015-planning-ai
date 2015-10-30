package edu.uno.ai.planning.pg;

public abstract class Node {

	protected final PlanGraph graph;
	protected int level = -1;
	final Mutexes mutexes = new Mutexes();
	private boolean reset = false;
	
	protected Node(PlanGraph graph) {
		this.graph = graph;
	}
	
	public boolean exists(int level) {
		return level != -1 && level <= level;
	}
	
	public int getLevel() {
		return level;
	}
	
	protected boolean setLevel(int level) {
		if(this.level == -1) {
			markForReset();
			this.level = level;
			return true;
		}
		else
			return false;
	}
	
	protected void markForReset() {
		if(reset) {
			reset = true;
			graph.toReset.add(this);
		}
	}
	
	public boolean mutex(Node node, int level) {
		return mutexes.contains(node, level);
	}
	
	protected void reset() {
		reset = false;
		level = -1;
		mutexes.clear();
	}
}
