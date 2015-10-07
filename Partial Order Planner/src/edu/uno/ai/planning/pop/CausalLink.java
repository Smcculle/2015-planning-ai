package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;

public class CausalLink implements Partial {

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
		return toString(Bindings.EMPTY);
	}

	@Override
	public String toString(Substitution substitution) {
		return tail.toString(substitution) + "-" + label.substitute(substitution) + "->" + head.toString(substitution);
	}
}
