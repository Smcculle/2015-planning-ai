package edu.uno.ai.planning.pop_un_loc;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

@RunWith(NestedRunner.class)
public class PlanSpaceDefinedSelectionTest {
  static Class<PlanSpaceDefinedSelection> describedClass() {
    return PlanSpaceDefinedSelection.class;
  }

  PlanSpaceDefinedSelection<Flaw> definition;
  PlanSpaceNode planSpaceNode;
  Flaws<Flaw> flaws;

  PlanSpaceDefinedSelection<Flaw> currentDefinition() {
    return new PlanSpaceDefinedSelection<Flaw>(planSpaceNode, flaws);
  }

  PlanSpaceDefinedSelection<Flaw> definitionWithEmptySelection() {
    return definitionWith(mock(PlanSpaceNode.class), new Flaws<Flaw>());
  }

  PlanSpaceDefinedSelection<Flaw> definitionWith(Flaws<Flaw> flaws) {
    return definitionWith(mock(PlanSpaceNode.class), flaws);
  }

  PlanSpaceDefinedSelection<Flaw> definitionWith(PlanSpaceNode planSpaceNode,
                                                 Flaws<Flaw> flaws) {
    this.planSpaceNode = planSpaceNode;
    this.flaws = flaws;
    return new PlanSpaceDefinedSelection<Flaw>(planSpaceNode, flaws);
  }

  @Before
  public void beforeExample() {
    definition = definitionWithEmptySelection();
  }

  public class flaws {
    @Test
    public void is_a_collection_of_flaws() {
      assertThat(definition.flaws(), instanceOf(Flaws.class));
    }
  }

  public class planSpaceNode {
    @Test
    public void is_the_context_of_the_selection() {
      assertThat(definition.planSpaceNode(), instanceOf(PlanSpaceNode.class));
    }
  }
}
