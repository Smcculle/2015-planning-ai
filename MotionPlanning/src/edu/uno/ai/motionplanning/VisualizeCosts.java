package edu.uno.ai.motionplanning;

import java.io.File;
import java.util.List;

import edu.uno.ai.motionplanning.Heuristics.Euclidean;
import edu.uno.ai.motionplanning.Heuristics.WeightedDistanceHeuristic;
import edu.uno.ai.motionplanning.Planners.AStar;

public class VisualizeCosts {
	public static void main (String[] args){
		ScenarioLoader sl=new ScenarioLoader(new File("./"),new File("scenarios/"));
        List<Scenario> complete=sl.loadAllScenarios();
        for(int i=0;i<complete.size();i++){
        	Scenario s=complete.get(i);
        	System.out.println(s);
        	for (float f=5; f>=0.99;f-=0.5){
        		 AStar pathing=new AStar(s,new WeightedDistanceHeuristic(f,new Euclidean()));
        		 long start=System.nanoTime();
        		 MotionPlan<?> p=pathing.search();
        		 long end=System.nanoTime();
        		 if (p!=null){
        			 System.out.println(""+f+","+(end-start)+","+p.getCost());
        		 }
        	}
        }
	}
}
