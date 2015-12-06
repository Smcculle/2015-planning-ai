package edu.uno.ai.planning.pop_un_loc;

public class OpenConditions extends Flaws<OpenCondition> {
  public OpenConditions() {
    super();
  }

  public OpenConditions(OpenCondition flaw) {
    super(flaw);
  }

  public OpenConditions(Step end) {
    super(end);
  }

  public OpenConditions(Iterable<OpenCondition> flaws) {
    super(flaws);
  }

  public OpenConditions(OpenCondition... flaws) {
    super(flaws);
  }

  @Override
  public OpenConditions openConditions() {
    return this;
  }
}
