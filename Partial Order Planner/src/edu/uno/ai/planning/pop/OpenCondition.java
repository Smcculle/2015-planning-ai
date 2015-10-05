package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.Step;

public class OpenCondition implements Flaw {

	private Literal literal;
	private PartialStep step;

	public OpenCondition(Literal literal, PartialStep step) {
		this.literal = literal;
		this.step = step;
	}

	public Literal literal() {
		return this.literal;
	}
	
	public PartialStep step() {
		return this.step;
	}
}
