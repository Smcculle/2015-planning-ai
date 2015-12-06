package edu.uno.ai.planning.pop_un_loc;

import java.util.Iterator;

public abstract class AbstractCriterion<T extends Flaw>
    implements Criterion<T> {

  @Override
  public T bestOf(T only) {
    return only;
  }

  @Override
  public abstract T bestOf(T first, T second);

  @Override
  public T bestOf(T... flaws) {
    T current = flaws[0];

    for (int i = 1; i < flaws.length; i++) {
      current = bestOf(current, flaws[i]);
    }

    return current;
  }

  @Override
  public T bestOf(Iterable<T> flaws) {
    Iterator<T> iterator = flaws.iterator();
    T best = iterator.next();

    while (iterator.hasNext())
      best = bestOf(best, iterator.next());

    return best;
  }

}
