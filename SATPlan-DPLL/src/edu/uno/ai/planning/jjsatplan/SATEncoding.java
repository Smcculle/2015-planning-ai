package edu.uno.ai.planning.jjsatplan;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Negation;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ImmutableArray;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SATEncoding {
    private HashMap<Expression, ArrayList<Step>> stateModifyingSteps;
    private ArrayList<Expression> fluents;
    private ArrayList<Expression> initialFluents;
    private ImmutableArray<Step> steps;
    private HashMap<String, CNFEncodingModel> encodingModel;

    private Problem problem ;
    public SATEncoding(Problem problem){
        this.problem = problem;
        this.stateModifyingSteps = new HashMap<>();
        this.fluents = new ArrayList<>();
        this.initialFluents = new ArrayList<>();
        this.encodingModel = new HashMap<>();

        this.steps = removeUnsatisfiableSteps((new StateSpaceProblem(problem)).steps);

        buildFluentsAndStateModifiers();
    }

    public SATConjunction encode(int time){
        SATConjunction conjunction = new SATConjunction();

        /*Fully Define the Initial condition at time 0*/
        conjunction.add(getInitialConjunctionFromExpression());
        /*Define what is not true in the initial state*/
        conjunction.add(getFluentsNotTrueInInitialState());

        for (int counter = 0; counter < time; counter++) {

            /*Add the A => P, E for each of the steps*/
            for (Step step : this.steps) {
                conjunction.add(stepToConjunction(step, counter));
            }

            /*Add Constrain so that only one action occurs at each time */
            conjunction.add(onlyStepOccurAtEachTime(this.steps, counter));

            /*Add Constrain for at least one step to occur at each time*/
            conjunction.add(atLeastOneStepOccursAtEachTime(counter));

            /*Add the classical frame axioms */
            conjunction.add(getFrameAxioms(counter));
        }

        /*Add the goals*/
        conjunction.add(getGoalConjunctionFromExpression(time));

        //conjunction.add(getDisjunctionFromExpression(problem.goal, time)); -> not necessary
        return conjunction;
    }

    /**
     * Converts the given expression into a conjunction. Each argument in the expression is
     * evaluated as a clause in the disjunction
     * @return a conjunction with clauses that are the arguments fo the expression
     */
    public SATConjunction getInitialConjunctionFromExpression(){
        SATConjunction conjunction = new SATConjunction();
        Expression expression = problem.initial.toExpression();
        if (expression instanceof Predication){
            conjunction.add(new SATClause(BooleanVariable.create(expression, 0)));
            expression = (expression instanceof Negation) ? expression.negate() : expression;
            if (!this.initialFluents.contains(expression)) this.initialFluents.add(expression);
            return conjunction;
        }
        else if (expression instanceof Conjunction){
            for (Expression argument : ((Conjunction)expression).arguments){
                conjunction.add(new SATClause(BooleanVariable.create(argument, 0)));
                expression = (argument instanceof Negation) ? expression.negate() : expression;
                this.initialFluents.add(argument);
            }
            return conjunction;
        }
        else{
            throw new RuntimeException("Cannot recognize Expression");
        }
    }

    /**
     * Gets the explanatory frame axioms for the given setof axioms
     */


    /**
     * Converts the given expression into a conjunction. Each argument in the expression is
     * evaluated as a clause in the disjunction
     * @return a conjunction with clauses that are the arguments fo the expression
     */
    public SATConjunction getGoalConjunctionFromExpression(int time){
        SATConjunction conjunction = new SATConjunction();
        Expression expression = problem.goal;
        if (expression instanceof Predication){
            conjunction.add(new SATClause(BooleanVariable.create(expression, time)));
            return conjunction;
        }
        else if (expression instanceof Conjunction){
            for (Expression argument : ((Conjunction)expression).arguments){
                conjunction.add(new SATClause(BooleanVariable.create(argument, time)));
            }
            return conjunction;
        }
        else{
            throw new RuntimeException("Cannot recognize Expression");
        }
    }

    /**
     * Every other state other than the initial state are set to false
     */
    private SATConjunction getFluentsNotTrueInInitialState(){
        SATConjunction conjunction = new SATConjunction();
        for (Expression fluent : fluents){
            if (!initialFluents.contains(fluent) && !initialFluents.contains(fluent.negate())){
                conjunction.add(new SATClause(BooleanVariable.create(fluent.negate(), 0)));
            }
        }
        return conjunction;
    }

    /**
     * Converts the planning conjunction to the SATDisjunciton. i.e for every literal in the
     * conjunction is used as a clause in the conjunction
     * @param conjunction
     * @param time
     * @return
     */
    public SATConjunction satConjunctionFromConjunction(Conjunction conjunction, int time){
        SATConjunction _conjunction = new SATConjunction();
        for (Expression argument : conjunction.arguments){
            _conjunction.add(new SATClause(BooleanVariable.create(argument, time)));
        }
        return _conjunction;
    }

    /**
     * Converts the step into a conjunction, Action implies both precondition and effects
     * @param step the step to be converted to the conjunction
     * @param time the time at which the step occurs, the preconditions are true at time time and the effects
     *             are true at time time + 1
     * @return returns the conjunction encodings of the given step
     */
    public SATConjunction stepToConjunction(Step step, int time){
        CNFEncodingModel encodingModel = new CNFEncodingModel(
                step.toString() + " - " + time, CNFEncodingModel.CNFVariableType.ACTION, time, step);
        this.encodingModel.put(step.toString() + " - " + time, encodingModel);

        SATConjunction conjunction = new SATConjunction();
        BooleanVariable stepLiteral = new BooleanVariable(step.toString() + " - " + time, null, true);

        conjunction.add(convertPreconditionOrStepToConjunction(stepLiteral, step.precondition, time));
        conjunction.add(convertPreconditionOrStepToConjunction(stepLiteral, step.effect, time + 1));

        return conjunction;
    }


    /**
     * This is a helper method to help encode action implies both preconditions and effects at time t
     * @param stepLiteral the step literal (step converted to boolean variable)
     * @param preConditionOrEffect precondition or effect
     * @param time the time of action
     * @return the conjunction of the given expression and step literal at time t
     */
    private SATConjunction convertPreconditionOrStepToConjunction(BooleanVariable stepLiteral, Expression preConditionOrEffect, int time){
        SATConjunction conjunction = new SATConjunction();
        if (preConditionOrEffect instanceof Predication){
            conjunction.add(getFromPredicateAndStep(stepLiteral, preConditionOrEffect, time));
        }
        else if (preConditionOrEffect instanceof Conjunction){
            for (Expression argument : ((Conjunction)preConditionOrEffect).arguments){
                conjunction.add(getFromPredicateAndStep(stepLiteral, argument, time));
            }
        }
        else if (preConditionOrEffect instanceof Negation){
            conjunction.add(getFromPredicateAndStep(stepLiteral, preConditionOrEffect, time));
        }
        else{
            throw new RuntimeException("Cannot Recognize the step precondition or effect.");
        }
        return conjunction;
    }

    /**
     * Helper method that converts the given stepLiteral and the argument at time to the clause
     * @param stepLiteral the step literal (step converted to boolean variable)
     * @param argument the predicate to be added as a clause in disjunction
     * @param time the time at which the predicate holds
     * @return the disjunction of the given arguments
     */
    private SATClause getFromPredicateAndStep(BooleanVariable stepLiteral, Expression argument, int time){
        SATClause disjunction = new SATClause();
        disjunction.add(stepLiteral);
        disjunction.add(BooleanVariable.create(argument, time));
        return disjunction;
    }


    /**
     * This returns the conjunctions which make sure that only one step occurs at each time level
     * @param steps set of all steps
     * @param time the time at which the step occurs
     * @return the conjunction which specifies that only one step can occur at each time
     */
    private SATConjunction onlyStepOccurAtEachTime(ImmutableArray<Step> steps, int time){
        SATConjunction conjunction = new SATConjunction();
        for (Step step : steps){
            for (Step innerStep : steps) {
                if (!step.equals(innerStep)) {
                    SATClause disjunction = new SATClause();
                    disjunction.add(new BooleanVariable(step.toString() + " - " + time, null, true));
                    disjunction.add(new BooleanVariable(innerStep.toString() + " - " + time, null, true));
                    conjunction.add(disjunction);
                }
            }
        }
        return conjunction;
    }

    /**
     * This returns the clauses that make sure that at least one step would occur at each time
     * @param time the time at which the step occurs
     * @return the clauses to ensure at least one step occurs at each time
     */
    private SATClause atLeastOneStepOccursAtEachTime(int time){
        SATClause disjunction = new SATClause();
        for (Step step : this.steps){
            disjunction.add(BooleanVariable.create(step.toString(), time));
        }
        return disjunction;
    }

    /**
     * Removes all unsatisfiable steps from the set of steps
     * @param steps all the steps
     * @return pruned set of steps after removing unsatisfying steps
     */
    ImmutableArray<Step> removeUnsatisfiableSteps(ImmutableArray<Step> steps){
        Set<Step> result = new HashSet<Step>();
        for (Step step: steps){
            if (hasSatisfiableEffects(step) &&  !stepHasBadPreconditionOrEffect(step.effect) && !stepHasBadPreconditionOrEffect(step.precondition)){
                result.add(step);
            }
        }
        ImmutableArray<Step> resultSteps = new ImmutableArray<Step>(result, Step.class);
        return resultSteps;
    }

    Boolean hasSatisfiableEffects(Step step){
        SATConjunction conjunction = new SATConjunction();
        if (step.effect instanceof Predication){
            conjunction.add(new SATClause(BooleanVariable.create(step.effect.toString(), 1)));
        }
        else if (step.effect instanceof  Conjunction) {
            for (Expression argument : ((edu.uno.ai.planning.logic.Conjunction) step.effect).arguments) {
                conjunction.add(new SATClause(BooleanVariable.create(argument.toString(), 1)));
            }
        }
        else{
            throw new RuntimeException("Unrecognized arguments");
        }

        SATProblem satProblem = new SATProblem(conjunction.convert(), new ArrayList<>());
        List<BooleanVariable> solution = SATSolver.getModel(satProblem, new ArrayList<>(), 10000);
        return !solution.isEmpty();
    }

    /**
     * This function checks the validity of the step by evaluating the effect of precondition
     * of that step
     * @param preconditionOrEffect precondition or effect of the step
     * @return the state of the precondition or the effect
     */
    private boolean stepHasBadPreconditionOrEffect(Expression preconditionOrEffect){
        boolean result = false;
        if (preconditionOrEffect instanceof Predication){
            return false;
        }
        else if (preconditionOrEffect instanceof Conjunction) {
            Conjunction conjunctionExpression = (Conjunction)preconditionOrEffect;
            for (Expression literal : conjunctionExpression.arguments) {
                if (hasMoreThanOneOccurence(literal.toString(), conjunctionExpression.toString()))
                {
                    result = true;
                    break;
                }
            }
        }
        else if (preconditionOrEffect instanceof Negation){
            return false;
        }
        else{
            System.out.println(preconditionOrEffect);
            throw new RuntimeException("what happened here");
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

    /**
     * Returns set of clauses that define the frame axioms for the given time
     * @param time the time for which the axioms are to be set
     * @return the clauses that define the frame axioms
     */
    public SATConjunction getFrameAxioms(int time){
        SATConjunction conjunction = new SATConjunction();
        for (Step step: this.steps){
            ArrayList<Expression> stepFluents = getFluentsFromExpression(step.effect);
            for(Expression fluent : fluents){
                if(!stepFluents.contains(fluent)){
                    for (int counter =0; counter <= time; counter++){
                        SATClause disjunction = new SATClause();
                        //negative to positive
                        disjunction.add(BooleanVariable.create(fluent.negate(), time));
                        disjunction.add(BooleanVariable.createNegated(step.toString(), time));
                        disjunction.add(BooleanVariable.create(fluent, time + 1));
                        conjunction.add(disjunction);

                        //positive to negative
                        disjunction = new SATClause();
                        disjunction.add(BooleanVariable.create(fluent, time));
                        disjunction.add(BooleanVariable.createNegated(step.toString(), time));
                        disjunction.add(BooleanVariable.create(fluent.negate(), time + 1));
                        conjunction.add(disjunction);
                    }
                }
            }
        }
        return conjunction;
    }

    /**
     * This function builds all the fluent and the steps which modify the states
     */
    @SuppressWarnings("serial")
    private void buildFluentsAndStateModifiers(){
        for(Step step: this.steps){
            for (Expression expression : effectsOfStep(step)){
                if (stateModifyingSteps.containsKey(expression)){
                    stateModifyingSteps.get(expression).add(step);
                }
                else{
                    stateModifyingSteps.put(expression, new ArrayList<Step>(){{
                        add(step);
                    }});
                }

                //Keep this fluent..would need it later
                if (expression instanceof Negation){
                    if (!fluents.contains(expression.negate()));
                    fluents.add(expression.negate());
                }
                else if (!fluents.contains(expression)){
                    fluents.add(expression);
                }
            }//end of inner for
        }//end of outer for
        fluents.addAll(getFluentsFromExpression(problem.initial.toExpression()));
    }

    ArrayList<Expression> getFluentsFromExpression(Expression expression){
        ArrayList<Expression> expressionFluents = new ArrayList<>();
        if (expression instanceof Predication){
            expressionFluents.add(expression);
        }
        else if (expression instanceof Conjunction){
            for (Expression expression1 : ((Conjunction)expression).arguments){
                expression1 = (expression1 instanceof Negation) ? expression1.negate() : expression1;
                expressionFluents.add(expression1);
            }
        }
        else{
            throw new RuntimeException("what happened here");
        }
        return expressionFluents;
    }


    /**
     * Converts the effects of the given step into an array of Expressions
     * @param step the step to be converted to the array
     * @return the array of expressions in the effect
     */
    private ArrayList<Expression> effectsOfStep(Step step){
        ArrayList<Expression> result = new ArrayList<>();

        if(step.effect instanceof  Predication){
            result.add(step.effect);
        }
        else if (step.effect instanceof  Conjunction){
            for (Expression expression : ((Conjunction)step.effect).arguments){
                result.add(expression);
            }
        }
        else{
            throw new RuntimeException("what happened here???");
        }
        return result;
    }

    /**
     * Returns the encodingModel used to encode the problem to the SATProblem
     * @return the encoding model for the problem
     */
    public HashMap<String, CNFEncodingModel> getEncodingModel(){
        return this.encodingModel;
    }
}
