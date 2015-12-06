package edu.uno.ai.planning.pop_un_loc;

import java.util.Iterator;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.util.ImmutableList;
import javaslang.collection.Stack;
import javaslang.control.Try;

public class Flaws<T extends Flaw> implements Iterable<T>, Partial {

  private final Stack<T> flaws;

  public Flaws() {
    flaws = Stack.empty();
  }

  public Flaws(T flaw) {
    flaws = Stack.of(flaw);
  }

  public Flaws(Step end) {
    Stack<T> tmp = Stack.empty();
    for (Literal precondition : end.preconditions)
      tmp = tmp.push((T) new OpenCondition(end, precondition));
    flaws = tmp;
  }

  public Flaws(T... flaws) {
    Stack<T> tmp = Stack.empty();
    for (T flaw : flaws)
      tmp = tmp.push(flaw);
    this.flaws = tmp;
  }

  public Flaws(Iterable<T> flaws) {
    this.flaws = Stack.ofAll(flaws);
  }

  public Flaws<T> add(T flaw) {
    return new Flaws<T>(flaws.push(flaw));
  }

  public Flaws<T> addLast(T flaw) {
    return new Flaws<T>(flaws.append(flaw));
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Flaws) {
      return flaws.equals(((Flaws) object).flaws);
    }
    return false;
  }

  public T first() {
    return Try.of(() -> flaws.head()).orElse(null);
  }

  @Override
  public int hashCode() {
    return flaws.hashCode();
  }

  @Override
  public Iterator<T> iterator() {
    return flaws.iterator();
  }

  public T last() {
    return Try.of(() -> flaws.last()).orElse(null);
  }

  public OpenConditions openConditions() {
    Stack<OpenCondition> openConditions = Stack.empty();
    for (Flaw flaw : flaws)
      if (flaw instanceof OpenCondition)
        openConditions = openConditions.append((OpenCondition) flaw);
    return new OpenConditions(openConditions);
  }

  public Flaws<T> remove(T flaw) {
    return new Flaws<T>(flaws.remove(flaw));
  }

  public T select() {
    return first();
  }

  public T selectBy(Criterion<T> criterion) {
    T selection = criterion.bestOf(flaws);

    if (selection == null)
      selection = first();

    return selection;
  }

  public Flaw selectFor(PlanSpaceNode planSpaceNode) {
    Flaw result = unsafeOpenConditions(planSpaceNode).select();
    if (result == null) {
      result = select();
    }

    return result;
  }

  public int size() {
    return flaws.length();
  }

  public ThreatenedCausalLinks threatenedCausalLinks() {
    Stack<ThreatenedCausalLink> links = Stack.empty();
    for (T flaw : flaws)
      if (flaw instanceof ThreatenedCausalLink)
        links = links.append((ThreatenedCausalLink) flaw);
    return new ThreatenedCausalLinks(links);
  }

  public ImmutableList<T> toImmutableList() {
    return new ImmutableList<T>(flaws);
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

  public OpenConditions unsafeOpenConditions(PlanSpaceNode planSpaceNode) {
    Stack<OpenCondition> openConditions = Stack.empty();
    for (OpenCondition openCondition : openConditions())
      if (planSpaceNode.isUnsafe(openCondition))
        openConditions = openConditions.append(openCondition);
    return new OpenConditions(openConditions);
  }
}
