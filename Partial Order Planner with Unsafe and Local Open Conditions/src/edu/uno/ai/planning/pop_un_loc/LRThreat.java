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

  public Boolean canBePromoted(ThreatenedCausalLink flaw) {
    if (planSpaceNode().promoteThreat(flaw) == null)
      return false;
    return true;
  }

  @Override
  public Integer refinementsOf(ThreatenedCausalLink flaw) {
    return 0;
  }
}
