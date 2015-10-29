package edu.uno.ai.planning.SATPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.SATPlan.CNFEncodingModel.CNFVariableType;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Negation;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * This class models the encoding of the following things:
 * 	1. Initital State
 * 	2. Steps
 * 		a. Preconditions
 * 		b. Effects
 * 	3. Goal State
 * 	4. At-least-one action happens at each time step
 *  5. Only one action happens at each time step
 *  6. Explanatory frame axioms (if state is changed then an action must have
 *     happened
 * 
 * @author Janak Dahal
 */
public class CNFEncoding {
		
	/* Stores the encoding in the Conjunctive Normal Form */
	private ArrayList<ArrayList<BooleanVariable>> cnf;	
	
	/* Stores steps that alter a state */
	private HashMap<Expression, ArrayList<Step>> frameAxiomBuilder;
	
	/* Stores the Encoding, i.e. info about the literal in the CNF*/
	private HashMap<String, CNFEncodingModel> encodingModel;
	
	/**
	 * Instantiates the Encoding
	 */
	public CNFEncoding() {
		this.cnf = new ArrayList<ArrayList<BooleanVariable>>();
		this.frameAxiomBuilder = new HashMap<Expression, ArrayList<Step>>();
		this.encodingModel = new HashMap<String, CNFEncodingModel>();
	}
	
	/**
	 * Returns the encoding model for this encoding
	 */
	public HashMap<String, CNFEncodingModel> getEncodingModel(){
		return this.encodingModel;
	}
	
	
	/**
	 * Prints the current encoding in a Conjunctive Normal Form
	 */
	public String toString(){
		String result = "";
		for (ArrayList<BooleanVariable> disjunctions : this.cnf){
			for (BooleanVariable BV : disjunctions){
				result += (BV.negation ? "~" : "") + BV.name + " V ";
			}
			if (!result.isEmpty()) result = 
					result.substring(0, result.length() - 3);
			result += "\n";
		}
		return result;
	}
	
	
	
	/**
	 * Creates a CNF for all the possible steps in the given number of time steps
	 * including the initial conditions at time 0 and the goal conditions at time
	 * timeMax
	 * 
	 * @param inital The initial condition of the problem
	 * @param steps The set of all the possible steps
	 * @param goal The goal condition of the problem
	 * @param timeMax The maximum number of time steps for the encoding
	 */
	public ArrayList<ArrayList<BooleanVariable>> encode(
			Expression initial, 
			ImmutableArray<Step> steps, 
			Expression goal, 
			int timeMax){
		ArrayList<ArrayList<BooleanVariable>> result = 
				new ArrayList<ArrayList<BooleanVariable>>();
		
		result.addAll(conjunctionFromExpression(initial, 0));		
		
		ArrayList<BooleanVariable> atleastOneActionHappensAtEachStep = 
				new ArrayList<BooleanVariable>();			
		
		for (int counter = 0; counter < timeMax; counter++)
		{
			atleastOneActionHappensAtEachStep = new ArrayList<BooleanVariable>();
			for (Step step: steps){
				atleastOneActionHappensAtEachStep.add(
						new BooleanVariable(step.toString() + " - " + counter, null, Boolean.FALSE));
				result.addAll(stepToConjunction(step, counter));
				stepToConjunction(step, counter);
			}
			result.add(atleastOneActionHappensAtEachStep);			
			result.addAll(onlyOneActionOccursAtEachStep(steps, counter));
		}
		
		result.addAll(getExplanatoryFrameAxioms(timeMax));
		
		result.addAll(conjunctionFromExpression(goal, timeMax));		
		
	    this.cnf = result;
		return result;		
	}
	
	/**
	 * Returns a CNF of a Step for a particular time
	 * @param step one of the possible Step in the plan than is to be encoded
	 * @param the time step value when the step happens
	 * @return A CNF of the step of the the form
	 * 				PRECONDITION AND ACTION => EFFECT
	 * 				
	 * 				~PRECONDITION OR ~ACTION OR ~ EFFECT
	 */
	private ArrayList<ArrayList<BooleanVariable>> 
								stepToConjunction(Step step, int time){
		ArrayList<ArrayList<BooleanVariable>> result = 
				new ArrayList<ArrayList<BooleanVariable>>();
		
		BooleanVariable stepLiteral = new BooleanVariable(
				step.toString() + " - " + time, null, Boolean.TRUE);
		
		CNFEncodingModel encodingModel = new CNFEncodingModel(
				step.toString() + " - " + time, CNFVariableType.ACTION, time);	
		
		this.encodingModel.put(step.toString() + " - " + time, encodingModel);
		
		ArrayList<BooleanVariable> disjunctionsForPreConditions = 
				new ArrayList<BooleanVariable>();
		disjunctionsForPreConditions.add(stepLiteral);
		
		for (Expression argument : ((Conjunction) step.precondition).arguments)
			disjunctionsForPreConditions.add(
					argumentToNegativeBooleanVariable(argument, time));
		
		for (Expression argument : ((Conjunction) step.effect).arguments)
		{	
			ArrayList<BooleanVariable> disjunctionsForEffectsCombined = 
					new ArrayList<BooleanVariable>();
			
			disjunctionsForEffectsCombined.addAll(
					disjunctionsForPreConditions);
			
			disjunctionsForEffectsCombined.add(
					argumentToBooleanVariable(argument, time + 1));
			
			result.add(disjunctionsForEffectsCombined);
			
			updateFrameAxiomBuilder(step, argument);
		}
		return result;
	}
	
	
	/**
	 * A private function that helps keeps track of all the state changes
	 * and generate a explanatory frame axiom in the end
	 * @param step One of the possible step for the domain
	 * @param one of the effect of this action
	 */
	private void updateFrameAxiomBuilder(Step step, Expression state){
		if(this.frameAxiomBuilder.get(state) == null){
			ArrayList<Step> allStepsWithThisEffect = new ArrayList<Step>();
			allStepsWithThisEffect.add(step);
			this.frameAxiomBuilder.put(state, allStepsWithThisEffect);
		}
		else{
			ArrayList<Step> allStepsWithThisEffect = 
					frameAxiomBuilder.get(state);
			if (!allStepsWithThisEffect.contains(step)){
				allStepsWithThisEffect.add(step);
				frameAxiomBuilder.put(state, allStepsWithThisEffect);
			}
		}
	}
	
	/**
	 * Given a step and a step-time, it returns a CNF with constrains
	 * explaining if a state of an object is changed between time-steps, then
	 * the action must have occurred
     * 
     * (not (on table a))
	 *		(move table a table)
	 *		(move table a a)
	 *		(move table a b)
	 * 		(moveToTable table a)
     * 
     *  not (on table a) - t V (on table a)- (t-1) V move1
     *  not (on table a) - t V (on table a)- (t-1) V move2 ...
	 *
	 * @param timeMax Maximum number of time-steps allowed for this plan
	 * 
	 * @return CNF describing the Explanatory Frame Axioms
	 */
	
	private ArrayList<ArrayList<BooleanVariable>> 
							getExplanatoryFrameAxioms(int timeMax){
		ArrayList<ArrayList<BooleanVariable>> result = 
				new ArrayList<ArrayList<BooleanVariable>>();
		
		Iterator it = frameAxiomBuilder.entrySet().iterator();		
		
	    while (it.hasNext()) {
	    	ArrayList<BooleanVariable> explanatoryClauseForAction = 
	    			new ArrayList<BooleanVariable>();
	        Map.Entry pair = (Map.Entry)it.next();
	        Expression action = (Expression)pair.getKey();
	        ArrayList<Step> setOfSteps = (ArrayList<Step>)pair.getValue();	        
	        
	        for (int counter = 1; counter <= timeMax; counter++)
	        {
	        	explanatoryClauseForAction = new ArrayList<BooleanVariable>();
		        Expression pureExpression = action;
		        if (action instanceof Negation){
		        	pureExpression = ((Negation)action).argument;
		        	explanatoryClauseForAction.add(
		        			new BooleanVariable(pureExpression.toString() + " - " + counter, null, Boolean.FALSE));
		        	explanatoryClauseForAction.add(
		        			new BooleanVariable(pureExpression.toString() + " - " + (counter - 1), null, Boolean.TRUE));
		        }
		        else{
		        	explanatoryClauseForAction.add(
		        			new BooleanVariable(pureExpression.toString() + " - " + counter, null, Boolean.TRUE));
		        	explanatoryClauseForAction.add(
		        			new BooleanVariable(pureExpression.toString() + " - " + (counter - 1), null, Boolean.FALSE));
		        }
		        
		        for (Step actionStep: setOfSteps){
		        	ArrayList<BooleanVariable> 
		        		explanatoryClauseForActionWithStep = 
		        					new ArrayList<BooleanVariable>();
		        	
		        	explanatoryClauseForActionWithStep.addAll(explanatoryClauseForAction);
		        	explanatoryClauseForActionWithStep.add(
		        			new BooleanVariable(actionStep.toString() + " - " + (counter - 1), null, Boolean.FALSE));
		        	
		        	result.add(explanatoryClauseForActionWithStep);
		        }
	        }
	        it.remove();
	    }
		
		return result;
	}
	
	/**
	 * This returns a CNF that ensures that only one step occurs at each time	
	 * @param allSteps Set of all possible steps for this domain
	 * @param time a time-step at which a step would run
	 * @return
	 */
	private static ArrayList<ArrayList<BooleanVariable>> 
		 onlyOneActionOccursAtEachStep(ImmutableArray<Step> allSteps, int time){
		ArrayList<ArrayList<BooleanVariable>> result = 
				new ArrayList<ArrayList<BooleanVariable>>();
			for (Step step: allSteps){
				for(Step innerStep: allSteps){
					if (!innerStep.equals(step)){
						ArrayList<BooleanVariable> tempDisjunct = 
								new ArrayList<BooleanVariable>();
						
						tempDisjunct.add(
								new BooleanVariable(step.toString() + " - " + time, null, Boolean.TRUE));
						tempDisjunct.add(
								new BooleanVariable(innerStep.toString() + " - " + time, null, Boolean.TRUE));
						
						result.add(tempDisjunct);
					}
				}//End of Inner all steps
			}//End of outer all steps
		return result;
	}
	
	/**
	 * This function converts the Expression into a CNF
	 * @param expression Either a predication of a conjunction
	 * @param time a time-step at which the given expression is true
	 * @return A CNF of the expression
	 */
	
	private static ArrayList<ArrayList<BooleanVariable>> 
			conjunctionFromExpression(Expression expression, int time)
	{		
		ArrayList<ArrayList<BooleanVariable>> result = 
				new ArrayList<ArrayList<BooleanVariable>>();	
		
		if (expression instanceof Predication){			
			ArrayList<BooleanVariable> temp = new ArrayList<BooleanVariable>();
			temp.add(argumentToBooleanVariable(expression, time));
			result.add(temp);
		}
		else{			
			for (Expression argument : ((Conjunction) expression).arguments)
			{	
				ArrayList<BooleanVariable> temp = new ArrayList<BooleanVariable>();
				temp.add(argumentToBooleanVariable(argument, time));
				result.add(temp);
			}		
		}
		return result;
	}
	
	/**
	 * This function converts an argument into a BooleanVariable
	 * 
	 * @param argument an argument to be converted to a BooleanVariable
	 * @param time a time-step at which the given argument holds
	 * @return A boolean variable representing the given argument
	 */
	
	private static BooleanVariable 
			argumentToBooleanVariable(Expression argument, int time)
	{
		BooleanVariable result;
		if (argument instanceof Negation){														
			result = new BooleanVariable(
					((Negation)argument).argument.toString() + " - " + time, null, Boolean.TRUE);
		}
		else{
			result = new BooleanVariable(argument.toString() + " - " + time, null, Boolean.FALSE);								
		}
		return result;
	}
	
	/**
	 * This function converts an argument into a negative BooleanVariable
	 * 
	 * @param argument an argument to be converted to a (-)BooleanVariable
	 * @param time a time-step at which the given argument holds
	 * @return A negative BooleanVariable representing the given argument
	 */	
	private static BooleanVariable 
		argumentToNegativeBooleanVariable(Expression argument, int time)
	{
		BooleanVariable result;
		if (argument instanceof Negation){														
			result = new BooleanVariable(
					((Negation)argument).argument.toString() + " - " + time, null, Boolean.FALSE);
		}
		else{
			result = new BooleanVariable(argument.toString() + " - " + time, null, Boolean.TRUE);								
		}
		return result;
	}
}
