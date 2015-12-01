package edu.uno.ai.planning.pop_un_loc;

import java.util.Iterator;
import java.util.Stack;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Substitution;

public class FlawStack implements Iterable<Flaw>, Partial {

  private Stack<Flaw> flaws;

  public FlawStack() {
    flaws = new Stack<Flaw>();
  }

  public FlawStack(Flaw flaw) {
    flaws = new Stack<Flaw>();
    push(flaw);
  }

  public Stack<Flaw> flaws() {
    return flaws;
  }

  public void push(Flaw flaw) {
    flaws.push(flaw);
  }

  @Override
  public String toString() {
    return toString(Bindings.EMPTY);
  }

  @Override
  public String toString(Substitution substitution) {
    String str = "FLAWS:";
    // for (Flaw flaw : flaws)
    // str += "\n " + flaw.toString(substitution);
    return str;
  }

  @Override
  public Iterator<Flaw> iterator() {
    return flaws().iterator();
  }
}
