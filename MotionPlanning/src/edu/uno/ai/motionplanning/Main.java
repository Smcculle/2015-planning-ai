/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning;
import edu.uno.ai.planning.TestSuite;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.*;

import java.io.File;
import java.util.*;
import edu.uno.ai.motionplanning.Planners.*;
import edu.uno.ai.motionplanning.Heuristics.*;
/**
 *
 * @author jgrimm
 */
public class Main {
    public static void main(String[] args){
        Random foo=new Random();
        ScenarioLoader sl=new ScenarioLoader(new File("./"),new File("./scenarios/"));
        List<Scenario> complete=sl.loadAllScenarios();
        System.out.println(complete.size()+" scenarios loaded.");
        System.out.println("KB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
        for(int i=0;i<complete.size();i++){
            int caseNum=foo.nextInt(complete.size());
            caseNum=i;
            StateSpaceProblem motionProblem=MotionProblemFactory.generateMotionProblem(complete.get(caseNum));
            AStar pathing=new AStar(complete.get(caseNum),new WeightedDistanceHeuristic(1.0f,new Euclidean()));
            MotionPlan<?>  p=pathing.search();
            if (p!=null){
                System.out.println(complete.get(caseNum));
               // p.markSolution(complete.get(i).getMap());
                //System.out.println(complete.get(i).getMap().toString());
                System.out.println(i+","+p.getCost()+","+complete.get(caseNum).getOptimal());
                System.out.println(pathing.toResultsString());
                for (Planner<?> planner: TestSuite.PLANNERS){
                	Result r=planner.findSolutuion(motionProblem, TestSuite.NODE_LIMIT, TestSuite.TIME_LIMIT);
                	System.out.println(r.toString());
                }
               

            }
            else if (p==null){                
                System.out.println(complete.get(caseNum));
                System.out.println(i+" Solution not found. Optimal "+complete.get(caseNum).getOptimal());
                //System.out.println(complete.get(caseNum).getMap());
            }
            p=null;
            pathing=null;            
        }
    }
    
}
