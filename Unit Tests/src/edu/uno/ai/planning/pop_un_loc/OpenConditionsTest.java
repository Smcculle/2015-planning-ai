package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
public class OpenConditionsTest {
  static Class<OpenConditions> describedClass() {
    return OpenConditions.class;
  }

  @Test
  public void is_a_flaws() {
    assertThat(describedClass(), typeCompatibleWith(OpenConditions.class));
  }

  static OpenConditions noOpenConditions() {
    return new OpenConditions();
  }

  static OpenConditions singleOpenCondition(OpenCondition openCondition) {
    return new OpenConditions(openCondition);
  }

  static OpenConditions manyOpenConditions(OpenCondition... openConditions) {
    return new OpenConditions(openConditions);
  }

  OpenConditions openConditions;

  @Before
  public void setup() {
    openConditions = noOpenConditions();
  }

  @Test
  public void implements_iterable_interface() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  @Test
  public void implements_partial_interface() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }

  public class add_openCondition {
    OpenCondition openCondition;

    @Before
    public void setup() {
      openCondition = mock(OpenCondition.class);
    }

    public class when_the_open_condition_is_already_included {
      @Before
      public void setup() {
        openConditions = singleOpenCondition(openCondition);
      }

      @Test
      public void adds_a_duplicate_of_the_open_condition() {
        assertThat(openConditions.add(openCondition),
                   contains(openCondition, openCondition));
      }
    }

    public class when_the_open_condition_is_not_already_included {
      OpenCondition first;
      OpenCondition second;

      @Before
      public void setup() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second);
      }

      @Test
      public void adds_the_open_condition_to_the_beginning_of_the_list() {
        assertThat(openConditions.add(openCondition),
                   contains(openCondition, second, first));
      }
    }
  }

  public class addLast_openCondition {
    OpenCondition openCondition;

    @Before
    public void beforeExample() {
      openCondition = mock(OpenCondition.class);
    }

    public class when_openCondition_is_already_included {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);
        third = openCondition = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void adds_the_duplicate_to_the_end_of_the_list() {
        assertThat(openConditions.addLast(openCondition),
                   contains(openCondition, second, first, openCondition));
      }
    }

    public class when_the_flaw_is_not_already_included {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);
        third = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void adds_the_flaw_to_the_end_of_the_list() {
        assertThat(openConditions.addLast(openCondition),
                   contains(third, second, first, openCondition));
      }
    }
  }

  public class chooseFirstFlaw {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_null() {
        assertThat(openConditions.chooseFirstFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      OpenCondition openCondition;

      @Before
      public void beforeExample() {
        openCondition = mock(OpenCondition.class);
        openConditions = singleOpenCondition(openCondition);
      }

      @Test
      public void is_that_flaw() {
        assertThat(openConditions.chooseFirstFlaw(), equalTo(openCondition));
      }
    }

    public class when_there_is_some_open_conditions {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);
        third = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void is_the_open_condition_most_recently_added() {
        assertThat(openConditions.chooseFirstFlaw(), equalTo(third));
      }
    }
  }

  public class chooseOpenCondition {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_null() {
        assertThat(openConditions.chooseFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_an_open_condition {
      OpenCondition flaw;

      @Before
      public void beforeExample() {
        flaw = mock(OpenCondition.class);
        openConditions = singleOpenCondition(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(openConditions.chooseFlaw(), equalTo(flaw));
      }
    }

    public class when_there_are_some_open_conditions {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);
        third = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void is_the_flaw_most_recently_added() {
        assertThat(openConditions.chooseFlaw(), equalTo(third));
      }
    }
  }

  public class chooseLastOpenCondition {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_null() {
        assertThat(openConditions.chooseLastFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      OpenCondition flaw;

      @Before
      public void beforeExample() {
        flaw = mock(OpenCondition.class);
        openConditions = singleOpenCondition(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(openConditions.chooseLastFlaw(), equalTo(flaw));
      }
    }

    public class when_there_are_some_open_conditions {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);
        third = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void is_the_oldest_flaw() {
        assertThat(openConditions.chooseLastFlaw(), equalTo(first));
      }
    }
  }

  public class remove_flaw {
    OpenCondition flaw;

    @Before
    public void beforeExample() {
      flaw = mock(OpenCondition.class);
    }

    public class when_the_flaw_is_not_already_included {
      @Before
      public void beforeExample() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_the_current_open_conditions() {
        assertThat(openConditions.remove(flaw), equalTo(openConditions));
      }
    }

    public class when_the_flaw_is_already_included {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(OpenCondition.class);
        second = flaw = mock(OpenCondition.class);
        third = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void is_open_conditions_in_the_same_order_without_the_condition() {
        assertThat(openConditions.remove(flaw), contains(third, first));
      }
    }
  }

  public class size {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_0() {
        assertThat(openConditions.size(), is(0));
      }
    }

    public class when_there_is_one_flaw {
      OpenCondition flaw;

      @Before
      public void beforeExample() {
        flaw = mock(OpenCondition.class);
        openConditions = singleOpenCondition(flaw);
      }

      @Test
      public void is_1() {
        assertThat(openConditions.size(), is(1));
      }
    }

    public class when_there_are_some_open_conditions {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void beforeExmple() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);
        third = mock(OpenCondition.class);

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void is_three() {
        assertThat(openConditions.size(), is(3));
      }
    }
  }

  public class toImmutableList {
    public class when_there_are_no_open_conditions {
      @Before
      public void setup() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_an_empty_immutable_list_of_open_conditions() {
        assertThat(openConditions.toImmutableList(),
                   equalTo(new ImmutableList<OpenCondition>()));
      }
    }

    public class when_there_are_some_open_conditions {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;

      @Before
      public void setup() {
        first = mock(OpenCondition.class);
        second = mock(OpenCondition.class);
        third = mock(OpenCondition.class);
        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void is_an_immutable_list_of_open_conditions_in_that_order() {
        assertThat(openConditions.toImmutableList(),
                   equalTo(new ImmutableList<OpenCondition>().add(first)
                                                             .add(second)
                                                             .add(third)));
      }
    }
  }

  public class toOpenConditions {
    @Test
    public void is_the_caller() {
      assertThat(openConditions.toOpenConditions(), is(openConditions));
    }
  }

  public class toString {
    public class when_there_are_no_open_conditions {
      @Before
      public void setup() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(openConditions.toString(),
                   equalTo(openConditions.toString(Bindings.EMPTY)));
      }
    }

    public class when_there_are_some_open_conditions {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(OpenCondition.class).toString(eq(Bindings.EMPTY))).willReturn(firstString)
                                                                             .getMock();
        secondString = "second";
        second = given(mock(OpenCondition.class).toString(eq(Bindings.EMPTY))).willReturn(secondString)
                                                                              .getMock();
        thirdString = "third";
        third = given(mock(OpenCondition.class).toString(eq(Bindings.EMPTY))).willReturn(thirdString)
                                                                             .getMock();

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(openConditions.toString(),
                   equalTo(openConditions.toString(Bindings.EMPTY)));
      }
    }
  }

  public class toString_substitution {
    Substitution substitution;

    @Before
    public void setup() {
      substitution = mock(Substitution.class);
    }

    public class when_there_are_no_open_conditions {
      @Before
      public void setup() {
        openConditions = noOpenConditions();
      }

      @Test
      public void is_just_FLAWS() {
        assertThat(openConditions.toString(substitution), equalTo("FLAWS:"));
      }
    }

    public class when_there_are_some_open_conditions {
      OpenCondition first;
      OpenCondition second;
      OpenCondition third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(OpenCondition.class).toString(eq(substitution))).willReturn(firstString)
                                                                           .getMock();
        secondString = "second";
        second = given(mock(OpenCondition.class).toString(eq(substitution))).willReturn(secondString)
                                                                            .getMock();
        thirdString = "third";
        third = given(mock(OpenCondition.class).toString(eq(substitution))).willReturn(thirdString)
                                                                           .getMock();

        openConditions = manyOpenConditions(first, second, third);
      }

      @Test
      public void starts_with_FLAWS() {
        assertThat(openConditions.toString(substitution), startsWith("FLAWS:"));
      }

      @Test
      public void contains_each_open_condition_toString_substitution_in_lifo_order() {
        assertThat(openConditions.toString(substitution),
                   stringContainsInOrder(thirdString, secondString,
                                         firstString));
      }
    }
  }

  public class toThreatenedCausalLinks {
    @Before
    public void beforeExample() {
      openConditions = manyOpenConditions(mock(OpenCondition.class),
                                          mock(OpenCondition.class));
    }

    @Test
    public void is_an_empty_threatened_causal_links() {
      assertThat(openConditions.toThreatenedCausalLinks(),
                 is(new ThreatenedCausalLinks()));
    }
  }

  public class toUnsafeOpenConditions {
    public class when_there_are_no_unsafe_open_conditions {
      @Before
      public void beforeExample() {
        openConditions = manyOpenConditions(mock(OpenCondition.class));
      }

      @Test
      public void is_an_empty_unsafe_open_conditions() {
        assertThat(openConditions.toUnsafeOpenConditions(),
                   is(new UnsafeOpenConditions()));
      }
    }

    public class when_there_are_some_unsafe_open_conditions {
      UnsafeOpenCondition firstUnsafeOpenCondition;
      UnsafeOpenCondition secondUnsafeOpenCondition;
      OpenCondition thirdOpenCondition;

      @Before
      public void beforeExample() {
        firstUnsafeOpenCondition = mock(UnsafeOpenCondition.class);
        secondUnsafeOpenCondition = mock(UnsafeOpenCondition.class);
        thirdOpenCondition = mock(OpenCondition.class);
        openConditions = manyOpenConditions(firstUnsafeOpenCondition,
                                            secondUnsafeOpenCondition,
                                            thirdOpenCondition);
      }

      @Test
      public void are_those_unsafe_open_conditions_in_order() {
        assertThat(openConditions.toUnsafeOpenConditions(),
                   contains(secondUnsafeOpenCondition,
                            firstUnsafeOpenCondition));
      }
    }
  }
}
