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
public class OpenPreconditionFlawTest {
  static Class<OpenPreconditionFlaw> describedClass() {
    return OpenPreconditionFlaw.class;
  }

  static OpenPreconditionFlaw openPreconditionFlaw() {
    return new OpenPreconditionFlaw(mock(Step.class), mock(Literal.class));
  }

  OpenPreconditionFlaw openPreconditionFlaw;

  @Before
  public void beforeExample() {
    openPreconditionFlaw = openPreconditionFlaw();
  }

  @Test
  public void is_a_flaw() {
    assertThat(describedClass(), typeCompatibleWith(Flaw.class));
  }

  public class precondition {
    @Test
    public void is_a_literal() {
      assertThat(openPreconditionFlaw.precondition(), isA(Literal.class));
    }
  }

  public class step {
    @Test
    public void is_a_step() {
      assertThat(openPreconditionFlaw.step(), isA(Step.class));
    }
  }
}
