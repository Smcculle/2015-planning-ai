package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;

public class OpenPreconditionFlaw implements Flaw {

  public final Literal precondition;
  public final Step step;

  OpenPreconditionFlaw(Step step, Literal precondition) {
    this.step = step;
    this.precondition = precondition;
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
