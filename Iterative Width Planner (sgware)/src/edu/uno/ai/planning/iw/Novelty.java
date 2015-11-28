package edu.uno.ai.planning.iw;

import edu.uno.ai.planning.State;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.StateSpaceNode;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ImmutableArray;

public class Novelty {

	public static final boolean hasNovelty(StateSpaceProblem problem, StateSpaceNode node, int size) {
		return hasNovelty(problem.literals, node, new Literal[size], 0, 0);
	}
	
	private static final boolean hasNovelty(ImmutableArray<Literal> literals, StateSpaceNode node, Literal[] conjunction, int index, int start) {
		if(index == conjunction.length)
			return !ever(node.parent, conjunction);
		else {
			for(int i=start; i<literals.length; i++) {
				Literal literal = literals.get(i);
				if(node.state.isTrue(literal)) {
					conjunction[index] = literal;
					if(hasNovelty(literals, node, conjunction, index + 1, i + 1))
						return true;
				}
			}
			return false;
		}
	}
	
	private static final boolean ever(StateSpaceNode node, Literal[] conjunction) {
		if(node == null)
			return false;
		else if(test(conjunction, node.state))
			return true;
		else
			return ever(node.parent, conjunction);
	}
	
	private static final boolean test(Literal[] conjunction, State state) {
		for(Literal literal : conjunction)
			if(!state.isTrue(literal))
				return false;
		return true;
	}
}
