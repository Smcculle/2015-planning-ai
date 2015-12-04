package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

import edu.uno.ai.planning.logic.Literal;

@RunWith(NestedRunner.class)
public class UnsafeOpenConditionTest {
  static Class<UnsafeOpenCondition> describedClass() {
    return UnsafeOpenCondition.class;
  }

  static UnsafeOpenCondition unsafeOpenCondition() {
    return new UnsafeOpenCondition(mock(Step.class), mock(Literal.class));
  }

  UnsafeOpenCondition unsafeOpenCondition;

  @Before
  public void beforeExample() {
    unsafeOpenCondition = unsafeOpenCondition();
  }

  @Test
  public void is_a_flaw() {
    assertThat(describedClass(), typeCompatibleWith(Flaw.class));
  }

  @Test
  public void is_an_open_condition() {
    assertThat(describedClass(), typeCompatibleWith(UnsafeOpenCondition.class));
  }

  public class isOpenCondition {
    @Test
    public void is_true() {
      assertThat(unsafeOpenCondition.isOpenCondition(), is(true));
    }
  }

  public class isThreat {
    @Test
    public void is_false() {
      assertThat(unsafeOpenCondition.isThreat(), is(false));
    }
  }

  public class isUnsafe {
    @Test
    public void is_true() {
      assertThat(unsafeOpenCondition.isUnsafe(), is(true));
    }
  }
}
