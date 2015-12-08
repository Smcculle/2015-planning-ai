/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import edu.uno.ai.motionplanning.Heuristics.DistanceHeuristic;
import edu.uno.ai.motionplanning.Planners.GridMap;

/**
 *
 * @author jgrimm
 */
public class MotionPlan<T extends Point> implements Comparable<MotionPlan<?>> {
	List<T> path;
	double cost;
	double heuristic;
	double actionCost;
	public final static double EPSILON = 1e-4;
	public final static double DIAGONAL_COST = Math.sqrt(2);

	public MotionPlan(T start, double heuristic) {
		path = new ArrayList<>();
		path.add(start);
		cost = 0;
		actionCost=0;
		this.heuristic = heuristic;
	}

	protected MotionPlan(MotionPlan<T> oldPlan, T next, double costInc, double heuristic) {
		path = new ArrayList<>();
		path.addAll(oldPlan.path);
		path.add(next);
		this.actionCost=costInc;
		this.cost = oldPlan.cost + costInc;
		this.heuristic = heuristic;
	}

	@Override
	public int compareTo(MotionPlan<?> p) {
		int i = Double.compare(cost + heuristic, p.cost + p.heuristic);
		if (i != 0) {
			return i;
		} else {
			return Double.compare(heuristic, p.heuristic);
		}
	}

	public boolean at(T t) {
		T currentLoc = path.get(path.size() - 1);
		double dx = currentLoc.getX() - t.getX();
		double dy = currentLoc.getY() - t.getY();
		if (dx > -EPSILON && dx < EPSILON && dy > -EPSILON && dy < EPSILON) {
			return true;
		} else {
			return false;
		}
	}

	public List<MotionPlan<T>> nextSteps(Scenario s, DistanceHeuristic dh){
		GridMap m = s.getMap();
		return nextSteps(s,m,dh);
	}
	
	public List<MotionPlan<T>> nextSteps(Scenario s, GridMap m, DistanceHeuristic dh) {
		ArrayList<MotionPlan<T>> nextSteps = new ArrayList<>();
		T currentLoc = path.get(path.size() - 1);
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				if (x == 0 && y == 0) {
					continue;
				}
				@SuppressWarnings("unchecked")
				T step = (T) currentLoc.clone();
				step.setLocation(currentLoc.getX() + x, currentLoc.getY() + y);
				double costInc = 1;
				if (x != 0 && y != 0) {
					costInc = DIAGONAL_COST;
				}
				if (m.isGood(currentLoc, step)) {
					MotionPlan<T> p = new MotionPlan<>(this, step, costInc, dh.cost(step, s.getEnd()));
					m.mark(p);
					nextSteps.add(p);
				}
			}
		}
		return nextSteps;
	}

	@SuppressWarnings("unchecked")
	public T getLoc() {
		T currentLoc = path.get(path.size() - 1);
		return (T) currentLoc.clone();
	}

	public double getActionCost(){
		return actionCost;
	}
	public double getCost() {
		return cost;
	}
	
	public T getParent () {
		return path.get(Math.max(0, path.size()-2));
	}
	
	//returns true if step was removed, returns false otherwise
	public boolean removeStep (T step, Scenario s, GridMap m, DistanceHeuristic dh) {
		//get index of step to be removed
		int index = path.indexOf(step);
		//cannot remove first or last step in path
		if (index < 1 || index > path.size()-2) {
			return false;
		}
		//check whether step can be removed
		boolean canRemoveStep = true;
		T start = path.get(index-1);
		T end   = path.get(index+1);
		for (Point p : blocksToCheck(start,end,s,m)) {
			if (!m.isClear(s, p.y, p.x)) {
				canRemoveStep = false;
				break;
			}
		}
		//remove step if possible
		if (canRemoveStep) {
			T mid = path.get(index);
			double costToRemove = start.distance(mid) + mid.distance(end);
			cost -= costToRemove;
			path.remove(index);
			actionCost=start.distance(end);
			cost += actionCost;
		}	
		return canRemoveStep;
	}
	
	private Set<Point> blocksToCheck (T start, T end, Scenario s, GridMap m) {
		Set<Point> blocks = new HashSet<Point>();
		int startX = start.x;
		int endX   = end.x;
		int startY = start.y;
		int endY   = end.x;
		int dX = endX - startX;
		int dY = endY - startY;
		double dX_dY;
		double dY_dX;
		int directionX;
		int directionY;
		//check for horizontal and vertical moves -- those have already been checked
		//this check also avoids a possible divide-by-zero error in the next step
		if (dX == 0 || dY == 0) { 
			return blocks;
		}
		//check for simple diagonal moves -- those also have already been checked
		directionX = (dX > 0 ? 1 : -1);
		directionY = (dY > 0 ? 1 : -1);
		dX_dY = (double)dX / dY;
		dY_dX = (double)dY / dX;
		if (withinEpsilon(dX_dY,1) || withinEpsilon(dX_dY,-1)) {
			return blocks;
		}
		//otherwise, build list of blocks to check
		//first, move in X direction, adding blocks as needed
		int x = startX;
		int nextBlockX;
		int nextBlockY;
		while (x != endX) {
			nextBlockX = (directionX == 1 ? x : x-1);
			x += directionX;
			nextBlockY = getNextBlock(startX, x, dY_dX); 
			blocks.add(new Point(nextBlockX, nextBlockY));
		}
		//then move in Y direction, adding blocks as needed
		int y = startY;
		while (y != endY) {
			nextBlockY = (directionY == 1 ? y : y-1);
			y += directionY;
			nextBlockX = getNextBlock(startY, y, dX_dY);
			blocks.add(new Point(nextBlockX, nextBlockY));
		}
		return blocks;
	}
	
	private boolean withinEpsilon(double value, double target) {
		return ( Math.abs(value - target) < EPSILON);
	}
	
	private int getNextBlock(int startCoord, int currentCoord, double changePerIncrement) {
		double nextBlock = (currentCoord - startCoord) * changePerIncrement;
		if (withinEpsilon(nextBlock, (double)((int)nextBlock + 1))) {
			nextBlock += 1;
		}
		return (int)nextBlock;
	}

	public void markSolution(GridMap m) {
		int i = 0;
		for (T p : path) {
			m.mark(p, 10000 + i++);
		}
	}

	public List<T> planSteps(){
		return Collections.unmodifiableList(path);
	}
}
