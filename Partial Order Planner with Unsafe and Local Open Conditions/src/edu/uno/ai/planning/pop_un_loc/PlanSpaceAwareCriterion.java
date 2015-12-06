package edu.uno.ai.planning.pop_un_loc;

public abstract class PlanSpaceAwareCriterion<T extends Flaw>
    extends AbstractCriterion<T> {

  private PlanSpaceNode planSpaceNode;

  public PlanSpaceAwareCriterion(PlanSpaceNode planSpaceNode) {
    this.planSpaceNode = planSpaceNode;
  }

  public PlanSpaceNode planSpaceNode() {
    return planSpaceNode;
  }
}
