package edu.uno.ai.planning.pop_un_loc;

import java.util.Iterator;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;
import javaslang.collection.Stack;
import javaslang.control.Try;

public class Flaws implements Iterable<Flaw>, Partial {

  private final Stack<Flaw> flaws;

  public Flaws() {
    flaws = Stack.empty();
  }

  public Flaws(Flaw flaw) {
    flaws = Stack.of(flaw);
  }

  public Flaws(Step end) {
    Stack<Flaw> tmp = Stack.empty();
    for (Literal precondition : end.preconditions)
      tmp = tmp.push(new OpenPreconditionFlaw(end, precondition));
    flaws = tmp;
  }

  public Flaws(Flaw... flaws) {
    Stack<Flaw> tmp = Stack.empty();
    for (Flaw flaw : flaws)
      tmp = tmp.push(flaw);
    this.flaws = tmp;
  }

  public Flaws(Iterable<Flaw> flaws) {
    this.flaws = Stack.ofAll(flaws);
  }

  public Flaws add(Flaw flaw) {
    return new Flaws(flaws.push(flaw));
  }

  public Flaws addLast(Flaw flaw) {
    return new Flaws(flaws.append(flaw));
  }

  public Flaw chooseFirstFlaw() {
    return Try.of(() -> flaws.head()).orElse(null);
  }

  public Flaw chooseFlaw() {
    return chooseFirstFlaw();
  }

  public Flaw chooseLastFlaw() {
    return Try.of(() -> flaws.last()).orElse(null);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Flaws) {
      return flaws.equals(((Flaws) object).flaws);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return flaws.hashCode();
  }

  @Override
  public Iterator<Flaw> iterator() {
    return flaws.iterator();
  }

  public Flaws remove(Flaw flaw) {
    return new Flaws(flaws.remove(flaw));
  }

  public int size() {
    return flaws.length();
  }

  public ImmutableList<Flaw> toImmutableList() {
    return new ImmutableList<Flaw>(flaws);
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
}
