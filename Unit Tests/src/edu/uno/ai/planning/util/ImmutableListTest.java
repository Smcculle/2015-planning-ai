package edu.uno.ai.planning.util;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class ImmutableListTest {
  Class<ImmutableList> describedClass() {
    return ImmutableList.class;
  }

  ImmutableList<Integer> emptyList() {
    return new ImmutableList<Integer>();
  }

  ImmutableList<Integer> listWith(Integer element) {
    return new ImmutableList<Integer>(element);
  }

  ImmutableList<Integer> listWith(Integer... elements) {
    return new ImmutableList<Integer>(elements);
  }

  ImmutableList<Integer> copyOfList(ImmutableList<Integer> list) {
    return new ImmutableList<Integer>(list);
  }

  ImmutableList<Integer> list;

  @Before
  public void beforeExample() {
    list = emptyList();
  }

  public class add_element {
    Integer element;

    @Before
    public void beforeExample() {
      element = new Integer(1);
    }

    public class when_list_is_empty {
      @Before
      public void beforeExample() {
        list = emptyList();
      }

      @Test
      public void is_an_immutable_list_with_the_element() {
        assertThat(list.add(element), equalTo(listWith(element)));
      }
    }

    public class when_list_is_not_empty {
      Integer first;

      @Before
      public void beforeExample() {
        first = new Integer(2);
        list = listWith(first);
      }

      @Test
      public void adds_the_element_to_the_front_of_the_list() {
        assertThat(list.add(element), contains(element, first));
      }
    }
  }

  public class addLast_element {
    Integer element;

    @Before
    public void beforeExample() {
      element = new Integer(1);
    }

    public class when_the_element_is_already_included {
      Integer first;
      Integer second;
      Integer third;

      @Before
      public void beforeExample() {
        first = new Integer(1);
        second = new Integer(2);
        third = element = new Integer(3);

        list = listWith(first, second, third);
      }

      @Test
      public void adds_the_duplicate_to_the_end_of_the_list() {
        assertThat(list.addLast(element),
                   contains(element, second, first, element));
      }

      public class when_the_element_is_not_already_included {
        Integer first;
        Integer second;
        Integer third;

        @Before
        public void beforeExample() {
          first = new Integer(1);
          second = new Integer(2);
          third = new Integer(3);

          list = listWith(first, second, third);
        }

        @Test
        public void adds_the_element_to_the_end_of_the_list() {
          assertThat(list.addLast(element),
                     contains(third, second, first, element));
        }
      }
    }
  }

  public class first {
    public class when_the_list_is_empty {
      @Before
      public void beforeExample() {
        list = emptyList();
      }

      @Test
      public void is_null() {
        assertThat(list.first(), is(nullValue()));
      }
    }

    public class when_the_list_has_an_element {
      Integer element;

      @Before
      public void beforeExample() {
        element = new Integer(1);
        list = listWith(element);
      }

      @Test
      public void is_that_element() {
        assertThat(list.first(), equalTo(element));
      }
    }

    public class when_the_list_has_some_elements {
      Integer first;
      Integer second;
      Integer third;

      @Before
      public void beforeExample() {
        first = new Integer(1);
        second = new Integer(2);
        third = new Integer(3);

        list = listWith(first, second, third);
      }

      @Test
      public void is_the_most_recently_added_element() {
        assertThat(list.first(), equalTo(third));
      }
    }
  }

  public class last {
    public class when_the_list_is_empty {
      @Before
      public void beforeExample() {
        list = emptyList();
      }

      @Test
      public void is_null() {
        assertThat(list.last(), is(nullValue()));
      }
    }

    public class when_the_list_has_an_element {
      Integer element;

      @Before
      public void beforeExample() {
        element = new Integer(1);
        list = listWith(element);
      }

      @Test
      public void is_that_element() {
        assertThat(list.last(), equalTo(element));
      }
    }

    public class when_the_list_has_some_elements {
      Integer first;
      Integer second;
      Integer third;

      @Before
      public void beforeExample() {
        first = new Integer(1);
        second = new Integer(2);
        third = new Integer(3);

        list = listWith(first, second, third);
      }

      @Test
      public void is_the_element_first_added() {
        assertThat(list.last(), equalTo(first));
      }
    }
  }

  public class remove_element {
    Integer element;

    @Before
    public void beforeExample() {
      element = new Integer(1);
    }

    public class when_the_element_is_already_not_included {
      @Before
      public void beforeExample() {
        list = emptyList();
      }

      @Test
      public void is_the_existing_list() {
        assertThat(list.remove(element), equalTo(list));
      }
    }

    public class when_the_element_is_included {
      Integer first;
      Integer second;
      Integer third;

      @Before
      public void beforeExample() {
        first = new Integer(1);
        element = second = new Integer(2);
        third = new Integer(3);

        list = listWith(first, second, third);
      }

      @Test
      public void is_a_list_in_the_same_order_without_the_element() {
        assertThat(list.remove(element), contains(third, first));
      }
    }
  }

  public class rest {
    public class when_there_are_no_elements {
      @Before
      public void beforeExample() {
        list = emptyList();
      }

      @Test
      public void is_null() {
        assertThat(list.rest(), is(nullValue()));
      }
    }

    public class when_there_is_an_element {
      Integer element;

      @Before
      public void beforeExample() {
        element = new Integer(1);
        list = listWith(element);
      }

      @Test
      public void is_an_empty_immutable_list() {
        assertThat(list.rest(), equalTo(new ImmutableList<Integer>()));
      }
    }

    public class when_there_are_some_elements {
      Integer first;
      Integer second;
      Integer third;

      @Before
      public void beforeExample() {
        first = new Integer(1);
        second = new Integer(2);
        third = new Integer(3);
        list = listWith(first, second, third);
      }

      @Test
      public void is_the_immutable_list_without_the_first_element() {
        assertThat(list.rest(), contains(second, first));
      }
    }
  }

  public class size {
    public class when_there_are_no_elements {
      @Before
      public void beforeExample() {
        list = emptyList();
      }

      @Test
      public void is_0() {
        assertThat(list.size(), is(0));
      }
    }

    public class when_there_is_one_element {
      Integer element;

      @Before
      public void beforeExample() {
        element = new Integer(1);
        list = listWith(element);
      }

      @Test
      public void is_1() {
        assertThat(list.size(), is(1));
      }
    }

    public class when_there_are_some_elements {
      Integer first;
      Integer second;
      Integer third;

      @Before
      public void beforeExample() {
        first = new Integer(1);
        second = new Integer(2);
        third = new Integer(3);
        list = listWith(first, second, third);
      }

      @Test
      public void is_3() {
        assertThat(list.size(), is(3));
      }
    }
  }
}
