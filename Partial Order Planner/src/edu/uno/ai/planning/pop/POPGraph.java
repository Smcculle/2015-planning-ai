package edu.uno.ai.planning.pop;

import java.util.*;

import org.jgrapht.experimental.dag.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.*;
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

	public POPGraph addEdge(Step fromStep, Step toStep) throws DirectedAcyclicGraph.CycleFoundException {
		POPGraph copy = this.copy();
		copy.graph.addDagEdge(fromStep, toStep);
		return copy;
	}

	public POPGraph addStep(Step newStep) {
		POPGraph copy = this.copy();
		copy.graph.addVertex(newStep);
		return copy;
	}

	public POPGraph addSteps(Iterable<Step> steps) {
		POPGraph copy = this.copy();
		for(Step step : steps) {
			copy = copy.addStep(step);
		}
		return copy;
	}

	public POPGraph addSteps(Step... steps) {
		POPGraph graph = this.copy();
		for(Step step : steps) {
			graph = graph.addStep(step);
		}
		return graph;
	}

	public boolean containsEdge(DefaultEdge edge) {
		return this.graph.containsEdge(edge);
	}

	public boolean containsStep(Step step) {
		return this.graph.containsVertex(step);
	}

	public POPGraph copy() {
		POPGraph copy = new POPGraph();
		for(Step step : this.graph.vertexSet()) {
			copy.graph.addVertex(step);
		}
		for(DefaultEdge edge : this.graph.edgeSet()) {
			try {
				copy.graph.addDagEdge(
					this.graph.getEdgeSource(edge),
					this.graph.getEdgeTarget(edge)
				);
			} catch (CycleFoundException e) {
				System.out.println(
					"You should not have been allowed to clone a POPGraph " +
					"with a cycle (according to JGraphT docs)."
				);
				e.printStackTrace();
			}
		}
		return copy;
	}

	public Iterator<Step> iterator() {
		return this.graph.iterator();
	}

	public DefaultEdge edgeBetween(Step source, Step target) {
		return this.graph.getEdge(source, target);
	}

	public Set<DefaultEdge> edgeSet() {
		return this.graph.edgeSet();
	}

	@Override
	public boolean equals(Object object) {
		boolean result = false;

		if (object instanceof POPGraph) {
			result = this.graph.equals(((POPGraph) object).graph);
		}

		return result;
	}

	public POPGraph promote(Step source, Step target) throws DirectedAcyclicGraph.CycleFoundException {
		return this.addEdge(source, target);
	}

	public Set<Step> stepSet() {
		return this.graph.vertexSet();
	}

	public DirectedAcyclicGraph<Step, DefaultEdge> toDirectedAcyclicGraph() {
		return this.graph;
	}

	@Override
	public String toString() {
		return this.graph.toString();
	}
}
