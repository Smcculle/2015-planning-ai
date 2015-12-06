package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.or;
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

  static Flaws<Flaw> noFlaws() {
    return new Flaws<Flaw>();
  }

  static Flaws<Flaw> singleFlaw(Flaw flaw) {
    return new Flaws<Flaw>(flaw);
  }

  static Flaws<Flaw> multipleFlaws(Flaw... flaws) {
    return new Flaws<Flaw>(flaws);
  }

  Flaws<Flaw> flaws;

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

  public class addLast_flaw {
    Flaw flaw;

    @Before
    public void beforeExample() {
      flaw = mock(Flaw.class);
    }

    public class when_the_flaw_is_already_included {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void beforeExample() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        third = flaw = mock(Flaw.class);

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void adds_the_duplicate_to_the_end_of_the_list() {
        assertThat(flaws.addLast(flaw), contains(flaw, second, first, flaw));
      }
    }

    public class when_the_flaw_is_not_already_included {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void beforeExample() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        third = mock(Flaw.class);

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void adds_the_flaw_to_the_end_of_the_list() {
        assertThat(flaws.addLast(flaw), contains(third, second, first, flaw));
      }
    }
  }

  public class chooseFirstUnsafeOpenCondition_planSpaceNode {
    PlanSpaceNode planSpaceNode;

    @Before
    public void beforeExample() {
      planSpaceNode = mock(PlanSpaceNode.class);
    }

    public class when_there_are_no_unsafe_open_conditions {
      Flaw flaw;
      OpenCondition openCondition;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        openCondition = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(openCondition)).willReturn(false);
        flaws = multipleFlaws(openCondition, flaw);
      }

      @Test
      public void is_null() {
        assertThat(flaws.chooseFirstUnsafeOpenCondition(planSpaceNode),
                   is(nullValue()));
      }
    }

    public class when_there_is_an_unsafe_open_condition {
      Flaw flaw;
      OpenCondition openCondition;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        openCondition = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(openCondition)).willReturn(true);
        flaws = multipleFlaws(openCondition, flaw);
      }

      @Test
      public void is_that_unsafe_open_condition() {
        assertThat(flaws.chooseFirstUnsafeOpenCondition(planSpaceNode),
                   is(openCondition));
      }
    }

    public class when_there_are_some_unsafe_open_conditions {
      OpenCondition firstUnsafe;
      Flaw flaw;
      OpenCondition secondUnsafe;

      @Before
      public void beforeExample() {
        firstUnsafe = mock(OpenCondition.class);
        flaw = mock(Flaw.class);
        secondUnsafe = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(or(eq(firstUnsafe),
                                        eq(secondUnsafe)))).willReturn(true);
        flaws = multipleFlaws(firstUnsafe, flaw, secondUnsafe);
      }

      @Test
      public void is_the_last_added_unsafe_open_condition() {
        assertThat(flaws.chooseFirstUnsafeOpenCondition(planSpaceNode),
                   is(secondUnsafe));
      }
    }
  }

  public class chooseFlaw {
    public class when_there_are_no_flaws {
      @Before
      public void beforeExample() {
        flaws = noFlaws();
      }

      @Test
      public void is_null() {
        assertThat(flaws.chooseFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(flaws.chooseFlaw(), equalTo(flaw));
      }
    }

    public class when_there_is_some_flaws {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void beforeExample() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        third = mock(Flaw.class);

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void is_the_flaw_most_recently_added() {
        assertThat(flaws.chooseFlaw(), equalTo(third));
      }
    }
  }

  public class chooseFlaw_planSpaceNode {
    PlanSpaceNode planSpaceNode;

    @Before
    public void beforeExample() {
      planSpaceNode = mock(PlanSpaceNode.class);
    }

    public class when_there_are_no_flaws {
      @Before
      public void beforeExample() {
        flaws = noFlaws();
      }

      @Test
      public void is_null() {
        assertThat(flaws.chooseFlaw(planSpaceNode), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(flaws.chooseFlaw(planSpaceNode), is(flaw));
      }
    }

    public class when_there_are_some_flaws {
      Flaw first;
      Flaw second;

      @Before
      public void beforeExample() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        flaws = multipleFlaws(first, second);
      }

      @Test
      public void is_the_last_added_flaw() {
        assertThat(flaws.chooseFlaw(planSpaceNode), is(second));
      }
    }

    public class when_there_is_an_unsafe_open_condition {
      Flaw flaw;
      OpenCondition openCondition;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        openCondition = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(openCondition)).willReturn(true);
        flaws = multipleFlaws(openCondition, flaw);
      }

      @Test
      public void is_that_open_condition() {
        assertThat(flaws.chooseFlaw(planSpaceNode), is(openCondition));
      }
    }

    public class when_there_are_some_unsafe_open_conditions {
      OpenCondition firstOpenCondition;
      Flaw flaw;
      OpenCondition secondOpenCondition;

      @Before
      public void beforeExample() {
        firstOpenCondition = mock(OpenCondition.class);
        flaw = mock(Flaw.class);
        secondOpenCondition = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(or(eq(firstOpenCondition),
                                        eq(secondOpenCondition)))).willReturn(true);
        flaws = multipleFlaws(firstOpenCondition, flaw, secondOpenCondition);
      }

      @Test
      public void is_the_last_added_unsafe_open_condition() {
        assertThat(flaws.chooseFlaw(planSpaceNode), is(secondOpenCondition));
      }
    }
  }

  public class first {
    public class when_there_are_no_flaws {
      @Before
      public void beforeExample() {
        flaws = noFlaws();
      }

      @Test
      public void is_null() {
        assertThat(flaws.first(), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(flaws.first(), equalTo(flaw));
      }
    }

    public class when_there_is_some_flaws {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void beforeExample() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        third = mock(Flaw.class);

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void is_the_flaw_most_recently_added() {
        assertThat(flaws.first(), equalTo(third));
      }
    }
  }

  public class last {
    public class when_there_are_no_flaws {
      @Before
      public void beforeExample() {
        flaws = noFlaws();
      }

      @Test
      public void is_null() {
        assertThat(flaws.last(), is(nullValue()));
      }
    }

    public class when_there_is_a_flaw {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_that_flaw() {
        assertThat(flaws.last(), equalTo(flaw));
      }
    }

    public class when_there_are_some_flaws {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void beforeExample() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        third = mock(Flaw.class);

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void is_the_oldest_flaw() {
        assertThat(flaws.last(), equalTo(first));
      }
    }
  }

  public class openConditions {
    public class when_there_are_no_open_conditions {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_an_empty_open_conditions() {
        assertThat(flaws.openConditions(), is(new OpenConditions()));
      }
    }

    public class when_there_is_an_open_condition {
      Flaw flaw;
      OpenCondition openCondition;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        openCondition = mock(OpenCondition.class);
        flaws = multipleFlaws(flaw, openCondition);
      }

      @Test
      public void is_an_open_conditions_with_that_condition() {
        assertThat(flaws.openConditions(), contains(openCondition));
      }
    }

    public class when_there_are_some_open_conditions {
      Flaw flaw;
      OpenCondition firstCondition;
      OpenCondition secondCondition;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        firstCondition = mock(OpenCondition.class);
        secondCondition = mock(OpenCondition.class);
        flaws = multipleFlaws(flaw, firstCondition, secondCondition);
      }

      @Test
      public void are_those_open_conditions_in_order() {
        assertThat(flaws.openConditions(),
                   contains(secondCondition, firstCondition));
      }
    }
  }

  public class remove_flaw {
    Flaw flaw;

    @Before
    public void beforeExample() {
      flaw = mock(Flaw.class);
    }

    public class when_the_flaw_is_not_already_included {
      @Before
      public void beforeExample() {
        flaws = noFlaws();
      }

      @Test
      public void is_the_current_flaws() {
        assertThat(flaws.remove(flaw), equalTo(flaws));
      }
    }

    public class when_the_flaw_is_already_included {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void beforeExample() {
        first = mock(Flaw.class);
        second = flaw = mock(Flaw.class);
        third = mock(Flaw.class);

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void is_flaws_in_the_same_order_without_the_flaw() {
        assertThat(flaws.remove(flaw), contains(third, first));
      }
    }
  }

  public class size {
    public class when_there_are_no_flaws {
      @Before
      public void beforeExample() {
        flaws = noFlaws();
      }

      @Test
      public void is_0() {
        assertThat(flaws.size(), is(0));
      }
    }

    public class when_there_is_one_flaw {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_1() {
        assertThat(flaws.size(), is(1));
      }
    }

    public class when_there_are_some_flaws {
      Flaw first;
      Flaw second;
      Flaw third;

      @Before
      public void beforeExmple() {
        first = mock(Flaw.class);
        second = mock(Flaw.class);
        third = mock(Flaw.class);

        flaws = multipleFlaws(first, second, third);
      }

      @Test
      public void is_three() {
        assertThat(flaws.size(), is(3));
      }
    }
  }

  public class threatenedCausalLinks {
    public class when_there_are_no_threatened_causal_links {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_an_empty_threatened_causal_links() {
        assertThat(flaws.threatenedCausalLinks(),
                   is(new ThreatenedCausalLinks()));
      }
    }

    public class when_there_is_a_threatened_causal_link {
      Flaw flaw;
      ThreatenedCausalLink link;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        link = mock(ThreatenedCausalLink.class);
        flaws = multipleFlaws(flaw, link);
      }

      @Test
      public void is_that_threatened_causal_link() {
        assertThat(flaws.threatenedCausalLinks(), contains(link));
      }
    }

    public class when_there_are_some_threatened_causal_links {
      Flaw flaw;
      ThreatenedCausalLink firstLink;
      ThreatenedCausalLink secondLink;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        firstLink = mock(ThreatenedCausalLink.class);
        secondLink = mock(ThreatenedCausalLink.class);
        flaws = multipleFlaws(flaw, firstLink, secondLink);
      }

      @Test
      public void are_those_threatened_causal_links_in_order() {
        assertThat(flaws.threatenedCausalLinks(),
                   contains(secondLink, firstLink));
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

  public class toUnsafeOpenConditions_planSpaceNode {
    PlanSpaceNode planSpaceNode;

    @Before
    public void beforeExample() {
      planSpaceNode = mock(PlanSpaceNode.class);
    }

    public class when_there_are_no_open_conditions {
      Flaw flaw;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        flaws = singleFlaw(flaw);
      }

      @Test
      public void is_an_empty_unsafe_open_conditions() {
        assertThat(flaws.toUnsafeOpenConditions(planSpaceNode),
                   is(new OpenConditions()));
      }
    }

    public class when_there_are_no_unsafe_open_conditions {
      OpenCondition openCondition;

      @Before
      public void beforeExample() {
        openCondition = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(eq(openCondition))).willReturn(false);

        flaws = singleFlaw(openCondition);
      }

      @Test
      public void is_an_empty_unsafe_open_conditions() {
        assertThat(flaws.toUnsafeOpenConditions(planSpaceNode),
                   is(new OpenConditions()));
      }
    }

    public class when_there_is_an_unsafe_open_condition {
      Flaw flaw;
      OpenCondition openCondition;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        openCondition = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(eq(openCondition))).willReturn(true);
        flaws = multipleFlaws(openCondition, flaw);
      }

      @Test
      public void is_that_open_condition() {
        assertThat(flaws.toUnsafeOpenConditions(planSpaceNode),
                   contains(openCondition));
      }
    }

    public class when_there_are_some_unsafe_open_conditions {
      Flaw flaw;
      OpenCondition firstCondition;
      OpenCondition secondCondition;

      @Before
      public void beforeExample() {
        flaw = mock(Flaw.class);
        firstCondition = mock(OpenCondition.class);
        secondCondition = mock(OpenCondition.class);
        given(planSpaceNode.isUnsafe(or(eq(firstCondition),
                                        eq(secondCondition)))).willReturn(true);
        flaws = multipleFlaws(flaw, firstCondition, secondCondition);
      }

      @Test
      public void are_those_open_conditions_in_order() {
        assertThat(flaws.toUnsafeOpenConditions(planSpaceNode),
                   contains(secondCondition, firstCondition));
      }
    }
  }
}
