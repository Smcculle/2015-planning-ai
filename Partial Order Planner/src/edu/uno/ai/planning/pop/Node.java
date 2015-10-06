package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.util.ImmutableList;

public class Node {

	public final Node parent;
	public final ImmutableList<Step> steps;
	public final Bindings bindings;
	public final Orderings orderings;
	public final ImmutableList<CausalLink> causalLinks;
	public final FlawList flaws;
	
	Node(Node parent, ImmutableList<Step> steps, Bindings bindings, Orderings orderings, ImmutableList<CausalLink> causalLinks, FlawList flaws) {
		this.parent = parent;
		this.steps = steps;
		this.bindings = bindings;
		this.orderings = orderings;
		this.causalLinks = causalLinks;
		this.flaws = flaws;
	}
	
	Node() {
		this.parent = null;
		Step start = new Step(Step.NO_LITERALS, );
	}
}
