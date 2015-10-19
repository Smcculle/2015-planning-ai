package edu.uno.ai.planning.util;

import java.util.*;

import edu.uno.ai.planning.logic.*;

public class LiteralCollector implements Iterable<Expression> {
	private ImmutableArray<Expression> literals;

	public LiteralCollector() {
		this.literals = new ImmutableArray<Expression>(new Expression[]{});
	}

	public LiteralCollector(Conjunction conjunction) {
		collectConjunctionLiterals(conjunction);
	}

	public LiteralCollector(Expression expression) {
		if (expression instanceof Literal) {
			collectLiteral((Literal)expression);
		}
		else if (expression instanceof Conjunction) {
			collectConjunctionLiterals((Conjunction)expression);
		}
	}

	public LiteralCollector(Literal literal) {
		collectLiteral(literal);
	}

	private void collectConjunctionLiterals(Conjunction conjunction) {
		this.literals = conjunction.arguments;
	}

	private void collectLiteral(Literal literal) {
		this.literals = new ImmutableArray<Expression>(
			new Expression[]{literal}
		);
	}

	@Override
	public Iterator<Expression> iterator() {
		return literals().iterator();
	}

	public ImmutableArray<Expression> literals() {
		return this.literals;
	}

	public int size() {
		return this.literals.length;
	}
}
