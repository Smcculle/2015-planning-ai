using System;
using System.Collections;
using System.Collections.Generic;

namespace Planning.Util
{
    /**
     * An array whose values cannot be modified.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     * @param <E> the type of object kept in the array
     */
    public class ImmutableArray<T> : IEnumerable<T>
    {
        /** The number of indices in the array */
        public readonly int length;

        /** The array being protected by this class */
        private readonly T[] _array;

        /** The array's hashcode */
        private int hashCode = 0;

        /**
         * Constructs a new immutable array which reflects the given array.
         * 
         * @param array the array to mirror
         */
        public ImmutableArray(T[] array)
        {
            length = array.Length;
            _array = array;
            GenerateHashCode();
        }

        /**
         * Creates an immutable array that contains the elements of a collection.
         * 
         * @param collection the collection to mirror
         * @param type the type of object kept in the array
         */
        public ImmutableArray(ICollection<T> collection, Type type)
        {
            length = collection.Count;
            _array = new T[length];
            collection.CopyTo(_array, 0);
            GenerateHashCode();
        }

        private void GenerateHashCode()
        {
            if (hashCode == 0)
            {
                hashCode = 1;
                for (int i = 0; i < _array.Length; i++)
                {
                    hashCode = hashCode * 31;
                    if (_array[i] == null)
                        hashCode = 0;
                    else
                        hashCode += ((object)_array[i]).GetHashCode();
                }
            }
        }

        public override int GetHashCode()
        {
            return hashCode;
        }

        public override bool Equals(object other)
        {
            if (other is ImmutableArray<T>)
            {
                T[] otherArray = ((ImmutableArray<T>)other)._array;
                if (_array.Length == otherArray.Length)
                {
                    for (int i = 0; i < _array.Length; i++)
                        if (!_array[i].Equals(otherArray[i]))
                            return false;
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the element at a given index.
         * 
         * @param index the index in the array
         * @return the element at that index
         */
        public T get(int index)
        {
            return _array[index];
        }

        /**
         * Checks if the array contains a given element.
         * 
         * @param element the element to search for
         * @return true if the array contains an object equal to the given object, false otherwise
         */
        public bool contains(T element)
        {
            return indexOf(element) != -1;
        }

        /**
         * Returns the index of the first object that is equal to a given object.
         * 
         * @param element the element to search for
         * @return the index of that object in the array, or -1 if no such object exists
         */
        public int indexOf(T element)
        {
            for (int i = 0; i < _array.Length; i++)
                if (_array[i].Equals(element))
                    return i;
            return -1;
        }

        public IEnumerator<T> GetEnumerator()
        {
            return ((IEnumerable<T>)_array).GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return ((IEnumerable<T>)_array).GetEnumerator();
        }
    }
}