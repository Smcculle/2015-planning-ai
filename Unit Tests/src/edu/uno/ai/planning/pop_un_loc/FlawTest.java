package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class FlawTest {
  static Class<Flaw> describedClass() {
    return Flaw.class;
  }

  @Test
  public void is_a_partial() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }
}
