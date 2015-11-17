package edu.uno.ai.planning.gp;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.StepNode;
import edu.uno.ai.planning.util.ImmutableList;

class StepPermutationIterator implements Iterator<ImmutableList<StepNode>> {

	private final int level;
	private final ImmutableList<LiteralNode> goals;
	private final StepNode[][] groups;
	private final int[] indices;
	private ImmutableList<StepNode> next = null;
	
	StepPermutationIterator(int level, ImmutableList<LiteralNode> goals) {
		this.level = level;
		this.goals = goals;
		this.groups = new StepNode[goals.length][];
		for(int i=0; i<groups.length; i++) {
			groups[i] = makeGroup(goals.first);
			goals = goals.rest;
		}
		this.indices = new int[groups.length];
		ImmutableList<StepNode> next = new ImmutableList<>();
		for(int i=0; i<groups.length; i++) {
			if(groups[i].length == 0)
				return;
			else
				next = next.add(groups[i][0]);
		}
		this.next = new ImmutableList<>();
		this.next = findNext();
		if(this.next != null && allPersistence(this.next))
			this.next = findNext();
	}
	
	private final StepNode[] makeGroup(LiteralNode goal) {
		ArrayList<StepNode> list = new ArrayList<>();
		for(StepNode producer : goal.getProducers(level)) {
			if(producer.persistence)
				list.add(0, producer);
			else
				list.add(producer);
		}
		return list.toArray(new StepNode[list.size()]);
	}
	
	private static final boolean allPersistence(ImmutableList<StepNode> steps) {
		if(steps.length == 0)
			return true;
		else if(!steps.first.persistence)
			return false;
		else
			return allPersistence(steps.rest);
	}
	
	private final ImmutableList<StepNode> findNext() {
		return findNext(0, new ImmutableList<>());
	}
	
	private final ImmutableList<StepNode> findNext(int index, ImmutableList<StepNode> steps) {
		if(index == groups.length) {
			if(steps.equals(next))
				return null;
			else
				return steps;
		}
		else {
			do {
				StepNode step = groups[index][indices[index]];
				if(canAdd(step, steps)) {
					ImmutableList<StepNode> result = findNext(index + 1, add(step, steps));
					if(result != null)
						return result;
				}
				indices[index]++;
			} while(indices[index] < groups[index].length);
			indices[index] = 0;
			return null;
		}
	}
	
	private final boolean canAdd(StepNode step, ImmutableList<StepNode> steps) {
		if(steps.length == 0)
			return true;
		else if(step.mutex(steps.first, level))
			return false;
		else
			return canAdd(step, steps.rest);
	}
	
	private static final ImmutableList<StepNode> add(StepNode step, ImmutableList<StepNode> steps) {
		if(steps.contains(step))
			return steps;
		else
			return steps.add(step);
	}
	
	@Override
	public String toString() {
		String str = "Step Permutation at Level " + level + ":";
		if(next == null)
			str += "\n  none";
		else {
			Iterator<LiteralNode> goals = this.goals.iterator();
			for(int i=0; i<groups.length; i++)
				str += "\n  " + goals.next() + " via " + groups[i][indices[i]];
		}
		return str;
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public ImmutableList<StepNode> next() {
		ImmutableList<StepNode> steps = next;
		next = findNext();
		return steps;
	}
}
