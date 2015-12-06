package edu.uno.ai.planning.pop_un_loc;

public class FlawSelection {
  public static PlanSpaceDefinedSelection<Flaw> flawFrom(PlanSpaceNode planSpaceNode) {
    return new PlanSpaceDefinedSelection<Flaw>(planSpaceNode,
                                               planSpaceNode.flaws());
  }

  private FlawSelection() {}
}
