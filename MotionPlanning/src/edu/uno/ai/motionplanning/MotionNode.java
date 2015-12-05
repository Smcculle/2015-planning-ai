package edu.uno.ai.motionplanning;

import java.awt.geom.*;
import java.util.*;
import edu.uno.ai.motionplanning.Heuristics.*;

public class MotionNode<T extends Point2D> implements Comparable<MotionNode<?>> {
	protected T location;
	protected double cost;
	protected double heuristic;
	protected double actionCost;
	public final static double EPSILON = 1e-4;

	public MotionNode(T point, double cost, double heuristic) {
		location = point;
		this.cost = cost;
		this.heuristic = heuristic;
	}

	protected MotionNode(MotionNode<T> oldNode, T next, double costInc, double heuristic) {
		location = next;
		this.actionCost = costInc;
		this.cost = oldNode.cost + costInc;
		this.heuristic = heuristic;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(double heuristicValue) {
		this.heuristic = heuristicValue;
	}

	@Override
	public int compareTo(MotionNode<?> n) {
		int i = Double.compare(cost + heuristic, n.cost + n.heuristic);
		if (i != 0) {
			return i;
		} else {
			return Double.compare(heuristic, n.heuristic);
		}
	}

	public T getLoc() {
		return location;
	}

	public List<MotionNode<T>> getSuccessorsEnd(Scenario s, DistanceHeuristic dh) {
		return getSuccessors(s,dh,true);
	}
	public List<MotionNode<T>> getSuccessorsStart(Scenario s, DistanceHeuristic dh) {
		return getSuccessors(s,dh,false);
	}

	protected List<MotionNode<T>> getSuccessors(Scenario s, DistanceHeuristic dh, boolean end) {
		ArrayList<MotionNode<T>> nextSteps = new ArrayList<>();
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				if (x == 0 && y == 0) {
					continue;
				}
				@SuppressWarnings("unchecked")
				T step = (T) location.clone();
				step.setLocation(location.getX() + x, location.getY() + y);
				double costInc = 1;
				if (x != 0 && y != 0) {
					costInc = 1.41421;
				}
				MotionNode<T> p = new MotionNode<T>(this, step, costInc,
						dh.cost(step, end ? s.getEnd() : s.getStart()));
				nextSteps.add(p);
			}
		}
		return nextSteps;
	}

	public boolean at(T t) {
		double dx = location.getX() - t.getX();
		double dy = location.getY() - t.getY();
		if (dx > -EPSILON && dx < EPSILON && dy > -EPSILON && dy < EPSILON) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object o){
		if (!(o instanceof MotionNode)){
			return false;
		}
		else {
			MotionNode<?> mn=(MotionNode<?>)o;
			if (location.equals(mn.location)){
				return true;
			}
		}
		return false;
	}
}
