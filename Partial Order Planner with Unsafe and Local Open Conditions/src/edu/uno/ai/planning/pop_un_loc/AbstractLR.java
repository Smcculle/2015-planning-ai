package edu.uno.ai.planning.pop_un_loc;

public abstract class AbstractLR<T extends Flaw>
    extends PlanSpaceAwareCriterion<T> {

  AbstractLR(PlanSpaceNode planSpaceNode) {
    super(planSpaceNode);
  }

  public abstract Integer refinementsOf(T flaw);
}
