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
public class UnsafeOpenConditionsTest {
  static Class<UnsafeOpenConditions> describedClass() {
    return UnsafeOpenConditions.class;
  }

  @Test
  public void is_a_flaws() {
    assertThat(describedClass(),
               typeCompatibleWith(UnsafeOpenConditions.class));
  }

  static UnsafeOpenConditions noUnsafeOpenConditions() {
    return new UnsafeOpenConditions();
  }

  static UnsafeOpenConditions singleUnsafeOpenCondition(UnsafeOpenCondition unsafeOpenCondition) {
    return new UnsafeOpenConditions(unsafeOpenCondition);
  }

  static UnsafeOpenConditions manyUnsafeOpenConditions(UnsafeOpenCondition... unsafeOpenConditions) {
    return new UnsafeOpenConditions(unsafeOpenConditions);
  }

  UnsafeOpenConditions unsafeOpenConditions;

  @Before
  public void setup() {
    unsafeOpenConditions = noUnsafeOpenConditions();
  }

  @Test
  public void implements_iterable_interface() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  @Test
  public void implements_partial_interface() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }

  public class add_unsafeOpenCondition {
    UnsafeOpenCondition unsafeOpenCondition;

    @Before
    public void setup() {
      unsafeOpenCondition = mock(UnsafeOpenCondition.class);
    }

    public class when_the_open_condition_is_already_included {
      @Before
      public void setup() {
        unsafeOpenConditions = singleUnsafeOpenCondition(unsafeOpenCondition);
      }

      @Test
      public void adds_a_duplicate_of_the_open_condition() {
        assertThat(unsafeOpenConditions.add(unsafeOpenCondition),
                   contains(unsafeOpenCondition, unsafeOpenCondition));
      }
    }

    public class when_the_open_condition_is_not_already_included {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;

      @Before
      public void setup() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second);
      }

      @Test
      public void adds_the_open_condition_to_the_beginning_of_the_list() {
        assertThat(unsafeOpenConditions.add(unsafeOpenCondition),
                   contains(unsafeOpenCondition, second, first));
      }
    }
  }

  public class addLast_unsafeOpenCondition {
    UnsafeOpenCondition unsafeOpenCondition;

    @Before
    public void beforeExample() {
      unsafeOpenCondition = mock(UnsafeOpenCondition.class);
    }

    public class when_unsafeOpenCondition_is_already_included {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);
        third = unsafeOpenCondition = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void adds_the_duplicate_to_the_end_of_the_list() {
        assertThat(unsafeOpenConditions.addLast(unsafeOpenCondition),
                   contains(unsafeOpenCondition, second, first,
                            unsafeOpenCondition));
      }
    }

    public class when_the_flaw_is_not_already_included {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);
        third = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void adds_the_flaw_to_the_end_of_the_list() {
        assertThat(unsafeOpenConditions.addLast(unsafeOpenCondition),
                   contains(third, second, first, unsafeOpenCondition));
      }
    }
  }

  public class chooseFirstFlaw {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_null() {
        assertThat(unsafeOpenConditions.chooseFirstFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      UnsafeOpenCondition unsafeOpenCondition;

      @Before
      public void beforeExample() {
        unsafeOpenCondition = mock(UnsafeOpenCondition.class);
        unsafeOpenConditions = singleUnsafeOpenCondition(unsafeOpenCondition);
      }

      @Test
      public void is_that_flaw() {
        assertThat(unsafeOpenConditions.chooseFirstFlaw(),
                   equalTo(unsafeOpenCondition));
      }
    }

    public class when_there_is_some_open_conditions {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);
        third = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void is_the_open_condition_most_recently_added() {
        assertThat(unsafeOpenConditions.chooseFirstFlaw(), equalTo(third));
      }
    }
  }

  public class chooseUnsafeOpenCondition {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_null() {
        assertThat(unsafeOpenConditions.chooseFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_an_open_condition {
      UnsafeOpenCondition flaw;

      @Before
      public void beforeExample() {
        flaw = mock(UnsafeOpenCondition.class);
        unsafeOpenConditions = singleUnsafeOpenCondition(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(unsafeOpenConditions.chooseFlaw(), equalTo(flaw));
      }
    }

    public class when_there_are_some_open_conditions {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);
        third = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void is_the_flaw_most_recently_added() {
        assertThat(unsafeOpenConditions.chooseFlaw(), equalTo(third));
      }
    }
  }

  public class chooseLastUnsafeOpenCondition {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_null() {
        assertThat(unsafeOpenConditions.chooseLastFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      UnsafeOpenCondition flaw;

      @Before
      public void beforeExample() {
        flaw = mock(UnsafeOpenCondition.class);
        unsafeOpenConditions = singleUnsafeOpenCondition(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(unsafeOpenConditions.chooseLastFlaw(), equalTo(flaw));
      }
    }

    public class when_there_are_some_open_conditions {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);
        third = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void is_the_oldest_flaw() {
        assertThat(unsafeOpenConditions.chooseLastFlaw(), equalTo(first));
      }
    }
  }

  public class remove_flaw {
    UnsafeOpenCondition flaw;

    @Before
    public void beforeExample() {
      flaw = mock(UnsafeOpenCondition.class);
    }

    public class when_the_flaw_is_not_already_included {
      @Before
      public void beforeExample() {
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_the_current_open_conditions() {
        assertThat(unsafeOpenConditions.remove(flaw),
                   equalTo(unsafeOpenConditions));
      }
    }

    public class when_the_flaw_is_already_included {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void beforeExample() {
        first = mock(UnsafeOpenCondition.class);
        second = flaw = mock(UnsafeOpenCondition.class);
        third = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void is_open_conditions_in_the_same_order_without_the_condition() {
        assertThat(unsafeOpenConditions.remove(flaw), contains(third, first));
      }
    }
  }

  public class size {
    public class when_there_are_no_open_conditions {
      @Before
      public void beforeExample() {
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_0() {
        assertThat(unsafeOpenConditions.size(), is(0));
      }
    }

    public class when_there_is_one_flaw {
      UnsafeOpenCondition flaw;

      @Before
      public void beforeExample() {
        flaw = mock(UnsafeOpenCondition.class);
        unsafeOpenConditions = singleUnsafeOpenCondition(flaw);
      }

      @Test
      public void is_1() {
        assertThat(unsafeOpenConditions.size(), is(1));
      }
    }

    public class when_there_are_some_open_conditions {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void beforeExmple() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);
        third = mock(UnsafeOpenCondition.class);

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void is_three() {
        assertThat(unsafeOpenConditions.size(), is(3));
      }
    }
  }

  public class toOpenConditions {
    @Test
    public void is_the_caller() {
      assertThat(unsafeOpenConditions.toOpenConditions(),
                 is(unsafeOpenConditions));
    }
  }

  public class toImmutableList {
    public class when_there_are_no_open_conditions {
      @Before
      public void setup() {
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_an_empty_immutable_list_of_open_conditions() {
        assertThat(unsafeOpenConditions.toImmutableList(),
                   equalTo(new ImmutableList<UnsafeOpenCondition>()));
      }
    }

    public class when_there_are_some_open_conditions {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;

      @Before
      public void setup() {
        first = mock(UnsafeOpenCondition.class);
        second = mock(UnsafeOpenCondition.class);
        third = mock(UnsafeOpenCondition.class);
        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void is_an_immutable_list_of_open_conditions_in_that_order() {
        assertThat(unsafeOpenConditions.toImmutableList(),
                   equalTo(new ImmutableList<UnsafeOpenCondition>().add(first)
                                                                   .add(second)
                                                                   .add(third)));
      }
    }
  }

  public class toString {
    public class when_there_are_no_open_conditions {
      @Before
      public void setup() {
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(unsafeOpenConditions.toString(),
                   equalTo(unsafeOpenConditions.toString(Bindings.EMPTY)));
      }
    }

    public class when_there_are_some_open_conditions {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(UnsafeOpenCondition.class).toString(eq(Bindings.EMPTY))).willReturn(firstString)
                                                                                   .getMock();
        secondString = "second";
        second = given(mock(UnsafeOpenCondition.class).toString(eq(Bindings.EMPTY))).willReturn(secondString)
                                                                                    .getMock();
        thirdString = "third";
        third = given(mock(UnsafeOpenCondition.class).toString(eq(Bindings.EMPTY))).willReturn(thirdString)
                                                                                   .getMock();

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(unsafeOpenConditions.toString(),
                   equalTo(unsafeOpenConditions.toString(Bindings.EMPTY)));
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
        unsafeOpenConditions = noUnsafeOpenConditions();
      }

      @Test
      public void is_just_FLAWS() {
        assertThat(unsafeOpenConditions.toString(substitution),
                   equalTo("FLAWS:"));
      }
    }

    public class when_there_are_some_open_conditions {
      UnsafeOpenCondition first;
      UnsafeOpenCondition second;
      UnsafeOpenCondition third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(UnsafeOpenCondition.class).toString(eq(substitution))).willReturn(firstString)
                                                                                 .getMock();
        secondString = "second";
        second = given(mock(UnsafeOpenCondition.class).toString(eq(substitution))).willReturn(secondString)
                                                                                  .getMock();
        thirdString = "third";
        third = given(mock(UnsafeOpenCondition.class).toString(eq(substitution))).willReturn(thirdString)
                                                                                 .getMock();

        unsafeOpenConditions = manyUnsafeOpenConditions(first, second, third);
      }

      @Test
      public void starts_with_FLAWS() {
        assertThat(unsafeOpenConditions.toString(substitution),
                   startsWith("FLAWS:"));
      }

      @Test
      public void contains_each_open_condition_toString_substitution_in_lifo_order() {
        assertThat(unsafeOpenConditions.toString(substitution),
                   stringContainsInOrder(thirdString, secondString,
                                         firstString));
      }
    }
  }

  public class toThreatenedCausalLinks {
    @Before
    public void beforeExample() {
      unsafeOpenConditions = manyUnsafeOpenConditions(mock(UnsafeOpenCondition.class),
                                                      mock(UnsafeOpenCondition.class));
    }

    @Test
    public void is_an_empty_threatened_causal_links() {
      assertThat(unsafeOpenConditions.toThreatenedCausalLinks(),
                 is(new ThreatenedCausalLinks()));
    }
  }
}
