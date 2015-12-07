package edu.uno.ai.planning.pop_un_loc;

public class LR extends AbstractLR<Flaw> {
  private LROpenCondition lrOpenCondition;
  private LRThreat lrThreat;

  LR(PlanSpaceNode planSpaceNode) {
    super(planSpaceNode);
    lrOpenCondition = new LROpenCondition(planSpaceNode);
    lrThreat = new LRThreat(planSpaceNode);
  }

  @Override
  public Flaw bestOf(Flaw first, Flaw second) {
    if (refinementsOf(first) <= refinementsOf(second))
      return first;
    return second;
  }

  @Override
  public Integer refinementsOf(Flaw flaw) {
    if (flaw instanceof OpenCondition)
      return lrOpenCondition.refinementsOf((OpenCondition) flaw);
    if (flaw instanceof ThreatenedCausalLink)
      return lrThreat.refinementsOf((ThreatenedCausalLink) flaw);
    return 0;
  }
}
