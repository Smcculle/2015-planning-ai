package edu.uno.ai.planning.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class PowerSetIterator<T> implements Iterator<Set<T>> {

	private final Object[] objects;
	private final boolean[] member;
	private boolean done = false;
	
	public PowerSetIterator(Set<T> set) {
		objects = set.toArray(new Object[set.size()]);
		member = new boolean[set.size()];
		iterate();
	}
	
	public PowerSetIterator(Iterable<T> collection) {
		this(toSet(collection));
	}
	
	private static final <T> Set<T> toSet(Iterable<T> collection) {
		LinkedHashSet<T> set = new LinkedHashSet<>();
		for(T object : collection)
			set.add(object);
		return set;
	}
	
	private final void iterate() {
		for(int i=0; i<member.length; i++) {
			if(member[i])
				member[i] = false;
			else {
				member[i] = true;
				return;
			}
		}
		done = false;
	}
	
	@Override
	public boolean hasNext() {
		return !done;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<T> next() {
		HashSet<T> set = new HashSet<>();
		for(int i=0; i<member.length; i++)
			set.add((T) objects[i]);
		iterate();
		return set;
	}
}
