using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * Utility methods for converting expressions to conjunctive/disjunctive normal forms.
     * 
     * @author Stephen G. Ware
     * @author Edward Thomas Garcia
     */
    class NormalForms
    {
        /**
	 * Returns true if and only if the given Expression is a Literal.
	 * 
	 * @param expression The Expression to test
	 * @return true If the Expression is a Literal, false otherwise
	 */
        public static bool isLiteral(Expression expression)
        {
            return expression is Literal;
        }

        /**
         * Returns true if and only if the given Expression is a clause.
         * Note: Literal is not a clause
         * 
         * @param expression The Expression to test
         * @return true If the Expression is a clause, false otherwise
         */
        public static bool isClause(Expression expression)
        {
            if (expression is NAryBooleanExpression)
            {
                NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
                foreach (Expression argument in nAryBooleanExpression.arguments)
                    if (!isLiteral(argument))
                        return false;

                return true;
            }

            return false;
        }

        /**
         * Returns true if and only if the given Expression is a conjunctive
         * clause.
         * 
         * @param expression The Expression to test
         * @return true If the Expression is a conjunctive clause, false otherwise
         */
        public static bool isConjunctiveClause(Expression expression)
        {
            if (isClause(expression))
                if (expression is Conjunction)
                    return true;

            return false;
        }

        /**
         * Returns true if and only if the given Expression is a disjunctive
         * clause.
         * 
         * @param expression The Expression to test
         * @return true If the Expression is a disjunctive clause, false otherwise
         */
        public static bool isDisjunctiveClause(Expression expression)
        {
            if (isClause(expression))
                if (expression is Disjunction)
                    return true;

            return false;
        }

        /**
         * Returns true if and only if the given Expression is in conjunctive
         * normal form.
         * 
         * @param expression The Expression to test
         * @return true if The Expression is in conjunctive normal form, false otherwise
         */
        public static bool isCNF(Expression expression)
        {
            if (expression is Conjunction)
            {
                Conjunction conjunction = (Conjunction)expression;
                foreach (Expression argument in conjunction.arguments)
                    if (!isDisjunctiveClause(argument))
                        return false;

                return true;
            }
            return false;
        }

        /**
         * Returns true if and only if the given Expression is in disjunctive
         * normal form.
         * 
         * @param expression The Expression to test
         * @return true If the Expression is in disjunctive normal form, false otherwise
         */
        public static bool isDNF(Expression expression)
        {
            if (expression is Disjunction)
            {
                Disjunction disjunction = (Disjunction)expression;
                foreach (Expression argument in disjunction.arguments)
                    if (!isConjunctiveClause(argument))
                        return false;

                return true;
            }
            return false;
        }

        /**
         * Converts any {@link Conjunction} to conjunctive normal form.
         * 
         * @param conjunction The Expression to convert
         * @return expression The Expression in conjunctive normal form
         */
        public static Expression toCNF(Conjunction conjunction)
        {
            if (isCNF(conjunction) || conjunction == null)
                return conjunction;

            Expression conjunctiveNormalForm = convertToNegationNormalForm(conjunction);
            conjunctiveNormalForm = standardizeVariables(conjunctiveNormalForm);
            conjunctiveNormalForm = skolemizeStatement(conjunctiveNormalForm);
            conjunctiveNormalForm = dropAllUniversalQuantifiers(conjunctiveNormalForm);
            conjunctiveNormalForm = distributeOrOverAnd(conjunctiveNormalForm);

            conjunctiveNormalForm = removeDuplicates(conjunctiveNormalForm);
            conjunctiveNormalForm = removeTautologies(conjunctiveNormalForm);
            conjunctiveNormalForm = removeTermsThatCancelOut(conjunctiveNormalForm);
            conjunctiveNormalForm = removePerfectSuperSets(conjunctiveNormalForm);

            conjunctiveNormalForm = wrapLiteralsWithDisjunctions(conjunctiveNormalForm);

            if (!(conjunctiveNormalForm is Conjunction))
                conjunctiveNormalForm = new Conjunction(conjunctiveNormalForm);

            return conjunctiveNormalForm;
        }

        /**
         * Converts any {@link Disjunction} to disjunctive normal form.
         * 
         * @param disjunction The Expression to convert
         * @return expression The Expression in disjunctive normal form
         */
        public static Expression toDNF(Disjunction disjunction)
        {
            if (isDNF(disjunction) || disjunction == null)
                return disjunction;

            Expression disjunctiveNormalForm = convertToNegationNormalForm(disjunction);
            disjunctiveNormalForm = standardizeVariables(disjunctiveNormalForm);
            disjunctiveNormalForm = skolemizeStatement(disjunctiveNormalForm);
            disjunctiveNormalForm = dropAllUniversalQuantifiers(disjunctiveNormalForm);
            disjunctiveNormalForm = distributeAndOverOr(disjunctiveNormalForm);

            disjunctiveNormalForm = removeDuplicates(disjunctiveNormalForm);
            disjunctiveNormalForm = removeTautologies(disjunctiveNormalForm);
            disjunctiveNormalForm = removeTermsThatCancelOut(disjunctiveNormalForm);
            disjunctiveNormalForm = removePerfectSuperSets(disjunctiveNormalForm);

            disjunctiveNormalForm = wrapLiteralsWithConjunctions(disjunctiveNormalForm);

            if (!(disjunctiveNormalForm is Disjunction))
                disjunctiveNormalForm = new Disjunction(disjunctiveNormalForm);

            return disjunctiveNormalForm;
        }

        /**
         * Converts any {@link Disjunction} to conjunctive normal form.
         * 
         * @param disjunction The Expression to convert
         * @return expression The Expression in conjunctive normal form
         */
        public static Expression toCNF(Disjunction disjunction)
        {
            return toCNF(new Conjunction(disjunction));
        }

        /**
         * Converts any {@link Conjunction} to disjunctive normal form.
         * 
         * @param conjunction The Expression to convert
         * @return expression The Expression in disjunctive normal form
         */
        public static Expression toDNF(Conjunction conjunction)
        {
            return toDNF(new Disjunction(conjunction));
        }

        /**
         * Converts any {@link Expression} to conjunctive normal form.
         * 
         * @param expression The Expression to convert
         * @return expression The Expression in conjunctive normal form
         */
        public static Expression toCNF(Expression expression)
        {
            return toCNF(new Conjunction(expression));
        }

        /**
         * Converts any {@link Expression} to disjunctive normal form.
         * 
         * @param conjunction The Expression to convert
         * @return expression The Expression in disjunctive normal form
         */
        public static Expression toDNF(Expression expression)
        {
            return toDNF(new Disjunction(expression));
        }

        /***********************************************************************************************************
        ************************************************************************************************************
        ***********************************************************************************************************/

        /**
         * Converts any {@link Expression} to negation normal form.
         * 
         * @param expression The Expression to convert
         * @return expression The Expression in negated normal form
         */
        private static Expression convertToNegationNormalForm(Expression expression)
        {
            Expression negationNormalForm = eliminateImplicationsAndEquivalancies(expression);
            negationNormalForm = moveNotInwards(negationNormalForm);
            return negationNormalForm;
        }

        /**
         * Removes all implications and equivalences from an {@link Expression}. 
         * 
         * @param expression The Expression to eliminate implications and equivalences
         * @return expression The Expression without implications or equivalences
         */
        private static Expression eliminateImplicationsAndEquivalancies(Expression expression)
        {
            // TODO Auto-generated method stub
            return expression;
        }

        /**
         * Moves NOT inwards as much as possible in a given {@link Expression}. 
         * 
         * @param expression The Expression to move NOT inwards
         * @return expression The Expression with NOT moved inwards
         */
        private static Expression moveNotInwards(Expression expression)
        {
            if (isClause(expression))
                return expression;

            if (expression is Negation)
            {
                Expression argument = ((Negation)expression).argument;
                if (argument is Negation)
                    return moveNotInwards(((Negation)argument).argument);
                else if (argument is Conjunction)
                    return moveNotInwards(new Disjunction(((Conjunction)argument).NegateArguments()));
                else if (argument is Disjunction)
                    return moveNotInwards(new Conjunction(((Disjunction)argument).NegateArguments()));
            }

            ImmutableArray<Expression> arguments = null;
            if (expression is NAryBooleanExpression)
                arguments = ((NAryBooleanExpression)expression).arguments;

            if (arguments != null)
            {
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in arguments)
                    newArguments.Add(moveNotInwards(argument));

                if (expression is Conjunction)
                    return new Conjunction(getArray(newArguments));
                else if (expression is Disjunction)
                    return new Disjunction(getArray(newArguments));
            }

            return expression;
        }

        /**
         * Standardizes variable names such that they are not conflicting with each other.
         * 
         * @param expression The Expression to standardize
         * @return expression The Expression with standardized variables
         */
        private static Expression standardizeVariables(Expression expression)
        {
            // TODO Auto-generated method stub
            return expression;
        }

        /**
         * Replace existential quantifiers with Skolem constants or functions
         * 
         * @param expression The Expression to Skolemize
         * @return expression The Expression with Skolem constants/functions
         */
        private static Expression skolemizeStatement(Expression expression)
        {
            // TODO Auto-generated method stub
            return expression;
        }

        /**
         * Removes all universal quantifiers from an {@link Expression}
         * 
         * @param expression The Expression to remove all universal quantifiers
         * @return expression The Expression without universal quantifiers
         */
        private static Expression dropAllUniversalQuantifiers(Expression expression)
        {
            // TODO Auto-generated method stub
            return expression;
        }

        /**
         * Continuously distribute OR/{@link Disjunction} over AND/{@link Conjunction}
         * 
         * @param expression The Expression to distribute OR over AND
         * @return expression The Expression in conjunctive normal form (albeit not simplified)
         */
        private static Expression distributeOrOverAnd(Expression expression)
        {
            expression = expression.Simplify();

            if (isConjunctiveClause(expression))
                return expression;
            if (isDisjunctiveClause(expression))
                return expression;
            if (isCNF(expression))
                return expression;

            if (expression is Disjunction)
            {
                // Get First Complex Conjunction
                Conjunction complexConjunction = null;
                foreach (Expression argument in ((Disjunction)expression).arguments)
                    if (!isLiteral(argument))
                        if (argument is Conjunction)
                            if (((Conjunction)argument).arguments.length > 1)
                            {
                                complexConjunction = (Conjunction)argument;
                                break;
                            }

                if (complexConjunction == null) return expression;

                // Get Other Arguments
                List<Expression> arguments = new List<Expression>();
                foreach (Expression argument in ((Disjunction)expression).arguments)
                    if (argument != complexConjunction)
                        arguments.Add(argument);

                if (arguments.Count == 0) return expression;
                Expression withoutComplexConjunction = new Disjunction(getArray(arguments));

                // Create a Disjunction with each argument of complex argument
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in complexConjunction.arguments)
                    newArguments.Add(distributeOrOverAnd(new Disjunction(argument, withoutComplexConjunction)).Simplify());

                return distributeOrOverAnd(new Conjunction(getArray(newArguments)).Simplify());
            }

            if (expression is Conjunction)
            {
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in ((Conjunction)expression).arguments)
                    newArguments.Add(distributeOrOverAnd(argument));

                return new Conjunction(getArray(newArguments)).Simplify();
            }
            return expression;
        }

        /**
         * Continuously distribute AND/{@link Conjunction} over OR/{@link Disjunction}
         * 
         * @param expression The Expression to distribute AND over OR
         * @return expression The Expression in disjunctive normal form (albeit not simplified)
         */
        private static Expression distributeAndOverOr(Expression expression)
        {
            expression = expression.Simplify();

            if (isClause(expression))
                return expression;
            if (isDNF(expression))
                return expression;

            if (expression is Conjunction)
            {
                Disjunction complexDisjunction = null;
                foreach (Expression argument in ((Conjunction)expression).arguments)
                    if (!isLiteral(argument))
                        if (argument is Disjunction)
                            if (((Disjunction)argument).arguments.length > 1)
                            {
                                complexDisjunction = (Disjunction)argument;
                                break;
                            }

                if (complexDisjunction == null) return expression;

                List<Expression> arguments = new List<Expression>();
                foreach (Expression argument in ((Conjunction)expression).arguments)
                    if (argument != complexDisjunction)
                        arguments.Add(argument);

                if (arguments.Count == 0) return expression;
                Expression withoutComplexDisjunction = new Conjunction(getArray(arguments));

                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in complexDisjunction.arguments)
                    newArguments.Add(distributeAndOverOr(new Conjunction(argument, withoutComplexDisjunction)).Simplify());

                return distributeAndOverOr(new Disjunction(getArray(newArguments)).Simplify());

            }

            if (expression is Disjunction)
            {
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in ((Disjunction)expression).arguments)
                    newArguments.Add(distributeAndOverOr(argument));

                return new Disjunction(getArray(newArguments)).Simplify();
            }
            return expression;
        }

        /**
         * Removes duplication within an Expression
         * 
         * @param expression The Expression which may contain duplicates
         * @return expression The Expression without duplicates
         */
        private static Expression removeDuplicates(Expression expression)
        {
            if (expression is NAryBooleanExpression)
            {
                NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
                List<Expression> nonDuplicateArguments = new List<Expression>();
                foreach (Expression argument in nAryBooleanExpression.arguments)
                    if (!nonDuplicateArguments.Contains(argument))
                        nonDuplicateArguments.Add(removeDuplicates(argument));

                if (expression is Conjunction)
                    return new Conjunction(getArray(nonDuplicateArguments));
                else if (expression is Disjunction)
                    return new Disjunction(getArray(nonDuplicateArguments));
            }
            return expression;
        }

        /**
         * Removes tautologies, or occurrences where a Predication and the Negation of that Predication
         * exist in the same clause.
         * 
         * Examples:
         * AvBv(C~C) == AvB
         * (A)(B)(Cv~C) = (A)(B)
         * 
         * @param expression The Expression which may contain tautologies
         * @return expression The Expression without tautologies.
         */
        private static Expression removeTautologies(Expression expression)
        {
            if (!isCNF(expression) && !isDNF(expression))
                return expression;

            if (expression is NAryBooleanExpression)
            {
                NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in nAryBooleanExpression.arguments)
                {
                    if (argument is NAryBooleanExpression)
                    {
                        if (isConjunctiveClause(argument) || isDisjunctiveClause(argument))
                        {
                            NAryBooleanExpression subExpression = (NAryBooleanExpression)argument;
                            bool addArgument = true;
                            foreach (Expression subArgument in subExpression.arguments)
                            {
                                Expression negativeTest = subArgument.Negate();
                                foreach (Expression subArgumentTest in subExpression.arguments)
                                    if (subArgumentTest == negativeTest)
                                    {
                                        addArgument = false;
                                        break;
                                    }
                                if (!addArgument) break;
                            }
                            if (!addArgument) continue;
                        }
                    }
                    newArguments.Add(argument);
                }

                if (expression is Conjunction)
                    return new Conjunction(getArray(newArguments));
                else if (expression is Disjunction)
                    return new Disjunction(getArray(newArguments));
            }
            return expression;
        }

        /**
         *  Remove terms that cancel out, or a predication that exist in one clause and its
         *  negation existing on another clause, but all else remaining the same.
         *  
         *  Examples:
         *  (AB)v(A~B) == A
         *  (AvB)(Av~B) == A
         * 
         * @param expression The Expression that may contain terms that cancel out
         * @return expression The Expression after canceling terms out
         */
        private static Expression removeTermsThatCancelOut(Expression expression)
        {
            if (expression is NAryBooleanExpression)
            {
                NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
                if (nAryBooleanExpression.arguments.length > 1)
                {
                    List<Expression> newArguments = new List<Expression>();
                    foreach (Expression arguments in nAryBooleanExpression.arguments)
                        newArguments.Add(arguments);

                    for (int i = newArguments.Count - 1; i >= 0; i--)
                    {
                        Expression newArgument = newArguments[i];
                        if (newArgument is NAryBooleanExpression)
                        {
                            NAryBooleanExpression argument = (NAryBooleanExpression)newArguments[i];
                            foreach (Expression term in argument.arguments)
                            {
                                List<Expression> argumentsOutsideTheTerm = new List<Expression>();
                                foreach (Expression otherArgument in ((NAryBooleanExpression)argument).arguments)
                                    if (term != otherArgument)
                                        argumentsOutsideTheTerm.Add(otherArgument);

                                Expression remainingExpression = null;
                                if (argument is Conjunction)
                                    remainingExpression = new Conjunction(getArray(argumentsOutsideTheTerm));
                                else if (argument is Disjunction)
                                    remainingExpression = new Disjunction(getArray(argumentsOutsideTheTerm));

                                Expression cancelOutExpression = null; ;
                                argumentsOutsideTheTerm.Add(term.Negate());
                                if (argument is Conjunction)
                                    cancelOutExpression = new Conjunction(getArray(argumentsOutsideTheTerm));
                                else if (argument is Disjunction)
                                    cancelOutExpression = new Disjunction(getArray(argumentsOutsideTheTerm));

                                foreach (Expression testArgument in nAryBooleanExpression.arguments)
                                    if (testArgument != argument)
                                        if (areArgumentsEqual(cancelOutExpression, testArgument))
                                        {
                                            newArguments.Remove(argument);
                                            newArguments.Remove(testArgument);
                                            newArguments.Add(remainingExpression);
                                            break;
                                        }
                            }
                        }
                    }

                    if (newArguments.Count != nAryBooleanExpression.arguments.length)
                    {
                        if (nAryBooleanExpression is Conjunction)
                            return new Conjunction(getArray(newArguments));
                        else if (nAryBooleanExpression is Disjunction)
                            return new Disjunction(getArray(newArguments));
                    }
                }
            }

            return expression;
        }

        /**
         * Removes Perfect Super Sets, or if one clause can fit entirely in another clause,
         * remove the other clause.
         * 
         * Examples:
         * (AB)v(ABC) == AB
         * (AvB)(AvBvC) == AvB
         * 
         * @param expression The Expression which may contain perfect super sets
         * @return expression The Expression without perfect super super sets
         */
        private static Expression removePerfectSuperSets(Expression expression)
        {
            if (expression is NAryBooleanExpression)
            {
                NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression arguments in nAryBooleanExpression.arguments)
                    newArguments.Add(arguments);

                for (int i = newArguments.Count - 1; i >= 0; i--)
                {
                    Expression argument = newArguments[i];
                    foreach (Expression otherArgument in newArguments)
                        if (argument != otherArgument)
                            if (areArgumentsWithin(otherArgument, argument))
                            {
                                newArguments.RemoveAt(i);
                                break;
                            }
                }

                if (newArguments.Count != nAryBooleanExpression.arguments.length)
                {
                    if (nAryBooleanExpression is Conjunction)
                        return new Conjunction(getArray(newArguments));
                    else if (nAryBooleanExpression is Disjunction)
                        return new Disjunction(getArray(newArguments));
                }
            }
            return expression;
        }


        /**
         * Wraps any literals in {@link Expression} to with a new (@link Disjunction)
         * 
         * @param expression The Expression which may contain Literals not within a Disjunction
         * @return expression The Expression where all Literals are within a Disjunction
         */
        private static Expression wrapLiteralsWithDisjunctions(Expression expression)
        {
            if (isLiteral(expression))
                return new Disjunction(expression);

            if (expression is NAryBooleanExpression)
            {
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in ((NAryBooleanExpression)expression).arguments)
                    newArguments.Add(wrapLiteralsWithDisjunctions(argument));

                if (expression is Conjunction)
                    return new Conjunction(getArray(newArguments)).Simplify();
                if (expression is Disjunction)
                    return new Disjunction(getArray(newArguments)).Simplify();
            }

            if (expression is Negation)
                return new Negation(wrapLiteralsWithDisjunctions(((Negation)expression).argument));

            return expression;
        }

        /**
         * Wraps any literals in {@link Expression} to with a new (@link Conjunction)
         * 
         * @param expression The Expression which may contain Literals not within a Conjunction
         * @return expression The Expression where all Literals are within a Conjunction
         */
        private static Expression wrapLiteralsWithConjunctions(Expression expression)
        {
            if (isLiteral(expression))
                return new Conjunction(expression);

            if (expression is NAryBooleanExpression)
            {
                List<Expression> newArguments = new List<Expression>();
                foreach (Expression argument in ((NAryBooleanExpression)expression).arguments)
                    newArguments.Add(wrapLiteralsWithConjunctions(argument));

                if (expression is Conjunction)
                    return new Conjunction(getArray(newArguments)).Simplify();
                if (expression is Disjunction)
                    return new Disjunction(getArray(newArguments)).Simplify();
            }

            if (expression is Negation)
                return new Negation(wrapLiteralsWithConjunctions(((Negation)expression).argument));

            return expression;
        }

        /**
         * Converts ArrayList<Expression> to Expression[]
         * 
         * @param expressionList The ArrayList of Expression
         * @return array The Array of Expression
         */
        private static Expression[] getArray(List<Expression> expressionList)
        {
            return expressionList.ToArray();
        }

        /**
         * Checks to see if {@link Expression} are the same type (Disjunction/Conjunction)
         * and checks to see if they contain the same arguments
         * 
         * @param a First Expression to compare
         * @param b Second Expression to compare
         * @return true if statements have same arguments and Expressions are of same type.
         */
        private static bool areArgumentsEqual(Expression a, Expression b)
        {
            if (a == b)
                return true;

            if ((a is Conjunction && b is Conjunction)
                    || (a is Disjunction && b is Disjunction))
            {
                NAryBooleanExpression nAryA = (NAryBooleanExpression)a;
                NAryBooleanExpression nAryB = (NAryBooleanExpression)b;

                if (nAryA.arguments.length != nAryB.arguments.length)
                    return false;

                foreach (Expression needleArgument in nAryA.arguments)
                    if (!nAryB.arguments.contains(needleArgument))
                        return false;

                return true;
            }

            return false;
        }

        /**
         * Checks to see if needle {@link Expression} have arguments within 
         * the haystack Expression of same type (Conjunction/Disjunction).
         * 
         * @param needle The Expression that may have arguments within the haystack
         * @param haystack The Expression that may contain the needle
         * @return true If the needle is in the haystack
         */
        private static bool areArgumentsWithin(Expression needle, Expression haystack)
        {
            if (needle == haystack)
                return true;

            if ((needle is Conjunction && haystack is Conjunction)
                    || (needle is Disjunction && haystack is Disjunction))
            {
                NAryBooleanExpression nNeedle = (NAryBooleanExpression)needle;
                NAryBooleanExpression nHaystack = (NAryBooleanExpression)haystack;

                foreach (Expression needleArgument in nNeedle.arguments)
                    if (!nHaystack.arguments.contains(needleArgument))
                        return false;

                return true;
            }

            return false;
        }
    }
}
