package edu.uno.ai.planning.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;

public class LiteralCollectorTest {
	@Test
	public void has_a_collection_of_literals() {
		LiteralCollector collector = new LiteralCollector();

		assertThat(collector.literals(), instanceOf(ImmutableArray.class));
	}
}
