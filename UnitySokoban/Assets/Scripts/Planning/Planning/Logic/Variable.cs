using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * A variable represents a placeholder for a constant whose value is not yet
     * known.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class Variable : Term
    {

        /** The next ID number to be used when creating a unique term */
        private static int nextID = 0;

        /** Stores the names of all variables */
        private static HashSet<String> names = new HashSet<String>();

        /**
         * Constructs a new variable with a given type and name.
         * 
         * @param type the type of the variable
         * @param name the name of the variable
         */
        public Variable(String type, String name) : base(type, name)
        {
            names.Add(name);
        }

        public override string ToString()
        {
            return "?" + name;
        }

        public override bool IsGround()
        {
            return false;
        }

        /**
         * Creates a new variable whose name is similar to this one's but which is
         * guaranteed to be unique.
         * 
         * @return a unique variable with a similar name to this variable's name
         */
        public Variable makeUnique()
        {
            String name;
            do
            {
                name = this.name + "-" + nextID++;
            } while (!names.Contains(name));
            return new Variable(type, name);
        }

        public override Term substitute(Substitution substitution)
        {
            return substitution.get(this);
        }
    }
}
