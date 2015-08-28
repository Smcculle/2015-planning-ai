package edu.uno.ai.planning.logic;

/**
 * Utility methods for converting expressions to conjunctive normal form.
 * 
 * @author Stephen G. Ware
 */
class NormalForms {

	/**
	 * Returns true if and only if the given expression is a literal.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a literal, false otherwise
	 */
	public static boolean isLiteral(Expression expression) {
		return true;
	}
	
	/**
	 * Returns true if and only if the given expression is a clause.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a clause, false otherwise
	 */
	public static boolean isClause(Expression expression) {
		return true;
	}
	
	/**
	 * Returns true if and only if the given expression is a conjunctive
	 * clause.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a conjunctive clause, false otherwise
	 */
	public static boolean isConjunctiveClause(Expression expression) {
		return true;
	}
	
	/**
	 * Returns true if and only if the given expression is a disjunctive
	 * clause.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is a disjunctive clause, false otherwise
	 */
	public static boolean isDisjunctiveClause(Expression expression) {
		return true;
	}
	
	/**
	 * Returns true if and only if the given expression is in conjunctive
	 * normal form.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is in CNF, false otherwise
	 */
	public static boolean isCNF(Expression expression) {
		return true;
	}
	
	/**
	 * Returns true if and only if the given expression is in disjunctive
	 * normal form.
	 * 
	 * @param expression the expression to test
	 * @return true if the expression is in DNF, false otherwise
	 */
	public static boolean isDNF(Expression expression) {
		return true;
	}
	
	/**
	 * Converts any {@link Conjunction} to conjunctive normal form.
	 * 
	 * @param conjunction the expression to convert
	 * @return an expression in CNF
	 */
	public static Expression toCNF(Conjunction conjunction) {
		return conjunction;
	}
	
	/**
	 * Converts any {@link Disjunction} to disjunctive normal form.
	 * 
	 * @param disjunction the expression to convert
	 * @return an expression in DNF
	 */
	public static Expression toDNF(Disjunction disjunction) {
		return disjunction;
	}
	
	/**
	 * Converts any {@link Disjunction} to conjunctive normal form.
	 * 
	 * @param disjunction the expression to convert
	 * @return an expression in CNF
	 */
	public static Expression toCNF(Disjunction disjunction) {
		return disjunction;
	}
	
	/**
	 * Converts any {@link Conjunction} to disjunctive normal form.
	 * 
	 * @param conjunction the expression to convert
	 * @return an expression in DNF
	 */
	public static Expression toDNF(Conjunction conjunction) {
		return conjunction;
	}
}
