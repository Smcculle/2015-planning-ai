package edu.uno.ai.motionplanning;
import java.awt.geom.*;
import java.util.*;

public class MotionNode<T extends Point2D> implements Comparable<MotionNode<?>>{
	protected T location;
	protected double cost;
	protected double heuristic;
	final double EPSILON = 1e-10;
	
	public MotionNode(T point, double cost, double heuristic){
		location=point;
		this.cost=cost;
		this.heuristic=heuristic;
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
	public T getLoc(){
		return location;
	}
	
	public List<MotionNode<T>> getSuccessors(){
		return null;
	}
	
	public boolean at(T t){
		double dx = location.getX() - t.getX();
		double dy = location.getY() - t.getY();
		if (dx > -EPSILON && dx < EPSILON && dy > -EPSILON && dy < EPSILON) {
			return true;
		} else {
			return false;
		}
	}
	
}
