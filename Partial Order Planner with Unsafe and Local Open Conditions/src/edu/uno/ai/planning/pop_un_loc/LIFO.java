package edu.uno.ai.planning.pop_un_loc;

import java.util.Iterator;

// Our core data structure (Flaws) is already in LIFO,
// so the first ones will be the last ones added.
public class LIFO<T extends Flaw> extends AbstractCriterion<T> {
  @Override
  public T bestOf(T first, T second) {
    return first;
  }

  @Override
  public T bestOf(T... flaws) {
    return flaws[0];
  }

  @Override
  public T bestOf(Iterable<T> flaws) {
    Iterator<T> iterator = flaws.iterator();
    if (iterator.hasNext())
      return flaws.iterator().next();
    return null;
  }
}
