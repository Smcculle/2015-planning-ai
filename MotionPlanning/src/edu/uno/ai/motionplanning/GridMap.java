package edu.uno.ai.motionplanning;

import java.io.*;
import java.util.*;
import java.awt.geom.Point2D;

public class GridMap {

    protected int[][] grid;
    protected float[][] history;
    protected static WeakHashMap<String, GridMap> loadedMaps = null;
    protected String name;

    public GridMap(int width, int height, String name) {
        grid = new int[height][width];
        history = new float[height][width];
        clearHistory();
        this.name = name;
    }

    public GridMap(GridMap m) {
        this.grid = m.grid;
        this.name = m.name;
        this.history = new float[grid.length][grid[0].length];
        clearHistory();
    }

    public void clearHistory() {
    	setHistory(0.0f);
    }
    protected void setHistory(float value){
        for (int i = 0; i < history.length; i++) {
            Arrays.fill(history[i], value);
        }
    }
    public static GridMap load(String path) {
        synchronized (GridMap.class) {
            if (loadedMaps == null) {
                loadedMaps = new WeakHashMap<String, GridMap>();
            }
        }
        /*if (loadedMaps.containsKey(path)) {
            return loadedMaps.get(path);
        }*/

        try {
            FileInputStream fs = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fs);
            BufferedReader br = new BufferedReader(isr);
            br.skip("type ".length());
            String typeLine = br.readLine();
            br.skip("height ".length());
            String heightLine = br.readLine();
            br.skip("width ".length());
            String widthLine = br.readLine();
            String mapLine = br.readLine();
            assert(mapLine.contains("map"));
            int height = Integer.parseInt(heightLine);
            int width = Integer.parseInt(widthLine);
            if (typeLine.contains("octile")) {
                GridMap temp = new GridMap(width, height, path);
                temp.readOctileMap(br);
                br.close();
                isr.close();
                fs.close();
                loadedMaps.put(path, temp);
                return temp;
            } else {
                br.close();
                isr.close();
                fs.close();
                return null;
            }
        } catch (Exception e) {

            return null;
        }
    }

    protected void readOctileMap(BufferedReader br) throws IOException {
        for (int y = 0; y < grid.length; y++) {
            String terrain = br.readLine();
            terrain = terrain.toUpperCase();
            for (int x = 0; x < grid[y].length; x++) {
                char tile = terrain.charAt(x);
                switch (tile) {
                    case '@':
                    case '0':
                    case 'T':
                        grid[y][x] = -1;
                        break;
                    case 'W':
                        grid[y][x] = 2;
                        break;
                    case 'S'://swamp is supposedly a meaningful distinction, but I don't think so.
                    default:
                        grid[y][x] = 1;
                        break;
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("\n");
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] < 0) {
                    sb.append("*");
                } else if (grid[y][x] == 2) {
                    sb.append("W");
                }
                else if (history[y][x]>10000){
                    int lastDigit=(int)(history[y][x]-10000)%10;
                    sb.append(lastDigit);
                }else if (history[y][x]>0){
                    sb.append(".");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getHeight() {
        return grid.length;
    }

    public int getWidth() {
        return grid[0].length;
    }

    public boolean isGood(Point2D start, Point2D end) {
        try {
            if (history[(int) Math.floor(end.getY())][(int) Math.floor(end.getX())] > 0) {
                return false;
            }
            int startGridVal = grid[(int) Math.floor(start.getY())][(int) Math.floor(start.getX())];
            int endGridVal = grid[(int) Math.floor(end.getY())][(int) Math.floor(end.getX())];
            if (startGridVal == endGridVal) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return false;
    }

    public boolean isClear(Scenario s, int y, int x){
    	try{
    		if (grid[y][x]==grid[s.getStart().y][s.getStart().x]){
    			return true;
    		}
    	}
    	catch(ArrayIndexOutOfBoundsException e){
    		
    	}
    	return false;
    }
    public void mark(MotionPlan<?> p) {
        Point2D spot = p.getLoc();
        mark(spot,(float)p.getCost() + 0.01f);
    }
    public void mark(Point2D spot, float cost) {
        history[(int) Math.floor(spot.getY())][(int) Math.floor(spot.getX())] = cost;
    }

    public String getName() {
        return name;
    }
}
