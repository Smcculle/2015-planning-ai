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
		if (isCNF(conjunction))
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
		if (isDNF(disjunction))
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
	
	private static Expression convertToNegationNormalForm(Expression expression) {
		Expression negationNormalForm = eliminateImplicationsAndEquivalancies(expression);
		negationNormalForm = moveNotInwards(negationNormalForm);
		return negationNormalForm;
	}	
	
	private static Expression eliminateImplicationsAndEquivalancies(Expression expression) {
		return expression;
	}
	
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
		if (expression instanceof Conjunction) arguments = ((Conjunction) expression).arguments;
		else if (expression instanceof Disjunction) arguments = ((Disjunction) expression).arguments;
		
		if (arguments != null)
		{
			ArrayList<Expression> newArguments = new ArrayList<Expression>();
			for (Expression argument : arguments)
				if (argument instanceof Negation)
					newArguments.add(moveNotInwards(argument));
				else
					newArguments.add(argument);
			
			if (expression instanceof Conjunction)
				return new Conjunction(getArray(newArguments));
			else if (expression instanceof Disjunction)
				return new Disjunction(getArray(newArguments));
		}
		return expression;
	}
	
	private static Expression standardizeVariables(Expression expression) {
		return expression;
	}
	
	private static Expression skolemizeStatement(Expression expression) {
		return expression;
	}
	
	private static Expression dropAllUniversalQuantifiers(Expression expression) {
		return expression;
	}

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
			Conjunction complexConjunction = null;
			for (Expression argument : ((Disjunction) expression).arguments)
				if (!isLiteral(argument))
					if (argument instanceof Conjunction)
						if (((Conjunction) argument).arguments.length > 1)
						{
							complexConjunction = (Conjunction)argument;
							break;
						}
			
			ArrayList<Expression> arguments = new ArrayList<Expression>();
			for (Expression argument : ((Disjunction) expression).arguments)
				if (argument != complexConjunction)
					arguments.add(argument);
			Expression withoutComplexConjunction = new Disjunction(getArray(arguments));
			
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
	
	private static Expression removeTermsThatCancelOut(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}
	
	private static Expression removePerfectSuperSets(Expression expression) {
		// TODO Auto-generated method stub
		return expression;
	}
	
	private static Expression[] getArray(ArrayList<Expression> expressionList)
	{
		Expression[] expressions = new Expression[expressionList.size()];
		expressionList.toArray(expressions);
		return expressions;
	}
}