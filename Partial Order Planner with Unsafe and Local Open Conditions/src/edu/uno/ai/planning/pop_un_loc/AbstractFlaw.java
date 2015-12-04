package edu.uno.ai.planning.pop_un_loc;

public abstract class AbstractFlaw implements Flaw {

  public AbstractFlaw() {}

  @Override
  public Boolean isOpenCondition() {
    return false;
  }

  @Override
  public Boolean isThreat() {
    return false;
  }

  @Override
  public Boolean isUnsafe() {
    return true;
  }
}
