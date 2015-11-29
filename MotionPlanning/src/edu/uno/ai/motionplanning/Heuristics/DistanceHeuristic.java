/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning.Heuristics;

/**
 *
 * @author jgrimm
 */
import java.awt.geom.Point2D;

public interface DistanceHeuristic {
    public abstract float cost (Point2D p1, Point2D p2);    
}
