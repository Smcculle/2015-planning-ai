package edu.uno.ai.motionplanning.Heuristics;

import java.awt.geom.Point2D;

public class Zero implements DistanceHeuristic {

	@Override
	public float cost(Point2D p1, Point2D p2) {
		return 0;
	}

}
