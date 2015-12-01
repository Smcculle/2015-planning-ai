package edu.uno.ai.planning.pop_un_loc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Substitution;

public class FlawStack implements Collection<Flaw>, Iterable<Flaw>, Partial {

  private Stack<Flaw> flaws;

  public FlawStack() {
    flaws = new Stack<Flaw>();
  }

  public FlawStack(Flaw flaw) {
    flaws = new Stack<Flaw>();
    push(flaw);
  }

  @Override
  public boolean add(Flaw flaw) {
    return flaws().add(flaw);
  }

  @Override
  public boolean addAll(Collection<? extends Flaw> collection) {
    return flaws().addAll(collection);
  }

  @Override
  public boolean contains(Object object) {
    if (object instanceof Flaw) {
      return flaws().contains(object);
    }
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return flaws().containsAll(collection);
  }

  public Stack<Flaw> flaws() {
    return flaws;
  }

  @Override
  public boolean isEmpty() {
    return flaws().isEmpty();
  }

  @Override
  public Iterator<Flaw> iterator() {
    return flaws().iterator();
  }

  public void push(Flaw flaw) {
    flaws.push(flaw);
  }

  @Override
  public boolean remove(Object object) {
    return flaws().remove(object);
  }

  @Override
  public int size() {
    return flaws().size();
  }

  @Override
  public Object[] toArray() {
    return flaws().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return flaws().toArray(a);
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

  @Override
  public Iterator<Flaw> iterator() {
    return flaws().iterator();
  }
}
