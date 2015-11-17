package edu.uno.ai.planning.gp;

import java.util.Iterator;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Step;

public class TotalOrderPlan implements Plan {
	
	public final Step first;
	public final TotalOrderPlan rest;
	public final int size;
	
	private TotalOrderPlan(Step first, TotalOrderPlan rest) {
		this.first = first;
		this.rest = rest;
		this.size = rest.size + 1;
	}
	
	public TotalOrderPlan() {
		this.first = null;
		this.rest = null;
		this.size = 0;
	}

	@Override
	public int size() {
		return size;
	}
	
	private final class MyIterator implements Iterator<Step> {

		private TotalOrderPlan plan = TotalOrderPlan.this;
		
		@Override
		public boolean hasNext() {
			return plan.size > 0;
		}

		@Override
		public Step next() {
			Step next = plan.first;
			plan = plan.rest;
			return next;
		}
	}
	
	@Override
	public Iterator<Step> iterator() {
		return new MyIterator();
	}
	
	public TotalOrderPlan add(Step first) {
		return new TotalOrderPlan(first, this);
	}
}
