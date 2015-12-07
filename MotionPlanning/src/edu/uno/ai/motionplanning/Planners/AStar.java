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
public class AStar implements MotionPlanner{

    protected Scenario scenario;
    protected GridMap map;
    protected PriorityQueue<MotionPlan<Point>> frontier;
    protected DistanceHeuristic dh;
    protected long visited;
    protected long expanded;
    protected long start;
    protected long end;
    protected int nodeLimit;
	protected boolean succeeded;
	protected float solutionCost;
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
        nodeLimit=-1;
    }

    public MotionPlan<Point> search() {
    	succeeded=false;
    	start=System.nanoTime();
        while (!frontier.isEmpty()) {
            MotionPlan<Point> currentPlan = frontier.remove();
            visited++;
            if (nodeLimit>0 && visited>nodeLimit){
            	reason=" node limit exceeded.";
            	break;
            }
            if (currentPlan.at(scenario.getEnd())) {
            	succeeded=true;
            	end=System.nanoTime();
            	solutionCost=(float) currentPlan.getCost();
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
		String out = "["+getPlannerName()+" ";
		if(succeeded)
			out += "succeeded";
		else
			out += "failed";
		out += " on " + scenario.getName() + " in motion; ";
		out += visited + " visited, " + expanded + " expanded; ";
		out += getTime();
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

	@Override
	public float getSolutionCost() {
		return solutionCost;
	}

	@Override
	public long getFirstSolutionTime() {
		return end-start;
	}

	@Override
	public long getSolutionTime() {
		return end-start;
	}

	@Override
	public void setNodeLimit(int NodeLimit) {
		this.nodeLimit=NodeLimit;		
	}
	@Override
	public String getPlannerName(){
		return "A*";
	}
	
}
