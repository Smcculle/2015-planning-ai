package edu.uno.ai.planning.util;

import java.util.Iterator;

/**
 * An object that allows an array to be treated as
 * {@link java.lang.Iterable}.
 * 
 * @author Stephen G. Ware
 * @param <E> the kind of element in the array
 */
public class ArrayIterable<E> implements Iterable<E> {

	/** The array */
	private final E[] array;
	
	/**
	 * Constructs a new iterable for the given array.
	 * 
	 * @param array the array
	 */
	public ArrayIterable(E[] array) {
		this.array = array;
	}
	
	@Override
	public Iterator<E> iterator() {
		return new ArrayIterator<E>(array);
	}
}
