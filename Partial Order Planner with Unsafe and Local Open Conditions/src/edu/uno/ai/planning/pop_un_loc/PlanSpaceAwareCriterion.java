package edu.uno.ai.planning.pop_un_loc;

public abstract class PlanSpaceAwareCriterion<T extends Flaw>
    implements Criterion<T> {

  private PlanSpaceNode planSpaceNode;

  public PlanSpaceAwareCriterion(PlanSpaceNode planSpaceNode) {
    this.planSpaceNode = planSpaceNode;
  }

  @Override
  public abstract T bestOf(T only);

  @Override
  public abstract T bestOf(T first, T second);

  @Override
  public abstract T bestOf(T... flaws);

  public PlanSpaceNode planSpaceNode() {
    return planSpaceNode;
  }
}
