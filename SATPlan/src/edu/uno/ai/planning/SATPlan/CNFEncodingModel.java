package edu.uno.ai.planning.SATPlan;

import edu.uno.ai.planning.Step;

/**
 * This class models the CNFEncoding model that helps to decode th result
 * from the SATSolver
 * 
 * @author Janak Dahal*
 */
public class CNFEncodingModel {
	/* The name of the model that matches the argument of the CNF */
	public String name;
	
	/* The type (is action or a state) that identifies the argument of CNF */
	public CNFVariableType type;
	
	/* The time at which the action or state is true*/
	public int time;

	/*The step*/
	public Step step;
	
	/* The enumerated types of variable in the CNF */
	public enum CNFVariableType {
	    ACTION, STATE
	}
	
	/*
	 * Instantiates a CNF Encoding Model
	 */
	public CNFEncodingModel(String name, CNFVariableType type, int time, Step step){
		this.name = name;
		this.type = type;
		this.time = time;
		this.step = step;
	}
}


