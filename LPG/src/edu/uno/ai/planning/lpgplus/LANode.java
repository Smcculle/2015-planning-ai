package edu.uno.ai.planning.lpgplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uno.ai.planning.graphplan.PlanGraph;
import edu.uno.ai.planning.graphplan.PlanGraphLevelMutex;
import edu.uno.ai.planning.graphplan.PlanGraphLiteral;
import edu.uno.ai.planning.graphplan.PlanGraphStep;

public class LANode {
	
	private static Map<PlanGraphLiteral, PlanGraphStep> pStepMap;
	private static PlanGraph graph;
	
	public Set<PlanGraphLiteral> facts;
	public Set<PlanGraphStep> pSteps;

	/** unique step at this level */
	private PlanGraphStep step;
	
	public LANode previous;
	public LANode next;
	
	public LANode(LANode previous, LANode next, PlanGraphStep step) {
		
		this(previous, next);
		addStep(step);
	}
	
	public LANode(LANode previous, LANode next) {
		this.previous = previous;
		this.next = next;
		facts = new HashSet<PlanGraphLiteral>();
		pSteps = new HashSet<PlanGraphStep>();
	}
	
	public void setPMap(Map<PlanGraphLiteral, PlanGraphStep> pStepMap) {
		LANode.pStepMap = pStepMap;
	}
	
	public void setGraph(PlanGraph graph) {
		LANode.graph = graph;
	}

	
	public void addStep(PlanGraphStep step) {
		
		this.step = step;
		
		Set<PlanGraphStep> nextLevelPSteps = new HashSet<PlanGraphStep>(pSteps);
		Set<PlanGraphStep> deletePSteps = new HashSet<PlanGraphStep>(getMutexStepList());
		
		for (PlanGraphLiteral fact: step.getChildNodes() ) {
			facts.add(fact);
			nextLevelPSteps.add(pStepMap.get(fact));
		}
		
		deletePSteps.retainAll(nextLevelPSteps);
		nextLevelPSteps.removeAll(deletePSteps);
		if (deletePSteps.size() > 0) {
			for(PlanGraphStep psStep : deletePSteps)
				facts.remove(psStep.getChildNodes().get(0));
			if (next != null)
				next.propagateDeletePStep(deletePSteps);
		}
			
		if( nextLevelPSteps.size() > 0 && next != null)
			next.propagateAddPStep(nextLevelPSteps);
		
	}
	
	public PlanGraphStep getStep(){
		return step;
	}
	
	public boolean contains(PlanGraphLiteral fact){
		return facts.contains(fact);
	}
	
	public void removeStep() {
		Set<PlanGraphStep> unblockedPSteps = new HashSet<PlanGraphStep>(getMutexStepList());
		Set<PlanGraphStep> deletePSteps = new HashSet<PlanGraphStep>();
		unblockedPSteps.retainAll(pSteps);
		
		for (PlanGraphLiteral fact : step.getChildNodes()) {
			facts.remove(fact);
			deletePSteps.add(pStepMap.get(fact));
		}	
		
		if(unblockedPSteps.size() > 0 && next != null)
			propagateAddPStep(unblockedPSteps);
		
		if(deletePSteps.size() > 0 && next != null)
			propagateDeletePStep(deletePSteps);
	}
	
	private List<PlanGraphStep> getMutexStepList() {
		
		if (step != null) {
			PlanGraphLevelMutex mutexLevel = (PlanGraphLevelMutex) graph.getLevel(graph.countLevels() - 1);
			List<PlanGraphStep> result = mutexLevel.getMutuallyExclusiveSteps().get(step);
			if(result != null)
				return new ArrayList<PlanGraphStep>(result);
			else
				return Collections.emptyList();
		}	
		else
			return Collections.emptyList();
		
	}
	
	private void propagateDeletePStep(Set<PlanGraphStep> deletePSteps) {
		
		/* add all persistent steps*/
		for(Iterator<PlanGraphStep> it = deletePSteps.iterator(); it.hasNext();) {
			PlanGraphStep pStep = it.next();
			PlanGraphLiteral fact = pStep.getChildNodes().get(0);
			if(!facts.remove(fact))
				it.remove();
		}
		
		if( deletePSteps.size() > 0 && next != null)
				next.propagateDeletePStep(deletePSteps);
		
	}
	
	private void propagateAddPStep(Set<PlanGraphStep> newPSteps) {
		
		/* add all persistent steps*/
		pSteps.addAll(newPSteps);
		newPSteps.removeAll(getMutexStepList());
		
		/* add facts of any psteps that aren't blocked and propagate*/
		if(newPSteps.size() > 0) {
			for (Iterator<PlanGraphStep> it = newPSteps.iterator(); it.hasNext();) {
				PlanGraphStep pStep = (PlanGraphStep) it.next();
				PlanGraphLiteral fact = pStep.getChildNodes().get(0);
				if (facts.contains(fact))
					it.remove();
				else
					facts.add(fact);
			}
		}
		if(next != null)
			next.propagateAddPStep(newPSteps);
	}	
}		
				
		

	

