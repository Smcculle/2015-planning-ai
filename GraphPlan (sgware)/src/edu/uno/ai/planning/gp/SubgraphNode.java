package edu.uno.ai.planning.gp;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.pg.StepNode;
import edu.uno.ai.planning.util.ImmutableList;
import edu.uno.ai.planning.util.PowerSetIterator;

public class SubgraphNode {
	
	private static final TotalOrderPlan EMPTY = new TotalOrderPlan();

	public final SubgraphNode parent;
	public final TotalOrderPlan plan;
	public final int level;
	public final ImmutableList<LiteralNode> goals;
	int descendants = 0;
	private final PowerSetIterator<StepNode> sets;
	
	SubgraphNode(PlanGraph graph) {
		this.parent = null;
		this.plan = EMPTY;
		this.level = graph.size() - 1;
		this.goals = toList(graph.goals);
		this.sets = makePowerSetIterator(goals, level);
	}
	
	SubgraphNode(SubgraphNode parent, TotalOrderPlan plan, int level, ImmutableList<LiteralNode> goals) {
		this.parent = parent;
		this.plan = plan;
		this.level = level;
		this.goals = goals;
		this.sets = makePowerSetIterator(goals, level);
		SubgraphNode ancestor = parent;
		while(ancestor != null) {
			ancestor.descendants++;
			ancestor = ancestor.parent;
		}
	}
	
	private static final <T> ImmutableList<T> toList(Iterable<T> collection) {
		ImmutableList<T> list = new ImmutableList<>();
		for(T object : collection)
			list = list.add(object);
		return list;
	}
	
	private static final PowerSetIterator<StepNode> makePowerSetIterator(Iterable<LiteralNode> goals, int level) {
		LinkedHashSet<StepNode> steps = new LinkedHashSet<>();
		for(LiteralNode goal : goals)
			for(StepNode producer : goal.getProducers(level))
				steps.add(producer);
		return new PowerSetIterator<>(steps);
	}
	
	public SubgraphRoot getRoot() {
		SubgraphNode node = this;
		while(node.parent != null)
			node = node.parent;
		return (SubgraphRoot) node;
	}
	
	public SubgraphNode expand() {
		while(sets.hasNext()) {
			Set<StepNode> steps = sets.next();
			if(check(steps)) {
				TotalOrderPlan childPlan = this.plan;
				int childLevel = level - 1;
				LinkedHashSet<LiteralNode> childGoals = new LinkedHashSet<>();
				for(StepNode stepNode : steps) {
					if(!stepNode.persistence)
						childPlan = childPlan.add(stepNode.step);
					for(LiteralNode precondition : stepNode.getPreconditions(level))
						childGoals.add(precondition);
				}
				return new SubgraphNode(this, childPlan, childLevel, toList(childGoals));
			}
		}
		return null;
	}
	
	private final boolean check(Set<StepNode> steps) {
		return !allPersistence(steps) && !anyMutex(steps);
	}
	
	private final boolean allPersistence(Set<StepNode> steps) {
		for(StepNode step : steps)
			if(!step.persistence)
				return false;
		return true;
	}
	
	private final boolean anyMutex(Set<StepNode> steps) {
		StepNode[] array = steps.toArray(new StepNode[steps.size()]);
		for(int i=0; i<array.length; i++)
			for(int j=i+1; j<array.length; j++)
				if(array[i].mutex(array[j], level))
					return true;
		return false;
	}
}
