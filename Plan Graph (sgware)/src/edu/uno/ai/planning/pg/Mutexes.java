package edu.uno.ai.planning.pg;

import java.util.HashMap;

class Mutexes {

	private final HashMap<Node, Integer> nodes = new HashMap<>();
	
	public void add(Node node, int level) {
		Integer end = nodes.get(node);
		if(end == null)
			nodes.put(node, level);
		else if(end == -1)
			return;
		else if(end < level)
			nodes.put(node, level);
	}
	
	public boolean contains(Node node, int level) {
		if(!node.exists(level))
			throw new IllegalArgumentException(node + " does not exist at level " + level + ".");
		Integer end = nodes.get(node);
		if(end == null)
			return false;
		else if(end == -1)
			return true;
		else
			return level <= end;
	}
	
	public void clear() {
		nodes.clear();
	}
}
