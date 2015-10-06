package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.util.ImmutableList;

public class PlanSpaceNode {

	public final PlanSpaceNode parent;
	public final ImmutableList<Step> steps;
	public final Bindings bindings;
	public final Orderings orderings;
	public final ImmutableList<CausalLink> causalLinks;
	public final FlawList flaws;
	int visited = 0;
	int expanded = 0;
	
	protected PlanSpaceNode(PlanSpaceNode parent, ImmutableList<Step> steps, Bindings bindings, Orderings orderings, ImmutableList<CausalLink> causalLinks, FlawList flaws) {
		this.parent = parent;
		this.steps = steps;
		this.bindings = bindings;
		this.orderings = orderings;
		this.causalLinks = causalLinks;
		this.flaws = flaws;
		PlanSpaceNode ancestor = parent;
		while(ancestor != null) {
			ancestor.expanded++;
			ancestor = ancestor.parent;
		}
	}
	
	protected PlanSpaceNode(Problem problem) {
		this.parent = null;
		Step start = new Step(Expression.TRUE, problem.initial.toExpression());
		Step end = new Step(problem.goal, Expression.TRUE);
		this.steps = new ImmutableList<Step>().add(start).add(end);
		this.bindings = Bindings.EMPTY;
		this.orderings = new Orderings().add(start, end);
		this.causalLinks = new ImmutableList<>();
		this.flaws = new FlawList(end);
	}
	
	public PlanSpaceRoot getRoot() {
		PlanSpaceNode current = this;
		while(!(current instanceof PlanSpaceRoot))
			current = current.parent;
		return (PlanSpaceRoot) current;
	}
	
	void expand() {
		// Check search limit.
		PlanSpaceRoot root = getRoot();
		if(root.limit == root.visited)
			throw new SearchLimitReachedException();
		
		// Notify all ancestors that this node has been visited.
		PlanSpaceNode ancestor = parent;
		while(ancestor != null) {
			ancestor.visited++;
			ancestor = ancestor.parent;
		}
	}
}
