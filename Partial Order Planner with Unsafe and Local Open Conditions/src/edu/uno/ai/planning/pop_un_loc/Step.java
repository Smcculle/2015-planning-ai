package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.HashSubstitution;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class Step implements Partial {

  private static final ImmutableArray<Literal> NO_LITERALS = new ImmutableArray<>(new Literal[0]);
  private static final ImmutableArray<Variable> NO_PARAMETERS = new ImmutableArray<>(new Variable[0]);

  private static final ImmutableArray<Literal> getLiterals(Expression expression) {
    Literal[] literals;
    if (expression instanceof Literal)
      literals = new Literal[] { (Literal) expression };
    else {
      Conjunction conjunction = (Conjunction) expression;
      literals = new Literal[conjunction.arguments.length];
      for (int i = 0; i < literals.length; i++)
        literals[i] = (Literal) conjunction.arguments.get(i);
    }
    return new ImmutableArray<>(literals);
  }

  public final ImmutableArray<Literal> effects;
  public final ImmutableArray<Variable> parameters;
  public final ImmutableArray<Literal> preconditions;
  public final Operator operator;

  Step(Expression precondition, Expression effect) {
    operator = null;
    parameters = NO_PARAMETERS;
    preconditions = precondition == Expression.TRUE ? NO_LITERALS
                                                    : getLiterals(precondition);
    effects = effect == Expression.TRUE ? NO_LITERALS : getLiterals(effect);
  }

  Step(Operator operator) {
    this.operator = operator;
    HashSubstitution substitution = new HashSubstitution();
    Variable[] parameters = new Variable[operator.parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      parameters[i] = operator.parameters.get(i).makeUnique();
      substitution.set(operator.parameters.get(i), parameters[i]);
    }
    this.parameters = new ImmutableArray<>(parameters);
    preconditions = getLiterals(operator.precondition.substitute(substitution));
    effects = getLiterals(operator.effect.substitute(substitution));
  }

  public ImmutableArray<Literal> effects() {
    return effects;
  }

  public boolean isStart() {
    return operator == null && preconditions == NO_LITERALS;
  }

  public boolean isEnd() {
    return operator == null && effects == NO_LITERALS;
  }

  public edu.uno.ai.planning.Step makeStep(Substitution substitution) {
    HashSubstitution mapping = new HashSubstitution();
    for (int i = 0; i < parameters.length; i++)
      mapping.set(operator.parameters.get(i),
                  substitution.get(parameters.get(i)));
    return operator.makeStep(mapping);
  }

  public ImmutableArray<Variable> parameters() {
    return parameters;
  }

  public ImmutableArray<Literal> preconditions() {
    return preconditions;
  }

  public Operator operator() {
    return operator;
  }

  @Override
  public String toString() {
    return toString(Bindings.EMPTY);
  }

  @Override
  public String toString(Substitution substitution) {
    if (isStart())
      return "start";
    else if (isEnd())
      return "end";
    String str = "(" + operator.name;
    for (Variable parameter : parameters)
      str += " " + parameter.substitute(substitution);
    str += ")";
    return str;
  }
}
