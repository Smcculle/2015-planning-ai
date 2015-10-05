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
	private POPGraph newEmptyPopGraph() {
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

	@Test public void can_add_a_step_to_self() {
		Step step = stepOne();
		POPGraph newGraph = newEmptyPopGraph().addStep(step);
		assertThat(
			newGraph.toDirectedAcyclicGraph().containsVertex(step),
			is(true)
		);
	}

	@Test public void can_return_a_popgraph_equal_to_self_plus_new_step() {
		Step onlyStep = mock(Step.class);
		POPGraph emptyGraph = newEmptyPopGraph();
		POPGraph singleStepGraph = emptyGraph.addStep(onlyStep);

		assertThat(emptyGraph, not(equalTo(singleStepGraph)));
		assertThat(emptyGraph.toDirectedAcyclicGraph().containsVertex(onlyStep), is(false));
		assertThat(singleStepGraph.toDirectedAcyclicGraph().containsVertex(onlyStep), is(true));

		Step extraStep = mock(Step.class);
		POPGraph twoStepGraph = singleStepGraph.addStep(extraStep);

		assertThat(singleStepGraph.toDirectedAcyclicGraph().containsVertex(extraStep), is(false));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(onlyStep), is(true));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(extraStep), is(true));
	}

	@Test public void newly_added_steps_have_no_edges() {
		Step mockedStep = mock(Step.class);
		POPGraph newGraph = newEmptyPopGraph().addStep(mockedStep);
		Set<DefaultEdge> edgesOfNewStep = newGraph.toDirectedAcyclicGraph().edgesOf(mockedStep);

		assertTrue(edgesOfNewStep.isEmpty());
	}


	@Test public void steps_are_only_added_once() {
		Step onlyStep = mock(Step.class);
		POPGraph graph = newEmptyPopGraph().addStep(onlyStep);
		Iterator<Step> iterator = graph.iterator();

		assertTrue(iterator.hasNext());
		assertThat(iterator.next(), equalTo(onlyStep));
		assertFalse(iterator.hasNext());
	}

	@Test public void adding_more_than_one_step_does_not_add_edges() {
		Step firstMockedStep = mock(Step.class);
		Step secondMockedStep = mock(Step.class);
		POPGraph graph = newEmptyPopGraph().addStep(firstMockedStep)
			.addStep(secondMockedStep);
		Set<DefaultEdge> edgesOfGraph = graph.toDirectedAcyclicGraph().edgeSet();

		assertTrue(edgesOfGraph.isEmpty());
	}

	@Test public void can_return_a_popgraph_equal_to_self_plus_multiple_steps_via_an_iterable() {
		ArrayList<Step> steps = new ArrayList<Step>();
		steps.add(mock(Step.class));
		steps.add(mock(Step.class));
		POPGraph emptyGraph = newEmptyPopGraph();
		POPGraph twoStepGraph = emptyGraph.addSteps(steps);

		assertThat(emptyGraph.toDirectedAcyclicGraph().containsVertex(steps.get(0)), is(false));
		assertThat(emptyGraph.toDirectedAcyclicGraph().containsVertex(steps.get(1)), is(false));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(0)), is(true));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(1)), is(true));

		steps.add(mock(Step.class));
		steps.add(mock(Step.class));
		POPGraph fourStepGraph = emptyGraph.addSteps(steps);

		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(2)), is(false));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(3)), is(false));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(0)), is(true));
		assertThat(fourStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(1)), is(true));
		assertThat(fourStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(2)), is(true));
		assertThat(fourStepGraph.toDirectedAcyclicGraph().containsVertex(steps.get(3)), is(true));
	}

	@Test public void can_return_a_popgraph_equal_to_self_plus_multiple_steps_via_varargs() {
		Step firstStep = mock(Step.class);
		Step secondStep = mock(Step.class);
		POPGraph emptyGraph = newEmptyPopGraph();
		POPGraph twoStepGraph = emptyGraph.addSteps(firstStep, secondStep);

		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(firstStep), is(true));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(secondStep), is(true));

		Step thirdStep = mock(Step.class);
		Step fourthStep = mock(Step.class);
		POPGraph fourStepGraph = twoStepGraph.addSteps(thirdStep, fourthStep);

		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(thirdStep), is(false));
		assertThat(twoStepGraph.toDirectedAcyclicGraph().containsVertex(fourthStep), is(false));
		assertThat(fourStepGraph.toDirectedAcyclicGraph().containsVertex(thirdStep), is(true));
		assertThat(fourStepGraph.toDirectedAcyclicGraph().containsVertex(fourthStep), is(true));
	}

	@Test public void can_copy_self() {
		POPGraph graph = newEmptyPopGraph();
		POPGraph copyOfGraph = graph.copy();
		assertThat(graph, is(equalTo(copyOfGraph)));
	}

	@Test public void affecting_copies_will_not_affect_original() {
		Step onlyStep = mock(Step.class);
		POPGraph graph = newEmptyPopGraph();
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
		POPGraph graph = newEmptyPopGraph().addStep(firstStep).addStep(secondStep);
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
		POPGraph emptyGraph = newEmptyPopGraph();
		assertThat(emptyGraph.stepSet().isEmpty(), is(true));

		Step onlyStep = mock(Step.class);
		POPGraph singleStepGraph = newEmptyPopGraph().addStep(onlyStep);
		assertThat(singleStepGraph.stepSet().size(), is(1));
		assertThat(singleStepGraph.stepSet(), contains(onlyStep));
	}

	@Test public void can_return_set_of_edges() throws Exception {
		POPGraph emptyGraph = newEmptyPopGraph();
		assertThat(emptyGraph.edgeSet().isEmpty(), is(true));

		Step firstStep = mock(Step.class);
		Step secondStep = mock(Step.class);
		POPGraph singleEdgeGraph = newEmptyPopGraph().addStep(firstStep)
			.addStep(secondStep)
			.addEdge(firstStep, secondStep);
		assertThat(singleEdgeGraph.edgeSet().size(), is(1));
		DefaultEdge edge = singleEdgeGraph.graph().getEdge(firstStep, secondStep);
	}

	@Test public void can_return_string_representation_of_self() throws CloneTypeMismatchException {
		POPGraph emptyGraph = newEmptyPopGraph();
		assertThat(emptyGraph.toString(), equalTo(emptyGraph.graph().toString()));
	}

	@Test public void has_a_directed_acyclic_graph()
			throws CloneTypeMismatchException {
		Class<DirectedAcyclicGraph> graphClass = DirectedAcyclicGraph.class;
		assertThat(newEmptyPopGraph().graph(), is(instanceOf(graphClass)));
	}

	@Test public void is_equal_to_an_identical_popgraph() {
		POPGraph graph = newEmptyPopGraph();
		assertThat(graph.equals(graph), is(true));
	}

	@Test public void is_not_equal_to_non_identical_popgraphs() throws Exception {
		POPGraph graph = newEmptyPopGraph();
		POPGraph differentGraph = newEmptyPopGraph().addStep(mock(Step.class));
		assertThat(graph, is(not(equalTo(differentGraph))));
	}

	@Test public void is_not_equal_to_non_popgraphs() {
		assertThat(newEmptyPopGraph(), is(not(equalTo(mock(Object.class)))));
	}

	@Test public void can_add_an_edge_between_steps() throws Exception {
		Step stepOne = stepOne();
		Step stepTwo = stepTwo();
		POPGraph newGraph = newEmptyPopGraph()
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
