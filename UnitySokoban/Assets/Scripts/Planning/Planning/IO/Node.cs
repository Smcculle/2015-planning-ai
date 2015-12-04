using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace Planning.IO
{
    /**
 * The abstract parent class of
 * {@link edu.uno.ai.planning.io.List} and
 * {@link edu.uno.ai.planning.io.Symbol}.
 * 
 * @author Stephen G. Ware
 */
    public class Node
    {

        /** The node that follows this node (i.e. its next sibling) or null if this is the last node in a list */
        public readonly Node next;

        /**
         * Constructs a new node with a given next sibling.
         * 
         * @param next the next sibling
         */
        public Node(Node next)
        {
            this.next = next;
        }

        /**
         * Returns the node's next sibling or throws an exception if no such node
         * exists.
         * 
         * @return the next sibling
         * @throws FormatException if no next sibling exists
         */
        public Node requireNext()
        {
            if (next == null)
                throw new FormatException("Expected something after \"" + this + "\"");
            else
                return next;
        }

        /**
         * Tests if this node is a
         * {@link edu.uno.ai.planning.io.Symbol}.
         * 
         * @return true if the node is a symbol, false otherwise
         */
        public bool isSymbol()
        {
            return this is Symbol;
        }

        /**
         * Tests if this node is a
         * {@link edu.uno.ai.planning.io.Symbol} specific symbol.
         * 
         * @param value the value of the symbol
         * @return true if the node is that symbol, false otherwise
         */
        public bool isSymbol(String value)
        {
            return this is Symbol && ((Symbol)this).value.Equals(value);
        }

        /**
         * Casts this node to a symbol.
         * 
         * @return the node as a symbol
         * @throws FormatException if the node is not a symbol
         */
        public Symbol asSymbol()
        {
            if (isSymbol())
                return (Symbol)this;
            else
                throw new FormatException("Expected symbol but encountered \"" + this + "\"");
        }

        /**
         * Casts this node to a specific symbol.
         * 
         * @param value the value of the symbol
         * @return the node as the symbol
         * @throws FormatException if the node is not a symbol
         */
        public Symbol asSymbol(String value)
        {
            if (isSymbol(value))
                return (Symbol)this;
            else
                throw new FormatException("Expected \"" + value + "\" but encountered \"" + this + "\"");
        }

        /**
         * Tests if this node is a
         * {@link edu.uno.ai.planning.io.List}.
         * 
         * @return true if the node is a list, false otherwise
         */
        public bool isList()
        {
            return this is List;
        }

        /**
         * Tests if this node is a
         * {@link edu.uno.ai.planning.io.List} of a certain length.
         * 
         * @param min the minimum number of elements the list can have
         * @param max the maximum number of elements the list can have (-1 indicates any number of elements)
         * @return true if the node is such a list, false otherwise
         */
        public bool isList(int min, int max)
        {
            if (max == -1)
                return isList() && ((List)this).length >= min;
            else
                return isList() && ((List)this).length >= min && ((List)this).length <= max;
        }

        /**
         * Casts the node to a list.
         * 
         * @return the node as a list
         * @throws FormatException if the node is not a list
         */
        public List asList()
        {
            if (isList())
                return (List)this;
            else
                throw new FormatException("Expected list but encountered \"" + this + "\"");
        }

        /**
         * Casts the node to a list of a certain length.
         * 
         * @param min the minimum number of elements the list can have
         * @param max the maximum number of elements the list can have (-1 indicates any number of elements)
         * @return the node as a list of that length
         * @throws FormatException if the node is not a list
         */
        public List asList(int min, int max)
        {
            if (isList(min, max))
                return (List)this;
            String message = "Expected list of length at least " + min;
            if (max != -1)
                message += " but no more than " + max;
            throw new FormatException(message + " but encountered \"" + this + "\"");
        }

        /** The open list symbol */
        static readonly string OPEN = "(";

        /** The close list symbol */
        static readonly string CLOSE = ")";

        /**
         * Parse a given file as a node.
         * 
         * @param file the file to parse
         * @return a node
         * @throws IOException if an IO exception occured while reading the file
         */
        public static Node parse(string fileString)
        {
            string delimiters = @"(\s|\(|\))";
            string[] tokenArray = Regex.Split(fileString, delimiters);
            List<string> tokens = new List<string>();
            foreach (string token in tokenArray)
                if (token != "" && token != " " && token != "\r" && token != "\n" && token != "\t")
                    tokens.Add(token);
            IEnumerator<String> ti = tokens.GetEnumerator();
            ti.MoveNext();
            Node node = parseNode(ti);
            if (ti.MoveNext() && ti.Current == CLOSE)
                throw new IOException("Encountered end of list before start of list");
            else
                return node;
        }

        /**
         * Parse a set of tokens as either a list or a symbol.
         * 
         * @param tokens the tokens to parse
         * @return a node
         */
        static Node parseNode(IEnumerator<String> tokens)
        {
            if (tokens.Current == null)
                return null;
            else if (tokens.Current == OPEN)
            {
                tokens.MoveNext();
                return parseList(tokens);
            }
            else if (tokens.Current == CLOSE)
                throw new FormatException("Encountered end of list before start of list");
            else
                return parseSymbol(tokens);
        }

        /**
         * Parse a set of tokens as a list.
         * 
         * @param tokens the tokens to parse
         * @return a list
         */
        static List parseList(IEnumerator<String> tokens)
        {
            if (tokens.Current == null)
                throw new FormatException("Encountered end of file before end of list");
            Node first = parseNode(tokens);
            if (tokens.Current == null)
                throw new FormatException("Encountered end of file before end of list");
            tokens.MoveNext();
            if (tokens.Current == CLOSE)
                return new List(first, null);
            else
                return new List(first, parseNode(tokens));
        }

        /**
         * Parse a set of tokens as a symbol.
         * 
         * @param tokens the tokens to parse
         * @return a symbol
         */
        static Symbol parseSymbol(IEnumerator<String> tokens)
        {
            string value = tokens.Current;
            if (!tokens.MoveNext() || tokens.Current == CLOSE)
                return new Symbol(value, null);
            else
                return new Symbol(value, parseNode(tokens));
        }
    }
}
