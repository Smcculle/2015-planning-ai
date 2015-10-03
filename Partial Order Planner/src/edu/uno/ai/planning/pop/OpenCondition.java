package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Literal;

public class OpenCondition implements Flaw {

	private Literal literal;

	public OpenCondition(Literal literal) {
		this.literal = literal;
	}

	public Literal literal() {
		return this.literal;
	}
}
