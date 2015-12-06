package edu.uno.ai.planning.pop_un_loc;

public class LIFO<T extends Flaw> extends AbstractCriterion<T> {
  @Override
  public T bestOf(T first, T second) {
    // Our core data structure (Flaws) is already in LIFO,
    // so the first ones will be the last ones added.
    return first;
  }

  @Override
  public T bestOf(T... flaws) {
    return flaws[0];
  }
}
