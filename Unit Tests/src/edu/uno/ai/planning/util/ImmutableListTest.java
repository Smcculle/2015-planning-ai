package edu.uno.ai.planning.util;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
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
      Integer second;

      @Before
      public void beforeExample() {
        second = new Integer(2);
        list = listWith(second);
      }

      @Test
      public void adds_the_element_to_the_front_of_the_list() {
        assertThat(list.add(element), contains(element, second));
      }
    }
  }
}
