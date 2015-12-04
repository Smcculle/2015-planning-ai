package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OpenConditionsTest {
  static Class<OpenConditions> describedClass() {
    return OpenConditions.class;
  }

  @Test
  public void is_a_flaws() {
    assertThat(describedClass(), typeCompatibleWith(Flaws.class));
  }
}
