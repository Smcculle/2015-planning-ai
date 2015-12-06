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
public class ThreatenedCausalLinksTest {
  static Class<ThreatenedCausalLinks> describedClass() {
    return ThreatenedCausalLinks.class;
  }

  @Test
  public void is_a_flaws() {
    assertThat(describedClass(),
               typeCompatibleWith(ThreatenedCausalLinks.class));
  }

  static ThreatenedCausalLinks noThreatenedCausalLinks() {
    return new ThreatenedCausalLinks();
  }

  static ThreatenedCausalLinks singleThreatenedCausalLink(ThreatenedCausalLink unsafeOpenCondition) {
    return new ThreatenedCausalLinks(unsafeOpenCondition);
  }

  static ThreatenedCausalLinks manyThreatenedCausalLinks(ThreatenedCausalLink... threatenedCausalLinks) {
    return new ThreatenedCausalLinks(threatenedCausalLinks);
  }

  ThreatenedCausalLinks threatenedCausalLinks;

  @Before
  public void setup() {
    threatenedCausalLinks = noThreatenedCausalLinks();
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
    ThreatenedCausalLink unsafeOpenCondition;

    @Before
    public void setup() {
      unsafeOpenCondition = mock(ThreatenedCausalLink.class);
    }

    public class when_the_threatened_causal_link_is_already_included {
      @Before
      public void setup() {
        threatenedCausalLinks = singleThreatenedCausalLink(unsafeOpenCondition);
      }

      @Test
      public void adds_a_duplicate_of_the_threatened_causal_link() {
        assertThat(threatenedCausalLinks.add(unsafeOpenCondition),
                   contains(unsafeOpenCondition, unsafeOpenCondition));
      }
    }

    public class when_the_threatened_causal_link_is_not_already_included {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;

      @Before
      public void setup() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second);
      }

      @Test
      public void adds_the_threatened_causal_link_to_the_beginning_of_the_list() {
        assertThat(threatenedCausalLinks.add(unsafeOpenCondition),
                   contains(unsafeOpenCondition, second, first));
      }
    }
  }

  public class addLast_unsafeOpenCondition {
    ThreatenedCausalLink unsafeOpenCondition;

    @Before
    public void beforeExample() {
      unsafeOpenCondition = mock(ThreatenedCausalLink.class);
    }

    public class when_unsafeOpenCondition_is_already_included {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void beforeExample() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);
        third = unsafeOpenCondition = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void adds_the_duplicate_to_the_end_of_the_list() {
        assertThat(threatenedCausalLinks.addLast(unsafeOpenCondition),
                   contains(unsafeOpenCondition, second, first,
                            unsafeOpenCondition));
      }
    }

    public class when_the_threatened_causal_link_is_not_already_included {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void beforeExample() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);
        third = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void adds_the_threatened_causal_link_to_the_end_of_the_list() {
        assertThat(threatenedCausalLinks.addLast(unsafeOpenCondition),
                   contains(third, second, first, unsafeOpenCondition));
      }
    }
  }

  public class chooseFirstFlaw {
    public class when_there_are_no_threatened_causal_links {
      @Before
      public void beforeExample() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_null() {
        assertThat(threatenedCausalLinks.first(), is(nullValue()));
      }
    }

    public class when_there_is_a_threatened_causal_link {
      ThreatenedCausalLink unsafeOpenCondition;

      @Before
      public void beforeExample() {
        unsafeOpenCondition = mock(ThreatenedCausalLink.class);
        threatenedCausalLinks = singleThreatenedCausalLink(unsafeOpenCondition);
      }

      @Test
      public void is_that_threatened_causal_link() {
        assertThat(threatenedCausalLinks.first(),
                   equalTo(unsafeOpenCondition));
      }
    }

    public class when_there_is_some_threatened_causal_links {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void beforeExample() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);
        third = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void is_the_threatened_causal_link_most_recently_added() {
        assertThat(threatenedCausalLinks.first(), equalTo(third));
      }
    }
  }

  public class chooseThreatenedCausalLink {
    public class when_there_are_no_threatened_causal_links {
      @Before
      public void beforeExample() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_null() {
        assertThat(threatenedCausalLinks.chooseFlaw(), is(nullValue()));
      }
    }

    public class when_there_is_an_threatened_causal_link {
      ThreatenedCausalLink threatenedCausalLink;

      @Before
      public void beforeExample() {
        threatenedCausalLink = mock(ThreatenedCausalLink.class);
        threatenedCausalLinks = singleThreatenedCausalLink(threatenedCausalLink);
      }

      @Test
      public void is_that_threatened_casual_link() {
        assertThat(threatenedCausalLinks.chooseFlaw(),
                   equalTo(threatenedCausalLink));
      }
    }

    public class when_there_are_some_threatened_causal_links {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void beforeExample() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);
        third = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void is_the_threatened_causal_link_most_recently_added() {
        assertThat(threatenedCausalLinks.chooseFlaw(), equalTo(third));
      }
    }
  }

  public class chooseLastThreatenedCausalLink {
    public class when_there_are_no_threatened_causal_links {
      @Before
      public void beforeExample() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_null() {
        assertThat(threatenedCausalLinks.last(), is(nullValue()));
      }
    }

    public class when_there_is_a_threatened_causal_link {
      ThreatenedCausalLink threatenedCausalLink;

      @Before
      public void beforeExample() {
        threatenedCausalLink = mock(ThreatenedCausalLink.class);
        threatenedCausalLinks = singleThreatenedCausalLink(threatenedCausalLink);
      }

      @Test
      public void is_that_threatened_causal_link() {
        assertThat(threatenedCausalLinks.last(),
                   equalTo(threatenedCausalLink));
      }
    }

    public class when_there_are_some_threatened_causal_links {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void beforeExample() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);
        third = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void is_the_oldest_threatened_casusal_link() {
        assertThat(threatenedCausalLinks.last(), equalTo(first));
      }
    }
  }

  public class remove_threatened_causal_link {
    ThreatenedCausalLink threatenedCausalLink;

    @Before
    public void beforeExample() {
      threatenedCausalLink = mock(ThreatenedCausalLink.class);
    }

    public class when_the_threatened_causal_link_is_not_already_included {
      @Before
      public void beforeExample() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_the_current_threatened_causal_links() {
        assertThat(threatenedCausalLinks.remove(threatenedCausalLink),
                   equalTo(threatenedCausalLinks));
      }
    }

    public class when_the_threatened_causal_link_is_already_included {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void beforeExample() {
        first = mock(ThreatenedCausalLink.class);
        second = threatenedCausalLink = mock(ThreatenedCausalLink.class);
        third = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void is_threatened_causal_links_in_the_same_order_without_the_condition() {
        assertThat(threatenedCausalLinks.remove(threatenedCausalLink),
                   contains(third, first));
      }
    }
  }

  public class size {
    public class when_there_are_no_threatened_causal_links {
      @Before
      public void beforeExample() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_0() {
        assertThat(threatenedCausalLinks.size(), is(0));
      }
    }

    public class when_there_is_one_threatened_causal_link {
      ThreatenedCausalLink threatenedCausalLink;

      @Before
      public void beforeExample() {
        threatenedCausalLink = mock(ThreatenedCausalLink.class);
        threatenedCausalLinks = singleThreatenedCausalLink(threatenedCausalLink);
      }

      @Test
      public void is_1() {
        assertThat(threatenedCausalLinks.size(), is(1));
      }
    }

    public class when_there_are_some_threatened_causal_links {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void beforeExmple() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);
        third = mock(ThreatenedCausalLink.class);

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void is_three() {
        assertThat(threatenedCausalLinks.size(), is(3));
      }
    }
  }

  public class toImmutableList {
    public class when_there_are_no_threatened_causal_links {
      @Before
      public void setup() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_an_empty_immutable_list_of_threatened_causal_links() {
        assertThat(threatenedCausalLinks.toImmutableList(),
                   equalTo(new ImmutableList<ThreatenedCausalLink>()));
      }
    }

    public class when_there_are_some_threatened_causal_links {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;

      @Before
      public void setup() {
        first = mock(ThreatenedCausalLink.class);
        second = mock(ThreatenedCausalLink.class);
        third = mock(ThreatenedCausalLink.class);
        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void is_an_immutable_list_of_threatened_causal_links_in_that_order() {
        assertThat(threatenedCausalLinks.toImmutableList(),
                   equalTo(new ImmutableList<ThreatenedCausalLink>().add(first)
                                                                    .add(second)
                                                                    .add(third)));
      }
    }
  }

  public class toOpenConditions {
    @Before
    public void beforeExample() {
      threatenedCausalLinks = manyThreatenedCausalLinks(mock(ThreatenedCausalLink.class),
                                                        mock(ThreatenedCausalLink.class));
    }

    @Test
    public void is_an_empty_open_conditions() {
      assertThat(threatenedCausalLinks.toOpenConditions(),
                 is(new OpenConditions()));
    }
  }

  public class toString {
    public class when_there_are_no_threatened_causal_links {
      @Before
      public void setup() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(threatenedCausalLinks.toString(),
                   equalTo(threatenedCausalLinks.toString(Bindings.EMPTY)));
      }
    }

    public class when_there_are_some_threatened_causal_links {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(ThreatenedCausalLink.class).toString(eq(Bindings.EMPTY))).willReturn(firstString)
                                                                                    .getMock();
        secondString = "second";
        second = given(mock(ThreatenedCausalLink.class).toString(eq(Bindings.EMPTY))).willReturn(secondString)
                                                                                     .getMock();
        thirdString = "third";
        third = given(mock(ThreatenedCausalLink.class).toString(eq(Bindings.EMPTY))).willReturn(thirdString)
                                                                                    .getMock();

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void is_toString_subtitution_with_empty_bindings() {
        assertThat(threatenedCausalLinks.toString(),
                   equalTo(threatenedCausalLinks.toString(Bindings.EMPTY)));
      }
    }
  }

  public class toString_substitution {
    Substitution substitution;

    @Before
    public void setup() {
      substitution = mock(Substitution.class);
    }

    public class when_there_are_no_threatened_causal_links {
      @Before
      public void setup() {
        threatenedCausalLinks = noThreatenedCausalLinks();
      }

      @Test
      public void is_just_FLAWS() {
        assertThat(threatenedCausalLinks.toString(substitution),
                   equalTo("FLAWS:"));
      }
    }

    public class when_there_are_some_threatened_causal_links {
      ThreatenedCausalLink first;
      ThreatenedCausalLink second;
      ThreatenedCausalLink third;
      String firstString;
      String secondString;
      String thirdString;

      @Before
      public void setup() {
        firstString = "first";
        first = given(mock(ThreatenedCausalLink.class).toString(eq(substitution))).willReturn(firstString)
                                                                                  .getMock();
        secondString = "second";
        second = given(mock(ThreatenedCausalLink.class).toString(eq(substitution))).willReturn(secondString)
                                                                                   .getMock();
        thirdString = "third";
        third = given(mock(ThreatenedCausalLink.class).toString(eq(substitution))).willReturn(thirdString)
                                                                                  .getMock();

        threatenedCausalLinks = manyThreatenedCausalLinks(first, second, third);
      }

      @Test
      public void starts_with_FLAWS() {
        assertThat(threatenedCausalLinks.toString(substitution),
                   startsWith("FLAWS:"));
      }

      @Test
      public void contains_each_threatened_causal_link_toString_substitution_in_lifo_order() {
        assertThat(threatenedCausalLinks.toString(substitution),
                   stringContainsInOrder(thirdString, secondString,
                                         firstString));
      }
    }
  }

  public class toThreatenedCausalLinks {
    @Test
    public void is_the_caller() {
      assertThat(threatenedCausalLinks.toThreatenedCausalLinks(),
                 is(threatenedCausalLinks));
    }
  }

  public class toUnsafeOpenConditions_planSpaceNode {
    PlanSpaceNode planSpaceNode;

    @Before
    public void beforeExample() {
      planSpaceNode = mock(PlanSpaceNode.class);
      threatenedCausalLinks = manyThreatenedCausalLinks(mock(ThreatenedCausalLink.class),
                                                        mock(ThreatenedCausalLink.class));
    }

    @Test
    public void is_an_empty_unsafe_open_conditions() {
      assertThat(threatenedCausalLinks.toUnsafeOpenConditions(planSpaceNode),
                 is(new UnsafeOpenConditions()));
    }
  }
}
