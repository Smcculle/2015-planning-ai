package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
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

  public class bestOf_first_second {
    ThreatenedCausalLink first;
    ThreatenedCausalLink second;

    @Before
    public void beforeExample() {
      first = mock(ThreatenedCausalLink.class);
      second = mock(ThreatenedCausalLink.class);
    }

    public class when_first_has_lower_refinement_count_than_second {
      @Before
      public void beforeExample() {
        given(planSpaceNode.demoteThreat(first)).willReturn(mock(Orderings.class));
        given(planSpaceNode.promoteThreat(first)).willReturn(null);
        given(planSpaceNode.demoteThreat(second)).willReturn(mock(Orderings.class));
        given(planSpaceNode.promoteThreat(second)).willReturn(mock(Orderings.class));
      }

      @Test
      public void is_first() {
        assertThat(lrThreat.bestOf(first, second), is(first));
      }
    }

    public class when_second_has_lower_refinement_count_than_first {
      @Before
      public void beforeExample() {
        given(planSpaceNode.demoteThreat(first)).willReturn(mock(Orderings.class));
        given(planSpaceNode.promoteThreat(first)).willReturn(mock(Orderings.class));
        given(planSpaceNode.demoteThreat(second)).willReturn(null);
        given(planSpaceNode.promoteThreat(second)).willReturn(null);
      }

      @Test
      public void is_first() {
        assertThat(lrThreat.bestOf(first, second), is(second));
      }
    }

    public class when_they_have_equal_refinement_counts {
      @Before
      public void beforeExample() {
        given(planSpaceNode.demoteThreat(first)).willReturn(mock(Orderings.class));
        given(planSpaceNode.promoteThreat(first)).willReturn(null);
        given(planSpaceNode.demoteThreat(second)).willReturn(mock(Orderings.class));
        given(planSpaceNode.promoteThreat(second)).willReturn(null);
      }

      @Test
      public void is_first() {
        assertThat(lrThreat.bestOf(first, second), is(first));
      }
    }
  }

  public class canBeDemoted_flaw {
    ThreatenedCausalLink flaw;

    @Before
    public void beforeExample() {
      flaw = mock(ThreatenedCausalLink.class);
    }

    public class when_demoting_the_threat_is_valid {
      @Before
      public void beforeExample() {
        given(planSpaceNode.demoteThreat(flaw)).willReturn(mock(Orderings.class));
      }

      @Test
      public void is_true() {
        assertThat(lrThreat.canBeDemoted(flaw), is(true));
      }
    }

    public class when_demoting_the_threat_is_not_valid {
      @Before
      public void beforeExample() {
        given(planSpaceNode.demoteThreat(flaw)).willReturn(null);
      }

      @Test
      public void is_false() {
        assertThat(lrThreat.canBeDemoted(flaw), is(false));
      }
    }
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

  public class refinementsOf_flaw {
    ThreatenedCausalLink flaw;

    @Before
    public void beforeExample() {
      flaw = mock(ThreatenedCausalLink.class);
    }

    public class when_the_threat_cannot_be_demoted_or_promoted {
      @Before
      public void beforeExample() {
        given(planSpaceNode.demoteThreat(flaw)).willReturn(null);
        given(planSpaceNode.promoteThreat(flaw)).willReturn(null);
      }

      @Test
      public void is_0() {
        assertThat(lrThreat.refinementsOf(flaw), is(0));
      }
    }

    public class when_the_threat_can_be_demoted_not_promoted {
      @Before
      public void beforeExample() {
        given(planSpaceNode.demoteThreat(flaw)).willReturn(mock(Orderings.class));
        given(planSpaceNode.promoteThreat(flaw)).willReturn(null);
      }

      @Test
      public void is_1() {
        assertThat(lrThreat.refinementsOf(flaw), is(1));
      }
    }

    public class when_the_threat_can_be_promoted_not_demoted {
      @Before
      public void beforeExample() {
        given(planSpaceNode.promoteThreat(flaw)).willReturn(mock(Orderings.class));
        given(planSpaceNode.demoteThreat(flaw)).willReturn(null);
      }

      @Test
      public void is_1() {
        assertThat(lrThreat.refinementsOf(flaw), is(1));
      }
    }

    public class when_the_threat_can_be_promoted_or_demoted {
      @Before
      public void beforeExample() {
        given(planSpaceNode.promoteThreat(flaw)).willReturn(mock(Orderings.class));
        given(planSpaceNode.demoteThreat(flaw)).willReturn(mock(Orderings.class));
      }

      @Test
      public void is_2() {
        assertThat(lrThreat.refinementsOf(flaw), is(2));
      }
    }
  }
}
