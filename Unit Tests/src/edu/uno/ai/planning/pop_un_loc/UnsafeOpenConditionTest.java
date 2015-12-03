package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UnsafeOpenConditionTest {
  static Class<UnsafeOpenCondition> describedClass() {
    return UnsafeOpenCondition.class;
  }

  @Test
  public void is_a_flaw() {
    assertThat(describedClass(), typeCompatibleWith(Flaw.class));
  }

  @Test
  public void is_an_open_condition() {
    assertThat(describedClass(),
               typeCompatibleWith(OpenPreconditionFlaw.class));
  }
}
