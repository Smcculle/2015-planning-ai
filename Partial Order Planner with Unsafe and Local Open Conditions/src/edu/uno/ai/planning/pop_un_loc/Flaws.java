package edu.uno.ai.planning.pop_un_loc;

import java.util.Iterator;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;
import javaslang.collection.Stack;
import javaslang.control.Try;

public class Flaws<E extends Flaw> implements Iterable<E>, Partial {

  private final Stack<E> flaws;

  public Flaws() {
    flaws = Stack.empty();
  }

  public Flaws(E flaw) {
    flaws = Stack.of(flaw);
  }

  public Flaws(Step end) {
    Stack<E> tmp = Stack.empty();
    for (Literal precondition : end.preconditions)
      tmp = tmp.push((E) new OpenCondition(end, precondition));
    flaws = tmp;
  }

  public Flaws(E... flaws) {
    Stack<E> tmp = Stack.empty();
    for (E flaw : flaws)
      tmp = tmp.push(flaw);
    this.flaws = tmp;
  }

  public Flaws(Iterable<E> flaws) {
    this.flaws = Stack.ofAll(flaws);
  }

  public Flaws<E> add(E flaw) {
    return new Flaws<E>(flaws.push(flaw));
  }

  public Flaws<E> addLast(E flaw) {
    return new Flaws<E>(flaws.append(flaw));
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
  public Iterator<E> iterator() {
    return flaws.iterator();
  }

  public Flaws<E> remove(E flaw) {
    return new Flaws<E>(flaws.remove(flaw));
  }

  public int size() {
    return flaws.length();
  }

  public OpenConditions toOpenConditions() {
    Stack<OpenCondition> openConditions = Stack.empty();
    for (Flaw flaw : flaws)
      if (flaw instanceof OpenCondition)
        openConditions = openConditions.append((OpenCondition) flaw);
    return new OpenConditions(openConditions);
  }

  public ImmutableList<E> toImmutableList() {
    return new ImmutableList<E>(flaws);
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

  public ThreatenedCausalLinks toThreatenedCausalLinks() {
    Stack<ThreatenedCausalLink> links = Stack.empty();
    for (Flaw flaw : flaws)
      if (flaw instanceof ThreatenedCausalLink)
        links = links.append((ThreatenedCausalLink) flaw);
    return new ThreatenedCausalLinks(links);
  }

  public OpenConditions toUnsafeOpenConditions(PlanSpaceNode planSpaceNode) {
    Stack<OpenCondition> openConditions = Stack.empty();
    for (OpenCondition openCondition : toOpenConditions())
      if (planSpaceNode.isUnsafe(openCondition))
        openConditions = openConditions.append(openCondition);
    return new OpenConditions(openConditions);
  }
}
