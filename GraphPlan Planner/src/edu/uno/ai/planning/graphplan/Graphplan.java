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
	
	
	
	
	public void createHighestNode(){
		ArrayList<PlanGraphLiteral> properGoals = new ArrayList<>
		for (Literal lit: goals){
			
		}
		createNode(goals)
	}
	public void searchV2(){
	
		
		
	}
	
	public void doGraphPlan(){
		extend();
		if (pg.isLeveledOff()){
			
		}else{
			parentList.clear();
			doGraphPlan();
		}
		
	}
	
	public void extend(){
		nextPG(pg);
		
		for (PlanGraph node: parentList){
			if (node.getLevel() == (parentList.size() -1)){
				currentPlanGraph = node;
				currentLevel = parentList.size() - 1;
				break;
			}
		} 	
			
		search(expressionToLiterals(problem.goal));
	}
	
	public void search(ArrayList<Literal> goals){
		if (currentPlanGraph.getLevel() == 0){
			System.out.println("i got here");
			return;
		}
		else{
//			System.out.println(parentList.size());
			goalLiterals = goals;
		
			steps = currentPlanGraph.getCurrentSteps();
			
			for (PlanGraphStep step: steps){
				for (Literal goalLiteral: goalLiterals){
					for (Literal effectLiteral: expressionToLiterals(step.getStep().effect)){
						if (effectLiteral.equals(goalLiteral)){
							if (step.GetInitialLevel() == currentPlanGraph.getLevel()){
							howToAchieveGoals.add(step);
							iterateList.add(step);
							}
						}
					}
				}
			}
			
//			System.out.println(steps);
			
			
			howToAchieveGoalsList.addAll(howToAchieveGoals);
			
//			System.out.println(howToAchieveGoalsList);
			
			for (int i = 0; i < howToAchieveGoalsList.size(); i++) {
				for (int j = i+1; j < howToAchieveGoalsList.size(); j++) {
//					System.out.println(howToAchieveGoalsList.get(i));
//					System.out.println(howToAchieveGoalsList.get(j));
//					System.out.println(currentPlanGraph._mutexSteps);
					
					if (currentPlanGraph.isMutex(howToAchieveGoalsList.get(i), howToAchieveGoalsList.get(j))){
						
						iterateList.remove(howToAchieveGoalsList.get(i));
					}
				}
			}
//			System.out.println(iterateList);	
				
				
				
			for (PlanGraphStep sol: iterateList){
				solution._steps.add(sol);
			}
			
			for (PlanGraphStep step: iterateList){
				for (Literal preconditionToLiteral: expressionToLiterals(step.GetStep().precondition)){
					preconditions.add(preconditionToLiteral);
				}
			}
			
			howToAchieveGoals.clear();
			howToAchieveGoalsList.clear();
			iterateList.clear();
			currentLevel = currentLevel - 1;
			solution = new PlanGraph(solution);
			
			for (PlanGraph node: parentList){
				if (node.getLevel() == currentLevel){
					currentPlanGraph = node;
					break;
				}
			}
			
//			if (areStepsSolution()){
				search(preconditions);
//			}
			
		}
		
		System.out.println(solution.getAllSteps());
		System.out.println(solution.deleteRepeats());
		System.out.println(solution);
	}
	
	
	
	public Boolean areStepsSolution(){
		
		PlanGraph solTemp = solution;
		
		List<PlanGraphStep> match = pg.getSolvingActions(problem.goal);
		ArrayList<PlanGraphStep> x = new ArrayList<PlanGraphStep>();
		x.addAll(match);
		Collection<PlanGraphStep> list1 = x;
		Collection<PlanGraphStep> list2 = solTemp.getAllSteps();
		list2.removeAll(list1);

		if (list2.isEmpty()){
			return true;
		} else{
			return false;
		}
	}
	
	public void nextPG(PlanGraph pg){
		if (pg.getLevel() == 0){
			parentList.add(pg);
			return;
		}
		parentList.add(pg);
		nextPG(pg.getParent());
	}
	
	public PlanGraph findInitialPlanGraph(){
		PlanGraph temp = null;
		for (PlanGraph node: parentList){
			if (node.getLevel() == 0){
				temp = node;
				break;
			}
		} 
		return temp;	
	}
	
	
	////////////////////////////////
	
	
	/**
	 * step - the step we want to test
	 * prev - the previous plan graph from the list
	 * return true if our step can be applied to our literal list with the effect we want next
	 */
	public boolean isStepApplicable(PlanGraphStep step, int prev){
		for (PlanGraphLiteral literal1 : parentList.get(prev).getCurrentLiterals()){	// for each literal in the specified level's list
			if (literal1.equals(step.GetStep().precondition)){	// if we find a precondition that matches our step's precondition
				for (PlanGraphLiteral literal2 : parentList.get(prev + 1).getCurrentLiterals()){	// now look through the next level's list of literals 
					if (literal2.equals(step.GetStep().effect)) return true; // if we find a literal that matches our step's effect, that means it is applicable
				}
				return false;	// if we found a valid precondition but not the effect 
			}
		}
		return false;
	}

	/**
	 * Use above method first
	 * so if we have a PlanGraphStep that satisfies at least one of the effects we want
	 * return that step
	 * then can use the PlanGraphStep.getStep().effect and/or PlanGraphStep.getStep().precondition for whatever
	 */ 
	public PlanGraphStep getApplicableStep(PlanGraphStep step, int prev){
		for (PlanGraphLiteral literal1 : parentList.get(prev).getCurrentLiterals()){	// for each literal in the specified level's list
			if (literal1.equals(step.GetStep().precondition)){	// if we find a precondition that matches our step's precondition
				for (PlanGraphLiteral literal2 : parentList.get(prev + 1).getCurrentLiterals()){	// now look through the next level's list of literals 
					if (literal2.equals(step.GetStep().effect)) return step; // if we find a literal that matches our step's effect, that means it is applicable
				}
			}
		}
		return null;
	}

	/**
	 * Get a step, apply it
	 * and return what that previous literal needs to change to
	 */
	public PlanGraphLiteral applyApplicableStep(PlanGraphLiteral literal, int prev){
		if (canAddStep(literal)){
			PlanGraphStep temp = getAddableStep(literal);
			return new PlanGraphLiteral ((expressionToLiterals(temp.GetStep().precondition)).get(0));
		}
		return null;
	}

	/**
	 * Check if we can apply steps from our goal step backwards. 
	 * If we get any mutexes return false. 
	 * If no mutexes, return true.
	 */
	public boolean canApplyApplicableSteps(ArrayList<PlanGraphLiteral> literals, int prev){
		/** Saved bits, will check against level 0. */
		ArrayList<ArrayList<PlanGraphLiteral>> literalprecTotal = new ArrayList<ArrayList<PlanGraphLiteral>>();

		/** For while we have levels above 0. 0 is our initial state so we'll have to check that somewhere else. */
		for (int i = prev; i > 0; i--){
			ArrayList<PlanGraphLiteral> literalprec = new ArrayList<PlanGraphLiteral>();		// a list of our literal preconditions
			ArrayList<PlanGraphStep> steplist = new ArrayList<PlanGraphStep>();					// our steps

			ArrayList<PlanGraphLiteral> precList = new ArrayList<PlanGraphLiteral>();			// preconditions of steps - check for mutexes
			ArrayList<PlanGraphLiteral> fxList = new ArrayList<PlanGraphLiteral>();				// effects of steps - check for mutexes

			/** For each of our literals in our arraylist of literals, add its applicable step to our step array, and that step's precondition to our precondition array. 
			for (PlanGraphLiteral literal : literals){
				literalprec.add(applyApplicableStep(literal, i));
				steplist.add(getAddableStep(literal));
			}
			/** Check for mutexes. If mutexes, return false. */
			for (PlanGraphLiteral literal : literalprec){
				PlanGraphLiteral litNegation = new PlanGraphLiteral((new NegatedLiteral(literal.getLiteral())).negate());
				if (literalprec.contains(litNegation)) return false;
			}
			/** Add each step's preconditions to the precondition list, and effects to our effects list. */
			for (PlanGraphStep step : steplist){
				precList.add( new PlanGraphLiteral( (expressionToLiterals(step.GetStep().precondition)).get(0)));
				fxList.add( new PlanGraphLiteral( (expressionToLiterals(step.GetStep().effect)).get(0)));
			}
			/** If there are precondition mutexes, return false. */
			for (PlanGraphLiteral lit : precList){
				if (precList.contains(lit.getLiteral().negate())) return false;
			}
			/** If there are effect mutexes, return false. */
			for (PlanGraphLiteral lit : fxList){
				if (fxList.contains(new PlanGraphLiteral((new NegatedLiteral(lit.getLiteral())).negate()))) return false;
			}
			literalprecTotal.add(0, literalprec);
		}
		/** So far so good. Now check against our first literals. */
		for (PlanGraphLiteral literal : literalprecTotal.get(0)){
//			PlanGraphLiteral negate = new PlanGraphLiteral((new NegatedLiteral(literal.getLiteral())).negate());
//			if ( parentList.get(0).initialLiterals.contains(negate)) return false;
		}
		return true;	
	}

	/** 
	 * Apply applicable steps and get what our preconditions will have to be. 
	 * It's an arraylist of arraylists, so we have preconditions for each level. 
	 */
	public ArrayList<ArrayList<PlanGraphLiteral>> applyApplicableStepsGetLiterals(ArrayList<PlanGraphLiteral> literals, int prev){
		if (!canApplyApplicableSteps(literals, prev)) return null;
		ArrayList<ArrayList<PlanGraphLiteral>> literalList = new ArrayList<ArrayList<PlanGraphLiteral>>();
		for (int i = prev; i > 0; i--){
			ArrayList<PlanGraphLiteral> literalprec = new ArrayList<PlanGraphLiteral>();
			for (PlanGraphLiteral literal : literals){
				literalprec.add(applyApplicableStep(literal, i));
			}
			literalList.add(0, literalprec);
		}
		return literalList;
	}

	/** see if the step's effect matches our literal */
	public boolean isStepApplicableEffect(PlanGraphStep step, PlanGraphLiteral literal){
		return ( (literal.getLiteral().equals(step.GetStep().effect)) );
	}
	/** see if the step's percondition matches our literal */
	public boolean isStepApplicablePrecon(PlanGraphStep step, PlanGraphLiteral literal){
		return ((literal.getLiteral().equals(step.GetStep().precondition)));
	}

	/**
	 * effect - the literal we want to become true
	 * precon - the literal we need to have a precondition
	 * returns whether or not we have a step possible that can give us the effect we want
	 */ 
	public boolean canAddStep(PlanGraphLiteral effect, PlanGraphLiteral precon){
		for (PlanGraphStep step : pg.getAllSteps()){
			if (isStepApplicableEffect(step, effect) && (isStepApplicablePrecon(step, precon))) return true;
		}
		return false;
	}

	/**
	 * Use the above method first to see if an applicable step exists
	 * then use this one to get that step
	 */ 
	public PlanGraphStep getAddableStep(PlanGraphLiteral effect, PlanGraphLiteral precon){
		for (PlanGraphStep step : pg.getAllSteps()){
			if (canAddStep(effect, precon)) return step;
		}
		return null;
	}

	/** this is if we just have an effect and want to see if it can become true */
	public boolean canAddStep(PlanGraphLiteral effect){
		for (PlanGraphStep step : pg.getAllSteps()){
			if (isStepApplicableEffect(step, effect)) return true;
		}
		return false;	
	}

	/** use the above one first */
	public PlanGraphStep getAddableStep(PlanGraphLiteral effect){
		for (PlanGraphStep step : pg.getAllSteps()){
			if (canAddStep(effect)) return step;
		}
		return null;
	}

}
