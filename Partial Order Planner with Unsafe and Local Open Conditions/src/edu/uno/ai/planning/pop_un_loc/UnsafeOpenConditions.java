package edu.uno.ai.planning.pop_un_loc;

public class UnsafeOpenConditions extends Flaws<UnsafeOpenCondition> {

  public UnsafeOpenConditions() {
    super();
  }

  public UnsafeOpenConditions(UnsafeOpenCondition flaw) {
    super(flaw);
  }

  public UnsafeOpenConditions(UnsafeOpenCondition... flaws) {
    super(flaws);
  }

  public UnsafeOpenConditions(Iterable<UnsafeOpenCondition> flaws) {
    super(flaws);
  }

  @Override
  public UnsafeOpenConditions toUnsafeOpenConditions() {
    return this;
  }
}
