package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import edu.uno.ai.planning.Settings;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Predication;

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

	@Test public void has_a_directed_acyclic_graph()
			throws CloneTypeMismatchException {
		Class<DirectedAcyclicGraph> graphClass = DirectedAcyclicGraph.class;
		assertThat(popGraph().graph(), is(instanceOf(graphClass)));
	}

	@Test public void can_add_steps() throws Exception {
		Step step = stepOne();
		POPGraph newGraph = popGraph().addStep(step);
		assertThat(newGraph.graph().containsVertex(step), is(true));
	}
}
