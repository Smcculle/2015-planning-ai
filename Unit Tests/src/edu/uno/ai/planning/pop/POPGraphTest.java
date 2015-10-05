package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.jgrapht.experimental.dag.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.*;
import org.jgrapht.graph.*;
import org.junit.*;

import edu.uno.ai.planning.ss.*;

public class POPGraphTest {
	private Class<POPGraph> describedClass() {
		return POPGraph.class;
	}

	private POPGraph newEmptyPopGraph() {
		return new POPGraph();
	}

	@Test
	public void adding_a_step_does_not_affect_self() {
		PartialStep firstStep = mock(PartialStep.class);
		POPGraph oneStepGraph = newEmptyPopGraph().addStep(firstStep);
		PartialStep secondStep = mock(PartialStep.class);
		oneStepGraph.addStep(secondStep);

		assertThat(oneStepGraph, not(contains(secondStep)));
	}

	@Test
	public void adding_an_edge_does_not_affect_self() throws CycleFoundException {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);
		twoStepGraph.addEdge(firstStep, secondStep);

		assertThat(twoStepGraph.edgeSet(), is(empty()));
	}

	@Test
	public void adding_an_edge_creates_a_copy_of_self_with_the_edge() throws CycleFoundException {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);
		POPGraph connectedStepGraph = twoStepGraph.addEdge(firstStep, secondStep);
		DefaultEdge onlyEdge = connectedStepGraph.edgeBetween(firstStep, secondStep);

		assertThat(connectedStepGraph.edgeSet(), contains(onlyEdge));
	}

	@Test
	public void adding_steps_does_not_create_edges() {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph graph = newEmptyPopGraph().addStep(firstStep).addStep(secondStep);

		assertThat(graph.edgeSet(), is(empty()));
	}

	@Test
	public void adding_steps_via_iterable_does_not_affect_self() {
		ArrayList<PartialStep> steps = new ArrayList<PartialStep>();
		steps.add(mock(PartialStep.class));
		steps.add(mock(PartialStep.class));
		POPGraph emptyGraph = newEmptyPopGraph();
		emptyGraph.addSteps(steps);

		assertThat(emptyGraph, not(contains(steps.get(0))));
		assertThat(emptyGraph, not(contains(steps.get(1))));
	}

	@Test
	public void adding_steps_via_varargs_does_not_affect_self() {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph emptyGraph = newEmptyPopGraph();
		emptyGraph.addSteps(firstStep, secondStep);

		assertThat(emptyGraph, not(contains(firstStep)));
		assertThat(emptyGraph, not(contains(secondStep)));
	}

	@Test
	public void affecting_copies_does_not_affect_original() {
		PartialStep onlyStep = mock(PartialStep.class);
		POPGraph graph = newEmptyPopGraph();
		POPGraph modifiedCopyOfGraph = graph.copy().addStep(onlyStep);

		assertThat(graph,is(not(equalTo(modifiedCopyOfGraph))));
		assertThat(graph.containsStep(onlyStep), is(false));
		assertThat(modifiedCopyOfGraph.containsStep(onlyStep), is(true));
	}

	@Test
	public void can_demote_a_step_after_anoter() throws CycleFoundException {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		PartialStep thirdStep = mock(PartialStep.class);
		PartialStep fourthStep = mock(PartialStep.class);
		POPGraph fourStepGraph = newEmptyPopGraph().addSteps(
			firstStep,
			secondStep,
			thirdStep,
			fourthStep
		);
		POPGraph connectedGraph = fourStepGraph
			.addEdge(firstStep, secondStep)
			.addEdge(secondStep, fourthStep)
			.addEdge(firstStep, thirdStep)
			.addEdge(thirdStep, fourthStep);

		assertThat(connectedGraph, contains(firstStep, secondStep, thirdStep, fourthStep));

		POPGraph demotedGraph = connectedGraph.demote(secondStep, thirdStep);
		DefaultEdge edge = demotedGraph.edgeBetween(thirdStep, secondStep);

		assertThat(edge, notNullValue());
		assertThat(demotedGraph, contains(firstStep, thirdStep, secondStep, fourthStep));
	}

	@Test
	public void can_promote_a_step_before_another() throws CycleFoundException {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		PartialStep thirdStep = mock(PartialStep.class);
		PartialStep fourthStep = mock(PartialStep.class);
		POPGraph fourStepGraph = newEmptyPopGraph().addSteps(
			firstStep,
			secondStep,
			thirdStep,
			fourthStep
		);
		POPGraph connectedGraph = fourStepGraph
			.addEdge(firstStep, secondStep)
			.addEdge(secondStep, fourthStep)
			.addEdge(firstStep, thirdStep)
			.addEdge(thirdStep, fourthStep);

		assertThat(connectedGraph, contains(firstStep, secondStep, thirdStep, fourthStep));

		POPGraph promotedGraph = connectedGraph.promote(thirdStep, secondStep);
		DefaultEdge edge = promotedGraph.edgeBetween(thirdStep, secondStep);

		assertThat(edge, notNullValue());
		assertThat(promotedGraph, contains(firstStep, thirdStep, secondStep, fourthStep));
	}

	@Test
	public void can_report_if_it_contains_an_edge() throws Exception {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);
		POPGraph connectedTwoStepGraph = twoStepGraph.addEdge(firstStep, secondStep);
		DefaultEdge edge = connectedTwoStepGraph.edgeBetween(firstStep, secondStep);

		assertThat(twoStepGraph.containsEdge(edge), is(false));
		assertThat(connectedTwoStepGraph.containsEdge(edge), is(true));
	}

	@Test
	public void can_report_if_it_contains_a_step() {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph oneStepGraph = newEmptyPopGraph().addStep(firstStep);
		POPGraph twoStepGraph = oneStepGraph.addStep(secondStep);

		assertThat(oneStepGraph.containsStep(firstStep), is(true));
		assertThat(oneStepGraph.containsStep(secondStep), is(false));
		assertThat(twoStepGraph.containsStep(firstStep), is(true));
		assertThat(twoStepGraph.containsStep(secondStep), is(true));
	}

	@Test
	public void can_return_a_popgraph_equal_to_self_plus_multiple_steps_via_an_iterable() {
		ArrayList<PartialStep> steps = new ArrayList<PartialStep>();
		steps.add(mock(PartialStep.class));
		steps.add(mock(PartialStep.class));
		POPGraph emptyGraph = newEmptyPopGraph();
		POPGraph twoStepGraph = emptyGraph.addSteps(steps);

		assertThat(emptyGraph.containsStep(steps.get(0)), is(false));
		assertThat(emptyGraph.containsStep(steps.get(1)), is(false));
		assertThat(twoStepGraph.containsStep(steps.get(0)), is(true));
		assertThat(twoStepGraph.containsStep(steps.get(1)), is(true));

		steps.add(mock(PartialStep.class));
		steps.add(mock(PartialStep.class));
		POPGraph fourStepGraph = emptyGraph.addSteps(steps);

		assertThat(twoStepGraph.containsStep(steps.get(2)), is(false));
		assertThat(twoStepGraph.containsStep(steps.get(3)), is(false));
		assertThat(twoStepGraph.containsStep(steps.get(0)), is(true));
		assertThat(fourStepGraph.containsStep(steps.get(1)), is(true));
		assertThat(fourStepGraph.containsStep(steps.get(2)), is(true));
		assertThat(fourStepGraph.containsStep(steps.get(3)), is(true));
	}

	@Test
	public void can_return_a_popgraph_equal_to_self_plus_multiple_steps_via_varargs() {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph emptyGraph = newEmptyPopGraph();
		POPGraph twoStepGraph = emptyGraph.addSteps(firstStep, secondStep);

		assertThat(twoStepGraph.containsStep(firstStep), is(true));
		assertThat(twoStepGraph.containsStep(secondStep), is(true));

		PartialStep thirdStep = mock(PartialStep.class);
		PartialStep fourthStep = mock(PartialStep.class);
		POPGraph fourStepGraph = twoStepGraph.addSteps(thirdStep, fourthStep);

		assertThat(twoStepGraph.containsStep(thirdStep), is(false));
		assertThat(twoStepGraph.containsStep(fourthStep), is(false));
		assertThat(fourStepGraph.containsStep(thirdStep), is(true));
		assertThat(fourStepGraph.containsStep(fourthStep), is(true));
	}

	@Test
	public void can_return_a_popgraph_equal_to_self_plus_new_step() {
		PartialStep onlyStep = mock(PartialStep.class);
		POPGraph emptyGraph = newEmptyPopGraph();
		POPGraph singleStepGraph = emptyGraph.addStep(onlyStep);

		assertThat(emptyGraph, not(equalTo(singleStepGraph)));
		assertThat(emptyGraph.containsStep(onlyStep), is(false));
		assertThat(singleStepGraph.containsStep(onlyStep), is(true));

		PartialStep extraStep = mock(PartialStep.class);
		POPGraph twoStepGraph = singleStepGraph.addStep(extraStep);

		assertThat(singleStepGraph.containsStep(extraStep), is(false));
		assertThat(twoStepGraph.containsStep(onlyStep), is(true));
		assertThat(twoStepGraph.containsStep(extraStep), is(true));
	}

	@Test
	public void can_return_a_total_order_plan_representation_of_self() {
		POPGraph graph = newEmptyPopGraph();

		assertThat(graph.toTotalOrderPlan(), instanceOf(TotalOrderPlan.class));
	}

	@Test
	public void can_return_an_existing_edge_between_two_steps() throws Exception {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);
		POPGraph connectedTwoStepGraph = twoStepGraph.addEdge(firstStep, secondStep);

		assertThat(connectedTwoStepGraph.edgeBetween(firstStep, secondStep), notNullValue());
	}

	@Test
	public void can_return_directed_acyclic_graph_representation_of_self() {
		DirectedAcyclicGraph<PartialStep, DefaultEdge> emptyDirectedAcyclicGraph =
			new DirectedAcyclicGraph<>(DefaultEdge.class);
		POPGraph emptyGraph = new POPGraph(emptyDirectedAcyclicGraph);

		assertThat(emptyGraph.toDirectedAcyclicGraph(), equalTo(emptyDirectedAcyclicGraph));

		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		DirectedAcyclicGraph<PartialStep, DefaultEdge> directedAcyclicGraph =
			new DirectedAcyclicGraph<>(DefaultEdge.class);
		directedAcyclicGraph.addVertex(firstStep);
		directedAcyclicGraph.addVertex(secondStep);
		directedAcyclicGraph.addEdge(firstStep, secondStep);
		POPGraph graph = new POPGraph(directedAcyclicGraph);

		assertThat(graph.toDirectedAcyclicGraph(), equalTo(directedAcyclicGraph));
	}

	@Test
	public void can_return_set_of_steps() {
		POPGraph emptyGraph = newEmptyPopGraph();
		assertThat(emptyGraph.stepSet().isEmpty(), is(true));

		PartialStep onlyStep = mock(PartialStep.class);
		POPGraph singleStepGraph = newEmptyPopGraph().addStep(onlyStep);

		assertThat(singleStepGraph.stepSet().size(), is(1));
		assertThat(singleStepGraph.stepSet(), contains(onlyStep));
	}

	@Test
	public void can_return_set_of_edges() throws Exception {
		POPGraph emptyGraph = newEmptyPopGraph();
		assertThat(emptyGraph.edgeSet().isEmpty(), is(true));

		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph singleEdgeGraph = newEmptyPopGraph().addStep(firstStep)
			.addStep(secondStep)
			.addEdge(firstStep, secondStep);

		assertThat(singleEdgeGraph.edgeSet().size(), is(1));
	}

	@Test
	public void can_return_string_representation_of_self() {
		POPGraph emptyGraph = newEmptyPopGraph();

		assertThat(emptyGraph.toString(), equalTo(emptyGraph.toDirectedAcyclicGraph().toString()));
	}

	@Test
	public void cannot_add_a_duplicate_step() {
		PartialStep onlyStep = mock(PartialStep.class);
		POPGraph oneStepGraph = newEmptyPopGraph().addStep(onlyStep);
		POPGraph duplicateStepGraph = oneStepGraph.addStep(onlyStep);

		assertThat(oneStepGraph, contains(onlyStep));
		assertThat(duplicateStepGraph, contains(onlyStep));

		ArrayList<PartialStep> steps = new ArrayList<PartialStep>();
		steps.add(mock(PartialStep.class));
		steps.add(mock(PartialStep.class));
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(steps).addSteps(steps);

		assertThat(twoStepGraph, contains(steps.get(0), steps.get(1)));

		twoStepGraph = twoStepGraph.addSteps(steps.get(0), steps.get(1));

		assertThat(twoStepGraph, contains(steps.get(0), steps.get(1)));
	}

	@Test
	public void copies_are_equal_to_self() {
		POPGraph original = newEmptyPopGraph();
		POPGraph copy = original.copy();

		assertThat(original, equalTo(copy));
	}

	@Test
	public void copies_are_not_the_same_instance_as_self() {
		POPGraph original = newEmptyPopGraph();
		POPGraph copy = original.copy();

		assertThat(copy, is(not(sameInstance(original))));
	}

	@Test
	public void copying_does_not_damage_iterator() {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph graph = newEmptyPopGraph().addStep(firstStep).addStep(secondStep);

		assertThat(graph, contains(firstStep, secondStep));
	}

	@Test
	public void implements_iterable() {
		assertThat(describedClass(), typeCompatibleWith(Iterable.class));
	}

	@Test
	public void is_equal_to_an_identical_popgraph() {
		POPGraph graph = newEmptyPopGraph();

		assertThat(graph.equals(graph), is(true));
	}

	@Test
	public void is_not_equal_to_non_identical_popgraphs() {
		POPGraph graph = newEmptyPopGraph();
		POPGraph differentGraph = newEmptyPopGraph().addStep(mock(PartialStep.class));

		assertThat(graph, is(not(equalTo(differentGraph))));
	}

	@Test
	public void is_not_equal_to_non_popgraphs() {
		assertThat(newEmptyPopGraph(), is(not(equalTo(mock(Object.class)))));
	}

	@Test
	public void iteration_follows_topological_ordering() throws Exception {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		PartialStep thirdStep = mock(PartialStep.class);
		PartialStep fourthStep = mock(PartialStep.class);
		POPGraph fourStepGraph = newEmptyPopGraph().addSteps(
			firstStep,
			secondStep,
			thirdStep,
			fourthStep
		);
		POPGraph graph = fourStepGraph
			.addEdge(firstStep, secondStep)
			.addEdge(thirdStep, fourthStep)
			.addEdge(firstStep, thirdStep)
			.addEdge(thirdStep, secondStep);

		assertThat(graph, contains(firstStep, thirdStep, secondStep, fourthStep));

		graph = fourStepGraph
			.addEdge(firstStep, secondStep)
			.addEdge(firstStep, thirdStep)
			.addEdge(secondStep, fourthStep)
			.addEdge(thirdStep, fourthStep)
			.addEdge(thirdStep, secondStep);

		assertThat(graph, contains(firstStep, thirdStep, secondStep, fourthStep));
	}

	@Test
	public void newly_added_steps_have_no_edges() {
		PartialStep mockedStep = mock(PartialStep.class);
		POPGraph newGraph = newEmptyPopGraph().addStep(mockedStep);
		Set<DefaultEdge> edgesOfNewStep = newGraph.toDirectedAcyclicGraph().edgesOf(mockedStep);

		assertThat(edgesOfNewStep, is(empty()));
	}

	@Test
	public void steps_are_only_added_once() {
		PartialStep onlyStep = mock(PartialStep.class);
		POPGraph graph = newEmptyPopGraph().addStep(onlyStep);

		assertThat(graph, contains(onlyStep));
	}

	@Test
	public void will_return_null_if_edge_between_two_steps_does_not_exist() {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);

		assertThat(twoStepGraph.edgeBetween(firstStep, secondStep), nullValue());
	}

	@Test(expected = DirectedAcyclicGraph.CycleFoundException.class)
	public void will_throw_cycle_found_exception_if_new_edge_causes_cycle() throws CycleFoundException {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);
		POPGraph connectedGraph = twoStepGraph.addEdge(firstStep, secondStep);

		connectedGraph.addEdge(secondStep, firstStep);
	}

	@Test(expected = DirectedAcyclicGraph.CycleFoundException.class)
	public void will_throw_cycle_found_exception_if_promotion_causes_cycle() throws CycleFoundException {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);
		POPGraph connectedGraph = twoStepGraph.addEdge(firstStep, secondStep);

		connectedGraph.promote(secondStep, firstStep);
	}

	@Test(expected = DirectedAcyclicGraph.CycleFoundException.class)
	public void will_throw_cycle_found_exception_if_demotion_causes_cycle() throws CycleFoundException {
		PartialStep firstStep = mock(PartialStep.class);
		PartialStep secondStep = mock(PartialStep.class);
		POPGraph twoStepGraph = newEmptyPopGraph().addSteps(firstStep, secondStep);
		POPGraph connectedGraph = twoStepGraph.addEdge(firstStep, secondStep);

		connectedGraph.demote(firstStep, secondStep);
	}
}
