using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;

namespace GraphPlanSGW
{
    public class TotalOrderPlan : Plan
    {
        public readonly Step first;
        public readonly TotalOrderPlan rest;
        public readonly int size;

        private TotalOrderPlan(Step first, TotalOrderPlan rest)
        {
            this.first = first;
            this.rest = rest;
            this.size = rest.size + 1;
        }

        public TotalOrderPlan()
        {
            this.first = null;
            this.rest = null;
            this.size = 0;
        }

        public int Size()
        {
            return size;
        }

        private class MyIterator : IEnumerator<Step>
        {
            private TotalOrderPlan plan;
            private TotalOrderPlan firstPlan;
            private Step current;

            public Step Current { get { return current; } }
            object IEnumerator.Current { get { return current; } }

            public MyIterator(TotalOrderPlan plan)
            {
                this.plan = plan;
                this.firstPlan = plan;
            }

            public void Dispose()
            {
                plan = null;
                firstPlan = null;
                current = null;
            }

            public bool MoveNext()
            {
                if (plan == null)
                    return false;

                current = plan.first;
                plan = plan.rest;

                if (current == null)
                    return false;
                else
                    return true;
            }

            public void Reset()
            {
                plan = firstPlan;
                current = null;
            }
        }

        public TotalOrderPlan add(Step first)
        {
            return new TotalOrderPlan(first, this);
        }

        public IEnumerator<Step> GetEnumerator()
        {
            return new MyIterator(this);
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return new MyIterator(this);
        }
    }

}
