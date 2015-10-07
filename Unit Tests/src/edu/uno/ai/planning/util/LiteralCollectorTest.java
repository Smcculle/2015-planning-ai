package edu.uno.ai.planning.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import edu.uno.ai.planning.logic.*;

public class LiteralCollectorTest {
	@Test
	public void extracts_argument_literals_from_a_conjunction() {
		Literal firstArgument = mock(Literal.class);
		Literal secondArgument = mock(Literal.class);
		Conjunction conjunction = new Conjunction(firstArgument, secondArgument);
		LiteralCollector collector = new LiteralCollector(conjunction);

		assertThat(collector, containsInAnyOrder(
			firstArgument,
			secondArgument
		));
	}

	@Test
	public void saves_literal() {
		Literal literal = mock(Literal.class);
		LiteralCollector collector = new LiteralCollector(literal);

		assertThat(collector.size(), is(1));
		assertThat(collector, containsInAnyOrder(literal));
	}

	@Test
	public void has_a_collection_of_literals() {
		LiteralCollector collector = new LiteralCollector();

		assertThat(collector.literals(), instanceOf(ImmutableArray.class));
	}

	@Test
	public void implements_iterable() {
		assertThat(LiteralCollector.class, typeCompatibleWith(Iterable.class));
	}
}
