package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;

public class FlawList implements Partial {

  private static final ImmutableList<Flaw> remove(Flaw flaw,
                                                  ImmutableList<Flaw> flaws) {
    if (flaws.length == 0)
      return flaws;
    else if (flaws.first == flaw)
      return flaws.rest;
    else {
      ImmutableList<Flaw> rest = remove(flaw, flaws.rest);
      if (rest == flaws.rest)
        return flaws;
      else
        return rest.add(flaws.first);
    }
  }

  private final ImmutableList<Flaw> flaws;

  FlawList(Step end) {
    ImmutableList<Flaw> flaws = new ImmutableList<>();
    for (Literal precondition : end.preconditions)
      flaws = flaws.add(new OpenPreconditionFlaw(end, precondition));
    this.flaws = flaws;
  }

  private FlawList(ImmutableList<Flaw> flaws) {
    this.flaws = flaws;
  }

  public FlawList add(Flaw flaw) {
    return new FlawList(flaws.add(flaw));
  }

  public Flaw chooseFlaw() {
    return flaws.first;
  }

  public FlawList remove(Flaw flaw) {
    return new FlawList(remove(flaw, flaws));
  }

  public int size() {
    return flaws.length;
  }

  @Override
  public String toString() {
    return toString(Bindings.EMPTY);
  }

  @Override
  public String toString(Substitution substitution) {
    String str = "FLAWS:";
    for (Flaw flaw : flaws)
      str += "\n  " + flaw.toString(substitution);
    return str;
  }
}
