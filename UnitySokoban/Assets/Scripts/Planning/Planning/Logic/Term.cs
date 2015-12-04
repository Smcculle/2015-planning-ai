using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * Terms represent objects in the world.  Terms do not have a truth value.
     * 
     * @author Stephen G. Ware
     */
    public abstract class Term : Formula
    {
        /** A term's type specifies which kind of values it can have */
        public readonly string type;

        /** The name of the term */
        public readonly string name;

        /** The hash code for this term */
        private readonly int hashCode;

        /**
         * Constructs a new term with a given type and name.
         * 
         * @param type the type of the term
         * @param name the name of the term
         */
        public Term(String type, String name)
        {
            this.type = type;
            this.name = name;
            this.hashCode = type.GetHashCode() * name.GetHashCode();
        }

        public override bool Equals(object obj)
        {
            if (obj is Formula)
                return Equals((Formula)obj, Substitution.EMPTY);
            else
                return false;
        }

        public bool Equals(Formula other, Substitution substitution)
        {
            if (other is Term)
            {
                Term me = substitute(substitution);
                Term otherTerm = (Term)other.Substitute(substitution);
                return me.GetType() == otherTerm.GetType() && type.Equals(otherTerm.type) && name.Equals(otherTerm.name);
            }
            else
                return false;
        }

        public override int GetHashCode()
        {
            return hashCode;
        }

        public override string ToString()
        {
            return name;
        }

        public abstract Term substitute(Substitution substitution);
        public abstract bool IsGround();
        public int CompareTo(Formula other)
        {
            return ToString().CompareTo(other.ToString());
        }

        public Bindings Unify(Formula other, Bindings bindings)
        {
            if (other is Term)
                return bindings.setEqual(this, (Term)other);
            else
                return null;
        }

        Formula Formula.Substitute(Substitution substitution)
        {
            return substitute(substitution);
        }

    }

}
