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

    public MotionPlan search() {
        while (!frontier.isEmpty()) {
            MotionPlan<Point> currentPlan = frontier.remove();
            visited++;
            if (currentPlan.at(scenario.getEnd())) {
                return currentPlan;
            }
            List<MotionPlan<Point>> next = currentPlan.nextSteps(scenario, dh);
            expanded += next.size();
            frontier.addAll(next);
            if (frontier.isEmpty()){
                System.out.println("Frontier exhausted after considering "+visited+" states.");
            }            
        }
        return null;
    }
}
