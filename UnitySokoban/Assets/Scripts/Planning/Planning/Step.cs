using Planning.Logic;
using System;

namespace Planning
{
    /**
     * A step is a single ground action in a plan which changes the world's state.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class Step : IComparable<Step>
    {
        /** The name of the step */
        public readonly string name;

        /** What must be true before the step can be taken */
        public readonly Expression precondition;

        /** What becomes true after the step has been taken */
        public readonly Expression effect;

        /**
         * Constructs a new step.
         * 
         * @param name the name of the step
         * @param precondition the precondition (must be ground)
         * @param effect the effect (must be ground)
         * @throws IllegalArgumentException if either the precodition or effect are not ground
         */
        public Step(String name, Expression precondition, Expression effect)
        {
            if (!precondition.IsGround())
                throw new ArgumentException("Precondition not ground");
            if (!effect.IsGround())
                throw new ArgumentException("Effect not ground");
            this.name = name;
            this.precondition = precondition;
            this.effect = effect;
        }

        public override string ToString()
        {
            return name;
        }

        public int CompareTo(Step other)
        {
            return name.CompareTo(other.name);
        }
    }
}