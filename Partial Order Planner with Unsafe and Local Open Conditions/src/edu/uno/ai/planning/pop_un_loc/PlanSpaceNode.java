package edu.uno.ai.planning.pop_un_loc;

import java.util.PriorityQueue;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;

public class PlanSpaceNode {

	private static int nextID = 0;
	
	public final int id = nextID++;
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
	
	@Override
	public String toString() {
		String str = "=== PARTIAL ORDER PLAN " + id + " ===\nSTEPS:" + toString(orderings, bindings);
		str += "\nBINDINGS: " + bindings;
		str += "\n" + orderings.toString(bindings);
		str += "\nCAUSAL LINKS:" + toString(causalLinks, bindings);
		str += "\n" + flaws.toString(bindings);
		return str;
	}
	
	private static final String toString(Iterable<? extends Partial> list, Substitution substitution) {
		String str = "";
		for(Partial element : list)
			str += "\n  " + element.toString(substitution);
		return str;
	}
		
	public PlanSpaceRoot getRoot() {
		PlanSpaceNode current = this;
		while(!(current instanceof PlanSpaceRoot))
			current = current.parent;
		return (PlanSpaceRoot) current;
	}
	
	void expand(PriorityQueue<PlanSpaceNode> queue) {
		// Check search limit.
		PlanSpaceRoot root = getRoot();
		if(root.limit == root.visited)
			throw new SearchLimitReachedException();
		// Repair flaw.
		Flaw flaw = flaws.chooseFlaw();
		if(flaw instanceof OpenPreconditionFlaw)
			fix((OpenPreconditionFlaw) flaw, queue);
		else
			fix((ThreatenedCausalLinkFlaw) flaw, queue);
		// Notify all ancestors that this node has been visited.
		PlanSpaceNode ancestor = parent;
		while(ancestor != null) {
			ancestor.visited++;
			ancestor = ancestor.parent;
		}
	}
	
	private final void fix(OpenPreconditionFlaw flaw, PriorityQueue<PlanSpaceNode> queue) {
		// Consider all existing steps.
		for(Step step : steps)
			fix(flaw, step, queue);
		// Consider adding a new step of each operator type.
		for(Operator operator : getRoot().problem.domain.operators)
			fix(flaw, new Step(operator), queue);
	}
	
	private final void fix(OpenPreconditionFlaw flaw, Step step, PriorityQueue<PlanSpaceNode> queue) {
		// Check each effect of the step.
		for(Literal effect : step.effects) {
			Bindings newBindings = flaw.precondition.unify(effect, bindings);
			if(newBindings != null) {
				// The tail of the causal link must come before the head.
				Orderings newOrderings = orderings.add(step, flaw.step);
				if(newOrderings != null) {
					ImmutableList<Step> newSteps = steps;
					FlawList newFlaws = flaws.remove(flaw);
					// If the step is new, add the step, its orderings, and its flaws.
					if(!steps.contains(step)) {
						newSteps = newSteps.add(step);
						Step start = null;
						Step end = null;
						for(Step s : steps) {
							if(s.isStart())
								start = s;
							if(s.isEnd())
								end = s;
						}
						newOrderings = newOrderings.add(start, step).add(step, end);
						for(Literal precondition : step.preconditions)
							newFlaws = newFlaws.add(new OpenPreconditionFlaw(step, precondition));
					}
					// Create a new causal link.
					ImmutableList<CausalLink> newCausalLinks = causalLinks.add(new CausalLink(step, flaw.precondition, flaw.step));
					// Check for threats.
					newFlaws = checkForThreats(newSteps, newBindings, newOrderings, newCausalLinks, newFlaws);
					// Create the new child node.
					PlanSpaceNode newNode = new PlanSpaceNode(this, newSteps, newBindings, newOrderings, newCausalLinks, newFlaws);
					queue.add(newNode);
				}
			}
		}
	}
	
	private final FlawList checkForThreats(ImmutableList<Step> steps, Bindings bindings, Orderings orderings, ImmutableList<CausalLink> causalLinks, FlawList flaws) {
		// For each causal link...
		for(CausalLink link : causalLinks) {
			Literal label = link.label.negate().substitute(bindings);
			// Label must be ground to be a definite threat.
			if(label.isGround()) {
				// For each step...
				for(Step step : steps) {
					// It must be possible to order the step between the tail and head of the causal link.
					if(orderings.allows(link.tail, step, link.head)) {
						// For each effect of the step...
						for(Literal effect : step.effects) {
							effect = effect.substitute(bindings);
							// If the effect is identical to the negated label, this is a definite threat.
							if(effect.equals(label))
								flaws = flaws.add(new ThreatenedCausalLinkFlaw(link, step));
						}
					}
				}
			}
		}
		return flaws;
	}
	
	private final void fix(ThreatenedCausalLinkFlaw flaw, PriorityQueue<PlanSpaceNode> queue) {
		FlawList newFlaws = flaws.remove(flaw);
		// Promote
		Orderings promote = orderings.add(flaw.link.head, flaw.threat);
		if(promote != null) {
			PlanSpaceNode newNode = new PlanSpaceNode(this, steps, bindings, promote, causalLinks, newFlaws);
			queue.add(newNode);
		}
		// Demote
		Orderings demote = orderings.add(flaw.threat, flaw.link.tail);
		if(demote != null) {
			PlanSpaceNode newNode = new PlanSpaceNode(this, steps, bindings, demote, causalLinks, newFlaws);
			queue.add(newNode);
		}
	}
}
