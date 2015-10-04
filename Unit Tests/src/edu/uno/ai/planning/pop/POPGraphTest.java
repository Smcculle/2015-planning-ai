package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.Test;

public class POPGraphTest {
	private POPGraph popGraph() {
		return new POPGraph();
	}

	@Test public void has_a_directed_acyclic_graph() {
		Class<DirectedAcyclicGraph> graphClass = DirectedAcyclicGraph.class;
		assertThat(popGraph().graph(), is(instanceOf(graphClass)));
	}
}
