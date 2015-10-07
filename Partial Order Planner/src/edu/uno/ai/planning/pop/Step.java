package edu.uno.ai.planning.pop;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.HashSubstitution;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class Step {

	private static final ImmutableArray<Variable> NO_PARAMETERS = new ImmutableArray<>(new Variable[0]);
	private static final ImmutableArray<Literal> NO_LITERALS = new ImmutableArray<>(new Literal[0]);
	
	public final Operator operator;
	public final ImmutableArray<Variable> parameters;
	public final ImmutableArray<Literal> preconditions;
	public final ImmutableArray<Literal> effects;
	
	Step(Expression precondition, Expression effect) {
		this.operator = null;
		this.parameters = NO_PARAMETERS;
		this.preconditions = precondition == Expression.TRUE ? NO_LITERALS : getLiterals(precondition);
		this.effects = effect == Expression.TRUE ? NO_LITERALS : getLiterals(effect);
	}
	
	Step(Operator operator) {
		this.operator = operator;
		HashSubstitution substitution = new HashSubstitution();
		Variable[] parameters = new Variable[operator.parameters.length];
		for(int i=0; i<parameters.length; i++) {
			parameters[i] = operator.parameters.get(i).makeUnique();
			substitution.set(operator.parameters.get(i), parameters[i]);
		}
		this.parameters = new ImmutableArray<>(parameters);
		this.preconditions = getLiterals(operator.precondition.substitute(substitution));
		this.effects = getLiterals(operator.effect.substitute(substitution));
	}
	
	private static final ImmutableArray<Literal> getLiterals(Expression expression) {
		Literal[] literals;
		if(expression instanceof Literal)
			literals = new Literal[]{ (Literal) expression };
		else {
			Conjunction conjunction = (Conjunction) expression;
			literals = new Literal[conjunction.arguments.length];
			for(int i=0; i<literals.length; i++)
				literals[i] = (Literal) conjunction.arguments.get(i);
		}
		return new ImmutableArray<>(literals);
	}
	
	@Override
	public String toString() {
		String str = "(" + operator.name;
		for(Variable parameter : parameters)
			str += " " + parameter;
		return str + ")";
	}
	
	public boolean isStart() {
		return operator == null && preconditions == NO_LITERALS;
	}
	
	public boolean isEnd() {
		return operator == null && effects == NO_LITERALS;
	}
	
	public edu.uno.ai.planning.Step makeStep(Substitution substitution) {
		HashSubstitution sub = new HashSubstitution();
		for(int i=0; i<parameters.length; i++)
			sub.set(operator.parameters.get(i), substitution.get(parameters.get(i)));
		return operator.makeStep(substitution);
	}
}
