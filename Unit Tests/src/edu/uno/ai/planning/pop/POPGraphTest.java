package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.jgrapht.experimental.dag.*;
import org.jgrapht.graph.*;
import org.junit.*;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.logic.*;

public class POPGraphTest {
	private POPGraph popGraph() {
		return new POPGraph();
	}

	private Literal literal() {
		return new Predication(
			"POP Graph Test Predicate",
			new Constant(Settings.DEFAULT_TYPE, "A")
		);
	}

	private Step stepOne() {
		return new Step(
			"POP Graph Test Step One",
			new Conjunction(literal()),
			new Conjunction(literal())
		);
	}

	private Step stepTwo() {
		return new Step(
			"POP Graph Test Step Two",
			new Conjunction(literal()),
			new Conjunction(literal())
		);
	}

	@Test public void can_return_set_of_steps() throws Exception {
		POPGraph emptyGraph = popGraph();
		assertThat(emptyGraph.stepSet().isEmpty(), is(true));

		Step onlyStep = mock(Step.class);
		POPGraph singleStepGraph = popGraph().addStep(onlyStep);
		assertThat(singleStepGraph.stepSet().size(), is(1));
		assertThat(singleStepGraph.stepSet(), contains(onlyStep));
	}

	@Test public void has_a_directed_acyclic_graph()
			throws CloneTypeMismatchException {
		Class<DirectedAcyclicGraph> graphClass = DirectedAcyclicGraph.class;
		assertThat(popGraph().graph(), is(instanceOf(graphClass)));
	}

	@Test public void is_equal_to_an_identical_popgraph() {
		POPGraph graph = popGraph();
		assertThat(graph.equals(graph), is(true));
	}

	@Test public void is_not_equal_to_non_identical_popgraphs() throws Exception {
		POPGraph graph = popGraph();
		POPGraph differentGraph = popGraph().addStep(mock(Step.class));
		assertThat(graph, is(not(equalTo(differentGraph))));
	}

	@Test public void is_not_equal_to_non_popgraphs() {
		assertThat(popGraph(), is(not(equalTo(mock(Object.class)))));
	}

	@Test public void can_add_steps() throws Exception {
		Step step = stepOne();
		POPGraph newGraph = popGraph().addStep(step);
		assertTrue(newGraph.graph().containsVertex(step));
	}

	@Test public void can_add_an_edge_between_steps() throws Exception {
		Step stepOne = stepOne();
		Step stepTwo = stepTwo();
		POPGraph newGraph = popGraph()
			.addStep(stepOne)
			.addStep(stepTwo)
			.addEdge(stepOne, stepTwo);
		DefaultEdge stepOneToStepTwoEdge = newGraph.graph().getEdge(
			stepOne,
			stepTwo
		);
		assertThat(stepOneToStepTwoEdge, is(not(nullValue())));
		assertTrue(newGraph.graph().containsEdge(stepOneToStepTwoEdge));
	}
}
