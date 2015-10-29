package edu.uno.ai.planning.SATPlan;


import java.util.ArrayList;
import java.util.ListIterator;

public class BooleanVariable {
	String name;
	Boolean value;
	Boolean negation;
	
	public BooleanVariable(String name, Boolean value, Boolean negation){
		this.name = name;
		this.value = value;
		this.negation = negation;
	}
	
	public static Boolean booleanVariablesEqual(BooleanVariable BV1, BooleanVariable BV2){
		if(BV1.name.equals(BV2.name) && BV1.negation == BV2.negation)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	public static Boolean booleanVariablesOpposites(BooleanVariable BV1, BooleanVariable BV2){
		if(BV1.name.equals(BV2.name) && BV1.negation != BV2.negation){			
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static Boolean containsEqualBooleanVariable(ArrayList<BooleanVariable> BVarray, BooleanVariable BV){
		for(BooleanVariable testBV : BVarray){
			if(booleanVariablesEqual(testBV, BV))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static Boolean containsOppositeBooleanVariable(ArrayList<BooleanVariable> BVarray, BooleanVariable BV){
		for(BooleanVariable testBV : BVarray){
			if(booleanVariablesOpposites(testBV, BV))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static ArrayList<BooleanVariable> removeBooleanVariable(ArrayList<BooleanVariable> BVarray, BooleanVariable BV){
//		System.out.println("Making sure to remove.");
		ListIterator<BooleanVariable> iterator;
		iterator = BVarray.listIterator();
		while(iterator.hasNext()){
			if(BV.name.equals(iterator.next().name)){
				iterator.remove();
				break;
			}
		}
		
		return BVarray;
	}
}