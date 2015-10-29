package edu.uno.ai.planning.SATPlan;

import java.util.ArrayList;

public class SATProblem {
	
	ArrayList<ArrayList<BooleanVariable>> conjunction;
	ArrayList<BooleanVariable> mainList;
	
	public SATProblem(ArrayList<ArrayList<BooleanVariable>> conjunction, ArrayList<BooleanVariable> mainList){
		this.conjunction = conjunction;
		this.mainList = mainList;
	}
}

