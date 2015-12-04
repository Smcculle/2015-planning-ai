using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning
{
    /**
     * A heuristic is a function which estimates how much work remains to be done
     * to convert a partial solution to a problem into a complete solution.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     * @param <E> the kind of object representing a partial solution
     */
    public interface Heuristic<E>
    {

        /**
         * Returns an estimate of how much work remains to be done to convert the
         * partial solution into a complete solution.
         * 
         * @param object the partial solution
         * @return an estimate of remaining work
         */
        int evaluate(E obj);
    }
}
