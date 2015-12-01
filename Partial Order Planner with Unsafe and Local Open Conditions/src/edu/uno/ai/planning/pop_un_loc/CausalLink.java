package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;

public class CausalLink implements Partial {

  public final Step head;
  public final Literal label;
  public final Step tail;

  CausalLink(Step tail, Literal label, Step head) {
    this.tail = tail;
    this.label = label;
    this.head = head;
  }

  public Boolean isGround() {
    return label.isGround();
  }

  public Literal negatedLabelWithBindings(Bindings bindings) {
    return label.negate().substitute(bindings);
  }

  @Override
  public String toString() {
    return toString(Bindings.EMPTY);
  }

  @Override
  public String toString(Substitution substitution) {
    return tail.toString(substitution) + "-" + label.substitute(substitution) +
           "->" + head.toString(substitution);
  }
}
