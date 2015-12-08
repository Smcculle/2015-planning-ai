package edu.uno.ai.planning;

import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.util.ImmutableArray;

public class DurativeDomain extends Domain {

	public DurativeDomain(String name, Constant[] constants, Operator[] operators) {
		super(name, constants, operators);
		// nothing else to do
	}

	public DurativeDomain(String name, ImmutableArray<Constant> constants, 
			ImmutableArray<Operator> operators) {
		super(name, constants, operators);
		// nothing else to do
	}
	
	public boolean isDurativeDomain(){
		for(Operator o : operators)
			if(o instanceof DurativeOperator)
				return true;
		return false;
	}

}
