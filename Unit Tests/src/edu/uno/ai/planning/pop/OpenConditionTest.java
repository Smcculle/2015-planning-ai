package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import edu.uno.ai.planning.Settings;
import edu.uno.ai.planning.logic.*;
import org.junit.Test;

public class OpenConditionTest {
	private Class<OpenCondition> describedClass() {
		return OpenCondition.class;
	}

	@Test public void is_a_flaw() {
		assertThat(describedClass(), typeCompatibleWith(Flaw.class));
	}
}