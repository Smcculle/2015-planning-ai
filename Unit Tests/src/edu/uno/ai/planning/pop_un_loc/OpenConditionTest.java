package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

import edu.uno.ai.planning.logic.Literal;

@RunWith(NestedRunner.class)
public class OpenConditionTest {
  static Class<OpenCondition> describedClass() {
    return OpenCondition.class;
  }

  static OpenCondition openCondition() {
    return new OpenCondition(mock(Step.class), mock(Literal.class));
  }

  OpenCondition openCondition;

  @Before
  public void beforeExample() {
    openCondition = openCondition();
  }

  @Test
  public void is_a_flaw() {
    assertThat(describedClass(), typeCompatibleWith(Flaw.class));
  }

  public class precondition {
    @Test
    public void is_a_literal() {
      assertThat(openCondition.precondition(), isA(Literal.class));
    }
  }

  public class step {
    @Test
    public void is_a_step() {
      assertThat(openCondition.step(), isA(Step.class));
    }
  }
}
