package edu.uno.ai.planning.pop;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import edu.uno.ai.planning.Step;

public class POPGraph {
	private DirectedAcyclicGraph<Step, DefaultEdge> graph;

	public POPGraph() {
		Class<DefaultEdge> edgeClass = DefaultEdge.class;
		this.graph = new DirectedAcyclicGraph<Step, DefaultEdge>(edgeClass);
	}

	public POPGraph(DirectedAcyclicGraph<Step, DefaultEdge> graph) {
		this.graph = graph;
	}

	@SuppressWarnings("unchecked")
	public DirectedAcyclicGraph<Step, DefaultEdge> graph()
			throws CloneTypeMismatchException {
		Object clone = this.graph.clone();
		if (clone instanceof DirectedAcyclicGraph) {
			return (DirectedAcyclicGraph<Step, DefaultEdge>)clone;
		}
		throw new CloneTypeMismatchException("Graph clone is not a DirectedAcyclicGraph");
	}
}
