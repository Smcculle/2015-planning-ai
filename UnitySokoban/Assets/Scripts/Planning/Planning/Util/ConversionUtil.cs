using Planning.Logic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Util
{
    public class ConversionUtil
    {
        /**
         * Helper function to get all the literals from an Expression
         * 
         * @param expression The Expression to convert to list
         * @return List<Literal> List of literals in expression
         */
        static public List<Literal> expressionToLiterals(Expression expression)
        {
            List<Literal> literals = new List<Literal>();
            if (expression is Literal)
                literals.Add((Literal)expression);
            else
            {
                Conjunction cnf = (Conjunction)expression.ToCNF();
                foreach (Expression disjunction in cnf.arguments)
                    if (((Disjunction)disjunction).arguments.length == 1)
                        literals.Add((Literal)((Disjunction)disjunction).arguments.get(0));
                // else -- Do Nothing!
            }
            return literals;
        }
    }
}
