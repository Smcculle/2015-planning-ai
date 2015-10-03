package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.Step;

public class OpenCondition implements Flaw {

	private Literal literal;
	private Step step;

	public OpenCondition(Literal literal, Step step) {
		this.literal = literal;
		this.step = step;
	}

	public Literal literal() {
		return this.literal;
	}
	
	public Step step() {
		return this.step;
	}
}
