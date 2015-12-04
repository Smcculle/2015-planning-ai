using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * A literal is an atomic expression (i.e. one which cannot be decomposed into
     * smaller expressions) or the negation of such an atomic expression.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public interface Literal : Expression
    {
        new Literal Substitute(Substitution substitution);
        new Literal Negate();
    }
}
