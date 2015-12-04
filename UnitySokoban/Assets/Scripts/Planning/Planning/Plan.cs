using System.Collections.Generic;

namespace Planning
{
    /**
     * A plan is a sequence of step for achieving a goal.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public interface Plan : IEnumerable<Step>
    {
        /**
         * Returns the number of steps in the plan.
         * 
         * @return the number of steps
         */
        int Size();
    }
}