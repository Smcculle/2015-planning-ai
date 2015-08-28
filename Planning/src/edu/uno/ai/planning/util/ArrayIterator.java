package edu.uno.ai.planning.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates through all the elements in an array.
 * 
 * @author Stephen G. Ware
 * @param <E> the kind of object returned by the iterator
 */
public class ArrayIterator<E> implements Iterator<E> {

	/** The array */
	private final E[] array;
	
	/** The current element */
	private int index;
	
	/**
	 * Constructs a new array iterator for a given array starting at a given
	 * index.
	 * 
	 * @param array the array to iterate through
	 * @param start the first element to return
	 */
	public ArrayIterator(E[] array, int start) {
		this.array = array;
		this.index = start;
	}
	
	/**
	 * Constructs a new array iterator for a given array starting at a given
	 * index.  This constructor is equivalent to
	 * <code>ArrayIterator(array, 0)</code>.
	 * 
	 * @param array the array to iterate through
	 */
	public ArrayIterator(E[] array) {
		this(array, 0);
	}

	@Override
	public boolean hasNext() {
		return index < array.length;
	}

	@Override
	public E next() {
		if(!hasNext())
			throw new NoSuchElementException("The iterator contains no more elements.");
		E element = array[index];
		index++;
		return element;
	}
}
