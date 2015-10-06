package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Literal;

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
	
	@Override
	public boolean equals(Object other){
		if(!(other instanceof OpenCondition))
			return false;
		return (this.literal == ((OpenCondition)other).literal && this.step == ((OpenCondition)other).step);
		
	}
}
