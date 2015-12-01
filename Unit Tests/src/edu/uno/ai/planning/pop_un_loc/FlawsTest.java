package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class FlawsTest {
  Class<Flaws> describedClass() {
    return Flaws.class;
  }

  @Test
  public void implements_partial_interface() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }
}
