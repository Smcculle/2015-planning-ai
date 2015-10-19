package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.jgrapht.experimental.dag.*;
import org.jgrapht.graph.*;
import org.junit.*;

import edu.uno.ai.planning.*;

public class DirectedAcyclicGraphTest {
	private DirectedAcyclicGraph<Step, DefaultEdge> newEmptyGraph() {
		return new DirectedAcyclicGraph<Step, DefaultEdge>(DefaultEdge.class);
	}

	@Test public void vertices_are_added_once() {
		DirectedAcyclicGraph<Step, DefaultEdge> graph = newEmptyGraph();
		Step onlyStep = mock(Step.class);
		graph.addVertex(onlyStep);
		Iterator<Step> iterator = graph.iterator();

		assertTrue(iterator.hasNext());
		assertThat(iterator.next(), equalTo(onlyStep));
		assertFalse(iterator.hasNext());
	}

	@SuppressWarnings("unchecked")
	@Test public void clone_duplicates_vertices() {
		DirectedAcyclicGraph<Step, DefaultEdge> graph = newEmptyGraph();
		Step onlyStep = mock(Step.class);
		graph.addVertex(onlyStep);
		Iterator<Step> copyIterator =
			((DirectedAcyclicGraph<Step, DefaultEdge>) graph.clone())
			.iterator();

		assertThat(copyIterator.hasNext(), is(true));
		assertThat(copyIterator.next(), equalTo(onlyStep));
		assertThat(copyIterator.hasNext(), is(true));
		assertThat(copyIterator.next(), equalTo(onlyStep));
		assertThat(copyIterator.hasNext(), is(false));
	}
}
