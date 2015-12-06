package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class LRThreatTest {
  static Class<LRThreat> describedClass() {
    return LRThreat.class;
  }

  LRThreat lrThreat;
  PlanSpaceNode planSpaceNode;

  LRThreat lrThreat() {
    return lrThreatIn(mock(PlanSpaceNode.class));
  }

  LRThreat lrThreatIn(PlanSpaceNode planSpaceNode) {
    this.planSpaceNode = planSpaceNode;
    return new LRThreat(this.planSpaceNode);
  }

  @Before
  public void beforeExample() {
    lrThreat = lrThreat();
  }

  @Test
  public void is_a_plan_space_aware_criterion() {
    assertThat(describedClass(),
               typeCompatibleWith(PlanSpaceAwareCriterion.class));
  }

  public class canBePromoted_flaw {
    ThreatenedCausalLink flaw;

    @Before
    public void beforeExample() {
      flaw = mock(ThreatenedCausalLink.class);
    }

    public class when_adding_the_threat_before_the_step_is_valid {
      @Before
      public void beforeExample() {
        given(planSpaceNode.promoteThreat(flaw)).willReturn(mock(Orderings.class));
      }

      @Test
      public void is_true() {
        assertThat(lrThreat.canBePromoted(flaw), is(true));
      }
    }

    public class when_adding_the_threat_before_the_step_is_invalid {
      @Before
      public void beforeExample() {
        given(planSpaceNode.promoteThreat(flaw)).willReturn(null);
      }

      @Test
      public void is_false() {
        assertThat(lrThreat.canBePromoted(flaw), is(false));
      }
    }
  }
}
