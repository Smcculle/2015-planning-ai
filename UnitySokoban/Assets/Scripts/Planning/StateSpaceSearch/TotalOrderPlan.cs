using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace StateSpaceSearchProject
{
    /**
     * A plan which specifies exactly what order steps should be executed in.  Note
     * that this data structure is immutable.  The {@link #addStep(Step)} method
     * returns a new plan with the given step added without modifying the plan on
     * which that method was called.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class TotalOrderPlan : Plan
    {

        /** The rest of the steps in the plan */
        private readonly TotalOrderPlan _first;

        /** The last step in the plan */
        private readonly Step _last;

        /** The number of steps in the plan */
        private readonly int _size;

        /**
         * Constructs a new plan with a given rest of steps and last step.
         * 
         * @param first the rest of the plan
         * @param last the last step
         */
        private TotalOrderPlan(TotalOrderPlan first, Step last)
        {
            _first = first;
            _last = last;
            _size = first._size + 1;
        }

        /**
         * Constructs a new plan with 0 steps.
         */
        public TotalOrderPlan()
        {
            _first = null;
            _last = null;
            _size = 0;
        }

        public int Size()
        {
            return _size;
        }

        public IEnumerator<Step> GetEnumerator()
        {
            Step[] steps = new Step[_size];
            TotalOrderPlan current = this;
            for (int i = _size - 1; i >= 0; i--)
            {
                steps[i] = current._last;
                current = current._first;
            }
            return new LinkedList<Step>(steps).GetEnumerator();
        }

        /**
         * Returns a new plan with the given step added at the end.
         * 
         * @param step the next step to take
         * @return a new plan whose last step is the given step
         */
        public TotalOrderPlan addStep(Step step)
        {
            return new TotalOrderPlan(this, step);
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }
    }
}
