package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.logic.Variable;

public class InvariantInstance extends Invariant {

	ArrayList<Term> objects;
	ArrayList<Term> coveredFacts; 
	int weight;

	public InvariantInstance(ArrayList<Variable> parameters, ArrayList<Atom> atoms, ArrayList<Term> objects){
		super(parameters, atoms);
		this.objects = objects;
	}
	
	@Override
	public boolean covers(Atom b){
		for(Atom a : this.atoms){
			if(a.coversGroundedAtom(b, this.parameters, this.objects))
				return true;
		}
		return false;
	}
}
