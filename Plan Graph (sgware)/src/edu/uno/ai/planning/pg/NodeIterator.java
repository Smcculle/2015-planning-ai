package edu.uno.ai.planning.pg;

import java.util.Iterator;

class NodeIterator<N extends Node> implements Iterator<N> {

	private final int level;
	private final Iterator<N> nodes;
	private N next;
	
	protected NodeIterator(int level, Iterable<N> nodes) {
		this.level = level;
		this.nodes = nodes.iterator();
		advance();
	}
	
	private final void advance() {
		while(nodes.hasNext()) {
			next = nodes.next();
			if(next.getLevel() <= level)
				return;
		}
		next = null;
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public N next() {
		N node = next;
		advance();
		return node;
	}
}