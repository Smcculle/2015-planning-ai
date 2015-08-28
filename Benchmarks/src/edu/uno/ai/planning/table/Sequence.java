package edu.uno.ai.planning.table;

public abstract class Sequence<T, V> implements Iterable<V> {

	public final T label;
	
	Sequence(T label) {
		this.label = label;
	}
	
	public abstract int size();
	
	public double sum() {
		double total = 0;
		for(V value : this) {
			if(value instanceof Integer)
				total += (int) value;
			else if(value instanceof Double)
				total += (double) value;
			else if(value instanceof Boolean)
				total++;
			else
				throw new IllegalArgumentException("Cannot add " + value.getClass() + ".");
		}
		return total;
	}
	
	public double average() {
		return sum() / ((double) size());
	}
}
