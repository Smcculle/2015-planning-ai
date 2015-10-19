package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import edu.uno.ai.planning.logic.*;

public class OpenConditionTest {
	private Class<OpenCondition> describedClass() {
		return OpenCondition.class;
	}

	private OpenCondition openCondition() {
		return new OpenCondition(mock(Literal.class), mock(PartialStep.class));
	}

	@Test public void has_a_literal() {
		assertThat(openCondition().literal(), instanceOf(Literal.class));
	}

	@Test public void has_a_step() {
		assertThat(openCondition().step(), instanceOf(PartialStep.class));
	}

	@Test public void is_a_flaw() {
		assertThat(describedClass(), typeCompatibleWith(Flaw.class));
	}
}