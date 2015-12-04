using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Util
{
    /**
     * A list of objects which cannot be modified.  Methods which would normally
     * modify the list return new lists that reflect the modification without
     * changing the original list object.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     * @param <E> the type of object kept in the list
     */
    public class ImmutableList<E> : IEnumerable<E>
    {

        /** The first (most recently added) element in the list */
        public readonly E first;

        /** The other elements in the list (elements 2 to n) */
        public readonly ImmutableList<E> rest;

        /** The number of elements in the list */
        public readonly int length;

        /**
         * Constructs a new immutable list with a given first element and a given
         * rest of the list.
         * 
         * @param first the first element in the list
         * @param rest the rest of the elements in the list
         */
        protected ImmutableList(E first, ImmutableList<E> rest)
        {
            this.first = first;
            this.rest = rest;
            this.length = rest.length + 1;
        }

        /**
         * Constructs a new, empty immutable list.
         */
        public ImmutableList()
        {
            this.first = default(E);
            this.rest = null;
            this.length = 0;
        }

        public override bool Equals(object other)
        {
            if (other is ImmutableList<E>)
            {
                ImmutableList<E> otherList = (ImmutableList<E>)other;
                if (length == otherList.length)
                {
                    if (length == 0)
                        return true;
                    else if (first.Equals(otherList.first))
                        return rest.Equals(otherList.rest);
                }
            }
            return false;
        }

        public override int GetHashCode()
        {
            if (length == 0)
                return 0;
            else
                return first.GetHashCode() + rest.GetHashCode();
        }

        /**
         * Returns a new list with the given element added as the first element.
         * 
         * @param element the element to add to this list
         * @return a new list with that elements as the first element
         */
        public ImmutableList<E> add(E element)
        {
            return new ImmutableList<E>(element, this);
        }

        /**
         * Indicates whether or not the list contains a given element.
         * 
         * @param element the element to search for
         * @return true if the list contains an object {@link Object#equals(Object)} to that element, false otherwise
         */
        public bool contains(E element)
        {
            ImmutableList<E> list = this;
            while (list.length != 0)
            {
                if (list.first.Equals(element))
                    return true;
                list = list.rest;
            }
            return false;
        }

        public IEnumerator<E> GetEnumerator()
        {
            return new MyIterator(this);
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return new MyIterator(this);
        }

        /**
         * Iterates through the list, starting at the first element and moving to
         * the last.
         * 
         * @author Stephen G. Ware
         * @ported Edward Thomas Garcia
         */
        private class MyIterator : IEnumerator<E>
        {
            /** The rest of the list not yet returned */
            ImmutableList<E> first;
            ImmutableList<E> current;
            E currentValue;

            public E Current { get { return currentValue; } }
            object IEnumerator.Current { get { return currentValue; } }

            public MyIterator(ImmutableList<E> current)
            {
                this.current = current;
                this.first = current;
            }

            private bool hasNext()
            {
                return current.first != null;
            }

            public void Dispose()
            {
                current = null;
                first = null;
                currentValue = default(E);
            }

            public bool MoveNext()
            {
                if (current == null)
                    return false;

                currentValue = current.first;
                current = current.rest;

                if (currentValue == null)
                    return false;
                else
                    return true;
            }

            public void Reset()
            {
                current = first;
                currentValue = default(E);
            }
        }
    }
}
