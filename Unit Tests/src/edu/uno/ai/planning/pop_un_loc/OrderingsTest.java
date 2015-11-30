package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class OrderingsTest {
  private Class<Orderings> describedClass() {
    return Orderings.class;
  }

  private Orderings newEmptyOrderings() {
    return new Orderings();
  }

  private Orderings orderingsWithSteps(Step first, Step second) {
    return new Orderings().add(first, second);
  }

  Orderings orderings;

  @Before public void setup() {
    orderings = newEmptyOrderings();
  }

  @Test public void implements_iterable_step() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  public class InstanceMethods {

    public class allowedOrdering_from_to {
      Step from;
      Step to;

      @Before public void setup() {
        from = mock(Step.class);
        to = mock(Step.class);
      }

      public class when_neither_step_has_an_ordering {
        @Before public void setup() {
          orderings = newEmptyOrderings();
        }

        @Test public void is_true() {
          assertThat(orderings.allowedOrdering(from, to), is(true));
          assertThat(orderings.allowedOrdering(to, from), is(true));
        }
      }

      public class when_one_step_has_no_ordering {
        Step notIncluded;

        @Before public void setup() {
          orderings = orderingsWithSteps(from, to);
        }

        @Test public void is_true() {
          assertThat(orderings.allowedOrdering(from, notIncluded), is(true));
          assertThat(orderings.allowedOrdering(notIncluded, from), is(true));
        }
      }

      public class when_the_ordering_already_exists {
        @Before public void setup() {
          orderings = orderingsWithSteps(from, to);
        }

        @Test public void is_true() {
          assertThat(orderings.allowedOrdering(from, to), is(true));
        }
      }

      public class when_the_ordering_would_create_a_cycle {
        @Before public void setup() {
          orderings = orderingsWithSteps(from, to);
        }

        @Test public void is_false() {
          assertThat(orderings.allowedOrdering(to, from), is(false));
        }
      }
    }
  }

  @Test public void cannot_create_cycles() {
    Step start = mock(Step.class);
    Step middle = mock(Step.class);
    Step end = mock(Step.class);
    Orderings orderings = orderingsWithSteps(start, end).add(start, middle)
                                                        .add(middle, start);

    assertThat(orderings, is(nullValue()));
  }

  @Test public void report_if_a_step_can_be_concurrent_with_another() {
    Step start = mock(Step.class);
    Step left = mock(Step.class);
    Step right = mock(Step.class);
    Step end = mock(Step.class);
    Step notIncluded = mock(Step.class);
    Orderings orderings = orderingsWithSteps(start, end).add(start, left)
                                                        .add(left, end)
                                                        .add(start, right)
                                                        .add(right, end);

    assertThat(orderings.mayBeConcurrent(left, right), is(true));
    assertThat(orderings.mayBeConcurrent(right, left), is(true));

    assertThat(orderings.mayBeConcurrent(start, left), is(false));
    assertThat(orderings.mayBeConcurrent(start, right), is(false));
    assertThat(orderings.mayBeConcurrent(start, end), is(false));

    assertThat(orderings.mayBeConcurrent(end, left), is(false));
    assertThat(orderings.mayBeConcurrent(end, right), is(false));
    assertThat(orderings.mayBeConcurrent(end, start), is(false));

    assertThat(orderings.mayBeConcurrent(start, start), is(false));
    assertThat(orderings.mayBeConcurrent(left, left), is(false));
    assertThat(orderings.mayBeConcurrent(right, right), is(false));
    assertThat(orderings.mayBeConcurrent(end, end), is(false));

    assertThat(orderings.mayBeConcurrent(notIncluded, start), is(false));
    assertThat(orderings.mayBeConcurrent(notIncluded, left), is(false));
    assertThat(orderings.mayBeConcurrent(notIncluded, right), is(false));
    assertThat(orderings.mayBeConcurrent(notIncluded, end), is(false));
  }

  @Test public void report_if_a_step_is_included_in_the_orderings() {
    Step start = mock(Step.class);
    Step end = mock(Step.class);
    Step notIncluded = mock(Step.class);
    Orderings orderings = orderingsWithSteps(start, end);

    assertThat(orderings.hasStep(start), is(true));
    assertThat(orderings.hasStep(end), is(true));
    assertThat(orderings.hasStep(notIncluded), is(false));
  }

  @Test public void report_if_a_step_is_ordered_before_another_step() {
    Step start = mock(Step.class);
    Step end = mock(Step.class);
    Orderings twoStepOrderings = orderingsWithSteps(start, end);

    assertThat(twoStepOrderings.hasOrdering(start, end), is(true));
    assertThat(twoStepOrderings.hasOrdering(end, start), is(false));

    assertThat(newEmptyOrderings().hasOrdering(start, end), is(false));
  }

  @Test public void report_if_steps_are_included_in_the_orderings() {
    Step start = mock(Step.class);
    Step end = mock(Step.class);
    Step notIncluded = mock(Step.class);
    Orderings orderings = orderingsWithSteps(start, end);

    assertThat(orderings.hasSteps(start), is(true));
    assertThat(orderings.hasSteps(start, end), is(true));
    assertThat(orderings.hasSteps(notIncluded), is(false));
    assertThat(orderings.hasSteps(start, end, notIncluded), is(false));
  }

}
