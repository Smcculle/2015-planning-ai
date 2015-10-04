package edu.uno.ai.planning.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * An array whose values cannot be modified.
 * 
 * @author Stephen G. Ware
 * @param <E> the type of object kept in the array
 */
public class ImmutableArray<E> implements Iterable<E> {

	/** The number of indices in the array */
	public final int length;
	
	/** The array being protected by this class */
	private final E[] array;
	
	/** The array's hashcode */
	private int hashCode = 0;
	
	/**
	 * Constructs a new immutable array which reflects the given array.
	 * 
	 * @param array the array to mirror
	 */
	public ImmutableArray(E[] array) {
		this.length = array.length;
		this.array = array;
	}
	
	/**
	 * Creates an immutable array that contains the elements of a collection.
	 * 
	 * @param collection the collection to mirror
	 * @param type the type of object kept in the array
	 */
	@SuppressWarnings("unchecked")
	public ImmutableArray(Collection<E> collection, Class<E> type) {
		this.length = collection.size();
		this.array = collection.toArray((E[]) Array.newInstance(type, length));
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof ImmutableArray) {
			Object[] otherArray = ((ImmutableArray<?>) other).array;
			if(array.length == otherArray.length) {
				for(int i=0; i<array.length; i++)
					if(!array[i].equals(otherArray[i]))
						return false;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if(hashCode == 0) {
			hashCode = 1;
			for(int i=0; i<array.length; i++)
				hashCode = hashCode * 31 + (array[i] == null ? 0 : array[i].hashCode());
		}
		return hashCode;
	}
	
	/**
	 * Returns the element at a given index.
	 * 
	 * @param index the index in the array
	 * @return the element at that index
	 */
	public final E get(int index) {
		return array[index];
	}
	
	/**
	 * Checks if the array contains a given element.
	 * 
	 * @param element the element to search for
	 * @return true if the array contains an object equal to the given object, false otherwise
	 */
	public boolean contains(E element) {
		return indexOf(element) != -1;
	}
	
	/**
	 * Returns the index of the first object that is equal to a given object.
	 * 
	 * @param element the element to search for
	 * @return the index of that object in the array, or -1 if no such object exists
	 */
	public int indexOf(E element) {
		for(int i=0; i<array.length; i++)
			if(array[i].equals(element))
				return i;
		return -1;
	}
	
	/**
	 * Added for convenience by 
	 * Dustin Peabody
	 * This just makes a copy of the array as an array list so we can do stuff to it the original array is left intact
	 * This is not a deep clone but it should be fine for our needs
	 */
	public ArrayList<E> clone(){
		ArrayList<E> temp = new ArrayList<E>();
		for(E item : this.array){
			temp.add(item);
		}
		return temp;
	}
	
	@Override
	public void forEach(Consumer<? super E> consumer) {
		for(int i=0; i<length; i++)
			consumer.accept(array[i]);
	}

	@Override
	public Iterator<E> iterator() {
		return new ArrayIterator<E>(array);
	}
}
