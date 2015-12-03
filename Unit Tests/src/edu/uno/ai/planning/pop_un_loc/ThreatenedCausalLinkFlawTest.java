package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class ThreatenedCausalLinkFlawTest {
  static Class<ThreatenedCausalLinkFlaw> describedClass() {
    return ThreatenedCausalLinkFlaw.class;
  }

  static ThreatenedCausalLinkFlaw threatenedCausalLinkFlaw() {
    return new ThreatenedCausalLinkFlaw(mock(CausalLink.class),
                                        mock(Step.class));
  }

  ThreatenedCausalLinkFlaw threatenedCausalLinkFlaw;

  @Before
  public void beforeExample() {
    threatenedCausalLinkFlaw = threatenedCausalLinkFlaw();
  }

  @Test
  public void is_a_flaw() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }

  public class link {
    @Test
    public void is_a_causal_link() {
      assertThat(threatenedCausalLinkFlaw.link(), isA(CausalLink.class));
    }
  }

  public class threat {
    @Test
    public void is_a_step() {
      assertThat(threatenedCausalLinkFlaw.threat(), isA(Step.class));
    }
  }
}
