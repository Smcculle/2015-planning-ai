using Planning.Logic;
using Planning.Util;

namespace Planning
{
    /**
 * A planning domain is a reusable set of action templates called
 * {@link Operator}s that describes all the ways the state of the world can
 * change.
 * 
 * @author Stephen G. Ware
 * @ported Edward Thomas Garcia
 */
    public class Domain
    {

        /** The name of the domain */
        public readonly string name;

        /** A set of objects that must exist in for problems in this domain */
        public readonly ImmutableArray<Constant> constants;

        /** A set of action templates */
        public readonly ImmutableArray<Operator> operators;

        /**
         * Constructs a new domain.
         * 
         * @param name the name of the domain
         * @param constants a set of objects that must exist in for problems in this domain
         * @param operators a set of action templates
         */
        public Domain(string name, ImmutableArray<Constant> constants, ImmutableArray<Operator> operators)
        {
            this.name = name;
            this.constants = constants;
            this.operators = operators;
        }

        /**
         * Constructs a new domain.
         * 
         * @param name the name of the domain
         * @param constants a set of objects that must exist in for problems in this domain
         * @param operators a set of action templates
         */
        public Domain(string name, Constant[] constants, params Operator[] operators) :
            this(name, new ImmutableArray<Constant>(constants), new ImmutableArray<Operator>(operators))
        {
        }

        public override int GetHashCode()
        {
            return name.GetHashCode();
        }

        public override string ToString()
        {
            return "[" + name + ": " + operators.length + " operators]";
        }
    }
}