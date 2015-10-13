package edu.uno.ai.planning.graphplan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;


public class Graphplan {

	Problem problem;
	PlanGraph pg;
	PlanGraph currentPlanGraph;
	PlanGraph solution;
	int currentLevel = new Integer(-1);
	ArrayList<PlanGraph> parentList = new ArrayList<PlanGraph>();
	ArrayList<PlanGraph> solutions;
	ArrayList<PlanGraphStep> iterateList = new ArrayList<PlanGraphStep>();
	ArrayList<Literal> preconditions = new ArrayList<Literal>();
	ArrayList<Literal> goalLiterals = new ArrayList<Literal>();
	ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
	Set<PlanGraphStep> goalsToAchieve = new HashSet<PlanGraphStep>();
	Iterator<PlanGraphStep> iter;

	
	public Graphplan(Problem problem) {
		this.problem = problem;
		pg = PlanGraph.create(this.problem);
		solution = new PlanGraph(problem);
		solution._steps.clear();
		
		
	}
	
	public void extend(){
		pg = new PlanGraph(pg);
		nextPG(pg);
		
		for (PlanGraph node: parentList){
			if (node.getLevel() == (parentList.size() -1)){
				currentPlanGraph = node;
				currentLevel = parentList.size() - 1;
				break;
			}
		} 	
			
//		if (!currentPlanGraph.isGoalNonMutex(problem.goal)){
//			extend();
//		}
		search(expressionToLiterals(problem.goal));
	}
	
	public void search(ArrayList<Literal> goals){
		if (currentPlanGraph.getLevel() == 0){
			System.out.println("i got here");
			return;
		}
		else{
			
			goalLiterals = goals;
			steps = currentPlanGraph.getCurrentSteps();
			
			for (PlanGraphStep step: steps){
				for (Literal goalLiteral: goalLiterals){
					for (Literal effectLiteral: expressionToLiterals(step.GetStep().effect)){
						if (effectLiteral.equals(goalLiteral)){
							goalsToAchieve.add(step);
							iterateList.add(step);
						}
					}
				}
			}
			
			
			iter = goalsToAchieve.iterator();
		
		
			PlanGraphStep temp = iter.next();
			for (int y = 1; y < iterateList.size(); y++){
				for (int i = y; i < iterateList.size(); i++){
					if (currentPlanGraph.isMutex(temp, iterateList.get(i))){
						iterateList.remove(temp);
					}
				}
				iter.next();
			}
			
			solution._steps.addAll(iterateList);
			for (PlanGraphStep step: iterateList){
				for (Literal preconditionToLiteral: expressionToLiterals(step.GetStep().precondition)){
					preconditions.add(preconditionToLiteral);
				}
			}

			goalsToAchieve.clear();
			iterateList.clear();
			currentLevel = currentLevel - 1;

			for (PlanGraph node: parentList){
				if (node.getLevel() == currentLevel){
					currentPlanGraph = node;
					break;
				}
			}

			search(preconditions);

		}
		
		
		
//		System.out.println(solution.getLevel());	
//		System.out.println(solution.getAllSteps());	
		
	}
	
	public void areStepsSolution(){
		solution = new PlanGraph(solution);
		System.out.println(parentList.get(0).getSolvingActions(problem.goal));
		System.out.println(solution.getAllSteps());
		
		List<PlanGraphStep> match = pg.getSolvingActions(problem.goal);
		ArrayList<PlanGraphStep> x = new ArrayList<PlanGraphStep>();
		x.addAll(match);
		Collection<PlanGraphStep> list1 = x;
		Collection<PlanGraphStep> list2 = solution.getAllSteps();
		list2.removeAll(list1);

		if (list2.isEmpty()){
			return;
		} else{
			
		}
	}
//		
//		for (PlanGraphStep item: match){
//			for (PlanGraphStep item2: solution.getAllSteps()){
//				if (item == )
//			}
//			
//		}
		
		
//		for (PlanGraphStep step: solution._steps){
////			if (step.GetStep() == )
//		}
//		
	
	
	
	
	public void nextPG(PlanGraph pg){
		if (pg.getLevel() == 0){
			parentList.add(pg);
			return;
		}
		parentList.add(pg);
		nextPG(pg.getParent());
	}
	
	/**
	 * Helper function to get all the literals from an Expression
	 * 
	 * @param expression The Expression to convert to list
	 * @return ArrayList<Literal> List of literals in expression
	 */
	public ArrayList<Literal> expressionToLiterals(Expression expression)
	{
		ArrayList<Literal> literals = new ArrayList<Literal>();
		if (expression instanceof Literal)
			literals.add((Literal)expression);
		else
		{
			Conjunction cnf = (Conjunction)expression.toCNF();
			for (Expression disjunction : cnf.arguments)
				if (((Disjunction) disjunction).arguments.length == 1)
					literals.add((Literal)((Disjunction) disjunction).arguments.get(0));
				// else -- Do Nothing!
		}
		return literals;
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

	/** see if we can use nop. may be a worthless method */
	public boolean canNopEffect(PlanGraphLiteral effect, int position){
		for (PlanGraphLiteral literal : parentList.get(position - 1).getCurrentLiterals()){	// for each literal in the specified location literal list
			if (literal.equals(effect)) return true;
		}
		return false;	
	}

	public boolean canNopPrecondition(PlanGraphLiteral precondition, int position){
		for (PlanGraphLiteral literal : parentList.get(position + 1).getCurrentLiterals()){	// for each literal in the specified location literal list
			if (literal.equals(precondition)) return true;
		}
		return false;	
	}
	
	public boolean canNopPrev(int position){
		for (PlanGraphLiteral precon : parentList.get(position -1 ).getCurrentLiterals()){
			for (PlanGraphLiteral effect : parentList.get(position).getCurrentLiterals()){
				if (effect.equals(precon)) return true;
			}
		}
		return false;
	}

	/** see if the step's effect matches our literal */
	public boolean isStepApplicableEffect(PlanGraphStep step, PlanGraphLiteral literal){
		return ( (literal.getLiteral().equals(step.GetStep().effect)) );
	}
	/** see if the step's percondition matches our literal */
	public boolean isStepApplicablePrecon(PlanGraphStep step, PlanGraphLiteral literal){
		return ((literal.getLiteral().equals(step.GetStep().effect)));
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
