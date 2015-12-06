package edu.uno.ai.planning.pop_un_loc;

public class LROpenCondition extends AbstractLR<OpenCondition> {
  public LROpenCondition(PlanSpaceNode planSpaceNode) {
    super(planSpaceNode);
  }

  @Override
  public OpenCondition bestOf(OpenCondition first, OpenCondition second) {
    return first;
  }

  @Override
  public Integer refinementsOf(OpenCondition flaw) {
    return 0;
  }
}
