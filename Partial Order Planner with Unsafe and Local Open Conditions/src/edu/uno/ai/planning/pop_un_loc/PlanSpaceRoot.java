package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.shsp.AdditiveHeuristic;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class PlanSpaceRoot extends PlanSpaceNode {

  public final Problem problem;
  private final AdditiveHeuristic additiveHeuristic;

  int limit = -1;

  PlanSpaceRoot(Problem problem) {
    super(problem);
    this.problem = problem;
    additiveHeuristic = new AdditiveHeuristic(stateSpaceProblem());
  }

  @Override
  public void enforceNodeLimit() {
    if (isAtLimit())
      throw new SearchLimitReachedException();
  }

  @Override
  public Boolean isAtLimit() {
    return limit == visited;
  }

  @Override
  public Boolean isRoot() {
    return true;
  }

  public Problem problem() {
    return problem;
  }

  void setNodeLimit(int limit) {
    this.limit = limit;
  }

  @Override
  public StateSpaceProblem stateSpaceProblem() {
    return new StateSpaceProblem(problem());
  }
}
