package edu.uno.ai.planning.fd;

import java.util.ArrayList;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.fd.Assignment;
import edu.uno.ai.planning.fd.Atom;
import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Formula;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Substitution;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.util.ImmutableArray;

public class StateVariable implements Literal{
	public static Atom NO_VALUE = new Atom("NO_VALUE", new ImmutableArray<Term>(new Term[0]));

	public String name;
	public ArrayList<Atom> domain;
	public DirectedGraph<Atom, LabelledEdge> DTG;
	public Atom value;	
	
	public StateVariable(String name, ArrayList<Atom> domain){
		this.name = name;
		this.domain = domain;
		this.domain.add(NO_VALUE);
		this.value = null;
	}
	
	public void buildDomainTransitionGraph(ArrayList<MPTStep> steps){
		// Create an empty directed graph
		DTG = new DefaultDirectedGraph<Atom, LabelledEdge>(LabelledEdge.class);

		// Create a node in the graph for each value in this variable's domain
		for(Atom atom : this.domain){
			DTG.addVertex(atom);
		}

		// For each grounded operator 
		for(MPTStep step : steps){
			// If it affects this variable,
			if(step.affectedVariables.contains(this)){
				Atom nextValue=null;
				Atom prevValue=null;
				// Then let nextValue be the value of this variable in any effect
				for(Assignment a : step.effect){
					if(a.variable.equals(this)){
						nextValue = a.value;
						// If this effect has a precondition on this variable, set prevValue to that
						if(a.precondition!=null && a.precondition.variable.equals(this))
							prevValue = a.precondition.value;
					}
				}
				ArrayList<Assignment> edgeLabel = new ArrayList<Assignment>();
				// Label the edge with all the preconditions of this operator
				edgeLabel.addAll(step.precondition);
				// If there is a precondition of this operator on variable v, let prevValue be that value  
				for(Assignment a : step.precondition){
					if(a.variable.equals(this)){
						prevValue = a.value;
						// Remove that precondition from the label
						edgeLabel.remove(a);
					}
				}
				// Otherwise, let prevValue be NO_VALUE
				if(prevValue==null)
					prevValue = NO_VALUE;
				// Add an edge from dPrev to dNext	
				if(nextValue!=null && !prevValue.equals(nextValue))
					DTG.addEdge(prevValue, nextValue, new LabelledEdge(edgeLabel, step));
			}
		}
		
	}
	
	public void setValue(Atom a){
		if(!domain.contains(a)){
			System.out.println("[!]: Value not in domain. Setting value to NO_VALUE.");
			this.value = NO_VALUE;
		}
		this.value = a;
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 71 * hash + this.name.hashCode();
	    hash = 71 * hash + this.domain.hashCode();
	    return hash;
	}
	
	/** 
	 * Tests if two state variables are the same
	 * @param other
	 * @return
	 */
	@Override
	public boolean equals(Object other){
		if(!(other instanceof StateVariable))
			return false;
		StateVariable otherVariable = (StateVariable) other;
		if(!name.equals(otherVariable.name) || !domain.equals(otherVariable.domain))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		String result = name+": {";
		for(int i=0; i<domain.size(); i++){
			result += domain.get(i);
			if(i<domain.size()-1)
				result += ", ";
		}
		result += "}";
		return result;
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
	public Literal substitute(Substitution substitution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Literal negate() {
		// TODO Auto-generated method stub
		return null;
	}
}
