package edu.uno.ai.planning.pop_un_loc;

public class PlanSpaceDefinedSelection<T extends Flaw> {
  private Flaws<T> flaws;
  private PlanSpaceNode planSpaceNode;

  public PlanSpaceDefinedSelection(PlanSpaceNode planSpaceNode,
                                   Flaws<T> subset) {
    this.flaws = subset;
    this.planSpaceNode = planSpaceNode;
  }

  public Flaws<T> flaws() {
    return flaws;
  }

  public PlanSpaceNode planSpaceNode() {
    return planSpaceNode;
  }
}
