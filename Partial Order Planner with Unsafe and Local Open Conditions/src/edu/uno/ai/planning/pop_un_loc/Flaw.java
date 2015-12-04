package edu.uno.ai.planning.pop_un_loc;

public interface Flaw extends Partial {

  public Boolean isOpenCondition();

  public Boolean isThreat();

  public Boolean isUnsafe();
}
