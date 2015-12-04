namespace Planning.IO
{
    /**
     * A symbol is a string of characters.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class Symbol : Node
    {
        /** The value of the symbol */
        public readonly string value;

        /**
         * Constructs a new symbol.
         * 
         * @param value the value
         * @param next the next symbol in this list (if any)
         */
        public Symbol(string value, Node next) : base(next)
        {
            this.value = value;
        }

        public override string ToString()
        {
            return value;
        }
    }
}