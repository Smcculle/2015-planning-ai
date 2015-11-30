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
  Class<Orderings> describedClass() {
    return Orderings.class;
  }

  Orderings emptyOrderings() {
    return new Orderings();
  }

  Orderings orderingsWithOrder(Step first, Step second) {
    return new Orderings().add(first, second);
  }

  Orderings orderingsWithMiddleStep(Step step) {
    Step start = mock(Step.class);
    Step end = mock(Step.class);
    return orderingsWithOrder(start, end).add(start, step).add(step, end);
  }

  Orderings orderingsWithMiddleSteps(Step... steps) {
    Step start = mock(Step.class);
    Step end = mock(Step.class);
    Orderings orderings = orderingsWithOrder(start, end);

    for (Step step : steps) {
      orderings = orderings.add(start, step).add(step, end);
    }

    return orderings;
  }

  Orderings orderings;

  @Before
  public void setup() {
    orderings = emptyOrderings();
  }

  @Test
  public void implements_iterable_step() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  public class InstanceMethods {

    public class add_before_after {
      Step before;
      Step after;

      @Before
      public void setup() {
        before = mock(Step.class);
        after = mock(Step.class);
      }

      public class when_the_ordering_causes_a_cycle {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(after, before);
        }

        @Test
        public void is_null() {
          assertThat(orderings.add(before, after), is(nullValue()));
        }
      }
    }

    public class allowedOrdering_from_to {
      Step from;
      Step to;

      @Before
      public void setup() {
        from = mock(Step.class);
        to = mock(Step.class);
      }

      public class when_neither_step_has_an_ordering {
        @Before
        public void setup() {
          orderings = emptyOrderings();
        }

        @Test
        public void is_true() {
          assertThat(orderings.allowedOrdering(from, to), is(true));
          assertThat(orderings.allowedOrdering(to, from), is(true));
        }
      }

      public class when_one_step_has_no_ordering {
        Step notIncluded;

        @Before
        public void setup() {
          orderings = orderingsWithOrder(from, to);
        }

        @Test
        public void is_true() {
          assertThat(orderings.allowedOrdering(from, notIncluded), is(true));
          assertThat(orderings.allowedOrdering(notIncluded, from), is(true));
        }
      }

      public class when_the_ordering_already_exists {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(from, to);
        }

        @Test
        public void is_true() {
          assertThat(orderings.allowedOrdering(from, to), is(true));
        }
      }

      public class when_the_ordering_causes_a_cycle {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(from, to);
        }

        @Test
        public void is_false() {
          assertThat(orderings.allowedOrdering(to, from), is(false));
        }
      }
    }

    public class hasStep_step {
      Step step;

      @Before
      public void setup() {
        step = mock(Step.class);
      }

      public class when_step_is_ordered {
        @Before
        public void setup() {
          orderings = orderingsWithMiddleStep(step);
        }

        @Test
        public void is_true() {
          assertThat(orderings.hasStep(step), is(true));
        }
      }

      public class when_step_is_not_ordered {
        @Before
        public void steup() {
          orderings = emptyOrderings();
        }

        @Test
        public void is_false() {
          assertThat(orderings.hasStep(step), is(false));
        }
      }
    }

    public class hasSteps_steps {
      public class when_all_the_steps_are_ordered {
        Step first;
        Step second;

        @Before
        public void setup() {
          first = mock(Step.class);
          second = mock(Step.class);
          orderings = orderingsWithOrder(first, second);
        }

        @Test
        public void is_true() {
          assertThat(orderings.hasSteps(first, second), is(true));
          assertThat(orderings.hasSteps(second, first), is(true));
        }
      }

      public class when_any_step_is_not_ordered {
        Step first;
        Step second;
        Step unordered;

        @Before
        public void setup() {
          first = mock(Step.class);
          second = mock(Step.class);
          unordered = mock(Step.class);
          orderings = orderingsWithOrder(first, second);
        }

        @Test
        public void is_false() {
          assertThat(orderings.hasSteps(first, second, unordered), is(false));
        }
      }
    }

    public class hasOrdering_from_to {
      Step from;
      Step to;

      @Before
      public void setup() {
        from = mock(Step.class);
        to = mock(Step.class);
      }

      public class when_from_is_ordered_before_to {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(from, to);
        }

        @Test
        public void is_true() {
          assertThat(orderings.hasOrdering(from, to), is(true));
        }
      }

      public class when_from_is_not_ordered {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(to, mock(Step.class));
        }

        @Test
        public void is_false() {
          assertThat(orderings.hasOrdering(from, to), is(false));
        }
      }

      public class when_from_is_not_ordered_before_to {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(to, from);
        }

        @Test
        public void is_false() {
          assertThat(orderings.hasOrdering(from, to), is(false));
        }
      }

      public class when_to_is_not_ordered {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(from, mock(Step.class));
        }

        @Test
        public void is_false() {
          assertThat(orderings.hasOrdering(from, to), is(false));
        }
      }
    }

    public class mayBeConcurrent_first_second {
      Step first;
      Step second;

      @Before
      public void setup() {
        first = mock(Step.class);
        second = mock(Step.class);
      }

      public class when_first_is_not_ordered {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(mock(Step.class), second);
        }

        @Test
        public void is_false() {
          assertThat(orderings.mayBeConcurrent(first, second), is(false));
        }
      }

      public class when_first_is_ordered_before_second {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(first, second);
        }

        @Test
        public void is_false() {
          assertThat(orderings.mayBeConcurrent(first, second), is(false));
        }
      }

      public class when_neither_is_ordered_relative_to_the_other {
        @Before
        public void setup() {
          orderings = orderingsWithMiddleSteps(first, second);
        }

        @Test
        public void is_true() {
          assertThat(orderings.mayBeConcurrent(first, second), is(true));
          assertThat(orderings.mayBeConcurrent(second, first), is(true));
        }
      }

      public class when_second_is_not_ordered {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(first, mock(Step.class));
        }

        @Test
        public void is_false() {
          assertThat(orderings.mayBeConcurrent(first, second), is(false));
        }
      }

      public class when_second_is_ordered_before_first {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(second, first);
        }

        @Test
        public void is_false() {
          assertThat(orderings.mayBeConcurrent(first, second), is(false));
        }
      }

      public class when_they_are_identical {
        @Before
        public void setup() {
          orderings = orderingsWithOrder(first, mock(Step.class));
          second = first;
        }

        @Test
        public void is_false() {
          assertThat(orderings.mayBeConcurrent(first, second), is(false));
        }
      }
    }
  }
}