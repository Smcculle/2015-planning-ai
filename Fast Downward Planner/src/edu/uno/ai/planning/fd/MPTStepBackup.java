package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.fd.Assignment;
import edu.uno.ai.planning.logic.Conjunction;

public class MPTStepBackup extends Step{

	/** What must be true before the step can be taken */
	public final ArrayList<Assignment> precondition;
	
	/** What becomes true after the step has been taken */
	public final ArrayList<Assignment> effect;
	
	/** The state variables affected by this step */
	public ArrayList<StateVariable> affectedVariables;


	/**
	 * Constructs a new step.
	 * 
	 * @param name the name of the step
	 * @param precondition the precondition
	 * @param effect the effect
	 * @throws IllegalArgumentException if my code is bad and the super() call throws an exception
	 */
	public MPTStepBackup(String name, ArrayList<Assignment> precondition, ArrayList<Assignment> effect) {
		super(name, new Conjunction(), new Conjunction()); // happy?
		this.precondition = precondition;
		this.effect = effect;
		affectedVariables = new ArrayList<StateVariable>();
		for(Assignment a : precondition){
			if(!affectedVariables.contains(a.variable)){
				affectedVariables.add(a.variable);
			}			
		}
		for(Assignment a : effect){
			if(!affectedVariables.contains(a.variable)){
				affectedVariables.add(a.variable);
			}
		}
	}	
	
	@Override
	public String toString(){
		String s = name+":\n";
		s+="\tPreconditions: ";
		for(Assignment a : precondition){
			s += a+", ";
		}
		s+="\n\tEffects: ";
		for(Assignment a : effect){
			s += a+", ";
		}
		s+="\n";
		return s;
	}
	
	
}
