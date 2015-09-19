package edu.uno.ai.planning.logic;

import edu.uno.ai.planning.MutableState;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * Represents a single statement in function-free predicate logic.
 * 
 * @author Stephen G. Ware
 */
public class Predication extends ExpressionObject implements Literal {

	/** The predicate (i.e. relationship) applies to the terms */
	public final String predicate;
	
	/** The terms to which the predicate applies */
	public final ImmutableArray<Term> terms;
	
	/**
	 * Constructs a new predication with a given predicate and set of terms.
	 * 
	 * @param predicate the predicate
	 * @param terms the terms
	 */
	public Predication(String predicate, ImmutableArray<Term> terms) {
		this.predicate  = predicate;
		this.terms = terms;
	}
	
	/**
	 * Constructs a new predication with a given predicate and set of terms.
	 * 
	 * @param predicate the predicate
	 * @param terms the terms
	 */
	public Predication(String predicate, Term...terms) {
		this(predicate, new ImmutableArray<>(terms));
	}
	
	@Override
	public boolean equals(Formula other, Substitution substitution) {
		if(other instanceof Predication) {
			Predication otherPred = (Predication) other;
			if(!predicate.equals(otherPred.predicate) || terms.length != otherPred.terms.length)
				return false;
			for(int i=0; i<terms.length; i++)
				if(!terms.get(i).equals(otherPred.terms.get(i), substitution))
					return false;
			return true;
		}
		return false;
	}
	
	/** The predication's hash code */
	private int hashCode = 0;
	
	@Override
	public int hashCode() {
		if(hashCode == 0)
			hashCode = predicate.hashCode() + terms.hashCode();
		return hashCode;
	}
	
	@Override
	public String toString() {
		String str = "(" + predicate;
		for(Term term : terms)
			str += " " + term;
		return str + ")";
	}
	
	@Override
	public int compareTo(Formula other) {
		if(other instanceof Negation)
			return -1 * other.compareTo(this);
		else
			return super.compareTo(other);
	}
	
	@Override
	public boolean isGround() {
		for(Term term : terms)
			if(!term.isGround())
				return false;
		return true;
	}

	/**
	 * Returns a copy of the set of terms, but after each has been substituted
	 * according to a given substitution.
	 * 
	 * @param substitution the substitution
	 * @return the terms, replaced by their substitutions
	 */
	protected ImmutableArray<Term> substituteTerms(Substitution substitution) {
		Term[] sub = new Term[terms.length];
		for(int i=0; i<terms.length; i++)
			sub[i] = terms.get(i).substitute(substitution);
		return new ImmutableArray<>(sub);
	}
	
	@Override
	public Predication substitute(Substitution substitution) {
		return new Predication(predicate, substituteTerms(substitution));
	}
	
	@Override
	public Bindings unify(Formula other, Bindings bindings) {
		if(other instanceof Predication) {
			Predication otherProp = (Predication) other;
			if(terms.length != otherProp.terms.length)
				return null;
			for(int i=0; i<terms.length && bindings!=null; i++)
				bindings = terms.get(i).unify(otherProp.terms.get(i), bindings);
			return bindings;
		}
		return null;
	}

	@Override
	public boolean isTestable() {
		return true;
	}

	@Override
	public boolean isTrue(State state) {
		return state.isTrue(this);
	}

	@Override
	public boolean isImposable() {
		return true;
	}

	@Override
	public void impose(MutableState state) {
		state.impose(this);
	}
	
	@Override
	public NegatedLiteral negate() {
		return new NegatedLiteral(this);
	}

	@Override
	public Expression toCNF() {
		return NormalForms.toCNF(this);
	}

	@Override
	public Expression toDNF() {
		return NormalForms.toDNF(this);
	}
}
