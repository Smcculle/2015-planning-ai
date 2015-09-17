package edu.uno.ai.planning.logic;

import java.util.ArrayList;

import edu.uno.ai.planning.util.ImmutableArray;

/**
 * Utility methods for converting expressions to conjunctive normal form.
 * 
 * @author Stephen G. Ware
 * @author Edward Thomas Garcia
 */
class NormalForms {	
	/**
	 * Returns true if and only if the given expression is a literal.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a literal, false otherwise
	 */
	public static boolean isLiteral(Expression expression) {
		return expression instanceof Literal;
	}
	
	/**
	 * Returns true if and only if the given expression is a clause.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a clause, false otherwise
	 */
	public static boolean isClause(Expression expression) {
		if (expression instanceof Literal)
			return true;
		
		if (expression instanceof NAryBooleanExpression)
		{
			NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression) expression;
			for (Expression argument : nAryBooleanExpression.arguments)
				if (!isLiteral(argument))
					return false;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if and only if the given expression is a conjunctive
	 * clause.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a conjunctive clause, false otherwise
	 */
	public static boolean isConjunctiveClause(Expression expression) {
		if (isLiteral(expression))
			return true;
		
		if (isClause(expression))
			if (expression instanceof Conjunction)
				return true;
		
		return false;
	}
	
	/**
	 * Returns true if and only if the given expression is a disjunctive
	 * clause.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a disjunctive clause, false otherwise
	 */
	public static boolean isDisjunctiveClause(Expression expression) {
		if (isLiteral(expression))
			return true;
		
		if (isClause(expression))
			if (expression instanceof Disjunction)
				return true;
		
		return false;
	}
	
	/**
	 * Returns true if and only if the given expression is in conjunctive
	 * normal form.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is in CNF, false otherwise
	 */
	public static boolean isCNF(Expression expression) {
		if (expression instanceof Literal)
			return true;
		
		if (expression instanceof Conjunction)
		{
			Conjunction conjunction = (Conjunction)expression;
			for (Expression argument : conjunction.arguments)
				if (!isDisjunctiveClause(argument))
					return false;
			
			return true;
		}
		return false;
	}
	
	/**
	 * Returns true if and only if the given expression is in disjunctive
	 * normal form.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is in DNF, false otherwise
	 */
	public static boolean isDNF(Expression expression) {
		if (expression instanceof Literal)
			return true;
		
		if (expression instanceof Disjunction)
		{
			Disjunction disjunction = (Disjunction)expression;
			for (Expression argument : disjunction.arguments)
				if (!isConjunctiveClause(argument))
					return false;
			
			return true;
		}
		return false;
	}
	
	/**
	 * Converts any {@link Conjunction} to conjunctive normal form.
	 * 
	 * @param conjunction the expression to convert
	 * @return an expression in CNF
	 */
	public static Expression toCNF(Conjunction conjunction) {
		if (isCNF(conjunction) || conjunction == null)
			return conjunction;
		
		Expression conjunctiveNormalForm = convertToNegationNormalForm(conjunction);
		conjunctiveNormalForm = standardizeVariables(conjunctiveNormalForm);
		conjunctiveNormalForm = skolemizeStatement(conjunctiveNormalForm);
		conjunctiveNormalForm = dropAllUniversalQuantifiers(conjunctiveNormalForm);
		conjunctiveNormalForm = distributeOrOverAnd(conjunctiveNormalForm);
		
		if (!(conjunctiveNormalForm instanceof Conjunction))
			conjunctiveNormalForm = new Conjunction(conjunctiveNormalForm);
		
		conjunctiveNormalForm = removeDuplicates(conjunctiveNormalForm);
		conjunctiveNormalForm = removeTautologies(conjunctiveNormalForm);
		conjunctiveNormalForm = removeTermsThatCancelOut(conjunctiveNormalForm);
		conjunctiveNormalForm = removePerfectSuperSets(conjunctiveNormalForm);
		
		return conjunctiveNormalForm;
	}

	/**
	 * Converts any {@link Disjunction} to disjunctive normal form.
	 * 
	 * @param disjunction the expression to convert
	 * @return an expression in DNF
	 */
	public static Expression toDNF(Disjunction disjunction) {
		if (isDNF(disjunction) || disjunction == null)
			return disjunction;
		
		Expression disjunctiveNormalForm = convertToNegationNormalForm(disjunction);
		disjunctiveNormalForm = standardizeVariables(disjunctiveNormalForm);
		disjunctiveNormalForm = skolemizeStatement(disjunctiveNormalForm);
		disjunctiveNormalForm = dropAllUniversalQuantifiers(disjunctiveNormalForm);
		disjunctiveNormalForm = distributeAndOverOr(disjunctiveNormalForm);
		
		if (!(disjunctiveNormalForm instanceof Disjunction))
			disjunctiveNormalForm = new Disjunction(disjunctiveNormalForm);
		
		disjunctiveNormalForm = removeDuplicates(disjunctiveNormalForm);
		disjunctiveNormalForm = removeTautologies(disjunctiveNormalForm);
		disjunctiveNormalForm = removeTermsThatCancelOut(disjunctiveNormalForm);
		disjunctiveNormalForm = removePerfectSuperSets(disjunctiveNormalForm);
		
		return disjunctiveNormalForm;
	}

	/**
	 * Converts any {@link Disjunction} to conjunctive normal form.
	 * 
	 * @param disjunction the expression to convert
	 * @return an expression in CNF
	 */
	public static Expression toCNF(Disjunction disjunction) {
		return toCNF(new Conjunction(disjunction.simplify()));
	}
	
	/**
	 * Converts any {@link Conjunction} to disjunctive normal form.
	 * 
	 * @param conjunction the expression to convert
	 * @return an expression in DNF
	 */
	public static Expression toDNF(Conjunction conjunction) {
		return toDNF(new Disjunction(conjunction.simplify()));
	}

/***********************************************************************************************************
************************************************************************************************************
***********************************************************************************************************/
	
	/**
	 * Converts any {@link Expression} to negation normal form.
	 * 
	 * @param expression to convert
	 * @return negated normal form expression
	 */
	private static Expression convertToNegationNormalForm(Expression expression) {
		Expression negationNormalForm = eliminateImplicationsAndEquivalancies(expression);
		negationNormalForm = moveNotInwards(negationNormalForm);
		return negationNormalForm;
	}	
	
	/**
	 * Removes all implications and equivalences from an {@link Expression}. 
	 * 
	 * @param An expression to eliminate implications and equivalences
	 * @return An expression without an implications or equivalences
	 */
	private static Expression eliminateImplicationsAndEquivalancies(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}
	
	/**
	 * Moves NOT inwards as much as possible in a given {@link Expression}. 
	 * 
	 * @param An expression to move NOT inwards
	 * @return An expression with NOT moved inwards as much as possible
	 */
	private static Expression moveNotInwards(Expression expression) {
		if (isConjunctiveClause(expression))
			return expression;
		if (isDisjunctiveClause(expression))
			return expression;
		
		if (expression instanceof Negation)
		{
			Expression argument = ((Negation) expression).argument;
			if (argument instanceof Negation)
				return moveNotInwards(((Negation)argument).argument);
			else if (argument instanceof Conjunction)
				return moveNotInwards(new Disjunction(((Conjunction) argument).negateArguments()));
			else if (argument instanceof Disjunction)
				return moveNotInwards(new Conjunction(((Disjunction) argument).negateArguments()));
		}
		
		ImmutableArray<Expression> arguments = null;
		if (expression instanceof NAryBooleanExpression)
			arguments = ((NAryBooleanExpression) expression).arguments;
		
		if (arguments != null)
		{
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression argument : arguments)
				newArguments.add(moveNotInwards(argument));
			
			if (expression instanceof Conjunction)
				return new Conjunction(getArray(newArguments));
			else if (expression instanceof Disjunction)
				return new Disjunction(getArray(newArguments));
		}
		
		return expression;
	}
	
	/**
	 * Standardizes variable names such that they are not conflicting with each other.
	 * 
	 * @param An expression
	 * @return An expression with standardized variables
	 */
	private static Expression standardizeVariables(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}
	
	/**
	 * Replace existential quantifiers with Skolem constants or functions
	 * 
	 * @param An expression
	 * @return An expression with Skolem constants/functions without existential quantifiers.
	 */
	private static Expression skolemizeStatement(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}
	
	/**
	 * Removes all universal quantifiers from an {@link Expression}
	 * 
	 * @param An expression
	 * @return An expression without universal quantifiers
	 */
	private static Expression dropAllUniversalQuantifiers(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}

	/**
	 * Continuously distribute OR/{@link Disjunction} over AND/{@link Conjunction}
	 * 
	 * @param An expression
	 * @return An expression in conjunctive normal form (albeit not simplified)
	 */
	private static Expression distributeOrOverAnd(Expression expression)
	{
		expression = expression.simplify();
		
		if (isConjunctiveClause(expression))
			return expression;
		if (isDisjunctiveClause(expression))
			return expression;
		if (isCNF(expression))
			return expression;
		
		if (expression instanceof Disjunction)
		{
			// Get First Complex Conjunction
			Conjunction complexConjunction = null;
			for (Expression argument : ((Disjunction) expression).arguments)
				if (!isLiteral(argument))
					if (argument instanceof Conjunction)
						if (((Conjunction) argument).arguments.length > 1)
						{
							complexConjunction = (Conjunction)argument;
							break;
						}
			
			// Get Other Arguments
			ArrayList<Expression> arguments = new ArrayList<Expression>();
			for (Expression argument : ((Disjunction) expression).arguments)
				if (argument != complexConjunction)
					arguments.add(argument);
			Expression withoutComplexConjunction = new Disjunction(getArray(arguments));
			
			// Create a Disjunction with each argument of complex argument
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression argument : complexConjunction.arguments)
				newArguments.add(distributeOrOverAnd(new Disjunction(argument, withoutComplexConjunction)).simplify());
				
			return distributeOrOverAnd(new Conjunction(getArray(newArguments)).simplify());
		}
		
		if (expression instanceof Conjunction)
		{
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression argument : ((Conjunction)expression).arguments)
				newArguments.add(distributeOrOverAnd(argument));
				
			return new Conjunction(getArray(newArguments)).simplify();
		}
		return expression;
	}

	/**
	 * Continuously distribute AND/{@link Conjunction} over OR/{@link Disjunction}
	 * 
	 * @param An expression
	 * @return An expression in disjunctive normal form (albeit not simplified)
	 */
	private static Expression distributeAndOverOr(Expression expression)
	{
		expression = expression.simplify();
		
		if (isDisjunctiveClause(expression))
			return expression;
		if (isConjunctiveClause(expression))
			return expression;
		if (isDNF(expression))
			return expression;
		
		if (expression instanceof Conjunction)
		{
			Disjunction complexDisjunction = null;
			for (Expression argument : ((Conjunction) expression).arguments)
				if (!isLiteral(argument))
					if (argument instanceof Disjunction)
						if (((Disjunction) argument).arguments.length > 1)
						{
							complexDisjunction = (Disjunction)argument;
							break;
						}
			
			ArrayList<Expression> arguments = new ArrayList<Expression>();
			for (Expression argument : ((Conjunction) expression).arguments)
				if (argument != complexDisjunction)
					arguments.add(argument);
			Expression withoutComplexDisjunction = new Conjunction(getArray(arguments));
			
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression argument : complexDisjunction.arguments)
				newArguments.add(distributeAndOverOr(new Conjunction(argument, withoutComplexDisjunction)).simplify());
				
			return distributeAndOverOr(new Disjunction(getArray(newArguments)).simplify());
			
		}
		
		if (expression instanceof Disjunction)
		{
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression argument : ((Disjunction)expression).arguments)
				newArguments.add(distributeAndOverOr(argument));
				
			return new Disjunction(getArray(newArguments)).simplify();
		}
		return expression;
	}

	/**
	 * Removes duplication within an expression
	 * 
	 * @param An expression
	 * @return An expression without duplicates
	 */
	private static Expression removeDuplicates(Expression expression) {
		if (expression instanceof NAryBooleanExpression)
		{
			NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
			ArrayList<Expression> nonDuplicateArguments = new ArrayList<Expression>();
			for (Expression argument : nAryBooleanExpression.arguments)
				if (!nonDuplicateArguments.contains(argument))
					nonDuplicateArguments.add(removeDuplicates(argument));
			
			if(expression instanceof Conjunction) 
				return new Conjunction(getArray(nonDuplicateArguments));
			else if (expression instanceof Disjunction) 
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
	 * @param An expression
	 * @return An expression without tautologies.
	 */
	private static Expression removeTautologies(Expression expression) {
		if (!isCNF(expression) && !isDNF(expression))
			return expression;
		
		if (expression instanceof NAryBooleanExpression)
		{
			NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression argument : nAryBooleanExpression.arguments)
			{
				if (argument instanceof NAryBooleanExpression)
				{
					if (isConjunctiveClause(argument) || isDisjunctiveClause(argument))
					{
						NAryBooleanExpression subExpression = (NAryBooleanExpression)argument;
						boolean addArgument = true;
						for (Expression subArgument : subExpression.arguments)
						{
							Expression negativeTest = subArgument.negate();
							for (Expression subArgumentTest : subExpression.arguments)
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
				newArguments.add(argument);
			}
			
			if(expression instanceof Conjunction) 
				return new Conjunction(getArray(newArguments));
			else if (expression instanceof Disjunction) 
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
	 * @param expression
	 * @return
	 */
	private static Expression removeTermsThatCancelOut(Expression expression) {
		if (expression instanceof NAryBooleanExpression)
		{
			NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
			if (nAryBooleanExpression.arguments.length > 1)
			{
				ArrayList<Expression> newArguments = new ArrayList<Expression>();
				for (Expression arguments : nAryBooleanExpression.arguments)
					newArguments.add(arguments);
				
				for (int i = newArguments.size() - 1; i >= 0; i--)
				{
					Expression newArgument = newArguments.get(i); 
					if (newArgument instanceof NAryBooleanExpression)
					{
						NAryBooleanExpression argument = (NAryBooleanExpression)newArguments.get(i);
						for (Expression term : argument.arguments)
						{
							ArrayList<Expression> argumentsOutsideTheTerm = new ArrayList<>();
							for (Expression otherArgument : ((NAryBooleanExpression) argument).arguments)
								if (term != otherArgument)
									argumentsOutsideTheTerm.add(otherArgument);
							
							Expression remainingExpression = null;
							if (argument instanceof Conjunction)
								remainingExpression = new Conjunction(getArray(argumentsOutsideTheTerm));
							else if (argument instanceof Disjunction)
								remainingExpression = new Disjunction(getArray(argumentsOutsideTheTerm));
							
							Expression cancelOutExpression = null;;
							argumentsOutsideTheTerm.add(term.negate());
							if (argument instanceof Conjunction)
								cancelOutExpression = new Conjunction(getArray(argumentsOutsideTheTerm));
							else if (argument instanceof Disjunction)
								cancelOutExpression = new Disjunction(getArray(argumentsOutsideTheTerm));
							
							for (Expression testArgument : nAryBooleanExpression.arguments)
								if (testArgument != argument)
									if (areArgumentsEqual(cancelOutExpression, testArgument))
									{
										newArguments.remove(argument);
										newArguments.remove(testArgument);
										newArguments.add(remainingExpression);
										break;
									}
						}
					}
				}
				
				if (newArguments.size() != nAryBooleanExpression.arguments.length)
				{
					if (nAryBooleanExpression instanceof Conjunction)
						return new Conjunction(getArray(newArguments));
					else if (nAryBooleanExpression instanceof Disjunction)
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
	 * @param expression
	 * @return
	 */
	private static Expression removePerfectSuperSets(Expression expression) {
		if (expression instanceof NAryBooleanExpression)
		{
			NAryBooleanExpression nAryBooleanExpression = (NAryBooleanExpression)expression;
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression arguments : nAryBooleanExpression.arguments)
				newArguments.add(arguments);
			
			for (int i = newArguments.size() - 1; i >= 0; i--)
			{
				Expression argument = newArguments.get(i);
				for (Expression otherArgument : newArguments)
					if (argument != otherArgument)
						if (areArgumentsWithin(otherArgument, argument))
						{
							newArguments.remove(i);
							break;
						}
			}
			
			if (newArguments.size() != nAryBooleanExpression.arguments.length)
			{
				if (nAryBooleanExpression instanceof Conjunction)
					return new Conjunction(getArray(newArguments));
				else if (nAryBooleanExpression instanceof Disjunction)
					return new Disjunction(getArray(newArguments));
			}
		}
		return expression;
	}
	
	/**
	 * Converts ArrayList<Expression> to Expression[]
	 * 
	 * @param An ArrayList of Expression
	 * @return An Array of Expression
	 */
	private static Expression[] getArray(ArrayList<Expression> expressionList)
	{
		Expression[] expressions = new Expression[expressionList.size()];
		expressionList.toArray(expressions);
		return expressions;
	}
	
	/**
	 * Checks to see if {@link Expression} are the same type (Disjunction/Conjunction)
	 * and checks to see if they contain the same arguments
	 * 
	 * @param Two expressions
	 * @return true if statements have same arguments and expressions are of same type.
	 */
	private static boolean areArgumentsEqual(Expression a, Expression b)
	{
		if (a == b)
			return true;
		
		if ((a instanceof Conjunction && b instanceof Conjunction)
			|| (a instanceof Disjunction && b instanceof Disjunction))
		{
			NAryBooleanExpression nAryA = (NAryBooleanExpression)a;
			NAryBooleanExpression nAryB = (NAryBooleanExpression)b;
			
			if (nAryA.arguments.length != nAryB.arguments.length)
				return false;
			
			for(Expression needleArgument : nAryA.arguments)
				if (!nAryB.arguments.contains(needleArgument))
					return false;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks to see if needle {@link Expression} of same type (Conjunction/Disjunction)
	 * have arguments within the haystack Expression.
	 * 
	 * @param Needle and haystack Expression
	 * @return true if needle is in the haystack
	 */
	private static boolean areArgumentsWithin(Expression needle, Expression haystack)
	{
		if (needle == haystack)
			return true;
		
		if ((needle instanceof Conjunction && haystack instanceof Conjunction)
			|| (needle instanceof Disjunction && haystack instanceof Disjunction))
		{
			NAryBooleanExpression nNeedle = (NAryBooleanExpression)needle;
			NAryBooleanExpression nHaystack = (NAryBooleanExpression)haystack;
			
			for(Expression needleArgument : nNeedle.arguments)
				if (!nHaystack.arguments.contains(needleArgument))
					return false;
			
			return true;
		}
		
		return false;
	}
}