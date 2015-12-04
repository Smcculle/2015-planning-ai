package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;

public class OpenCondition extends AbstractFlaw {

  public final Literal precondition;
  public final Step step;

  OpenCondition(Step step, Literal precondition) {
    this.step = step;
    this.precondition = precondition;
  }

  public Literal precondition() {
    return precondition;
  }

  public Step step() {
    return step;
  }

  @Override
  public String toString() {
    return toString(Bindings.EMPTY);
  }

  @Override
  public String toString(Substitution substitution) {
    return precondition.substitute(substitution) + " open for " +
           step.toString(substitution);
  }
}
