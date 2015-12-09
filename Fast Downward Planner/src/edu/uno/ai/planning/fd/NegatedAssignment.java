package edu.uno.ai.planning.fd;


/**
 * A Negated Assignment specifies that a certain state variable should NOT be set to a certain value
 * @param variable
 * @param value
 */

public class NegatedAssignment extends Assignment{

	public NegatedAssignment(StateVariable variable, Atom value){
		super(variable, value);
	}
	
	public NegatedAssignment(StateVariable variable, Atom value, Assignment precondition){
		super(variable, value, precondition);
	}

	@Override
	public String toString(){
		String s = "";
		if(precondition != null)
			s += "if "+precondition+" then ";
		s += "("+variable.name+" != "+value+")";
		return s;
	}
	
	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 73 * hash + this.variable.hashCode();
	    hash = 73 * hash + this.value.hashCode();
	    return hash;
	}


	@Override
	public boolean equals(Object other){
		if(!(other instanceof NegatedAssignment))
			return false;
		Assignment otherAssignment = (NegatedAssignment)other;
		if (this.variable.equals(otherAssignment.variable) && this.value.equals(otherAssignment.value))
			return true;
		return false;
	}
	
	

}
