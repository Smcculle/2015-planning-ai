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
public class WeightedDistanceHeuristic implements DistanceHeuristic{
    protected float weight;
    protected DistanceHeuristic dh;
    public WeightedDistanceHeuristic(float weight, DistanceHeuristic dh){
        this.weight=weight;
        this.dh=dh;
    }
    @Override
    public float cost(Point2D p1, Point2D p2) {
        return weight*dh.cost(p1, p2);
    }
    
}
