package edu.uno.ai.planning.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A list of objects which cannot be modified.  Methods which would normally
 * modify the list return new lists that reflect the modification without
 * changing the original list object.
 * 
 * @author Stephen G. Ware
 * @param <E> the type of object kept in the list
 */
public class ImmutableList<E> implements Iterable<E> {

	/** The first (most recently added) element in the list */
	public final E first;
	
	/** The other elements in the list (elements 2 to n) */
	public final ImmutableList<E> rest;
	
	/** The number of elements in the list */
	public final int length;
	
	/**
	 * Constructs a new immutable list with a given first element and a given
	 * rest of the list.
	 * 
	 * @param first the first element in the list
	 * @param rest the rest of the elements in the list
	 */
	protected ImmutableList(E first, ImmutableList<E> rest) {
		this.first = first;
		this.rest = rest;
		this.length = rest.length + 1;
	}
	
	/**
	 * Constructs a new, empty immutable list.
	 */
	public ImmutableList() {
		this.first = null;
		this.rest = null;
		this.length = 0;
	}
	
	/**
	 * Returns a new list with the given element added as the first element.
	 * 
	 * @param element the element to add to this list
	 * @return a new list with that elements as the first element
	 */
	public ImmutableList<E> add(E element) {
		return new ImmutableList<E>(element, this);
	}
	
	/**
	 * Indicates whether or not the list contains a given element.
	 * 
	 * @param element the element to search for
	 * @return true if the list contains an object {@link Object#equals(Object)} to that element, false otherwise
	 */
	public boolean contains(E element) {
		ImmutableList<E> list = this;
		while(list.length != 0) {
			if(list.first.equals(element))
				return true;
			list = list.rest;
		}
		return false;
	}
	
	@Override
	public Iterator<E> iterator() {
		return new MyIterator();
	}

	/**
	 * Iterates through the list, starting at the first element and moving to
	 * the last.
	 * 
	 * @author Stephen G. Ware
	 */
	private final class MyIterator implements Iterator<E> {

		/** The rest of the list not yet returned */
		ImmutableList<E> current = ImmutableList.this;
		
		@Override
		public boolean hasNext() {
			return current.first != null;
		}

		@Override
		public E next() {
			if(!hasNext())
				throw new NoSuchElementException("Iterator exhausted");
			E element = current.first;
			current = current.rest;
			return element;
		}
	}
}
