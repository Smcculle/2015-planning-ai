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

	private Literal literal() {
		return new Predication(
			"Open Condition Predicate",
			new Constant(Settings.DEFAULT_TYPE, "A")
		);
	}

	private OpenCondition openCondition() {
		return new OpenCondition(literal());
	}

	@Test public void has_a_literal() {
		assertThat(openCondition().literal(), instanceOf(Literal.class));
	}

	@Test public void is_a_flaw() {
		assertThat(describedClass(), typeCompatibleWith(Flaw.class));
	}
}