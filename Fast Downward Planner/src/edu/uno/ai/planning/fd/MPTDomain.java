package edu.uno.ai.planning.fd;

import java.util.ArrayList;

public class MPTDomain{
	public final String name;
	public final ArrayList<StateVariable> variables;
	
	public MPTDomain(String name, ArrayList<StateVariable> variables){
		this.name = name;
		this.variables = variables;
	}
	
	public String toString(){
		String s = "Domain: "+this.name+"\n";
		s+="Variables: \n";
		for(StateVariable v : this.variables){
			s+="\t"+v+"\n";
		}
		s+="\n";
		return s;
	}
}
