package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FlawTest {
	private Class<Flaw> describedClass() {
		return Flaw.class;
	}

	@Test public void is_an_interface() {
		assertThat(describedClass().isInterface(), is(true));
	}
}
