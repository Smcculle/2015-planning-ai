package edu.uno.ai.planning.util;

import java.util.*;

import edu.uno.ai.planning.logic.*;

public class LiteralCollector implements Iterable<Literal> {
	private ImmutableArray<Literal> literals;

	public LiteralCollector() {
		this.literals = new ImmutableArray<Literal>(new Literal[]{});
	}

	@Override
	public Iterator<Literal> iterator() {
		return literals().iterator();
	}

	public ImmutableArray<Literal> literals() {
		return this.literals;
	}
}
