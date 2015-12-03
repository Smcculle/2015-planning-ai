package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.logic.Literal;

public class UnsafeOpenCondition extends OpenCondition implements Flaw {

  public UnsafeOpenCondition(Step step, Literal precondition) {
    super(step, precondition);
  }
}
