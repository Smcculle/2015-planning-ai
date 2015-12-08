/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning.Heuristics;

import java.awt.geom.Point2D;

/**
 *
 * @author jgrimm
 */
public class Euclidean implements DistanceHeuristic{
    public Euclidean(){        
    }
    


    @Override
    public float cost(Point2D p1, Point2D p2) {
    	return (float)(p1.distance(p2));
    }
}
