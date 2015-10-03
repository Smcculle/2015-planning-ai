package edu.uno.ai.planning.pop;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.*;

public class OpenConditionTest {
	public Class<OpenCondition> describedClass() {
		return OpenCondition.class;
	}

	@Test public void is_a_flaw() {
		assertThat(describedClass(), typeCompatibleWith(Flaw.class));
	}
}