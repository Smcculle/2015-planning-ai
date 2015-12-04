namespace Planning.IO
{
    /**
     * An object parser provides a means of translating a node into an object of
     * a specific kind.
     * 
     * @author Stephen G. Ware
     * @param <E> the kind of object this parser constructs
     */
    public interface ObjectParser<T>
    {

        /**
         * Converts the given node into an object of type T.
         * 
         * @param node the node to parse
         * @param parser the parser from which this parser was called (in case other nodes need to be parsed)
         * @return an object of type T
         */
        T parse(Node node, PDDLParser parser);
    }
}