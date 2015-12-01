package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

import edu.uno.ai.planning.logic.Substitution;

@RunWith(NestedRunner.class)
public class FlawStackTest {
  Class<FlawStack> describedClass() {
    return FlawStack.class;
  }

  FlawStack emptyFlawStack() {
    return new FlawStack();
  }

  FlawStack flawStackWithFlaw(Flaw flaw) {
    return new FlawStack(flaw);
  }

  FlawStack flawStackWithFlaws(Flaw... flaws) {
    FlawStack result = new FlawStack();

    for (Flaw flaw : flaws) {
      result.push(flaw);
    }

    return result;
  }

  FlawStack flawStack;

  @Before
  public void setup() {
    flawStack = emptyFlawStack();
  }

  @Test
  public void implements_iterable() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  @Test
  public void implements_partial() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }

  public class flaws {
    Stack<Flaw> flaws;

    public class when_the_flaw_stack_is_empty {
      @Before
      public void setup() {
        flawStack = emptyFlawStack();
      }

      @Test
      public void is_an_empty_stack_of_flaws() {
        assertThat(flawStack.flaws(), equalTo(new Stack<Flaw>()));
      }
    }

    public class when_the_flaw_stack_has_one_flaw {
      Flaw flaw;

      @Before
      public void setup() {
        flaw = mock(Flaw.class);
        flawStack = flawStackWithFlaw(flaw);
      }

      @Test
      public void is_a_stack_with_the_one_flaw() {
        Stack<Flaw> stack = new Stack<Flaw>();
        stack.push(flaw);
        assertThat(flawStack.flaws(), equalTo(stack));
      }
    }

    public class when_the_flaw_stack_has_two_flaws {
      Flaw first;
      Flaw second;

      @Before
      public void setup() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        flawStack = flawStackWithFlaws(first, second);
      }

      @Test
      public void is_a_stack_with_the_second_then_the_first_added() {
        Stack<Flaw> stack = new Stack<Flaw>();
        stack.push(first);
        stack.push(second);
        assertThat(flawStack.flaws(), equalTo(stack));
      }
    }
  }

  public class push_flaw {
    Flaw flaw;

    @Before
    public void setup() {
      flaw = mock(Flaw.class);
      flawStack = emptyFlawStack();
    }

    @Test
    public void adds_the_flaw_to_the_flaw_stack() {
      assertThat(flawStack.flaws(), not(contains(flaw)));
      flawStack.push(flaw);
      assertThat(flawStack.flaws(), contains(flaw));
    }
  }

  public class toString_substitution {
    Substitution substitution;

    @Before
    public void setup() {
      substitution = mock(Substitution.class);
    }

    @Test
    public void starts_with_FLAWS() {
      assertThat(flawStack.toString(substitution), startsWith("FLAWS:"));
    }

    public class when_the_flaw_stack_is_empty {
      @Before
      public void setup() {
        flawStack = emptyFlawStack();
      }

      @Test
      public void is_just_FLAWS() {
        assertThat(flawStack.toString(substitution), equalTo("FLAWS:"));
      }
    }
  }
}
