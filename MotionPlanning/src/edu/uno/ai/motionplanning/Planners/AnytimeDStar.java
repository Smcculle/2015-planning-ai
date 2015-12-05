package edu.uno.ai.motionplanning.Planners;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import edu.uno.ai.motionplanning.*;
import edu.uno.ai.motionplanning.Heuristics.*;

public class AnytimeDStar extends GridMap implements Runnable {
	Scenario scenario;
	DistanceHeuristic dh;
	WeightedDistanceHeuristic wdh;
	protected PriorityQueue<MotionNode<Point>> open;
	protected HashMap<Point, MotionNode<Point>> closed;
	protected ArrayList<MotionNode<Point>> inconsistents;
	protected ConcurrentLinkedQueue<MotionNode<Point>> changes;
	protected PlanRunner runner;
	protected long visited;
	protected long expanded;
	protected float initialWeight;
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
		inconsistents = new ArrayList<>();
		changes = new ConcurrentLinkedQueue<>();
		this.scenario = s;
		this.dh = dh;
		this.wdh = new WeightedDistanceHeuristic(initialWeight, dh);
		fullPath = new ArrayList<Point>();
		setHistory(Float.POSITIVE_INFINITY);
		MotionNode<Point> p = new MotionNode<>(scenario.getEnd(), 0, wdh.cost(scenario.getEnd(), scenario.getStart()));
		perception(scenario.getStart());
		open.add(p);
		visited = 0;
		expanded = 1;
		this.knownMap = knownMap;
		if (!knownMap) {
			initGrid();
		}
		runner = new PlanRunner();
		this.initialWeight = initialWeight;
	}

	protected void initGrid() {
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[0].length; x++) {
				grid[y][x] = 1;
			}
		}

	}

	protected void computeOrImprovePath() {
		while (!runner.completed&&!open.isEmpty() && (open.peek().getHeuristic()
				+ open.peek().getCost()) <= history[scenario.getStart().y][scenario.getStart().x]
						+ wdh.cost(scenario.getEnd(), scenario.getStart())) {
			MotionNode<Point> currentNode = open.remove();
			float oldCost=history[currentNode.getLoc().y][currentNode.getLoc().x];
			visited++;
			if (fixInconsistency(currentNode.getLoc())) {
				closed.put(currentNode.getLoc(), currentNode);
			}
			List<MotionNode<Point>> successors = currentNode.getSuccessorsStart(scenario, wdh);
			for (MotionNode<Point> successor : successors) {
				updateState(successor);
			}
		}
	}

	protected void updateState(MotionNode<Point> s) {
		if (this.isClear(scenario, s.getLoc().y, s.getLoc().x) && !s.at(scenario.getEnd())) {
			double rhs = rhs(s.getLoc());
			s.setCost(rhs);
			double diff = rhs - history[s.getLoc().y][s.getLoc().x];
			open.remove(s);
			if (!(diff > -MotionNode.EPSILON && diff < MotionNode.EPSILON)) {
				if (closed.containsKey(s.getLoc())) {
					inconsistents.add(s);
				} else {
					open.add(s);
					expanded++;
				}
			}
		}
	}

	protected boolean fixInconsistency(Point p) {
		double rhs = rhs(p);
		if (history[p.y][p.x] > rhs) {
			history[p.y][p.x] = (float) rhs;
			return true;
		} else {// I really don't understand why they do this.
			//history[p.y][p.x] = Float.POSITIVE_INFINITY;
			return false;
		}
	}

	private double rhs(Point p) {
		if (p.equals(scenario.getEnd())){
			return 0;
		}
		double rhs = Double.POSITIVE_INFINITY;
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				double costInc = Math.sqrt(2);
				if (dx == 0 || dy == 0) {
					costInc = 1;
					if (dx == 0 && dy == 0) {
						continue;
					}
				}
				try {
					if (history[p.y + dy][p.x + dx] + costInc < rhs) {
						rhs = history[p.y + dy][p.x + dx] + costInc;
					}
				} catch(Exception e){
					
				}
			}
		}
		return rhs;
	}

	@Override
	public void run() {
		planning = true;
		edgesUpdated = false;
		computeOrImprovePath();
		startExecuting();
		while (planning && !runner.completed) {
			while (!changes.isEmpty()) {
				updateState(changes.remove());
			}
			if (runner.stuck) {
				wdh = new WeightedDistanceHeuristic(initialWeight, dh);
				requeueOpen();

			} else if (wdh.getWeight() > 1) {
				wdh.reduceWeight(0.5f);
				requeueOpen();
			}
			for (MotionNode<Point> inconsistent : inconsistents) {
				inconsistent.setHeuristic(wdh.cost(inconsistent.getLoc(), scenario.getStart()));
				open.add(inconsistent);
			}
			inconsistents.clear();
			computeOrImprovePath();
			startExecuting();
			if (wdh.getWeight() <1.01){
				while (!runner.completed&&changes.isEmpty()){
					try{
						Thread.sleep(1);
					}
					catch (InterruptedException ie){}
				}
			}
		}
	}

	protected void requeueOpen() {
		PriorityQueue<MotionNode<Point>> oldOpen = open;
		open = new PriorityQueue<>();
		for (MotionNode<Point> opened : oldOpen) {
			opened.setHeuristic(wdh.cost(opened.getLoc(), scenario.getStart()));
			open.add(opened);
		}
	}

	public void mark(MotionPlan<?> p) {
		Point2D spot = p.getLoc();
		mark(spot, (float) p.getCost());
	}

	public void startExecuting() {
		if (!runner.isAlive()&&!runner.running&&!runner.completed) {
			runner.start();
		}
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
							Point changed = new Point(p.x + dx, p.y + dy);
							changes.add(new MotionNode<Point>(changed, Float.POSITIVE_INFINITY,
									Float.POSITIVE_INFINITY));
						}

					} catch (Exception e) {
					}
				}
			}
		}
	}

	class PlanRunner extends Thread {
		MotionPlan<Point> plan;
		boolean completed = false;
		boolean failed = false;
		boolean running = false;
		boolean stuck = false;
		double totalCost;
		Point location;

		PlanRunner() {
			location = scenario.getStart();
			totalCost = 0;
		}
		
		@Override
		public void run() {
			completed = false;
			failed = false;
			running = true;
			
			while (!location.equals(scenario.getEnd())) {
				HashSet<Point> locations=new HashSet<>();
				double min = Double.MAX_VALUE;
				if (fullPath.isEmpty() || !location.equals(fullPath.get(fullPath.size() - 1))) {
					fullPath.add(location);
				} 
				if (locations.contains(location)){
					stuck=true;
				}
				else {
					locations.add(location);
					stuck=false;
				}
				Point loc = location;
				for (int dy = -1; dy <= 1; dy++) {
					for (int dx = -1; dx <= 1; dx++) {
						if (dx == 0 && dy == 0) {
							continue;
						}
						try {
							if (scenario.getMap().isClear(scenario, location.y + dy, location.x + dx)&&history[location.y + dy][location.x + dx] < min) {
								min = history[location.y + dy][location.x + dx];
								loc = new Point( location.x + dx,location.y + dy);
							}
						} catch (Exception e) {
						}
					}

				}
				totalCost += location.distance(loc);
				location = loc;
				perception(location);
				try {
					sleep(1);
				} catch (InterruptedException ie) {
				}
			}
			completed = true;
			running = false;
			System.err.println("Execution complete. Cost: "+totalCost);
		}
	}
}
