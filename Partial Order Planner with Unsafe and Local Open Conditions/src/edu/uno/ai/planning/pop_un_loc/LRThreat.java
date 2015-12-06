package edu.uno.ai.planning.pop_un_loc;

public class LRThreat extends AbstractLR<ThreatenedCausalLink> {

  LRThreat(PlanSpaceNode planSpaceNode) {
    super(planSpaceNode);
  }

  @Override
  public ThreatenedCausalLink bestOf(ThreatenedCausalLink first,
                                     ThreatenedCausalLink second) {
    if (refinementsOf(first) <= refinementsOf(second))
      return first;
    return second;
  }

  @Override
  public Integer refinementsOf(ThreatenedCausalLink flaw) {
    return 0;
  }
}
