package edu.uno.ai.planning.io;

/**
 * An object parser provides a means of translating a node into an object of
 * a specific kind.
 * 
 * @author Stephen G. Ware
 * @param <E> the kind of object this parser constructs
 */
public interface ObjectParser<E> {

	/**
	 * Converts the given node into an object of type E.
	 * 
	 * @param node the node to parse
	 * @param parser the parser from which this parser was called (in case other nodes need to be parsed)
	 * @return an object of type E
	 */
	public E parse(Node node, Parser parser);
}
