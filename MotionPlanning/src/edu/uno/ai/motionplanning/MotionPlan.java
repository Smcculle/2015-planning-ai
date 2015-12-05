/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import edu.uno.ai.motionplanning.Heuristics.DistanceHeuristic;
import edu.uno.ai.motionplanning.Planners.GridMap;

/**
 *
 * @author jgrimm
 */
public class MotionPlan<T extends Point2D> implements Comparable<MotionPlan<?>> {
	List<T> path;
	double cost;
	double heuristic;
	double actionCost;
	public final static double EPSILON = 1e-6;

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
					costInc = 1.41421;
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
