package edu.uno.ai.planning.pop_un_loc;

public class MW extends PlanSpaceAwareCriterion<OpenCondition> {
  public MW(PlanSpaceNode planSpaceNode) {
    super(planSpaceNode);
  }

  @Override
  public OpenCondition bestOf(OpenCondition first, OpenCondition second) {
    if (workOf(first) >= workOf(second))
      return first;
    return second;
  }

  public Integer workOf(OpenCondition flaw) {
    return 0;
  }
}
