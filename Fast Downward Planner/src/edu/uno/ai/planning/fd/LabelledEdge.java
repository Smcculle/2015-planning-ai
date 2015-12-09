package edu.uno.ai.planning.fd;

import java.util.ArrayList;

public class LabelledEdge{
	public ArrayList<Assignment> label;
	public int weight;
	public MPTStep step;
	
	public LabelledEdge(ArrayList<Assignment> label, MPTStep step){
		this.label = label;
		this.weight = 0;
		this.step = step;
	}
}
