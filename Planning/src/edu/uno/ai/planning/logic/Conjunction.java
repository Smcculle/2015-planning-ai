package edu.uno.ai.planning.logic;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * A conjunction is a Boolean expression that is true when all of its conjuncts
 * are true.
 * 
 * @author Stephen G. Ware
 */
public class Conjunction extends NAryBooleanExpression {

	/** The Boolean operator used for expressing conjunctions */
	public static final String CONJUNCTION_OPERATOR = "and";
	
	/**
	 * Constructs a new conjunction with a given set of conjuncts.
	 * 
	 * @param arguments the conjuncts
	 */
	public Conjunction(ImmutableArray<Expression> arguments) {
		super(CONJUNCTION_OPERATOR, arguments);
	}
	
	/**
	 * Constructs a new conjunction with a given set of conjuncts.
	 * 
	 * @param arguments the conjuncts
	 */
	public Conjunction(Expression...arguments) {
		super(CONJUNCTION_OPERATOR, arguments);
	}

	@Override
	public Conjunction substitute(Substitution substitution) {
		return new Conjunction(substituteArguments(substitution));
	}
	
	@Override
	public Bindings unify(Formula other, Bindings bindings) {
		if(other instanceof Conjunction)
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
			if(!argument.isTrue(state))
				return false;
		return true;
	}

	@Override
	public boolean isImposable() {
		return true;
	}
	
	@Override
	public void impose(MutableState state) {
		for(Expression argument : arguments)
			argument.impose(state);
	}

	@Override
	public ExpressionObject negate() {
		return new Disjunction(negateArguments());
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
			return new Conjunction(flatten());
	}
}
