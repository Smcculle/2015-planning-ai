package edu.uno.ai.planning.ff;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.ss.StateSpaceNode;

public class Util {

	public static void separateHelpful(Problem problem, StateSpaceNode stateNode, List<StateSpaceNode> helpful, List<StateSpaceNode> unhelpful) {
		Set<Literal> goalLiterals   = expressionToLiterals(problem.goal);
		Set<Literal> stateLiterals  = expressionToLiterals(stateNode.state.toExpression());
		Set<Literal> parentLiterals = (stateNode.parent == null ? new HashSet<Literal>() : expressionToLiterals(stateNode.parent.state.toExpression()));
		stateLiterals.removeAll(parentLiterals);
		boolean stateIsHelpful = false;
		for (Literal literal : stateLiterals) {
			if (goalLiterals.contains(literal)) {
				stateIsHelpful = true;
				helpful.add(stateNode);
				break;
			}
		}
		if (!stateIsHelpful) {
			unhelpful.add(stateNode);
		}
	}
	
	public static Set<Literal> expressionToLiterals(Expression expression) {
		Set<Literal> literals = new HashSet<Literal>();
		if (expression instanceof Literal) {
			literals.add((Literal)expression);
		} else {
			Conjunction cnf = (Conjunction)expression.toCNF();
			for (Expression disjunction : cnf.arguments)
				if (((Disjunction) disjunction).arguments.length == 1)
					literals.add((Literal)((Disjunction) disjunction).arguments.get(0));
				// else do nothing
		}
		return literals;
	}
}
