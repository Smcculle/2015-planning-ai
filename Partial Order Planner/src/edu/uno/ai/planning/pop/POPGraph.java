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

	public DirectedAcyclicGraph<Step, DefaultEdge> graph() {
		return this.graph;
	}
}
