package edu.uno.ai.planning.gp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.StepNode;

class StepPermutationIterator implements Iterator<Set<StepNode>> {

	private final int level;
	private final StepNode[][] steps;
	private final int[] indices;
	private boolean done = false;
	
	StepPermutationIterator(int level, Iterable<LiteralNode> goals) {
		this.level = level;
		ArrayList<StepNode[]> steps = new ArrayList<>();
		for(LiteralNode goal : goals)
			steps.add(toArray(goal.getProducers(level)));
		this.steps = new StepNode[steps.size()][];
		this.indices = new int[this.steps.length];
		advance();
	}
	
	private static final StepNode[] toArray(Iterable<StepNode> iterable) {
		ArrayList<StepNode> list = new ArrayList<>();
		for(StepNode step : iterable)
			list.add(step);
		return list.toArray(new StepNode[list.size()]);
	}
	
	private final StepNode step(int index) {
		return steps[index][indices[index]];
	}
	
	private final void advance() {
		while(!done && !check())
			increment();
	}
	
	private final void increment() {
		for(int i=0; i<indices.length; i++) {
			indices[i]++;
			if(indices[i] == steps[i].length)
				indices[i] = 0;
			else
				return;
		}
		done = true;
	}
	
	private final boolean check() {
		return !allPersistence() && !anyMutex();
	}
	
	private final boolean allPersistence() {
		for(int i=0; i<steps.length; i++)
			if(!step(i).persistence)
				return false;
		return true;
	}
	
	private final boolean anyMutex() {
		for(int i=0; i<steps.length; i++)
			for(int j=i+1; j<steps.length; j++)
				if(step(i).mutex(step(j), level))
					return true;
		return false;
	}
	
	@Override
	public boolean hasNext() {
		return !done;
	}

	@Override
	public Set<StepNode> next() {
		LinkedHashSet<StepNode> permutation = new LinkedHashSet<>();
		for(int i=0; i<steps.length; i++)
			permutation.add(step(i));
		increment();
		advance();
		return permutation;
	}
}
