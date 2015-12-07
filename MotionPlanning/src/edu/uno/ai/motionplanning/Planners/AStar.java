/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning.Planners;

import edu.uno.ai.motionplanning.*;
import edu.uno.ai.motionplanning.Heuristics.DistanceHeuristic;
import java.util.PriorityQueue;
import java.util.List;
import java.awt.Point;

/**
 *
 * @author jgrimm
 */
public class AStar {

    protected Scenario scenario;
    protected GridMap map;
    protected PriorityQueue<MotionPlan<Point>> frontier;
    protected DistanceHeuristic dh;
    protected long visited;
    protected long expanded;
    protected long start;
    protected long end;
	protected boolean succeeded;
	protected String reason="";
	
    public AStar(Scenario s, DistanceHeuristic dh) {
        scenario = s;
        map = new GridMap(scenario.getMap());
        frontier = new PriorityQueue<>();
        MotionPlan<Point> p = new MotionPlan<>(scenario.getStart(), dh.cost(scenario.getStart(), scenario.getEnd()));
        frontier.add(p);
        map.mark(p);
        this.dh = dh;
        visited=0;
        expanded=1;
    }

    public MotionPlan<Point> search() {
    	succeeded=false;
    	start=System.nanoTime();
        while (!frontier.isEmpty()) {
            MotionPlan<Point> currentPlan = frontier.remove();
            visited++;
            if (currentPlan.at(scenario.getEnd())) {
            	succeeded=true;
            	end=System.nanoTime();
                return currentPlan;
            }
            updateStates(currentPlan);
        }
        end=System.nanoTime();
        return null;
    }

	protected void updateStates(MotionPlan<Point> currentPlan) {
		List<MotionPlan<Point>> next = currentPlan.nextSteps(scenario, map, dh);
		expanded += next.size();
		frontier.addAll(next);
	}
    
    public GridMap getMap(){
    	return map;
    }
    
    public long getVisited(){
    	return visited;
    }
    public long getExpanded(){
    	return expanded;
    }
	public String toResultsString() {
		String out = "[AStar ";
		if(succeeded)
			out += "succeeded";
		else
			out += "failed";
		out += " on " + scenario.getMap().getName() + " in motion; ";
		out += visited + " visited, " + expanded + " expanded; ";
		out += getTime();
		if(reason != null)
			out += "; " + reason;
		return out + "]";
	}
	
	public String getTime() {
		long time=end-start;
		int nanos =(int)time%1000000;
		time/=1000000;
		int minutes = (int) (time / (1000*60));
		int seconds = (int) (time / 1000) % 60;
		int milliseconds = (int) (time % 1000);
		return String.format("%d:%d:%d:%d", minutes, seconds, milliseconds,nanos);
	}
}
