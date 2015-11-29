/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning;

import java.awt.Point;

/**
 *
 * @author jgrimm
 */
public class Scenario {

    protected GridMap board;
    protected Point start;
    protected Point end;
    protected int group;
    protected float optimal;
    public Scenario(GridMap map, int startx, int starty, int endx, int endy, float optimal, int group) {
        board = map;
        start = new Point(startx, starty);
        end = new Point(endx, endy);
        this.optimal=optimal;
        this.group=group;
    }

    public Point getStart() {
        return new Point(start);
    }
    public Point getEnd() {
        return new Point(end);
    }
    public GridMap getMap(){
        return board;
    }
    public float getOptimal(){
        return optimal;
    }
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(board.getName());
        sb.append(",");
        sb.append(start.toString());
        sb.append(",");
        sb.append(end.toString());
        sb.append(",");
        sb.append(optimal);
        return sb.toString();
    }
}
