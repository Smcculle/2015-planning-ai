package edu.uno.ai.motionplanning.Planners;

import java.awt.Point;
import java.util.List;

import edu.uno.ai.motionplanning.MotionPlan;
import edu.uno.ai.motionplanning.Scenario;
import edu.uno.ai.motionplanning.Heuristics.DistanceHeuristic;

public class BasicThetaStar extends AStar {

	public BasicThetaStar(Scenario s, DistanceHeuristic dh) {
		super(s, dh, "BasicTheta*");
	}
	
	@Override
	protected void updateStates (MotionPlan<Point> currentPlan) {
		List<MotionPlan<Point>> next = currentPlan.nextSteps(scenario, map, dh);
		for (MotionPlan<Point> mp : next) {
			mp.removeStep(currentPlan.getLoc(), scenario, map, dh);
		}
		expanded += next.size();
		frontier.addAll(next);
	}
	
	
}
