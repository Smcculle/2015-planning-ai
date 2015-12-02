package edu.uno.ai.motionplanning;
import ij.process.*;

public class VisualGridMap extends GridMap{
	public VisualGridMap(int width,int height, String name){
		super(width,height,name);
	}
	public VisualGridMap(GridMap m){
		super(m);
		this.grid=m.grid;
		this.history=m.history;
	}

	public ImageProcessor toGridImage(){
		ByteProcessor bp=new ByteProcessor(grid[0].length, grid.length);
		for (int y=0;y<grid.length;y++){
			for (int x=0;x<grid[0].length;x++){
				bp.set(x, y, grid[y][x]);
			}
		}
		return bp;
	}
	public ImageProcessor toHistoryImage(){
		FloatProcessor fp=new FloatProcessor(grid[0].length, grid.length);
		for (int y=0;y<grid.length;y++){
			for (int x=0;x<grid[0].length;x++){
				fp.setf(x, y, history[y][x]);
			}
		}
		return fp;
	}
}
