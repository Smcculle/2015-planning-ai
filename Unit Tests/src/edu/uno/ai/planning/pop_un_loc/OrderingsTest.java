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
