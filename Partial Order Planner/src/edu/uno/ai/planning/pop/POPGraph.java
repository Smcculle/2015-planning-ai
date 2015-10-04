package edu.uno.ai.planning.pop;

import java.util.*;

import org.jgrapht.experimental.dag.*;
import org.jgrapht.graph.*;

import edu.uno.ai.planning.*;

public class POPGraph {
	private DirectedAcyclicGraph<Step, DefaultEdge> graph;

	public POPGraph() {
		Class<DefaultEdge> edgeClass = DefaultEdge.class;
		this.graph = new DirectedAcyclicGraph<Step, DefaultEdge>(edgeClass);
	}

	public POPGraph(DirectedAcyclicGraph<Step, DefaultEdge> graph) {
		this.graph = graph;
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;

		if (object instanceof POPGraph) {
			result = this.graph.equals(((POPGraph) object).graph);
		}

		return result;
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

	public POPGraph addEdge(Step fromStep, Step toStep) throws Exception {
		DirectedAcyclicGraph<Step, DefaultEdge> newGraph = graph();
		newGraph.addDagEdge(fromStep, toStep);
		return new POPGraph(newGraph);
	}

	public POPGraph addStep(Step newStep) throws Exception {
		DirectedAcyclicGraph<Step, DefaultEdge> newGraph = graph();
		newGraph.addVertex(newStep);
		return new POPGraph(newGraph);
	}
}
