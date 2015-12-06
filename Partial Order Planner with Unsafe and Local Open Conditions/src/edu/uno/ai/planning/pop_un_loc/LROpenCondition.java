package edu.uno.ai.planning.pop_un_loc;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Literal;
import javaslang.collection.List;

public class LROpenCondition extends AbstractLR<OpenCondition> {
  public LROpenCondition(PlanSpaceNode planSpaceNode) {
    super(planSpaceNode);
  }

  @Override
  public OpenCondition bestOf(OpenCondition first, OpenCondition second) {
    if (refinementsOf(first) <= refinementsOf(second))
      return first;
    return second;
  }

  public List<Step> possibleSolutions() {
    return List.ofAll(planSpaceNode().steps()).appendAll(operatorSteps());
  }

  public List<Step> operatorSteps() {
    List<Step> result = List.empty();

    for (Operator operator : planSpaceNode().operators())
      result = result.push(new Step(operator));

    return result;
  }

  @Override
  public Integer refinementsOf(OpenCondition flaw) {
    Integer refinementCount = 0;

    for (Step step : possibleSolutions())
      if (solvesFlaw(step, flaw))
        refinementCount++;

    return refinementCount;
  }

  public Boolean solvesFlaw(Step step, OpenCondition flaw) {
    for (Literal effect : step.effects()) {
      Bindings newBindings = flaw.precondition()
                                 .unify(effect, planSpaceNode().bindings());
      if (newBindings != null) {
        Orderings newOrderings = planSpaceNode().orderings().add(step,
                                                                 flaw.step);
        if (newOrderings != null) {
          return true;
        }
      }
    }
    return false;
  }
}
