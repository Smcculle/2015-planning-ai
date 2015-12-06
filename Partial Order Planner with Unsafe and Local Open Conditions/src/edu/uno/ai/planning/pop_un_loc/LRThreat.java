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

  public Boolean canBeDemoted(ThreatenedCausalLink flaw) {
    if (planSpaceNode().demoteThreat(flaw) == null)
      return false;
    return true;
  }

  public Boolean canBePromoted(ThreatenedCausalLink flaw) {
    if (planSpaceNode().promoteThreat(flaw) == null)
      return false;
    return true;
  }

  @Override
  public Integer refinementsOf(ThreatenedCausalLink flaw) {
    Integer refinementCount = 0;
    if (canBePromoted(flaw))
      refinementCount++;
    if (canBeDemoted(flaw))
      refinementCount++;

    return refinementCount;
  }
}
