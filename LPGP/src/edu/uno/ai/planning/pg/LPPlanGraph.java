package edu.uno.ai.planning.pg;

import edu.uno.ai.planning.DurativeDomain;
import edu.uno.ai.planning.Problem;

public class LPPlanGraph extends PlanGraph {
	
	/**
	 * Default value of PlanGraph must calculate mutual exclusions.
	 * 
	 * @param problem The Problem in which to setup PlanGraph
	 */
	public LPPlanGraph (Problem problem)
	{
		super(problem, true);
		if(problem.domain instanceof DurativeDomain){
			DurativeDomain dd = (DurativeDomain) problem.domain;
			if(dd.isDurativeDomain())
				computeDurativeStaticMutexes();
		}
	}
	
	public LPPlanGraph(Problem problem, boolean mutexes) {
		super(problem , mutexes);
	}
	
	private final void computeDurativeStaticMutexes(){
		// Compute static mutexes for all pairs of steps.
		for(int i=0; i<steps.length; i++) {
			for(int j=i; j<steps.length; j++) {
				if(alwaysMutex(steps[i], steps[j])) {
					steps[i].mutexes.add(steps[j], Mutexes.ALWAYS);
					steps[j].mutexes.add(steps[i], Mutexes.ALWAYS);
				}
			}
		}
	}

	/**
	 * PDDL 2.1 requires a stronger requirement to ensure that actions do not
	 * interfere w/one another.
	 */
	private final boolean alwaysMutex(StepNode s1, StepNode s2) {
		// No Moving Targets: two actions cannot be executed concurrently if they
		//		add the same effect or delete the same effect
		if(s1 == s2)
			return false;
		if(movingTargets(s1, s2) || movingTargets(s2, s1))
			return true;
		return false;
	}
	
	private final boolean movingTargets(StepNode s1, StepNode s2) {
		for(LiteralNode s1Effect : s1.effects) {
			for(LiteralNode s2Effects : s2.effects)
				if(s2Effects.literal.equals(s1Effect.literal))
					return true;
		}
		return false;
	}
	/**
	 * Need to override this method from PlanGraph
	 * to keep invariant-checking literals from being 
	 * persisted within the Plan Graph
	 */
	@Override
	protected void addEdgesForStep(StepNode stepNode) {
		// don't add persistence steps for invariant-checking literals
		if( !( stepNode.persistence && stepNode.step.name.endsWith("-inv") ) )
			super.addEdgesForStep(stepNode);
	}
	
}
