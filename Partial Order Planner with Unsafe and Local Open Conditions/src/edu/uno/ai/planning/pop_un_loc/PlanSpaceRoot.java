package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.SearchLimitReachedException;

public class PlanSpaceRoot extends PlanSpaceNode {

  public final Problem problem;

  int limit = -1;

  PlanSpaceRoot(Problem problem) {
    super(problem);
    this.problem = problem;
  }

  public void enforceNodeLimit() {
    if (isAtLimit())
      throw new SearchLimitReachedException();
  }

  @Override
  public Boolean isAtLimit() {
    return limit == visited;
  }

  void setNodeLimit(int limit) {
    this.limit = limit;
  }
}
