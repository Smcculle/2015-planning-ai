package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;

@RunWith(NestedRunner.class)
public class FlawsTest {
  static Class<Flaws> describedClass() {
    return Flaws.class;
  }

  static Flaws noFlaws() {
    return new Flaws();
  }

  static Flaws singleFlaw(Flaw flaw) {
    return new Flaws(flaw);
  }

  static Flaws multipleFlaws(Flaw... flaws) {
    return new Flaws(flaws);
  }

  Flaws flaws;

  @Before
  public void setup() {
    flaws = noFlaws();
  }

  @Test
  public void implements_iterable_interface() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  @Test
  public void implements_partial_interface() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }

  public class add_flaw {
    Flaw flaw;

    @Before
    public void setup() {
      flaw = mock(Flaw.class);
    }

    public class when_the_flaw_is_already_included {
      @Before
      public void setup() {
        flaws = singleFlaw(flaw);
      }

      @Test
      public void adds_a_duplicate_of_the_flaw() {
        assertThat(flaws.add(flaw), contains(flaw, flaw));
      }
    }

    public class when_the_flaw_is_not_already_included {
      Flaw first;
      Flaw second;

      @Before
      public void setup() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);

        flaws = multipleFlaws(first, second);
      }

      @Test
      public void adds_the_flaw_to_the_beginning_of_the_list() {
        assertThat(flaws.add(flaw), contains(flaw, second, first));
      }
    }
  }

  public class toImmutableList {
    public class when_there_are_no_flaws {
      @Before
      public void setup() {
        flaws = noFlaws();
      }

      @Test
      public void is_an_empty_immutable_list_of_flaws() {
        assertThat(flaws.toImmutableList(), equalTo(new ImmutableList<Flaw>()));
      }
    }

    public class when_there_are_some_flaws {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void setup() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        third = mock(Flaw.class);
        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void is_an_immutable_list_of_flaws_in_that_order() {
        assertThat(flaws.toImmutableList(),
                   equalTo(new ImmutableList<Flaw>().add(first).add(second)
                                                    .add(third)));
      }
    }
  }

  public class toString {
    public class when_there_are_no_flaws {
      @Before
      public void setup() {
        flaws = noFlaws();
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(flaws.toString(), equalTo(flaws.toString(Bindings.EMPTY)));
      }
    }

    public class when_there_are_some_flaws {
      Flaw first;
      Flaw second;
      Flaw third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(Flaw.class).toString(eq(Bindings.EMPTY))).willReturn(firstString)
                                                                    .getMock();
        secondString = "second";
        second = given(mock(Flaw.class).toString(eq(Bindings.EMPTY))).willReturn(secondString)
                                                                     .getMock();
        thirdString = "third";
        third = given(mock(Flaw.class).toString(eq(Bindings.EMPTY))).willReturn(thirdString)
                                                                    .getMock();

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(flaws.toString(), equalTo(flaws.toString(Bindings.EMPTY)));
      }
    }
  }

  public class toString_substitution {
    Substitution substitution;

    @Before
    public void setup() {
      substitution = mock(Substitution.class);
    }

    public class when_there_are_no_flaws {
      @Before
      public void setup() {
        flaws = noFlaws();
      }

      @Test
      public void is_just_FLAWS() {
        assertThat(flaws.toString(substitution), equalTo("FLAWS:"));
      }
    }

    public class when_there_are_some_flaws {
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

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void starts_with_FLAWS() {
        assertThat(flaws.toString(substitution), startsWith("FLAWS:"));
      }

      @Test
      public void contains_each_flaws_toString_substitution_in_lifo_order() {
        assertThat(flaws.toString(substitution),
                   stringContainsInOrder(thirdString, secondString,
                                         firstString));
      }
    }
  }
}
