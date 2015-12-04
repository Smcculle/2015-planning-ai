using Planning.Logic;
using Planning.Util;
using System;
using System.Collections.Generic;

namespace Planning
{
    /**
 * A planning problem exists in some {@link Domain} and defines the initial
 * state of the world and a goal to be achieved.
 * 
 * @author Stephen G. Ware
 * @ported Edward Thomas Garcia
 */
    public class Problem
    {
        /** The name of the problem */
        public readonly string name;

        /** The domain in which this problem exists */
        public readonly Domain domain;

        /** All the objects that exist in the world */
        public readonly ImmutableArray<Constant> objects;

        /** The initial state of the world, which specifies the disposition of every object */
        public readonly State initial;

        /** Some goal that needs to be achieved by any solution to this problem */
        public readonly Expression goal;

        /**
         * Constructs a new problem.
         * 
         * @param name the name of the problem
         * @param domain the domain in which it exists
         * @param objects the objects in the world
         * @param initial the initial state
         * @param goal the goal
         */
        public Problem(string name, Domain domain, ImmutableArray<Constant> objects, State initial, Expression goal)
        {
            this.name = name;
            this.domain = domain;
            this.objects = objects;
            this.initial = initial;
            this.goal = goal;
        }

        /**
         * Constructs a new problem.
         * 
         * @param name the name of the problem
         * @param domain the domain in which it exists
         * @param objects the objects in the world
         * @param initial the initial state
         * @param goal the goal
         */
        public Problem(string name, Domain domain, Constant[] objects, State initial, Expression goal) :
            this(name, domain, new ImmutableArray<Constant>(objects), initial, goal)
        {
        }

        public override int GetHashCode()
        {
            return name.GetHashCode();
        }

        /**
         * Checks if a given plan is a solution to this problem.
         * 
         * @param plan the plan to test
         * @return true if the plan is a solution to the problem, false otherwise
         */
        public bool isSolution(Plan plan)
        {
            MutableState current = new MutableState(initial);
            foreach (Step step in plan)
            {
                if (step.precondition.IsTrue(current))
                    step.effect.Impose(current);
                else
                    return false;
            }
            return goal.IsTrue(current);
        }

        /**
         * Returns all the objects in the world which are of a given type.
         * 
         * @param type the type of objects to return
         * @return the (possibly empty) set of objects
         */
        public IEnumerable<Constant> getObjectsByType(String type)
        {
            Variable variable = new Variable(type, "variable");
            List<Constant> objects = new List<Constant>();
            foreach (Constant obj in this.objects)
                if (variable.Unify(obj, Bindings.EMPTY) != null)
                    objects.Add(obj);
            return objects;
        }

        public override string ToString()
        {
            return "[" + name + " in " + domain.name + "]";
        }
    }
}