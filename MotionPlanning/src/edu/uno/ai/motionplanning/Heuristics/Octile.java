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
public class Octile implements DistanceHeuristic{

    @Override
    public float cost(Point2D p1, Point2D p2) {
        double xdiff=Math.abs(p1.getX()-p2.getX());
        double ydiff=Math.abs(p1.getY()-p2.getY());
        return (float)(Math.max(xdiff,ydiff)+0.41f*Math.min(xdiff,ydiff));
    }
    
}
