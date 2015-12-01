package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
  public void implements_collection() {
    assertThat(describedClass(), typeCompatibleWith(Collection.class));
  }

  @Test
  public void implements_iterable() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  @Test
  public void implements_partial() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }

  public class contains_object {
    Flaw flaw;

    @Before
    public void setup() {
      flaw = mock(Flaw.class);
    }

    public class when_the_flaw_stack_is_empty {
      @Before
      public void setup() {
        flawStack = emptyFlawStack();
      }

      @Test
      public void is_false() {
        assertThat(flawStack.contains(flaw), is(false));
      }
    }

    public class when_the_flaw_stack_has_a_different_flaw {
      Flaw other;

      @Before
      public void setup() {
        other = mock(Flaw.class);
        flawStack = flawStackWithFlaw(other);
      }

      @Test
      public void is_false() {
        assertThat(flawStack.contains(flaw), is(false));
      }
    }

    public class when_the_flaw_stack_has_that_flaw {
      @Before
      public void setup() {
        flawStack = flawStackWithFlaw(flaw);
      }

      @Test
      public void is_true() {
        assertThat(flawStack.contains(flaw), is(true));
      }
    }

    public class when_the_flaw_stack_has_that_flaw_and_others {
      Flaw firstOther;
      Flaw secondOther;

      @Before
      public void setup() {
        firstOther = mock(Flaw.class);
        secondOther = mock(Flaw.class);

        flawStack = flawStackWithFlaws(flaw, firstOther, secondOther);
      }

      @Test
      public void is_true() {
        assertThat(flawStack.contains(flaw), is(true));
      }
    }
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

  public class size {
    public class when_the_flaw_stack_is_empty {
      @Before
      public void setup() {
        flawStack = emptyFlawStack();
      }

      @Test
      public void is_0() {
        assertThat(flawStack.size(), is(0));
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
      public void is_1() {
        assertThat(flawStack.size(), is(1));
      }
    }

    public class when_the_flaw_stack_has_five_flaws {
      List<Flaw> flaws;

      @Before
      public void setup() {
        flaws = new LinkedList<Flaw>();
        for (int i = 0; i < 5; i++) {
          flaws.add(mock(Flaw.class));
        }

        flawStack = flawStackWithFlaws(flaws.toArray(new Flaw[5]));
      }

      @Test
      public void is_5() {
        assertThat(flawStack.size(), is(5));
      }
    }
  }

  public class toString_substitution {
    Substitution substitution;

    @Before
    public void setup() {
      substitution = mock(Substitution.class);
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

    public class when_the_flaw_stack_has_flaws {
      Flaw first;
      Flaw second;
      Flaw third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(Flaw.class).toString(eq(substitution))).willReturn(firstString)
                                                                  .getMock();
        secondString = "second";
        second = given(mock(Flaw.class).toString(eq(substitution))).willReturn(secondString)
                                                                   .getMock();
        thirdString = "third";
        third = given(mock(Flaw.class).toString(eq(substitution))).willReturn(thirdString)
                                                                  .getMock();

        flawStack = flawStackWithFlaws(first, second, third);
      }

      @Test
      public void starts_with_FLAWS() {
        assertThat(flawStack.toString(substitution), startsWith("FLAWS:"));
      }

      @Test
      public void contains_each_flaws_toString_substitution() {
        assertThat(flawStack.toString(substitution),
                   containsString(firstString));
        assertThat(flawStack.toString(substitution),
                   containsString(secondString));
        assertThat(flawStack.toString(substitution),
                   containsString(thirdString));
      }
    }
  }
}
