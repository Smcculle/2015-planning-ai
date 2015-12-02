package edu.uno.ai.motionplanning;

import java.io.File;
import java.util.List;

import edu.uno.ai.motionplanning.Heuristics.Euclidean;
import edu.uno.ai.motionplanning.Heuristics.WeightedDistanceHeuristic;
import edu.uno.ai.motionplanning.Planners.AStar;
import ij.process.*;
import ij.io.*;
import ij.*;

public class VisualizeCosts {
	public static void main (String[] args){
		ScenarioLoader sl=new ScenarioLoader(new File("./"),new File("scenarios/"));
        List<Scenario> complete=sl.loadAllScenarios();
        for(int i=0;i<complete.size();i++){
        	Scenario s=complete.get(i);
        	System.out.println(s);
        	ImageStack is = new ImageStack(s.getMap().getWidth(),s.getMap().getHeight());
        	double min=Double.POSITIVE_INFINITY;
        	double max=0;
        	for (float f=5; f>=0.99;f-=0.5){
        		 AStar pathing=new AStar(s,new WeightedDistanceHeuristic(f,new Euclidean()));
        		 long start=System.nanoTime();
        		 MotionPlan<?> p=pathing.search();
        		 long end=System.nanoTime();
        		 if (p!=null){
        			 System.out.println(f+","+(end-start)+","+p.getCost());
        			 ImageProcessor temp=new VisualGridMap(pathing.getMap()).toHistoryImage();
        			 temp.resetMinAndMax();
        			 if (temp.getMin()<min){
        				 min=temp.getMin();
        			 }
        			 if (temp.getMax()>max){
        				 max=temp.getMax();
        			 }
        			 is.addSlice(s.toString()+","+f, temp);
        		 }
        	}
        	ImagePlus ip=new ImagePlus("Costs", is);
        	//IJ.run(ip, "physics", "");  //set the physics colormap
        	for( int w=1; w<=is.getSize();w++){
        		ImageProcessor big=is.getProcessor(w).resize(600);
        		big.setMinAndMax(min, max);
        		ColorProcessor cp = big.convertToColorProcessor();
        		String filename = is.getSliceLabel(w) ;
        		String sane = filename.replaceAll("[^a-zA-Z0-9\\_]+", "_")+ ".png";
        		FileSaver fs = new FileSaver(new ImagePlus("", cp));
        		fs.saveAsPng(sane);
        	}
        }
	}
}
