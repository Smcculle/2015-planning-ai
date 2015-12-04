package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Substitution;

public class ThreatenedCausalLink extends AbstractFlaw {

  public final CausalLink link;
  public final Step threat;

  public ThreatenedCausalLink(CausalLink link, Step threat) {
    this.link = link;
    this.threat = threat;
  }

  @Override
  public Boolean isThreat() {
    return true;
  }

  public CausalLink link() {
    return link;
  }

  public Step threat() {
    return threat;
  }

  @Override
  public String toString() {
    return toString(Bindings.EMPTY);
  }

  @Override
  public String toString(Substitution substitution) {
    return threat.toString(substitution) + " threatens " +
           link.toString(substitution);
  }
}
