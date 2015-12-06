package edu.uno.ai.planning.pop_un_loc;

public interface Criterion<T extends Flaw> {
  public T bestOf(T only);

  public T bestOf(T first, T second);

  public T bestOf(T... flaws);

  public T bestOf(Iterable<T> flaws);
}
