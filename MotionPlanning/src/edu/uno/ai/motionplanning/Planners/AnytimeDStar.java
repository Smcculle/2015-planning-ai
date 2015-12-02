package edu.uno.ai.motionplanning.Planners;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import edu.uno.ai.motionplanning.*;
import edu.uno.ai.motionplanning.Heuristics.*;

public class AnytimeDStar extends GridMap implements Runnable {
	Scenario scenario;
	DistanceHeuristic dh;
	WeightedDistanceHeuristic wdh;
	protected PriorityQueue<MotionNode<Point>> open;
	protected HashMap<Point, MotionNode<Point>> closed;
	protected long visited;
	protected long expanded;
	public final int perceptionDistance = 2;
	protected boolean planning;
	protected boolean edgesUpdated;
	protected MotionPlan<Point> currentSolution;
	protected Point goal;
	protected ArrayList<Point> fullPath;
	protected boolean knownMap;
	public AnytimeDStar(Scenario s, DistanceHeuristic dh, float initialWeight, boolean knownMap) {
		super(s.getMap().getWidth(), s.getMap().getHeight(), s.getMap().getName() + " known");
		this.scenario = s;
		this.dh = dh;
		this.wdh = new WeightedDistanceHeuristic(initialWeight, dh);
		fullPath = new ArrayList<Point>();
		setHistory(Float.POSITIVE_INFINITY);
		mark(scenario.getEnd(), 0);
		MotionNode<Point> p = new MotionNode<>(scenario.getEnd(),0, wdh.cost(scenario.getEnd(), scenario.getStart()));
		perception(scenario.getStart());
		open.add(p);
		visited = 0;
		expanded = 1;
		this.knownMap=knownMap;
	}

	protected void initGrid() {
		for (int y=0;y<grid.length;y++){
			for (int x=0;x<grid[0].length;x++){
				if (knownMap){
					grid[y][x]=(byte)(scenario.getMap().isClear(scenario, y, x)?1:0);
				}
				else{
					grid[y][x]=1;
				}
			}
		}

	}

	@Override
	public void run() {
		planning = true;
		edgesUpdated = false;
		while (planning) {
			while (!open.isEmpty()) {
				MotionNode<Point> currentNode = open.remove();
				closed.put(currentNode.getLoc(), currentNode);
				visited++;
				if (currentNode.at(scenario.getEnd())) {
					startExecuting();
					if (!edgesUpdated) {
						if (wdh.getWeight()< 1.00001) {
							planning=false;
						}
						else{ 
							wdh.reduceWeight(.5f);
							//requeue open list to update heuristics
						}
					}
				}
				List<MotionNode<Point>> next = currentNode.getSuccessors();
				expanded += next.size();
				open.addAll(next);
			}
			return;
		}
	}

	public void mark(MotionPlan<?> p) {
		Point2D spot = p.getLoc();
		mark(spot, (float) p.getCost());
	}

	public void startExecuting() {

	}

	protected void recalculatePaths(int x, int y) {

	}

	protected void perception(Point p) {
		if (!knownMap) {
			for (int dy = -perceptionDistance; dy <= perceptionDistance; dy++) {
				for (int dx = -perceptionDistance; dx <= perceptionDistance; dx++) {
					try {
						boolean passable = scenario.getMap().isClear(scenario, p.y + dy, p.x + dx);
						if (passable) {
							this.grid[p.y + dy][p.x + dx] = 1;
						} else {
							this.grid[p.y + dy][p.x + dx] = 0;
							recalculatePaths(p.x + dx, p.y + dy);
							edgesUpdated = true;
						}

					} catch (Exception e) {
					}
				}
			}
		}
	}

	class PlanRunner extends Thread {
		MotionPlan<Point> plan;
		boolean completed;
		boolean failed = false;
		Point location;

		PlanRunner(MotionPlan<Point> mp) {
			this.plan = mp;
			completed = false;
			failed = false;

		}

		public void run() {
			List<Point> steps = plan.planSteps();
			ArrayList<Point> mySteps = new ArrayList<>(steps);
			Collections.reverse(mySteps);
			Point lastPoint = mySteps.get(0);
			for (Point loc : mySteps) {
				if (!isClear(scenario, loc.x, loc.y)) {
					failed = true;
					return;
				}
				location = loc;
				perception(loc);
				try {
					sleep((int) (loc.distance(lastPoint) * 50));
				} catch (InterruptedException ie) {
					// parent wants us dead. Return
					return;
				}
			}
			completed = true;
		}
	}
}
