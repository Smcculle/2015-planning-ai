package edu.uno.ai.planning.SATPlan;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xpath.internal.operations.Bool;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.SATPlan.CNFEncodingModel.CNFVariableType;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Negation;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * This class models the encoding of the following things:
 * 	1. Initial State
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

	private ISATSolver satSolver;

	private Map<Expression, Literal> fluentsNotInThisActionsEffect;
	
	/**
	 * Instantiates the Encoding
	 */
	public CNFEncoding(ISATSolver satSolver) {
		this.satSolver = satSolver;
		this.cnf = new ArrayList<>();
		this.frameAxiomBuilder = new HashMap<>();
		this.encodingModel = new HashMap<>();
		this.fluentsNotInThisActionsEffect = new HashMap<>();
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

	public String getStringFromListOfBooleanVariables(List<BooleanVariable> disjunctions){
		String result = "";
//          for (ArrayList<BooleanVariable> disjunctions : this.cnf){
		for (BooleanVariable BV : disjunctions){
			result += (BV.negation ? "~" : "") + BV.name + " V ";
		}
		if (!result.isEmpty())
			result = result.substring(0, result.length() - 3);
		result += "\n";
//          }
		return result;
	}

	public String cnfToString(ArrayList<ArrayList<BooleanVariable>> givenCNF){
		String result = "";
		for (ArrayList<BooleanVariable> disjunctions : givenCNF){
			for (BooleanVariable BV : disjunctions){
				result += (BV.negation ? "~" : "") + BV.name + " V ";
			}
			if (!result.isEmpty())
				result = result.substring(0, result.length() - 3);
			result += "\n";
		}
		return result;
	}

	/**
	 * Everything that is not in the initial state at time = 0, it is not true in the
	 * initial state unless it is the precondition of the step that happens at the time = 0
	 * @param steps
	 * @return
	 */
	public ArrayList<ArrayList<BooleanVariable>> notTrueInTheInitialStep(Expression initial, ImmutableArray<Step> steps, int time){
		ArrayList<ArrayList<BooleanVariable>> result = new ArrayList<>();
		for (Step step : steps){
			for (Expression argument : ((Conjunction) step.effect).arguments)
			{
				boolean doesItNegateTheInitial = doesItNegateTheInitial(initial, argument);
				if ((argument instanceof Negation) && !doesItNegateTheInitial)

					result.add(new ArrayList<BooleanVariable>(){{
						add(argumentToBooleanVariable(argument, time));
					}});
				else if(!doesItNegateTheInitial)
					result.add(new ArrayList<BooleanVariable>(){{
								add(argumentToNegativeBooleanVariable(argument, time));
							}});
			}

			for (Expression argument : ((Conjunction) step.precondition).arguments)
			{
				boolean doesItNegateTheInitial = doesItNegateTheInitial(initial, argument);
				if ((argument instanceof Negation) && !doesItNegateTheInitial)

					result.add(new ArrayList<BooleanVariable>(){{
						add(argumentToBooleanVariable(argument, time));
					}});
				else if(!doesItNegateTheInitial)
					result.add(new ArrayList<BooleanVariable>(){{
						add(argumentToNegativeBooleanVariable(argument, time));
					}});
			}
		}
		return result;
	}


	private boolean doesItNegateTheInitial(Expression initial, Expression argument){
		boolean result = false;
		if (initial instanceof Predication){
			result = initial.equals(argument);
		}
		else {
			for (Expression thisArgument : ((Conjunction) initial).arguments) {
				if (((thisArgument instanceof Negation) && !(argument instanceof Negation) && argument.equals(thisArgument.negate()))
						|| (!(thisArgument instanceof Negation) && (argument instanceof Negation) && (argument.negate().equals(thisArgument)))
						|| argument.equals(thisArgument)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Creates a CNF for all the possible steps in the given number of time steps
	 * including the initial conditions at time 0 and the goal conditions at time
	 * timeMax
	 *
	 * @param initial The initial condition of the problem
	 * @param steps The set of all the possible steps
	 * @param goal The goal condition of the problem
	 * @param timeMax The maximum number of time steps for the encoding
	 */
	public ArrayList<ArrayList<BooleanVariable>> encode(
			Expression initial,
			ImmutableArray<Step> steps,
			Expression goal,
			int timeMax){
		ArrayList<ArrayList<BooleanVariable>> result =	new ArrayList<>();
		steps = removeUnsatisfiableSteps(steps);

		//1. add the initial condition
		result.addAll(conjunctionFromExpression(initial, 0));

		//2. Not true in the initial steps are also added to the CNF form
		result.addAll(notTrueInTheInitialStep(initial, steps, 0));

		ArrayList<BooleanVariable> atLeastOneActionHappensAtEachStep;
		for (int counter = 0; counter < timeMax; counter++)
		{
			atLeastOneActionHappensAtEachStep = new ArrayList<>();
			for (Step step: steps){
				atLeastOneActionHappensAtEachStep.add(
						new BooleanVariable(step.toString() + " - " + counter, null, Boolean.FALSE));
				result.addAll(stepToConjunction(step, counter));
			}

			//3. Add conjunctions to make sure at least one action occur at each step
			result.add(atLeastOneActionHappensAtEachStep);

			//4. Add conjunctions to make sure only one one occur at each step
			result.addAll(onlyOneActionOccursAtEachStep(steps, counter));
		}

		//5. Add the explanatory frame axioms
		result.addAll(getExplanatoryFrameAxioms(timeMax, steps.clone()));

		//will also need to add the classical frame axioms
		//may be that will help resolve the problem


		//6. Add the goal state
		result.addAll(conjunctionFromExpression(goal, timeMax));

		//7. This is actually the final step
		result.addAll(notTrueInTheInitialStep(goal, steps, timeMax));

	    this.cnf = result;
		return result;
	}

	protected ArrayList<BooleanVariable> oneClauseForOneEffectOrPreconditionLiteral(BooleanVariable stepLiteral, Expression expression, int time){
		ArrayList<BooleanVariable> disjunction = new ArrayList<>();
		disjunction.add(stepLiteral);//this is already negative
		disjunction.add(argumentToBooleanVariable(expression, time));
		return disjunction;
	}

	protected ArrayList<ArrayList<BooleanVariable>> actionImpliesToCNF(Expression expression, Step step, int time, boolean updateFrameAxioms){
		BooleanVariable stepLiteral = new BooleanVariable(
				step.toString() + " - " + time, null, Boolean.TRUE);
		ArrayList<ArrayList<BooleanVariable>> result = new ArrayList<>();
		if (expression instanceof Predication){
			result.add(oneClauseForOneEffectOrPreconditionLiteral(stepLiteral, expression, time));
			if (updateFrameAxioms) updateFrameAxiomBuilder(step, expression);
			return result;
		}
		else{
			for (Expression expressionLiteral : ((Conjunction) expression).arguments){
				result.add(oneClauseForOneEffectOrPreconditionLiteral(stepLiteral, expressionLiteral, time));
				if (updateFrameAxioms) updateFrameAxiomBuilder(step, expressionLiteral);
			}
			return result;
		}
	}

	protected ArrayList<ArrayList<BooleanVariable>>	stepToConjunction(Step step, int time) {
		ArrayList<ArrayList<BooleanVariable>> result =
				new ArrayList<>();

		CNFEncodingModel encodingModel = new CNFEncodingModel(
				step.toString() + " - " + time, CNFVariableType.ACTION, time, step);

		this.encodingModel.put(step.toString() + " - " + time, encodingModel);

		result.addAll(actionImpliesToCNF(step.precondition, step, time, false));

		result.addAll(actionImpliesToCNF(step.effect, step, time + 1, true));
		return result;
	}

	/**
	 * Returns a CNF of a Step for a particular time
	 * @param step one of the possible Step in the plan than is to be encoded
	 * @param time step value when the step happens
	 * @return A CNF of the step of the the form
	 * 				PRECONDITION AND ACTION => EFFECT
	 *
	 * 				~PRECONDITION OR ~ACTION OR ~ EFFECT
	 */
	protected ArrayList<ArrayList<BooleanVariable>> stepToConjunctionOld(Step step, int time){
		ArrayList<ArrayList<BooleanVariable>> result = new ArrayList<>();

		BooleanVariable stepLiteral = new BooleanVariable(
				step.toString() + " - " + time, null, Boolean.TRUE);

		CNFEncodingModel encodingModel = new CNFEncodingModel(
				step.toString() + " - " + time, CNFVariableType.ACTION, time, step);

		this.encodingModel.put(step.toString() + " - " + time, encodingModel);

		ArrayList<BooleanVariable> disjunctionsForPreConditions = new ArrayList<>();
		disjunctionsForPreConditions.add(stepLiteral);

		for (Expression argument : ((Conjunction) step.precondition).arguments)
			disjunctionsForPreConditions.add(argumentToNegativeBooleanVariable(argument, time));

		for (Expression argument : ((Conjunction) step.effect).arguments)
		{
			ArrayList<BooleanVariable> disjunctionsForEffectsCombined =	new ArrayList<>();

			disjunctionsForEffectsCombined.addAll(disjunctionsForPreConditions);

			disjunctionsForEffectsCombined.add(argumentToBooleanVariable(argument, time + 1));

			result.add(disjunctionsForEffectsCombined);

			updateFrameAxiomBuilder(step, argument);
		}
		return result;
	}


	/**
	 * A private function that helps keeps track of all the state changes
	 * and generate a explanatory frame axiom in the end
	 * @param step One of the possible step for the domain
	 * @param state of the effect of this action
	 */
	private void updateFrameAxiomBuilder(Step step, Expression state){
		if(this.frameAxiomBuilder.get(state) == null){
			ArrayList<Step> allStepsWithThisEffect = new ArrayList<>();
			allStepsWithThisEffect.add(step);
			this.frameAxiomBuilder.put(state, allStepsWithThisEffect);
		}
		else{
			ArrayList<Step> allStepsWithThisEffect = frameAxiomBuilder.get(state);
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

	private ArrayList<ArrayList<BooleanVariable>> getExplanatoryFrameAxioms(int timeMax, ArrayList<Step> allSteps){
		ArrayList<ArrayList<BooleanVariable>> result = new ArrayList<>();
		Iterator it = frameAxiomBuilder.entrySet().iterator();
	    while (it.hasNext()) {
	    	ArrayList<BooleanVariable> explanatoryClauseForAction;
			ArrayList<BooleanVariable> classicalAxiomForExpression;
	        Map.Entry pair = (Map.Entry)it.next();
	        Expression action = (Expression)pair.getKey();
	        ArrayList<Step> setOfSteps = (ArrayList<Step>)pair.getValue();
	        for (int counter = 1; counter <= timeMax; counter++)
			{
				for (Step step : allSteps){
					classicalAxiomForExpression = new ArrayList<>();
					if (!setOfSteps.contains(step)){
						boolean originalNegationOfAction = false;
						if (action instanceof  Negation)
							originalNegationOfAction = true;
						classicalAxiomForExpression.add(
								new BooleanVariable((originalNegationOfAction ? action.negate().toString() : action.toString()) + " - " + (counter - 1), null, originalNegationOfAction ? Boolean.FALSE : Boolean.TRUE));
						classicalAxiomForExpression.add(
								new BooleanVariable(step.toString() + " - " + (counter - 1), null, Boolean.FALSE));
						classicalAxiomForExpression.add(
								new BooleanVariable((originalNegationOfAction ? action.negate().toString() : action.toString()) + " - " + counter, null, originalNegationOfAction ? Boolean.TRUE: Boolean.FALSE));
					}
					ArrayList<ArrayList<BooleanVariable>> temp = new ArrayList<>();
					temp.add(classicalAxiomForExpression);
					result.add(classicalAxiomForExpression);
				}
	        	explanatoryClauseForAction = new ArrayList<>();
		        Expression pureExpression = action;
		        if (action instanceof Negation){
		        	pureExpression = action.negate();
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
		        	ArrayList<BooleanVariable> explanatoryClauseForActionWithStep = new ArrayList<>();

		        	explanatoryClauseForActionWithStep.addAll(explanatoryClauseForAction);
		        	explanatoryClauseForActionWithStep.add(
		        			new BooleanVariable(actionStep.toString() + " - " + (counter - 1), null, Boolean.FALSE));

		        	result.add(explanatoryClauseForActionWithStep);

		        }


	        }
//	        it.remove();
	    }

		return result;
	}

	/**
	 * This returns a CNF that ensures that only one step occurs at each time
	 * @param allSteps Set of all possible steps for this domain
	 * @param time a time-step at which a step would run
	 * @return
	 */
	protected ArrayList<ArrayList<BooleanVariable>> onlyOneActionOccursAtEachStep(ImmutableArray<Step> allSteps, int time){
		ArrayList<ArrayList<BooleanVariable>> result = new ArrayList<>();
			for (Step step: allSteps){
				for(Step innerStep: allSteps)
					if (!innerStep.equals(step)) {
						ArrayList<BooleanVariable> tempDisjunction = new ArrayList<>();
						tempDisjunction.add(new BooleanVariable(step.toString() + " - " + time, null, Boolean.TRUE));
						tempDisjunction.add(new BooleanVariable(innerStep.toString() + " - " + time, null, Boolean.TRUE));
						result.add(tempDisjunction);
					}//End of If
				//End of Inner all steps
			}//End of outer all steps
		return result;
	}

	/**
	 * This function converts the Expression into a CNF
	 * @param expression Either a predication of a conjunction
	 * @param time a time-step at which the given expression is true
	 * @return A CNF of the expression
	 */

	protected ArrayList<ArrayList<BooleanVariable>> conjunctionFromExpression(Expression expression, int time)
	{
		ArrayList<ArrayList<BooleanVariable>> result = new ArrayList<>();
		if (expression == null) return result;
		if (expression instanceof Predication){
			ArrayList<BooleanVariable> temp = new ArrayList<>();
			temp.add(argumentToBooleanVariable(expression, time));
			result.add(temp);
		}
		else{
			for (Expression argument : ((Conjunction) expression).arguments)
			{
				ArrayList<BooleanVariable> temp = new ArrayList<>();
				temp.add(argumentToBooleanVariable(argument, time));
				result.add(temp);
			}
		}
		return result;
	}

	protected ArrayList<BooleanVariable> predicationToDisjunction(Expression expression , int time){
		ArrayList<BooleanVariable> result =
				new ArrayList<>();
		if (expression == null) return result;
		if (expression instanceof Predication){
			result.add(argumentToBooleanVariable(expression, time));
		}
		return result;
	}

	protected ArrayList<BooleanVariable> predicationToNegativeDisjunction(Expression expression , int time){
		ArrayList<BooleanVariable> result =
				new ArrayList<>();
		if (expression == null) return result;
		if (expression instanceof Predication){
			result.add(argumentToNegativeBooleanVariable(expression, time));
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
	
	protected BooleanVariable argumentToBooleanVariable(Expression argument, int time)
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
	BooleanVariable 
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
	
	/**
	 * Returns if the step has the satisfiable effects. For example a step
	 * like move(table, table, table) has not table and table as an effect
	 * which is not satisfiable and hence the step doesn't make sense.
	 * @param step
	 * @return the boolean whether the effects of this step are satisfiable
	 */	
	Boolean hasSatisfiableEffects(Step step){	
		// System.out.println("step is " + step);
		// System.out.println("effect is " + step.effect);
		ArrayList<ArrayList<BooleanVariable>> effectsConjunction = new ArrayList<ArrayList<BooleanVariable>>();
		
		for (Expression argument : ((Conjunction) step.effect).arguments)
		{	
			ArrayList<BooleanVariable> onlyOneDisjunction = 
					new ArrayList<BooleanVariable>();
			
			onlyOneDisjunction.add(
					argumentToBooleanVariable(argument, 1));
			
			effectsConjunction.add(onlyOneDisjunction);
		}
		
		ArrayList<BooleanVariable> mainList = new ArrayList<BooleanVariable>();
		mainList.clear();
		
		SATProblem problemo = new SATProblem(effectsConjunction, mainList);
		
		List<BooleanVariable> solution = satSolver.getModel(problemo);

		if (solution == null)
			return false;
		else {
			for (BooleanVariable bv : solution){
				if (bv.value){
					return true;
				}
			}
			return false;
		}
	}

	ImmutableArray<Step> removeUnsatisfiableSteps(ImmutableArray<Step> steps){
//		System.out.println("Steps before refining");
//		printSteps(steps);
		Set<Step> result = new HashSet<Step>();
		for (Step step: steps){
			if (hasSatisfiableEffects(step)
					&& !stepHasBadPreconditionOrEffect(step.effect)
					&& !stepHasBadPreconditionOrEffect(step.precondition)){
				result.add(step);
			}
		}
//		System.out.println("Steps after refining");
		ImmutableArray<Step> resultSteps = new ImmutableArray<Step>(result, Step.class);
//		printSteps(resultSteps);
		return resultSteps;
	}

	private boolean stepHasBadPreconditionOrEffect(Expression preconditionOrEffect){
		boolean result = false;
		if (preconditionOrEffect instanceof Predication){
			return false;
		}
		else {
			Conjunction conjunctionExpression = (Conjunction)preconditionOrEffect;
			for (Expression literal : conjunctionExpression.arguments) {
				if (hasMoreThanOneOccurence(literal.toString(), conjunctionExpression.toString()))
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}

	private Boolean hasMoreThanOneOccurence(String wordToFind, String wordToSearchFrom){
		int i = 0;
		Pattern p = Pattern.compile(wordToFind);
		Matcher m = p.matcher( wordToSearchFrom );
		while (m.find()) {
			i++;
		}
		return i > 1;
	}

	private void printSteps(ImmutableArray<Step> steps){
		for (Step step: steps){
			System.out.println("Precondition is " + step.precondition);
			System.out.println(step);
			System.out.println("Effect is " + step.effect);
		}

		System.out.println("----DONE PRINTING----");
	}
}
