package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;

public class Flaws implements Partial {

  private final ImmutableList<Flaw> flaws;

  public Flaws() {
    flaws = new ImmutableList<Flaw>();
  }

  public Flaws(Flaw flaw) {
    flaws = new ImmutableList<Flaw>(flaw);
  }

  public Flaws(Step end) {
    ImmutableList<Flaw> tmp = new ImmutableList<Flaw>();
    for (Literal precondition : end.preconditions)
      tmp = tmp.add(new OpenPreconditionFlaw(end, precondition));
    flaws = tmp;
  }

  public Flaws(Flaw... flaws) {
    this.flaws = new ImmutableList<Flaw>(flaws);
  }

  public Flaws(ImmutableList<Flaw> flaws) {
    this.flaws = new ImmutableList<Flaw>(flaws);
  }

  @Override
  public String toString() {
    return toString(Bindings.EMPTY);
  }

  @Override
  public String toString(Substitution substitution) {
    String str = "FLAWS:";
    for (Flaw flaw : flaws)
      str += "\n " + flaw.toString(substitution);
    return str;
  }

  public ImmutableList<Flaw> toImmutableList() {
    return flaws;
  }
}
