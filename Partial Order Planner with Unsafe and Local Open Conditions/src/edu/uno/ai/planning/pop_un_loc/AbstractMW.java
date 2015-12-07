package edu.uno.ai.planning.pop_un_loc;

public abstract class AbstractMW<T extends Flaw>
    extends PlanSpaceAwareCriterion<T> {
  public AbstractMW(PlanSpaceNode planSpaceNode) {
    super(planSpaceNode);
  }

  @Override
  public T bestOf(T first, T second) {
    if (workOf(first) >= workOf(second))
      return first;
    return second;
  }

  public abstract Integer workOf(T flaw);
}
