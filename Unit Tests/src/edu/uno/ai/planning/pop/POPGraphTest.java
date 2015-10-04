package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.jgrapht.experimental.dag.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.*;
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

	@Test public void can_copy_self() throws CycleFoundException {
		POPGraph graph = popGraph();
		POPGraph copyOfGraph = graph.copy();
		assertThat(graph, is(equalTo(copyOfGraph)));
	}

	@Test public void affecting_copies_will_not_affect_original() throws CycleFoundException {
		Step onlyStep = mock(Step.class);
		POPGraph graph = popGraph();
		POPGraph modifiedCopyOfGraph = graph.copy().addStep(onlyStep);
		assertThat(graph, is(not(equalTo(modifiedCopyOfGraph))));
		assertThat(
			graph.toDirectedAcyclicGraph().containsVertex(onlyStep),
			is(false)
		);
		assertThat(
			modifiedCopyOfGraph.toDirectedAcyclicGraph().containsVertex(onlyStep),
			is(true)
		);
	}

	@Test public void copying_does_not_damage_iterator() {
		Step firstStep = mock(Step.class);
		Step secondStep = mock(Step.class);
		POPGraph graph = popGraph().addStep(firstStep).addStep(secondStep);
		Iterator<Step> iterator = graph.iterator();
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), equalTo(firstStep));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), equalTo(secondStep));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test public void can_return_directed_acyclic_graph_representation_of_self() {
		DirectedAcyclicGraph<Step, DefaultEdge> emptyDirectedAcyclicGraph =
			new DirectedAcyclicGraph<>(DefaultEdge.class);
		POPGraph emptyGraph = new POPGraph(emptyDirectedAcyclicGraph);
		assertThat(
			emptyGraph.toDirectedAcyclicGraph(),
			equalTo(emptyDirectedAcyclicGraph)
		);

		Step firstStep = mock(Step.class);
		Step secondStep = mock(Step.class);
		DirectedAcyclicGraph<Step, DefaultEdge> directedAcyclicGraph =
			new DirectedAcyclicGraph<>(DefaultEdge.class);
		directedAcyclicGraph.addVertex(firstStep);
		directedAcyclicGraph.addVertex(secondStep);
		directedAcyclicGraph.addEdge(firstStep, secondStep);
		POPGraph graph = new POPGraph(directedAcyclicGraph);
		assertThat(
			graph.toDirectedAcyclicGraph(),
			equalTo(directedAcyclicGraph)
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

	@Test public void can_return_set_of_edges() throws Exception {
		POPGraph emptyGraph = popGraph();
		assertThat(emptyGraph.edgeSet().isEmpty(), is(true));

		Step firstStep = mock(Step.class);
		Step secondStep = mock(Step.class);
		POPGraph singleEdgeGraph = popGraph().addStep(firstStep)
			.addStep(secondStep)
			.addEdge(firstStep, secondStep);
		assertThat(singleEdgeGraph.edgeSet().size(), is(1));
	}

	@Test public void can_return_string_representation_of_self() throws CloneTypeMismatchException {
		POPGraph emptyGraph = popGraph();
		assertThat(emptyGraph.toString(), equalTo(emptyGraph.graph().toString()));
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
