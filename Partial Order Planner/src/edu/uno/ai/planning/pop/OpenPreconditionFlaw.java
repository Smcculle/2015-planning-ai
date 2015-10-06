package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Literal;

public class OpenPreconditionFlaw implements Flaw {

	public final Step step;
	public final Literal precondition;
	
	OpenPreconditionFlaw(Step step, Literal precondition) {
		this.step = step;
		this.precondition = precondition;
	}
}
