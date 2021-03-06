package edu.uno.ai.planning.gp;

import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.pg.StepNode;
import edu.uno.ai.planning.util.ImmutableList;

public class SubgraphNode {
	
	private static final TotalOrderPlan EMPTY = new TotalOrderPlan();

	public final SubgraphNode parent;
	public final TotalOrderPlan plan;
	public final int level;
	public final ImmutableList<LiteralNode> goals;
	int descendants = 0;
	private final StepPermutationIterator permutations;
	
	SubgraphNode(PlanGraph graph) {
		this.parent = null;
		this.plan = EMPTY;
		this.level = graph.size() - 1;
		this.goals = toList(graph.goals);
		this.permutations = new StepPermutationIterator(level, goals);
	}
	
	SubgraphNode(SubgraphNode parent, TotalOrderPlan plan, int level, ImmutableList<LiteralNode> goals) {
		this.parent = parent;
		this.plan = plan;
		this.level = level;
		this.goals = goals;
		this.permutations = new StepPermutationIterator(level, goals);
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
	
	@Override
	public String toString() {
		String str;
		if(parent == null)
			str = "Subgraph:";
		else
			str = parent.toString();
		str += "\n  Level " + level + " goals:";
		for(LiteralNode literal : goals)
			str += " " + literal.literal;
		return str;
	}
	
	public SubgraphRoot getRoot() {
		SubgraphNode node = this;
		while(node.parent != null)
			node = node.parent;
		return (SubgraphRoot) node;
	}
	
	public SubgraphNode expand() {
		// If this would violate the search limit, throw an exception.
		SubgraphRoot root = getRoot();
		if(root.limit == root.descendants)
			throw new SearchLimitReachedException();
		// Loop until we generate a child node or run out of permutations.
		while(true) {
			// If this node has no more children, return null.
			if(!permutations.hasNext())
				return null;
			// Get the next permutation of steps.
			ImmutableList<StepNode> steps = permutations.next();
			// My child's plan is my plan plus all non-persistence steps.
			TotalOrderPlan childPlan = this.plan;
			for(StepNode stepNode : steps)
				if(!stepNode.persistence)
					childPlan = childPlan.add(stepNode.step);
			// My child's level is one level earlier than mine.
			int childLevel = level - 1;
			// My child's goals are the preconditions of the steps at my level.
			ImmutableList<LiteralNode> childGoals = new ImmutableList<>();
			for(StepNode stepNode : steps)
				for(LiteralNode precondition : stepNode.getPreconditions(level))
					if(!childGoals.contains(precondition))
						childGoals = childGoals.add(precondition);
			// If any of the child's goals are mutex, try the next permutation.
			// Otherwise, return the child node.
			if(!anyMutex(childGoals, childLevel))
				return new SubgraphNode(this, childPlan, childLevel, childGoals);
		}
	}
	
	private static final boolean anyMutex(ImmutableList<LiteralNode> literals, int level) {
		ImmutableList<LiteralNode> first = literals;
		while(first.length > 1) {
			ImmutableList<LiteralNode> second = first.rest;
			while(second.length > 0) {
				if(first.first.mutex(second.first, level))
					return true;
				second = second.rest;
			}
			first = first.rest;
		}
		return false;
	}
}