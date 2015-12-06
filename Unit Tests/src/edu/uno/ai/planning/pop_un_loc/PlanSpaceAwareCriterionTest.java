package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PlanSpaceAwareCriterionTest {
  static Class<PlanSpaceAwareCriterion> describedClass() {
    return PlanSpaceAwareCriterion.class;
  }

  @Test
  public void is_a_criterion() {
    assertThat(describedClass(), typeCompatibleWith(Criterion.class));
  }
}
