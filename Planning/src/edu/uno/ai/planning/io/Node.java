package edu.uno.ai.planning.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * The abstract parent class of
 * {@link edu.uno.ai.planning.io.List} and
 * {@link edu.uno.ai.planning.io.Symbol}.
 * 
 * @author Stephen G. Ware
 */
public abstract class Node {

	/** The node that follows this node (i.e. its next sibling) or null if this is the last node in a list */
	public final Node next;
	
	/**
	 * Constructs a new node with a given next sibling.
	 * 
	 * @param next the next sibling
	 */
	Node(Node next) {
		this.next = next;
	}
	
	/**
	 * Returns the node's next sibling or throws an exception if no such node
	 * exists.
	 * 
	 * @return the next sibling
	 * @throws FormatException if no next sibling exists
	 */
	public Node requireNext() {
		if(next == null)
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
	public boolean isSymbol() {
		return this instanceof Symbol;
	}
	
	/**
	 * Tests if this node is a
	 * {@link edu.uno.ai.planning.io.Symbol} specific symbol.
	 * 
	 * @param value the value of the symbol
	 * @return true if the node is that symbol, false otherwise
	 */
	public boolean isSymbol(String value) {
		return this instanceof Symbol && ((Symbol) this).value.equals(value);
	}
	
	/**
	 * Casts this node to a symbol.
	 * 
	 * @return the node as a symbol
	 * @throws FormatException if the node is not a symbol
	 */
	public Symbol asSymbol() {
		if(isSymbol())
			return (Symbol) this;
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
	public Symbol asSymbol(String value) {
		if(isSymbol(value))
			return (Symbol) this;
		else
			throw new FormatException("Expected \"" + value + "\" but encountered \"" + this + "\"");
	}
	
	/**
	 * Tests if this node is a
	 * {@link edu.uno.ai.planning.io.List}.
	 * 
	 * @return true if the node is a list, false otherwise
	 */
	public boolean isList() {
		return this instanceof List;
	}
	
	/**
	 * Tests if this node is a
	 * {@link edu.uno.ai.planning.io.List} of a certain length.
	 * 
	 * @param min the minimum number of elements the list can have
	 * @param max the maximum number of elements the list can have (-1 indicates any number of elements)
	 * @return true if the node is such a list, false otherwise
	 */
	public boolean isList(int min, int max) {
		if(max == -1)
			return isList() && ((List) this).length >= min;
		else
			return isList() && ((List) this).length >= min && ((List) this).length <= max;
	}
	
	/**
	 * Casts the node to a list.
	 * 
	 * @return the node as a list
	 * @throws FormatException if the node is not a list
	 */
	public List asList() {
		if(isList())
			return (List) this;
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
	public List asList(int min, int max) {
		if(isList(min, max))
			return (List) this;
		String message = "Expected list of length at least " + min;
		if(max != -1)
			message += " but no more than " + max;
		throw new FormatException(message + " but encountered \"" + this + "\"");
	}
	
	/** The open list symbol */
	static final String OPEN = "(".intern();
	
	/** The close list symbol */
	static final String CLOSE = ")".intern();
	
	/**
	 * A means of iterating through a set of tokens similar to an iterator.
	 * 
	 * @author Stephen G. Ware
	 */
	private static final class TokenIterator {

		/** The most recently returned token */
		public String current = null;
		
		/** An iterator of tokens */
		private final Iterator<String> tokens;
		
		/**
		 * Constructs a new token iterator for the given list of tokens.
		 * 
		 * @param tokens the tokens
		 */
		public TokenIterator(ArrayList<String> tokens) {
			this.tokens = tokens.iterator();
			if(this.tokens.hasNext())
				current = this.tokens.next();
		}

		/**
		 * Sets {@link #current} to be the next token in the set and returns
		 * this object.
		 * 
		 * @return this object
		 */
		public TokenIterator next() {
			if(tokens.hasNext())
				current = tokens.next();
			else
				current = null;
			return this;
		}
	}
	
	/**
	 * Parse a given file as a node.
	 * 
	 * @param file the file to parse
	 * @return a node
	 * @throws IOException if an IO exception occured while reading the file
	 */
	public static final Node parse(File file) throws IOException {
		ArrayList<String> tokens = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(new String(Files.readAllBytes(file.toPath())), " \t\r\n\f()", true);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if(!token.isEmpty())
				tokens.add(token.intern());
		}
		TokenIterator ti = new TokenIterator(tokens);
		Node node = parseNode(ti);
		if(ti.current == CLOSE)
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
	static final Node parseNode(TokenIterator tokens) {
		if(tokens.current == null)
			return null;
		else if(tokens.current == OPEN) 
			return parseList(tokens.next());
		else if(tokens.current == CLOSE)
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
	static final List parseList(TokenIterator tokens) {
		if(tokens.current == null)
			throw new FormatException("Encountered end of file before end of list");
		Node first = parseNode(tokens);
		if(tokens.current == null)
			throw new FormatException("Encountered end of file before end of list");
		tokens.next();
		if(tokens.current == CLOSE)
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
	static final Symbol parseSymbol(TokenIterator tokens) {
		String value = tokens.current;
		tokens.next();
		if(tokens.current == null || tokens.current == CLOSE)
			return new Symbol(value, null);
		else
			return new Symbol(value, parseNode(tokens));
	}
}
