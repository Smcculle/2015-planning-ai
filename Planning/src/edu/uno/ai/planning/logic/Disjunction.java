package edu.uno.ai.planning.logic;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * A disjunction is a Boolean expression that is true when any of its disjuncts
 * are true.
 * 
 * @author Stephen G. Ware
 */
public class Disjunction extends NAryBooleanExpression {

	/** The Boolean operator used for expressing disjunctions */
	public static final String DISJUNCTION_OPERATOR = "or";
	
	/**
	 * Constructs a new disjunction with a given set of disjuncts.
	 * 
	 * @param arguments the disjuncts
	 */
	public Disjunction(ImmutableArray<Expression> arguments) {
		super(DISJUNCTION_OPERATOR, arguments);
	}
	
	/**
	 * Constructs a new disjunction with a given set of disjuncts.
	 * 
	 * @param arguments the disjuncts
	 */
	public Disjunction(Expression...arguments) {
		super(DISJUNCTION_OPERATOR, arguments);
	}

	@Override
	public Disjunction substitute(Substitution substitution) {
		return new Disjunction(substituteArguments(substitution));
	}
	
	@Override
	public Bindings unify(Formula other, Bindings bindings) {
		if(other instanceof Disjunction)
			return unifyArguments((NAryBooleanExpression) other, bindings);
		else
			return null;
	}

	@Override
	public boolean isTestable() {
		return true;
	}

	@Override
	public boolean isTrue(State state) {
		for(Expression argument : arguments)
			if(argument.isTrue(state))
				return true;
		return false;
	}

	@Override
	public boolean isImposable() {
		return false;
	}
	
	@Override
	public void impose(MutableState state) {
		throw new IllegalArgumentException("Disjunction cannot be imposed");
	}

	@Override
	public ExpressionObject negate() {
		return new Conjunction(negateArguments());
	}
	
	@Override
	public Expression toCNF() {
		return NormalForms.toCNF(this);
	}

	@Override
	public Expression toDNF() {
		return NormalForms.toDNF(this);
	}
	
	@Override
	public Expression simplify() {
		if(arguments.length == 1)
			return arguments.get(0);
		else
			return new Disjunction(flatten());
	}
}
