package edu.uno.ai.planning.pop;

import java.util.*;

import org.jgrapht.experimental.dag.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.*;
import org.jgrapht.graph.*;

import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.ss.*;

public class POPGraph implements Iterable<PartialStep> {
	private DirectedAcyclicGraph<PartialStep, DefaultEdge> graph;

	public POPGraph() {
		Class<DefaultEdge> edgeClass = DefaultEdge.class;
		this.graph = new DirectedAcyclicGraph<PartialStep, DefaultEdge>(edgeClass);
	}

	public POPGraph(DirectedAcyclicGraph<PartialStep, DefaultEdge> graph) {
		this.graph = graph;
	}

	public POPGraph addEdge(PartialStep fromStep, PartialStep toStep) throws DirectedAcyclicGraph.CycleFoundException {
		POPGraph copy = this.copy();
		copy.graph.addDagEdge(fromStep, toStep);
		return copy;
	}

	public POPGraph addStep(PartialStep newStep) {
		POPGraph copy = this.copy();
		copy.graph.addVertex(newStep);
		return copy;
	}

	public POPGraph addSteps(Iterable<PartialStep> steps) {
		POPGraph copy = this.copy();
		for(PartialStep step : steps) {
			copy = copy.addStep(step);
		}
		return copy;
	}

	public POPGraph addSteps(PartialStep... steps) {
		POPGraph graph = this.copy();
		for(PartialStep step : steps) {
			graph = graph.addStep(step);
		}
		return graph;
	}

	public boolean containsEdge(DefaultEdge edge) {
		return this.graph.containsEdge(edge);
	}

	public boolean containsStep(PartialStep step) {
		return this.graph.containsVertex(step);
	}

	public POPGraph copy() {
		POPGraph copy = new POPGraph();
		for(PartialStep step : this.graph.vertexSet()) {
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

	public POPGraph demote(PartialStep source, PartialStep target) throws DirectedAcyclicGraph.CycleFoundException {
		return this.addEdge(target, source);
	}

	public DefaultEdge edgeBetween(PartialStep source, PartialStep target) {
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

	@Override
	public Iterator<PartialStep> iterator() {
		return this.graph.iterator();
	}

	public POPGraph promote(PartialStep source, PartialStep target) throws DirectedAcyclicGraph.CycleFoundException {
		return this.addEdge(source, target);
	}

	public Set<PartialStep> stepSet() {
		return this.graph.vertexSet();
	}

	public DirectedAcyclicGraph<PartialStep, DefaultEdge> toDirectedAcyclicGraph() {
		return this.graph;
	}

	@Override
	public String toString() {
		return this.graph.toString();
	}

	public TotalOrderPlan toTotalOrderPlanWithBindings(Substitution substitution) {
		TotalOrderPlan plan = new TotalOrderPlan();

		for(PartialStep partialStep : this) {
			if (!partialStep.isStart() && !partialStep.isEnd()) {
				plan = plan.addStep(partialStep.makeStep(substitution));
			}
		}

		return plan;
	}
}
