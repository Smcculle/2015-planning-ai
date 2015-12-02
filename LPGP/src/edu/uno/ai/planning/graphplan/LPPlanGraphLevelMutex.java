package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.util.ConversionUtil;

public class LPPlanGraphLevelMutex extends PlanGraphLevelMutex {
	
	/**
	 * Constructs a new PlanGraphLevel
	 * Does not create additional lists for facts/effect and steps/actions.
	 * This constructor is specifically intended to create root level of PlanGraph
	 * Calculates all mutual exclusions at constructor
	 * 	
	 * @param problem The StateSpaceProblem to setup PlanGraph Steps and Effects
	 */
	public LPPlanGraphLevelMutex (Problem problem, ArrayList<PlanGraphStep> steps, ArrayList<PlanGraphLiteral> effects,
			ArrayList<PlanGraphStep> persistenceSteps, LPPlanGraph planGraph) {
		super(problem, steps, effects, persistenceSteps, planGraph);
	}
	
	/**
	 * Constructs a new PlanGraphLevelMutex child
	 * Does not create additional lists for facts/effect and steps/actions.
	 * Calculates all mutual exclusions at constructor
	 * 
	 * @param parent The parent of new PlanGraph
	 */
	public LPPlanGraphLevelMutex (PlanGraphLevelMutex parent) {
		super(parent);
		checkForNoMovingTargets();
	}
	
	/**
	 * Checks to see if there are no moving targes with newly added step.
	 * Moving Target: One action cannot add or delete the same effect
	 * as another action
	 */
	private void checkForNoMovingTargets(){
		for (PlanGraphStep step : _steps){
			if (exists(step)){
				for (PlanGraphStep otherStep : _steps){
					if (step != otherStep && exists(otherStep)){
						// Check effects are not the same
						ArrayList<Literal> stepEffectLiterals = ConversionUtil.expressionToLiterals(step.getStep().effect);
						ArrayList<Literal> otherStepEffectLiterals = ConversionUtil.expressionToLiterals(otherStep.getStep().effect);
						for (Expression literal : stepEffectLiterals){
							if(otherStepEffectLiterals.contains(literal)){
								addMutexStep(step,otherStep);
								break;
							}
						}
					}
				}
			}
		}
		
	}

}
