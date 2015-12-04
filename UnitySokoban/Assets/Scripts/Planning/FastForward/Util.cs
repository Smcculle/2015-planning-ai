using Planning;
using Planning.Logic;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FastForward
{
    public class Util
    {
        public static void separateHelpful(Problem problem, StateSpaceNode stateNode, List<StateSpaceNode> helpful, List<StateSpaceNode> unhelpful)
        {
            List<Literal> goalLiterals = expressionToLiterals(problem.goal);
            List<Literal> stateLiterals = expressionToLiterals(stateNode.state.toExpression());
            List<Literal> parentLiterals = (stateNode.parent == null ? new List<Literal>() : expressionToLiterals(stateNode.parent.state.toExpression()));
            foreach (Literal parentLiteral in parentLiterals)
                stateLiterals.Remove(parentLiteral);
            bool stateIsHelpful = false;
            foreach (Literal literal in stateLiterals)
            {
                if (goalLiterals.Contains(literal))
                {
                    stateIsHelpful = true;
                    helpful.Add(stateNode);
                    break;
                }
            }
            if (!stateIsHelpful)
            {
                unhelpful.Add(stateNode);
            }
        }

        public static List<Literal> expressionToLiterals(Expression expression)
        {
            List<Literal> literals = new List<Literal>();
            if (expression is Literal) {
                literals.Add((Literal)expression);
            } else {
                Conjunction cnf = (Conjunction)expression.ToCNF();
                foreach (Expression disjunction in cnf.arguments)
                    if (((Disjunction)disjunction).arguments.length == 1)
                        literals.Add((Literal)((Disjunction)disjunction).arguments.get(0));
                // else do nothing
            }
            return literals;
        }
    }

}
