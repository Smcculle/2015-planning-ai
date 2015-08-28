package edu.uno.ai.planning.logic;

import java.util.ArrayList;

import edu.uno.ai.planning.util.ImmutableArray;

/**
 * The parent class of all Boolean expressions with an arbitrary number of
 * arguments.
 * 
 * @author Stephen G. Ware
 */
public abstract class NAryBooleanExpression extends BooleanExpression {

	/** The Boolean operator used for expressing this kind of expression */
	public final String operator;
	
	/** The expression's arguments */
	public final ImmutableArray<Expression> arguments;
	
	/**
	 * Constructs a new N-ary Boolean expression with the given operator and
	 * arguments.
	 * 
	 * @param operator the operator
	 * @param arguments the arguments
	 * @throws IllegalArgumentException if 0 arguments are provided
	 */
	public NAryBooleanExpression(String operator, ImmutableArray<Expression> arguments) {
		if(arguments.length == 0)
			throw new IllegalArgumentException("N-ary Boolean expression requires at least 1 argument");
		this.operator = operator;
		this.arguments = arguments;
	}
	
	/**
	 * Constructs a new N-ary Boolean expression with the given operator and
	 * arguments.
	 * 
	 * @param operator the operator
	 * @param arguments the arguments
	 * @throws IllegalArgumentException if 0 arguments are provided
	 */
	public NAryBooleanExpression(String operator, Expression...arguments) {
		this(operator, new ImmutableArray<>(arguments));
	}
	
	@Override
	public boolean equals(Formula other, Substitution substitution) {
		if(other instanceof NAryBooleanExpression) {
			NAryBooleanExpression otherNAB = (NAryBooleanExpression) other;
			if(operator != otherNAB.operator || arguments.length != otherNAB.arguments.length)
				return false;
			for(int i=0; i<arguments.length; i++)
				if(!arguments.get(i).equals(otherNAB.arguments.get(i), substitution))
					return false;
			return true;
		}
		return false;
	}
	
	/** The expression's hash code */
	private int hashCode = 0;
	
	@Override
	public int hashCode() {
		if(hashCode == 0)
			hashCode = operator.hashCode() + arguments.hashCode();
		return hashCode;
	}
	
	@Override
	public String toString() {
		String str = "(" + operator;
		for(Expression argument : arguments)
			str += " " +argument;
		return str + ")";
	}

	@Override
	public boolean isGround() {
		for(Expression argument : arguments)
			if(!argument.isGround())
				return false;
		return true;
	}
	
	/**
	 * Returns copies of the expression's arguments, but with terms replaced
	 * according to some substitution.
	 * 
	 * @param substitution the substitution
	 * @return the arguments with variables replaced
	 */
	protected ImmutableArray<Expression> substituteArguments(Substitution substitution) {
		Expression[] sub = new ExpressionObject[arguments.length];
		for(int i=0; i<arguments.length; i++)
			sub[i] = arguments.get(i).substitute(substitution);
		return new ImmutableArray<>(sub);
	}
	
	/**
	 * Given another N-ary Boolean expression, this method unifies each of this
	 * expression's arguments with the corresponding argument in the other
	 * expression and returns the resulting bindings.
	 * 
	 * @param other the other expression with which to unify
	 * @param bindings the set of bindings to which constraints will be added
	 * @return the new bindings (or null if the arguments cannot be unified)
	 */
	protected Bindings unifyArguments(NAryBooleanExpression other, Bindings bindings) {
		if(arguments.length != other.arguments.length)
			return null;
		for(int i=0; i<arguments.length && bindings!=null; i++)
			bindings = arguments.get(i).unify(other.arguments.get(i), bindings);
		return bindings;
	}
	
	/**
	 * Returns copies of the expression's arguments, but negated.
	 * 
	 * @return the negated arguments
	 */
	protected ImmutableArray<Expression> negateArguments() {
		Expression[] neg = new Expression[arguments.length];
		for(int i=0; i<arguments.length; i++)
			neg[i] = arguments.get(i).negate();
		return new ImmutableArray<>(neg);
	}
	
	/**
	 * If any of the arguments to this expression are expressions of the same
	 * type, their arguments are combined with this expression's arguments and
	 * the original expression is removed.
	 * 
	 * @return the arguments of the flattened expression
	 */
	protected Expression[] flatten() {
		ArrayList<Expression> arguments = new ArrayList<>();
		for(Expression argument : this.arguments) {
			if(getClass().isAssignableFrom(argument.getClass()))
				for(Expression a : ((NAryBooleanExpression) argument).arguments)
					arguments.add(a);
			else
				arguments.add(argument);
		}
		return arguments.toArray(new Expression[arguments.size()]);
	}
}
