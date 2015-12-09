package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.fd.Assignment;

public class MPTProblem extends Problem{ //must extend Problem in order to work with FDSearch

	public ArrayList<Assignment> initialAssignments;
	public ArrayList<Assignment> goalAssignments;
	public MPTDomain mptDomain;
	public ArrayList<MPTStep> mptSteps;
	
	public MPTProblem(Problem problem, MPTDomain mptDomain, ArrayList<Assignment> initialAssignments, ArrayList<Assignment> goalAssignments, ArrayList<MPTStep> steps){
		super(problem.name, problem.domain, problem.objects, problem.initial, problem.goal);
		this.mptDomain = mptDomain;
		this.initialAssignments = initialAssignments;
		this.goalAssignments = goalAssignments;
		this.mptSteps = steps;
	}
	
	/**
	 * Checks if a given plan is a solution to this problem.
	 * 
	 * @param plan the plan to test
	 * @return true if the plan is a solution to the problem, false otherwise
	 */
	@Override
	public boolean isSolution(Plan plan) {
		MutableMPTState current = new MutableMPTState(new MPTState(initialAssignments));
		for(Step step : (TotalOrderMPTPlan)plan) {
			if(current.isTrue(((MPTStep)step).precondition))
				current.impose(((MPTStep)step).effect);
			else
				return false;
		}
		return current.isTrue(goalAssignments);
	}	
	
	/**
	 * Finds a step which changes the variable's value from d1 to d2
	 * @param d1
	 * @param d2
	 * @return the step, or null if it couldn't find one
	 */
	public MPTStep findStep(StateVariable v, Atom d1, Atom d2){
		for(MPTStep step : mptSteps){
			if(step.affectedVariables.contains(v)){
				if(step.precondition.contains(new Assignment(v,d1)) && step.effect.contains(new Assignment(v,d2))){
					return step;
				}
			}
		}
		System.out.println("Error: findStep() failed.");
		return null;
	}

	
	/** 
	 * @return string of MPT problem		
	 */
	@Override
	public String toString(){
		String s = "Problem: "+this.name+"\n";		
		s+=this.mptDomain;
		s+="Initial State: \n";
		for(Assignment a : initialAssignments){
			s+="\t"+a+"\n";
		}
		s+="Goal: \n";
		for(Assignment a : goalAssignments){
			s+="\t"+a+"\n";
		}
		s+="Steps: \n";
		for(MPTStep step : mptSteps){
			s+="\t"+step+"\n";
		}
		return s;
	}

}