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
public class ThreatenedCausalLinkTest {
  static Class<ThreatenedCausalLink> describedClass() {
    return ThreatenedCausalLink.class;
  }

  static ThreatenedCausalLink threatenedCausalLink() {
    return new ThreatenedCausalLink(mock(CausalLink.class),
                                        mock(Step.class));
  }

  ThreatenedCausalLink threatenedCausalLink;

  @Before
  public void beforeExample() {
    threatenedCausalLink = threatenedCausalLink();
  }

  @Test
  public void is_a_flaw() {
    assertThat(describedClass(), typeCompatibleWith(Partial.class));
  }

  public class link {
    @Test
    public void is_a_causal_link() {
      assertThat(threatenedCausalLink.link(), isA(CausalLink.class));
    }
  }

  public class threat {
    @Test
    public void is_a_step() {
      assertThat(threatenedCausalLink.threat(), isA(Step.class));
    }
  }
}
