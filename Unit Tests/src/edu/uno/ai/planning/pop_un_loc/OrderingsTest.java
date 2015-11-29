package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OrderingsTest {
  private Class<Orderings> describedClass() {
    return Orderings.class;
  }

  @Test public void implements_iterable_step() {
    assertThat(describedClass(), typeCompatibleWith(Iterable.class));
  }
}
