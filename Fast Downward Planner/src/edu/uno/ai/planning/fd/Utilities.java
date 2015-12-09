package edu.uno.ai.planning.fd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class Utilities {

	/** 
	 * Get a list of an operator's add effects
	 */
	public static ArrayList<Atom> getAddEffects(Operator operator){
		ArrayList<Atom> addEffects = new ArrayList<Atom>();
		if(operator.effect instanceof Literal && !(operator.effect instanceof NegatedLiteral))
			addEffects.add(getAtom(operator.effect)); // should I use stripTypes for these?
		else{
			for(Expression effect : ((Conjunction)operator.effect).arguments){
				if(!(effect instanceof NegatedLiteral))
					addEffects.add(getAtom(effect));
			}
		}
		return addEffects;
	}
	
	/**
	 * Returns true if the given list contains more than one assignment for the same variable
	 * @param list
	 * @return
	 */
	public static boolean containsDuplicateVariables(ArrayList<Assignment> list){
		ArrayList<StateVariable> variables = new ArrayList<StateVariable>();
		for(Assignment assignment : list){
			if(variables.contains(assignment.variable))
				return true;
			variables.add(assignment.variable);
		}
		return false;
	}

	/**
	 * Returns a copy of the given list with redundancies removed
	 * @param list
	 * @return
	 */
	public static <E> ArrayList<E> clearDuplicates(ArrayList<E> list){
		ArrayList<E> newList = new ArrayList<E>();
		for(E item : list){
			if(!newList.contains(item))
				newList.add(item);
		}
		return newList;
	}

	public static ArrayList<Atom> getAddEffects(Step step){
		ArrayList<Atom> addEffects = new ArrayList<Atom>();
		if(step.effect instanceof Literal && !(step.effect instanceof NegatedLiteral))
			addEffects.add(getAtom(step.effect)); // should I use stripTypes for these?
		else{
			for(Expression effect : ((Conjunction)step.effect).arguments){
				if(!(effect instanceof NegatedLiteral))
					addEffects.add(getAtom(effect));
			}
		}
		return addEffects;		
	}
	
	/** 
	 * Get a list of an operator's delete effects
	 */
	public static ArrayList<Atom> getDeleteEffects(Operator operator){
		ArrayList<Atom> deleteEffects = new ArrayList<Atom>();
		if(operator.effect instanceof Literal){
			if(operator.effect instanceof NegatedLiteral)
				deleteEffects.add(getAtom(operator.effect));
		}else{
			for(Expression effect : ((Conjunction)operator.effect).arguments){
				if(effect instanceof NegatedLiteral)
					deleteEffects.add(getAtom(effect));
		}}
		return deleteEffects;
	}

	public static ArrayList<Atom> getDeleteEffects(Step step){
		ArrayList<Atom> deleteEffects = new ArrayList<Atom>();
		if(step.effect instanceof Literal){
			if(step.effect instanceof NegatedLiteral)
				deleteEffects.add(getAtom(step.effect));
		}else{
			for(Expression effect : ((Conjunction)step.effect).arguments){
				if(effect instanceof NegatedLiteral)
					deleteEffects.add(getAtom(effect));
		}}
		return deleteEffects;
	}

	
	/** 
	 * Prints a list
	 * @param <E>
	 */
	public static <T> void printList(ArrayList<T> list){
		for(T item : list){
			System.out.println(item);
		}
		System.out.println();
	}
	

	
	
	/**
	 * Strip types from an Atom's terms
	 * @param expression
	 * @return a copy of the Atom with no types
	 */
	public static Atom stripTypesFromAtom(Expression expression){
		Atom atom = getAtom(expression);
		Term[] terms = new Term[atom.terms.length];
		for(int i=0; i<terms.length; i++){
			if(atom.terms.get(i) instanceof Variable)
				terms[i] = new Variable("",atom.terms.get(i).name);
			else terms[i] = new Constant("",atom.terms.get(i).name);
		}
		return new Atom(atom.predicate, new ImmutableArray<Term>(terms));
	}
	
	/**
	 * Convert an expression into an Atom
	 * @param expression
	 * @return Atom, or null if not applicable
	 */
	public static Atom getAtom(Expression expression){
		Predication predication;
		if(expression instanceof NegatedLiteral)
			predication = (Predication)((NegatedLiteral)expression).argument;
		else if(expression instanceof Literal)
			predication = (Predication)expression;
		else return null;
		return new Atom(predication.predicate, predication.terms);
	}

	/**
	 * Don't crash
	 * @param problem
	 * @return
	 */
	public static boolean currentlyHandles(Problem problem){
		String n = problem.name;
		if(problem.domain.name.equals("blocks"))
			return (n.equals("do_nothing") || n.equals("easy_stack") || n.equals("easy_unstack") || n.equals("sussman"));			
		else if(problem.domain.name.equals("cargo"))
			return (n.equals("deliver_1") || n.equals("deliver_2") || n.equals("deliver_return_1") || n.equals("deliver_return_2"));
		return false;
	}


	
	/** 
	 * Calculates the powerset of a given list
	 * @param list
	 * @return powerset
	 */
	public static <T> List<List<T>> powerset(Collection<T> list) {
		List<List<T>> powerset = new ArrayList<List<T>>();
		powerset.add(new ArrayList<T>()); // add the empty set
		// For every item in the original list
		for (T item : list) {
			List<List<T>> newPowerset = new ArrayList<List<T>>();
			for (List<T> subset : powerset) {
				// Copy all of the current powerset's subsets
				newPowerset.add(subset);				
				// plus the subsets appended with the current item
				List<T> newSubset = new ArrayList<T>(subset);
				newSubset.add(item);
				newPowerset.add(newSubset);
			}
			powerset = newPowerset;
		}
		return powerset;
	}

}
