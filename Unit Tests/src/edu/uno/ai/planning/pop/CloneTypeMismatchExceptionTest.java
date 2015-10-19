package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CloneTypeMismatchExceptionTest {
	private Class<CloneTypeMismatchException> describedClass() {
		return CloneTypeMismatchException.class;
	}

	@Test public void is_an_exception() {
		assertThat(describedClass(), typeCompatibleWith(Exception.class));
	}
}
