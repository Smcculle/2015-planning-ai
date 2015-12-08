package edu.uno.ai.planning.jjblackbox;

import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Negation;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.Node;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.pg.StepNode;
import edu.uno.ai.planning.jjsatplan.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlanGraphEncoding {
    public Map<String, PlanGraphEncodingModel> stepEncodings;

    public PlanGraphEncoding(){
        this.stepEncodings = new HashMap<>();
    }


    public SATConjunction encode(PlanGraph planGraph){
        SATConjunction conjunction = new SATConjunction();

        /* add initial */
        conjunction.add(expressionToSATConjunction(planGraph.problem.initial.toExpression(), 0));

        /* add goal */
        conjunction.add(expressionToSATConjunction(planGraph.problem.goal, planGraph.size() - 1));

        /* step implies precondition */
        conjunction.add(stepImpliesPreconditions(planGraph.getStepNodes(), planGraph.size()));

        /* fact implies steps */
        conjunction.add(factsImpliesSteps(planGraph.getLiteralNodes(), planGraph.size()));

        /*Create mutexes for steps and facts */
        conjunction.add(createMutexConjunction(new ArrayList<>(planGraph.getStepNodes()), planGraph.size()));
        conjunction.add(createMutexConjunction(new ArrayList<>(planGraph.getLiteralNodes()), planGraph.size()));

//        System.out.println("cnf size is " + conjunction.size());
//        conjunction.add(createStepMutexRelation(new ArrayList<>(planGraph.getStepNodes()), planGraph.size()));

//        System.out.println("\nSteps Imply Preconditions:");
//        System.out.println(conjunction.toString());

        return conjunction;
    }

    /**
     * Converts the given expression into a conjunction. Each argument in the expression is
     * evaluated as a clause in the disjunction
     * @return a conjunction with clauses that are the arguments fo the expression
     */
    public SATConjunction expressionToSATConjunction(Expression initial, int time){
        SATConjunction conjunction = new SATConjunction();
        if (initial instanceof Predication){
            conjunction.add(new SATClause(BooleanVariable.create(initial, time)));
            return conjunction;
        }
        else if (initial instanceof Conjunction){
            for (Expression argument : ((Conjunction)initial).arguments){
                conjunction.add(new SATClause(BooleanVariable.create(argument, time)));
            }
            return conjunction;
        }
        else if (initial instanceof  Negation){
            conjunction.add(new SATClause(BooleanVariable.create(initial, time)));
            return conjunction;
        }
        else{
            throw new RuntimeException("Cannot recognize Expression");
        }
    }

    public SATConjunction factsImpliesSteps(List<LiteralNode> literalNodes, int planGraphSize){
        SATConjunction satConjunction = new SATConjunction();
        for (LiteralNode node: literalNodes) {
            for (int level = node.getLevel(); level < planGraphSize; level++) {
                if (level == 0) continue;
                SATClause stepDisjunction = new SATClause(BooleanVariable.createNegated(node.literal.toString(), level));
                for (StepNode producer: node.getProducers(level)) {
                    stepDisjunction.add(BooleanVariable.create(producer.step.toString(), level));
                }
                satConjunction.add(stepDisjunction);
            }
        }
        return satConjunction;
    }


    public SATConjunction stepImpliesPreconditions(List<StepNode> stepNodes, int planGraphSize){
        SATConjunction satConjunction = new SATConjunction();
        for (StepNode step : stepNodes) {
            for (int level = step.getLevel(); level < planGraphSize; level++) {
                if (!step.persistence){
                    stepEncodings.put(step.step.name + " - " + level, new PlanGraphEncodingModel(step.step, level));
                }
                for (LiteralNode precondition : step.getPreconditions(level)) {
                    SATClause stepDisjunction = new SATClause(BooleanVariable.createNegated(step.step.name, level));
                    stepDisjunction.add(BooleanVariable.create(precondition.literal.toString(), level - 1));
                    satConjunction.add(stepDisjunction);
                }
            }
        }
        return satConjunction;
    }

    public SATConjunction createStepMutexRelation(List<StepNode> nodes, int planGraphSize){
        SATConjunction conjunction = new SATConjunction();
        List<StepNode> copyOfNodes = new ArrayList<>(nodes);
        for (int levelCounter = 1; levelCounter < planGraphSize; levelCounter++){
            final int finalLevelCounter = levelCounter;
            List<StepNode> outerNodesForThisLevel =  nodes.stream().filter(x->x.exists(finalLevelCounter)).collect(Collectors.toList());
            for(StepNode outerNode : outerNodesForThisLevel){
                List<StepNode> innerNodesForThisLevel = copyOfNodes.stream().filter(x->x.exists(finalLevelCounter)).collect(Collectors.toList());
                for(StepNode innerNode : innerNodesForThisLevel){
                    if (!outerNode.equals(innerNode) && !innerNode.persistence){
                        SATClause disjunction = new SATClause();
                        disjunction.add(BooleanVariable.createNegated(innerNode.toString(), finalLevelCounter));
                        disjunction.add(BooleanVariable.createNegated(outerNode.toString(), finalLevelCounter));
                        conjunction.add(disjunction);
                    }
                }
            }
        }
        return conjunction;
    }


    public SATConjunction createMutexConjunction(List<Node> nodes, int planGraphSize){
        SATConjunction conjunction = new SATConjunction();

        List<Node> copyOfNodes = new ArrayList<>(nodes);

        for (int levelCounter = 1; levelCounter < planGraphSize; levelCounter++){
            final int finalLevelCounter = levelCounter;
            List<Node> outerNodesForThisLevel =  nodes.stream().filter(x->x.exists(finalLevelCounter)).collect(Collectors.toList());
            for(Node outerNode : outerNodesForThisLevel){
//                System.out.println("Node is " + outerNode + " at level " + finalLevelCounter);
                List<Node> innerNodesForThisLevel = copyOfNodes.stream().filter(x->x.exists(finalLevelCounter)).collect(Collectors.toList());
                for(Node innerNode : innerNodesForThisLevel){
//                    System.out.println("Checking " + innerNode + " with " + outerNode + " at level " + finalLevelCounter);
                    if ((innerNode.mutex(outerNode, finalLevelCounter) || outerNode.mutex(innerNode, finalLevelCounter))){

//                        System.out.println("added");

                        SATClause disjunction = new SATClause();

//                        if (innerNode instanceof StepNode){
//                            StepNode step = (StepNode)innerNode;
//
//
//
//                        }

//                        System.out.println("mutext is " + innerNode);

                        disjunction.add(BooleanVariable.createNegated(innerNode.toString(), finalLevelCounter));
                        disjunction.add(BooleanVariable.createNegated(outerNode.toString(), finalLevelCounter));

                        conjunction.add(disjunction);


                    }


                }
            }
        }
        return conjunction;
    }
}
