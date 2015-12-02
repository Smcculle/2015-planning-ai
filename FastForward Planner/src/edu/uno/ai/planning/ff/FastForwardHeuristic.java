package edu.uno.ai.planning.ff;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uno.ai.planning.State;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class FastForwardHeuristic extends PlanGraph {

	private final Set<Literal> goalLiterals;
	
	public FastForwardHeuristic(StateSpaceProblem problem) {
		super(problem, false); 				//false means do not evaluate mutexes
		this.goalLiterals = Util.expressionToLiterals(problem.goal);
	}
	
	@SuppressWarnings("unchecked")
	public int hValue (State state) {
		initialize(state);
		while (!goalAchieved() && !hasLeveledOff()) {
			extend();
		}
		if (!goalAchieved()) {
			return Integer.MAX_VALUE;
		}
		
		int hValue = 0;
		List<Literal>[] levelGoals = new List[size()];
		for (int i = 0; i < levelGoals.length; i++) {
			levelGoals[i] = new LinkedList<Literal>();
		}
		
		//start at end level
		int currentLevel = size()-1;
		//add all goals to level goals of end level to initialize traceback
		levelGoals[currentLevel].addAll(goalLiterals);
		
		while (currentLevel > 0) {
			for (Literal goal : levelGoals[currentLevel]) {
				//for each goal in level i, if goal exists in i-1, add goal to be achieved in i-1
				if (literalMap.get(goal).exists(currentLevel-1)) {
					levelGoals[currentLevel-1].add(goal);
				//if goal does not exist in i-1, find action that produces goal
				//and add its preconditions as goals to be achieved in i-1
				} else {
					hValue++;
					for (LiteralNode precondition : literalMap.get(goal).getProducers(currentLevel).iterator().next().getPreconditions(currentLevel-1)) {
						levelGoals[currentLevel-1].add(precondition.literal);
					}
				}
			}		
			currentLevel--;
		} //stop at first level
		return hValue;
	}
	
}