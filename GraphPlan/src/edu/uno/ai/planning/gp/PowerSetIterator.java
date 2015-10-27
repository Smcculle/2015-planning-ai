package edu.uno.ai.planning.gp;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.uno.ai.planning.pg.Level;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.StepNode;
import edu.uno.ai.planning.util.ImmutableList;

public class PowerSetIterator implements Iterator<ImmutableList<StepNode>> {

	private final int level;
	private final StepNode[] steps;
	private final boolean[] member;
	private boolean done = false;
	
	public PowerSetIterator(Level level, ImmutableList<LiteralNode> goals) {
		this.level = level.number;
		LinkedHashSet<StepNode> steps = new LinkedHashSet<>();
		for(LiteralNode goal : goals)
			collectSteps(this.level, goal, steps);
		this.steps = steps.toArray(new StepNode[steps.size()]);
		this.member = new boolean[steps.size()];
		advance();
	}
	
	private static final void collectSteps(int level, LiteralNode goal, Set<StepNode> steps) {
		Iterator<StepNode> producers = goal.getProducers(level);
		while(producers.hasNext())
			steps.add(producers.next());
	}
	
	private final void advance() {
		increment();
		while(!done && !check())
			increment();
	}
	
	private final void increment() {
		for(int i=0; i<member.length; i++) {
			if(!member[i]) {
				member[i] = true;
				return;
			}
			else
				member[i] = false;
		}
		done = true;
	}
	
	private final boolean check() {
		return !allPersistenceSteps() && !someStepsMutex();
	}
	
	private final boolean allPersistenceSteps() {
		for(int i=0; i<member.length; i++)
			if(member[i] && !steps[i].persistence)
				return false;
		return true;
	}
	
	private final boolean someStepsMutex() {
		for(int i=0; i<member.length; i++)
			if(member[i])
				for(int j=i+1; j<member.length; j++)
					if(member[j] && steps[i].mutex(steps[j], level))
						return true;
		return true;
	}
	
	@Override
	public boolean hasNext() {
		return !done;
	}

	@Override
	public ImmutableList<StepNode> next() {
		ImmutableList<StepNode> steps = new ImmutableList<StepNode>();
		for(int i=0; i<member.length; i++)
			if(member[i])
				steps = steps.add(this.steps[i]);
		return steps;
	}
}
