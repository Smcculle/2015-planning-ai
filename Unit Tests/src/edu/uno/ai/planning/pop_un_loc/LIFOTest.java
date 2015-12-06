package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class LIFOTest {
  static Class<LIFO> describedClass() {
    return LIFO.class;
  }

  LIFO<Flaw> lifo;

  @Before
  public void beforeExample() {
    lifo = new LIFO<Flaw>();
  }

  @Test
  public void is_a_criterion() {
    assertThat(describedClass(), typeCompatibleWith(Criterion.class));
  }

  public class bestOf_first_second {
    Flaw first;
    Flaw second;

    @Before
    public void beforeExample() {
      first = mock(Flaw.class);
      second = mock(Flaw.class);
    }

    @Test
    public void is_first() {
      assertThat(lifo.bestOf(first, second), is(first));
    }
  }

  public class bestOf_flaws {
    Flaw first;
    Flaw second;
    Flaw third;

    @Before
    public void beforeExample() {
      first = mock(Flaw.class);
      second = mock(Flaw.class);
      third = mock(Flaw.class);
    }

    @Test
    public void is_the_first_one() {
      assertThat(lifo.bestOf(first, second, third), is(first));
    }
  }

  public class bestOf_iterable_flaws {
    Flaws<Flaw> flaws;

    public class when_there_are_no_flaws {
      @Before
      public void beforeExample() {
        flaws = FlawsTest.noFlaws();
      }

      @Test
      public void is_null() {
        assertThat(lifo.bestOf(flaws), is(nullValue()));
      }
    }
  }
}
