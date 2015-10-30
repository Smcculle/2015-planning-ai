package edu.uno.ai.planning.pg;

import edu.uno.ai.planning.logic.Literal;

public class Level {

	public final int number;
	public final Level previous;
	protected final PlanGraph graph;
	
	protected Level(PlanGraph graph, int number) {
		this.graph = graph;
		this.number = number;
		this.previous = number == 0 ? null : graph.getLevel(number - 1);
	}
	
	final void computeMutexes() {
		for(int i=0; i<graph.steps.length; i++) {
			StepNode s1 = graph.steps[i];
			if(s1.exists(number)) {
				for(int j=i+1; j<graph.steps.length; j++) {
					StepNode s2 = graph.steps[j];
					if(s2.exists(number) && mutex(s1, s2)) {
						s1.mutexes.add(s2, number);
						s2.mutexes.add(s1, number);
					}
				}
			}
		}
	}
	
	private final boolean mutex(StepNode s1, StepNode s2) {
		// Check inconsistent effects.
		for(LiteralNode s1Effect : s1.effects) {
			Literal negation = s1Effect.literal.negate();
			for(LiteralNode s2Effect : s2.effects)
				if(s2Effect.literal.equals(negation))
					return true;
		}
		// Check interference.
		if(interference(s1, s2) || interference(s2, s1))
			return true;
		// Check competing needs.
		for(LiteralNode s1Precondition : s1.preconditions)
			for(LiteralNode s2Precondition : s2.preconditions)
				if(s1Precondition.mutex(s2Precondition, number - 1))
					return true;
		return false;
	}
	
	private final boolean interference(StepNode s1, StepNode s2) {
		for(LiteralNode s1Effect : s1.effects) {
			Literal negation = s1Effect.literal.negate();
			for(LiteralNode s2Precondition : s2.preconditions)
				if(s2Precondition.literal.equals(negation))
					return true;
		}
		return false;
	}
}
