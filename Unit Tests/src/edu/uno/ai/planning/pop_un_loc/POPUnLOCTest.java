package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import edu.uno.ai.planning.Planner;

public class POPUnLOCTest {
  private Class<POPUnLOC> describedClass() {
    return POPUnLOC.class;
  }

  @Test public void is_a_planner() {
    assertThat(describedClass(), typeCompatibleWith(Planner.class));
  }
}
