using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
  * A constant is a term which represents one specific object in the world.
  * 
  * @author Stephen G. Ware
  * @ported Edward Thomas Garcia
  */
    public class Constant : Term
    {
        /**
         * Constructs a new constant with the given type and name.
         * 
         * @param type the type of the constant
         * @param name the name of the constant
         */
        public Constant(String type, String name) : base(type, name)
        { 
        }

        public override bool IsGround() {
            return true;
        }

        public override Term substitute(Substitution substitution) {
            return this;
        }
    }
}
