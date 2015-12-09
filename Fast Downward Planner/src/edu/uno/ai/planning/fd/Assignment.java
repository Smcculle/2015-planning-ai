package edu.uno.ai.planning.fd;
import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Formula;
import edu.uno.ai.planning.logic.Substitution;

public class Assignment implements Expression{ 

	/**
	 * An Assignment specifies that a certain state variable should be set to a certain value
	 * @param variable
	 * @param value
	 */
	public StateVariable variable;
	public Atom value;
	public Assignment precondition; // Precondition for this assignment to apply, i.e. an "effect precondition"
	
	public Assignment(StateVariable variable, Atom value){
		this.variable = variable;
		this.value = value;
		this.precondition = null;
	}
	
	public Assignment(StateVariable variable, Atom value, Assignment precondition){
		this.variable = variable;
		this.value = value;
		this.precondition = precondition;
	}
	
	public String toString(){
		String s = "";
		if(precondition != null)
			s += "if "+precondition+" then ";
		s += "("+this.variable.name+" = "+this.value+")";
		return s;
	}
	
	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 71 * hash + this.variable.hashCode();
	    hash = 71 * hash + this.value.hashCode();
	    return hash;
	}


	@Override
	public boolean equals(Object other){
		if(!(other instanceof Assignment))
			return false;
		Assignment otherAssignment = (Assignment)other;
		if (this.variable.equals(otherAssignment.variable) && this.value.equals(otherAssignment.value))
			return true;
		return false;
	}
	
	
	/** Just don't call these */	

	@Override
	public boolean equals(Formula other, Substitution substitution) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGround() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Bindings unify(Formula other, Bindings bindings) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Formula o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Expression substitute(Substitution substitution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTestable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTrue(State state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isImposable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void impose(MutableState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Expression negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression toCNF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression toDNF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression simplify() {
		// TODO Auto-generated method stub
		return null;
	}
}
