package edu.uno.ai.planning.gp;

import edu.uno.ai.planning.pg.Level;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.util.ImmutableList;

public class SubgraphNode {
	
	private static final TotalOrderPlan EMPTY = new TotalOrderPlan();

	public final SubgraphNode parent;
	public final TotalOrderPlan plan;
	public final Level level;
	public final ImmutableList<LiteralNode> goals;
	int descendants = 0;
	private final PowerSetIterator sets;
	
	SubgraphNode(PlanGraph graph) {
		this.parent = null;
		this.plan = EMPTY;
		this.level = graph.getLevel(graph.size() - 1);
		ImmutableList<LiteralNode> goals = new ImmutableList<>();
		for(LiteralNode goal : graph.goals)
			goals = goals.add(goal);
		this.goals = goals;
		this.sets = new PowerSetIterator(level, goals);
	}
	
	SubgraphNode(SubgraphNode parent, TotalOrderPlan plan, Level level, ImmutableList<LiteralNode> goals) {
		this.parent = parent;
		this.plan = plan;
		this.level = level;
		this.goals = goals;
		this.sets = new PowerSetIterator(level, goals);
		SubgraphNode ancestor = parent;
		while(ancestor != null) {
			ancestor.descendants++;
			ancestor = ancestor.parent;
		}
	}
	
	public SubgraphRoot getRoot() {
		SubgraphNode node = this;
		while(node.parent != null)
			node = node.parent;
		return (SubgraphRoot) node;
	}
}
