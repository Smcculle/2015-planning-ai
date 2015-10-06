package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Literal;

public class CausalLink {

	public final Step tail;
	public final Literal label;
	public final Step head;
	
	CausalLink(Step tail, Literal label, Step head) {
		this.tail = tail;
		this.label = label;
		this.head = head;
	}
	
	@Override
	public String toString() {
		return tail + "-" + label + "->" + head;
	}
}
