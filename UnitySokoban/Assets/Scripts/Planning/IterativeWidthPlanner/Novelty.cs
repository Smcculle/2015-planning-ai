using Planning;
using Planning.Logic;
using Planning.Util;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace IterativeWidthPlanner
{
    public class Novelty
    {
        public static bool hasNovelty(StateSpaceProblem problem, StateSpaceNode node, int size)
        {
            return hasNovelty(problem.literals, node, new Literal[size], 0, 0);
        }

        private static bool hasNovelty(ImmutableArray<Literal> literals, StateSpaceNode node, Literal[] conjunction, int index, int start)
        {
            if (index == conjunction.Length)
                return !ever(node.parent, conjunction);
            else
            {
                for (int i = start; i < literals.length; i++)
                {
                    Literal literal = literals.get(i);
                    if (node.state.isTrue(literal))
                    {
                        conjunction[index] = literal;
                        if (hasNovelty(literals, node, conjunction, index + 1, i + 1))
                            return true;
                    }
                }
                return false;
            }
        }

        private static bool ever(StateSpaceNode node, Literal[] conjunction)
        {
            if (node == null)
                return false;
            else if (test(conjunction, node.state))
                return true;
            else
                return ever(node.parent, conjunction);
        }

        private static bool test(Literal[] conjunction, State state)
        {
            foreach (Literal literal in conjunction)
                if (!state.isTrue(literal))
                    return false;
            return true;
        }
    }
}