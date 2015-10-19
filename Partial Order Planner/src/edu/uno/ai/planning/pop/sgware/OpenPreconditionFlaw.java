package edu.uno.ai.planning.pop.sgware;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;

public class OpenPreconditionFlaw implements Flaw {

	public final Step step;
	public final Literal precondition;
	
	OpenPreconditionFlaw(Step step, Literal precondition) {
		this.step = step;
		this.precondition = precondition;
	}
	
	@Override
	public String toString() {
		return toString(Bindings.EMPTY);
	}

	@Override
	public String toString(Substitution substitution) {
		return precondition.substitute(substitution) + " open for " + step.toString(substitution);
	}
}
