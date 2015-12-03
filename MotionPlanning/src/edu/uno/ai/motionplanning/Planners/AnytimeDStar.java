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
	protected ArrayList<MotionNode<Point>> inconsistent;
	protected long visited;
	protected long expanded;
	public final int perceptionDistance = 2;
	protected boolean planning;
	protected boolean edgesUpdated;
	protected Point goal;
	protected ArrayList<Point> fullPath;
	protected boolean knownMap;

	public AnytimeDStar(Scenario s, DistanceHeuristic dh, float initialWeight, boolean knownMap) {
		super(s.getMap());
		open = new PriorityQueue<MotionNode<Point>>();
		closed = new HashMap<>();
		inconsistent = new ArrayList<>();
		this.scenario = s;
		this.dh = dh;
		this.wdh = new WeightedDistanceHeuristic(initialWeight, dh);
		fullPath = new ArrayList<Point>();
		setHistory(Float.POSITIVE_INFINITY);
		mark(scenario.getEnd(), 0);
		MotionNode<Point> p = new MotionNode<>(scenario.getEnd(), 0, wdh.cost(scenario.getEnd(), scenario.getStart()));
		perception(scenario.getStart());
		open.add(p);
		visited = 0;
		expanded = 1;
		this.knownMap = knownMap;
		if (!knownMap){
			initGrid();
		}
	}

	protected void initGrid() {
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[0].length; x++) {
				grid[y][x] = 1;
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
				if (currentNode.at(scenario.getStart())) {
					startExecuting();
					if (!edgesUpdated) {
						if (wdh.getWeight() < 1.00001) {
							planning = false;
						} else {
							wdh.reduceWeight(.5f);
							// requeue open list to update heuristics
							PriorityQueue<MotionNode<Point>> oldOpen = open;
							open = new PriorityQueue<MotionNode<Point>>();
							for (MotionNode<Point> mn : oldOpen) {
								mn.setHeuristic(wdh.cost(mn.getLoc(), scenario.getStart()));
								open.add(mn);
							}
						}
					}
				}
				List<MotionNode<Point>> next = currentNode.getSuccessors(scenario, wdh);
				for (MotionNode<Point> mn : next) {
					if (closed.containsKey(mn.getLoc())) {
						if (history[mn.getLoc().y][mn.getLoc().x] != mn.getCost()) {
							inconsistent.add(mn);
						}
					} else if (this.isClear(scenario, mn.getLoc().y, mn.getLoc().x)) {
						if (mn.getCost() != history[mn.getLoc().y][mn.getLoc().x]) {
							if (history[mn.getLoc().y][mn.getLoc().x] != Float.POSITIVE_INFINITY) {
								open.add(mn);
								expanded += 1;
								this.mark(mn.getLoc(), (float) mn.getCost());
							}
						}
					}
				}
			}
			if (!inconsistent.isEmpty()) {
				open.addAll(inconsistent);
				inconsistent.clear();
			} else {
				planning = false;
			}
		}
		PlanRunner pr = new PlanRunner();
		pr.run();
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

		PlanRunner() {
			completed = false;
			failed = false;

		}

		public void run() {
			Point p = scenario.getStart();
			double totalCost = 0;
			while (!p.equals(scenario.getEnd())) {
				double min = Double.MAX_VALUE;
				fullPath.add(p);
				Point loc = p;
				for (int y = -1; y <= 1; y++) {
					for (int x = -1; x <= 1; x++) {
						if (x == 0 && y == 0) {
							continue;
						}
						try {
							if (history[p.y - y][p.x - x] < min) {
								min = history[p.y + y][p.x + x];
								loc = new Point(p.y + y, p.x + x);
							}
						} catch (Exception e) {
						}
					}

				}
				totalCost += p.distance(loc);
				p = loc;
			}
		}
	}
}
