package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;


public class Graphplan extends Planner<GraphPlanSearch>{

	Problem problem;
	
	PlanGraph currentPlanGraph;
	PlanGraph solution;
	int currentLevel = new Integer(-1);
	
	ArrayList<PlanGraph> parentList = new ArrayList<PlanGraph>();
	ArrayList<PlanGraph> solutions;
	ArrayList<PlanGraphStep> iterateList = new ArrayList<PlanGraphStep>();
	ArrayList<Literal> preconditions = new ArrayList<Literal>();
	
	ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
	Set<PlanGraphStep> howToAchieveGoals = new HashSet<PlanGraphStep>();
	ArrayList<PlanGraphStep> howToAchieveGoalsList = new ArrayList<PlanGraphStep>();
	Iterator<PlanGraphStep> iter;

	public Graphplan() {
			super("GraphPlan");
	}
	
	@Override
	protected final GraphPlanSearch makeSearch(Problem problem){
		return new GraphPlanSearch(problem);
	}
	
}