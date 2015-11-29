package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

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

  @Test public void implements_iterable_step() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }

  @Test public void can_tell_if_a_step_is_included_in_the_orderings() {
  Step start = mock(Step.class);
  Step end = mock(Step.class);
  Step notIncluded = mock(Step.class);
  Orderings orderings = orderingsWithSteps(start, end);

  assertThat(orderings.hasStep(start), is(true));
  assertThat(orderings.hasStep(end), is(true));
  assertThat(orderings.hasStep(notIncluded), is(false));
  }

  @Test public void can_tell_if_a_step_is_ordered_before_another_step() {
    Step start = mock(Step.class);
    Step end = mock(Step.class);
    Orderings twoStepOrderings = orderingsWithSteps(start, end);

    assertThat(twoStepOrderings.hasOrdering(start, end), is(true));
    assertThat(twoStepOrderings.hasOrdering(end, start), is(false));

    assertThat(newEmptyOrderings().hasOrdering(start, end), is(false));
  }

  @Test public void can_tell_if_steps_are_included_in_the_orderings() {
  Step start = mock(Step.class);
  Step end = mock(Step.class);
  Step notIncluded = mock(Step.class);
  Orderings orderings = orderingsWithSteps(start, end);

  assertThat(orderings.hasSteps(start), is(true));
  assertThat(orderings.hasSteps(start, end), is(true));
  assertThat(orderings.hasSteps(notIncluded), is(false));
  assertThat(orderings.hasSteps(start, end, notIncluded), is(false));
  }

  @Test public void cannot_create_cycles() {
    Step start = mock(Step.class);
    Step middle = mock(Step.class);
    Step end = mock(Step.class);
    Orderings orderings = orderingsWithSteps(start, end)
                         .add(start, middle)
                         .add(middle, start);

    assertThat(orderings, is(nullValue()));
  }
}
