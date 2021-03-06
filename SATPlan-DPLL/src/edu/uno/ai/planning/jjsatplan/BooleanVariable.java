package edu.uno.ai.planning.jjsatplan;

import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Negation;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Represents a boolean variable.
 * 
 * @author Hooker
 */
public class BooleanVariable {
	
	/** The name of the variable */
	public String name;
	
	/** The truth value of the variable */
	public Boolean value;
	
	/** The negation value of the variable (TRUE if variable is negated) */
	public final Boolean negation;
	
	/**
	 * Constructs a new boolean variable.
	 * 
	 * @param name the name
	 * @param value can be TRUE or FALSE
	 * @param negation TRUE if negated, FALSE if not
	 */
	public BooleanVariable(String name, Boolean value, Boolean negation){
		this.name = name;
		this.value = value;
		this.negation = negation;
	}
	
	/**
	 * Tells whether or not two boolean variables have the same name and negation.
	 * 
	 * @param BV1 the first boolean variable
	 * @param BV2 the second boolean variable
	 * @return TRUE if names and negation values are equal, FALSE otherwise
	 */
	public static Boolean booleanVariablesEqual(BooleanVariable BV1, BooleanVariable BV2){
		if(BV1.name.equals(BV2.name) && BV1.negation == BV2.negation)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	/**
	 * Tells whether or not two boolean variables have the same name but opposite negation values.
	 * 
	 * @param BV1 the first boolean variable
	 * @param BV2 the second boolean variable
	 * @return TRUE if names are equal and negations are not equal, FALSE otherwise
	 */
	public static Boolean booleanVariablesOpposites(BooleanVariable BV1, BooleanVariable BV2){
		if(BV1.name.equals(BV2.name) && BV1.negation != BV2.negation)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	/**
	 * Tells whether or not an array has a boolean variable.
	 * 
	 * @param BVarray the array of boolean variables.
	 * @param BV the boolean variable
	 * @return TRUE if the array contains the boolean variable, FLASE otherwise
	 */
	public static Boolean containsEqualBooleanVariable(ArrayList<BooleanVariable> BVarray, BooleanVariable BV){
		for(BooleanVariable testBV : BVarray){
			if(booleanVariablesEqual(testBV, BV))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Tells whether or not an array has a boolean variable with the same name
	 * and an opposite negation.
	 * 
	 * @param BVarray the array of boolean variables.
	 * @param BV the boolean variable
	 * @return TRUE if the array contains the boolean variable with the same name, but
	 * opposite negation. FLASE otherwise
	 */
	public static Boolean containsOppositeBooleanVariable(ArrayList<BooleanVariable> BVarray, BooleanVariable BV){
		for(BooleanVariable testBV : BVarray){
			if(booleanVariablesOpposites(testBV, BV))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean equals(Object other){
		if (!(other instanceof  BooleanVariable))
			return false;
		else{
			BooleanVariable otherVariable = (BooleanVariable)other;
			return otherVariable.name.equals(this.name) && otherVariable.negation == this.negation;
		}
	}

	@Override
	public int hashCode(){
		return this.name.hashCode();
	}

	@Override
	public String toString(){
		return (this.negation ? "~" : "") + this.name + " - value = " + this.value;
	}

	public BooleanVariable negate(){
		return new BooleanVariable(this.name, this.value, !this.negation);
	}
	
	/**
	 * Removes a boolean variable from an array.
	 * 
	 * @param BVarray the array of boolean variables.
	 * @param BV the boolean variable
	 * @return the original array without BV
	 */
	public static ArrayList<BooleanVariable> removeBooleanVariable(ArrayList<BooleanVariable> BVarray, BooleanVariable BV){
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

	public static BooleanVariable create(Expression predication, int time){
		if (predication instanceof Negation){
			return new BooleanVariable(predication.negate().toString() + " - " + time, null, true);
		}
		else
			return new BooleanVariable(predication.toString() + " - " + time, null, false);
	}

	public static BooleanVariable createNegated(String name, int time){
		return new BooleanVariable(name + " - " + time, null, true);
	}

	public static BooleanVariable create(String name, int time){
		return new BooleanVariable(name + " - " + time, null, false);
	}
}