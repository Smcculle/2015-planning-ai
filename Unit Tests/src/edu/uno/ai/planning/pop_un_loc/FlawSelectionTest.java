package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.theInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class FlawSelectionTest {
  static Class<FlawSelection> describedClass() {
    return FlawSelection.class;
  }

  public class flawFrom_planSpaceNode {
    PlanSpaceNode planSpaceNode;

    @Before
    public void beforeExample() {
      planSpaceNode = mock(PlanSpaceNode.class);
    }

    @Test
    public void is_a_PlanSpaceDefinedSelection() {
      assertThat(FlawSelection.flawFrom(planSpaceNode),
                 instanceOf(PlanSpaceDefinedSelection.class));
    }

    @Test
    public void is_in_the_given_plan_space() {
      assertThat(FlawSelection.flawFrom(planSpaceNode).planSpaceNode(),
                 theInstance(planSpaceNode));
    }
  }
}
