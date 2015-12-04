using System;

namespace Planning.IO
{
    /**
 * A list is a collection of 0 to many nodes.
 * 
 * @author Stephen G. Ware
 * @ported Edward Thomas Garcia
 */
    public class List : Node
    {
        /** The number of elements in this list */
        public readonly int length;

        /** The first elerment in this list */
        public readonly Node first;

        /**
         * Constructs a new list with a given first symbol and the first element
         * and a given symbol as the first sibling of the first element.
         * 
         * @param first the first element in the list
         * @param next the second element in the list
         */
        public List(Node first, Node next) : base(next)
        {
            this.first = first;
            int l = 0;
            Node current = first;
            while (current != null)
            {
                l++;
                current = current.next;
            }
            this.length = l;
        }

        /**
         * Returns the first element in the list or throws an exception if no such
         * element exists.
         * 
         * @return the first element
         * @throws FormatException if the list does not have a first element
         */
        public Node requireFirst()
        {
            if (first == null)
                throw new FormatException("Expected non-empty list");
            else
                return first;
        }

        /**
         * Searches the list of a given symbol and returns the element after that
         * symbol if it is found.
         * 
         * @param keyword the symbol to search for
         * @return the element after that symbol, or null if the symbol was not found
         * @throws FormatException if there is no element after the given symbol
         */
        public Node get(String keyword)
        {
            Node current = first;
            while (current != null)
            {
                if (current.isSymbol(keyword))
                    return current.requireNext();
                current = current.next;
            }
            return null;
        }

        /**
         * Searches the list for a list starting with the given symbol and returns
         * it if it is found.
         * 
         * @param keyword the first symbol of the target list
         * @return the list, or null if no such list was found
         */
        public List find(String keyword)
        {
            Node current = first;
            while (current != null)
            {
                if (current.isList(1, -1) && current.asList().first.isSymbol(keyword))
                    return current.asList();
                current = current.next;
            }
            return null;
        }

        public override string ToString()
        {
            String str = "(";
            Node current = first;
            bool isFirst = true;
            while (current != null)
            {
                if (isFirst)
                    isFirst = false;
                else
                    str += " ";
                str += current;
                current = current.next;
            }
            return str + ")";
        }
    }
}