package edu.uno.ai.planning.fd;

import java.util.ArrayList;
import java.util.List;

import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.MutableState;
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
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.util.ImmutableArray;

public class MPTTranslator extends StateSpaceProblem{

	public MutableState normalizedInitial;
	public ArrayList<Constant> normalizedObjects;
	public ArrayList<Operator> normalizedOperators;
	public ArrayList<Invariant> invariants;
	public Domain normalizedDomain;
	public static NormalizedProblem relaxedProblem;
	public ArrayList<Atom> reachableAtoms;
	public ArrayList<ArrayList<Atom>> mutexGroups;
	public ArrayList<StateVariable> stateVariables;
	public MPTProblem newProblem;
	
	public MPTTranslator(Problem problem){
		super(problem);
		if(Utilities.currentlyHandles(problem)){
			normalize();
			calculateInvariants();	
			computeReachableAtoms();
			calculateMutexGroups();	
			chooseVariables();
			generateMPT();		
			//System.out.println(newProblem);
		}else{
			//create dummy problem and don't crash
			newProblem = new MPTProblem(problem, new MPTDomain(problem.domain.name, new ArrayList<StateVariable>()), new ArrayList<Assignment>(), new ArrayList<Assignment>(), new ArrayList<MPTStep>());
		}
	}

	/** ---------------------------------- 1. Normalization --------------------------------------- 
	 * Compile away typed objects/constants. For typed constants, add facts to initial state. For typed parameters of 
	 * operators, add operator preconditions.
	 */	
	public void normalize(){

		// Remove types from Objects (add facts to the initial state)
		normalizedInitial = new MutableState(initial);
		normalizedObjects = new ArrayList<Constant>();
		for(Constant obj : objects){
			Constant newConstant = new Constant("",obj.name);
			normalizedObjects.add(newConstant);
			Term[] singleTerm = new Term[1];
			singleTerm[0] = newConstant;
			normalizedInitial.impose(new Predication(obj.type, singleTerm));
		}
		for(Constant constant : domain.constants){
			Constant newConstant = new Constant("",constant.name);
			if(!normalizedObjects.contains(newConstant))
				normalizedObjects.add(newConstant);		
			Term[] singleTerm = new Term[1];
			singleTerm[0] = newConstant;
			normalizedInitial.impose(new Predication(constant.type, singleTerm));
		}

		// Remove types from Operator parameters (and add corresponding preconditions) and also de-type the preconditions
		normalizedOperators = new ArrayList<Operator>();
		for(Operator operator : domain.operators){
			ArrayList<Expression> newPreconditions = new ArrayList<Expression>();
			if(operator.precondition instanceof Literal){
				Atom newAtom = Utilities.stripTypesFromAtom(operator.precondition);
				if(operator.precondition instanceof NegatedLiteral)
					newPreconditions.add(new NegatedLiteral(newAtom));
				else
					newPreconditions.add(newAtom);
			}
			else{
				for(Expression precondition : ((Conjunction)operator.precondition).arguments){
					Atom newAtom = Utilities.stripTypesFromAtom(precondition);
					if(precondition instanceof NegatedLiteral)
						newPreconditions.add(new NegatedLiteral(newAtom));
					else
						newPreconditions.add(newAtom);
				}
			}
			Variable[] newVariables = new Variable[operator.parameters.length];
			for(int i=0; i<operator.parameters.length; i++){
				Variable v = operator.parameters.get(i);
				Variable newV = new Variable("",v.name);
				newVariables[i] = newV;
				if(!v.type.equals("object")) // because everything is implicitly an object
					newPreconditions.add(new Predication(v.type, newV));
			}

			// Also strip types from the terms in the operator effect
			ArrayList<Expression> newEffects = new ArrayList<Expression>();
			if(operator.effect instanceof Literal){
				Atom newAtom = Utilities.stripTypesFromAtom(operator.effect);
				if(operator.effect instanceof NegatedLiteral)
					newEffects.add(new NegatedLiteral(newAtom));
				else
					newEffects.add(newAtom);
			}else{
				for(Expression effect : ((Conjunction)operator.effect).arguments){
					Atom newAtom = Utilities.stripTypesFromAtom(effect);
					if(effect instanceof NegatedLiteral)
						newEffects.add(new NegatedLiteral(newAtom));
					else
						newEffects.add(newAtom);
				}
			}
			
			// Convert the lists of preconditions and effects back to single expressions
			Conjunction newPreconditionExpression = new Conjunction(new ImmutableArray<Expression>(newPreconditions.toArray(new Expression[newPreconditions.size()])));
			Conjunction newEffectExpression = new Conjunction(new ImmutableArray<Expression>(newEffects.toArray(new Expression[newEffects.size()])));

			// Create the new operator and add it to the list
			normalizedOperators.add(new Operator(operator.name, new ImmutableArray<Variable>(newVariables), newPreconditionExpression, newEffectExpression));
		}
	}
	
	/** ------------------------------- 2. Invariant Synthesis -------------------------------------
	 * Calculate monotonicity invariants
	 */	
	public void calculateInvariants(){
		ArrayList<Invariant> initialCandidates = generateInitialCandidates();
		invariants = new ArrayList<Invariant>();		
		
		// Keep trying to prove candidates are invariants until there are no more refinements to be made.
		ArrayList<Invariant> refinedCandidates = proveInvariants(initialCandidates);

		while(!refinedCandidates.isEmpty()){
			refinedCandidates = proveInvariants(refinedCandidates);
		}
		
		// Prune the list to eliminate redundancies
		while(pruneInvariants()){}
	}

	/**
	 * Generates the set of initial invariant candidates:
	 *  The set of initial candidates consists of all those candidates which contain at most one counted variable and exactly 
	 *  one atom, over a modifiable fluent predicate, whose parameters are distinct variables.
	 * @return initialCandidates
	 */
	public ArrayList<Invariant> generateInitialCandidates(){
		ArrayList<Invariant> initialCandidates = new ArrayList<Invariant>();
		ArrayList<Atom> atoms;
		ArrayList<Variable> variables;
		
		// Get the list of modifiable fluent predicates. But really, get the whole predication/atom.
		ArrayList<Atom> modifiableFluentPredicates = getModifiableFluentPredicates(); 
		
		// For each of these atoms
		for(Atom p : modifiableFluentPredicates){
			atoms = new ArrayList<Atom>();
			atoms.add(p);

			// Get the powerset of its Variables 
			variables = new ArrayList<Variable>();
			for(Term term : p.terms){
				if(term instanceof Variable)		// not Constants
					variables.add((Variable)term);
			}
			List<List<Variable>> powerset = Utilities.powerset(variables);
			
			// For each set in that power set, create an InvariantCandidate with those parameters, and this as the only atom
			for(List<Variable> list : powerset){
				// But only if it has at most one counted variable
				int countedVariables = 0;
				for(Term t : p.terms){
					if(t instanceof Variable && !list.contains(t)) 
						countedVariables++;
				}
				if(countedVariables <= 1)
					initialCandidates.add(new Invariant((ArrayList<Variable>)list, atoms));
			}
		}
		return initialCandidates;
	}
	
	/**
	 * Gets the set of modifiable fluent predicates (predicates which occur in operator effects)
	 * @return the list of predicates
	 */
	public ArrayList<Atom> getModifiableFluentPredicates(){
		ArrayList<Atom> modifiableFluentPredicates = new ArrayList<Atom>();
		for(Operator operator : normalizedOperators){
			for(Expression effect : ((Conjunction)operator.effect).arguments){
				Atom atom = Utilities.getAtom(effect);
				Term[] terms = new Term[atom.terms.length]; 
				// Uniquely name variables (v1, v2, ..., vn) 
				for(int i=0; i<atom.terms.length; i++){
					if(atom.terms.get(i) instanceof Constant) // UNLESS it's a constant. 
						terms[i] = atom.terms.get(i);
					else
						terms[i] = new Variable("","v"+(i+1));
				}
				Atom newAtom = new Atom(atom.predicate, new ImmutableArray<Term>(terms));
				if(!modifiableFluentPredicates.contains(newAtom))
					modifiableFluentPredicates.add(newAtom);
		}}
		return modifiableFluentPredicates;
	}
	
	/**
	 * Try to prove that each candidate is an invariant
	 * @return true if something was refined
	 */
	public ArrayList<Invariant> proveInvariants(ArrayList<Invariant> invariantCandidates){
		ArrayList<Invariant> refinedCandidates = new ArrayList<Invariant>();
		ArrayList<Invariant> tempRefinedCandidates;

		boolean rejected;
		for(Invariant candidate : invariantCandidates){
			rejected = false;
			for (Operator operator : normalizedOperators){
				if(checkOperatorTooHeavy(operator,candidate))	// <-- Might not be completely correct
					rejected = true;
				else{
					Atom unbalancedAddEffect = checkOperatorUnbalanced(operator,candidate);
					// If there was an unbalanced add effect
					if(unbalancedAddEffect!=null){
						// Reject this candidate, but try to refine it and add more candidates to the list
						rejected = true;
						tempRefinedCandidates = refineCandidate(candidate, operator, unbalancedAddEffect);
						for(Invariant c : tempRefinedCandidates){
							refinedCandidates.add(c);
			}}}}
			if(!rejected){
				if(!invariants.contains(candidate))
					invariants.add(candidate);
		}}
		return refinedCandidates;
	}

	/**
	 * Checks if the operator is too heavy:
	 * 	An operator is too heavy if, when ignoring delete effects, it can increase the weight of some instance of the candidate in some state
	 *  by at least 2.
	 * @return true if the candidate is rejected
	 */
	public boolean checkOperatorTooHeavy(Operator operator, Invariant candidate){
		// Look at every possible pair of this operator's add effects that both affect a predicate in the candidate
		ArrayList<Atom> addEffects = Utilities.getAddEffects(operator);
		ArrayList<String> candidatePredicates = candidate.getPredicates();
		for(Atom a1 : addEffects){
			for(Atom a2 : addEffects){
				if(addEffects.indexOf(a2) >= addEffects.indexOf(a1) && candidatePredicates.contains(a1.predicate) 
																	&& candidatePredicates.contains(a2.predicate)){
					// If the candidate covers both of them but they're not the same? 	// This might be wrong
					if((a1.predicate != a2.predicate) || (a1.terms.length != a2.terms.length)){
							if(candidate.covers(a1) && candidate.covers(a2)){						
								// And if (operator.precondition & -a1.atom & -a2.atom) is satisfiable???
								return true; // Reject the candidate
		}}}}}
		return false;
	}
		
	/**
	 * Checks if the operator is unbalanced:
	 *  An operator is unbalanced if it has an add effect that can increase the weight of an instance of the candidate in some state, 
	 *  but no delete effect that is guaranteed to decrease the weight of the same instance in the same state.
	 * @return the add effect which caused the operator to be unbalanced, or null if it's balanced
	 */
	public Atom checkOperatorUnbalanced(Operator operator, Invariant candidate){
		// Get lists of the operator's add and delete effects
		ArrayList<Atom> addEffects = Utilities.getAddEffects(operator);
		ArrayList<Atom> deleteEffects = Utilities.getDeleteEffects(operator);

		// For each add effect of the operator that affects a predicate in candidate.atoms
		ArrayList<String> candidatePredicates = candidate.getPredicates();
		boolean balanced = true;
		Atom candidateAtom = null;
		ArrayList<Term> parameterTermsMapped = new ArrayList<Term>(); // Starting them here means that I'm adding to the same maps for every matched atom in the candidate.
		ArrayList<Term> addEffectTermsMapped = new ArrayList<Term>();
		for(Atom addEffect : addEffects){
			if (candidatePredicates.contains(addEffect.predicate)){
				// Try to match it with an atom in the candidate
				for(Atom atom : candidate.atoms){
					// Both atoms must have the same predicate and number of terms
					if(atom.predicate==addEffect.predicate && atom.terms.length==addEffect.terms.length){
						boolean cool = true;
						for(Term t : atom.terms){
							// Wherever a Constant appears in the candidate atom, the addEffect must have the same Constant
							if(t instanceof Constant){
								if(!addEffect.terms.get(atom.terms.indexOf(t)).equals(t))
									cool = false;
							// Wherever a parameter appears in the candidate atom, the addEffect must have a Variable
							} else if(candidate.parameters.contains(t)){
								if(addEffect.terms.get(atom.terms.indexOf(t)) instanceof Constant){
									cool = false;
								}
							}
						}
						// If we found an atom that matches (and there'll only ever be one, right?)
						if(cool){
							candidateAtom = atom;
							for(Term term : candidateAtom.terms){
								if(candidate.parameters.contains(term)){
									// Map its parameters to the variables now associated with them in the add effect.
									parameterTermsMapped.add(term);
									addEffectTermsMapped.add(addEffect.terms.get(atom.terms.indexOf(term)));
									/// SOOO, I'm adding mappings for ALL candidate atoms that match this add effect. But there should only be one, right? Should I check?
				}}}}}
				// For each such add effect that we find, look for a delete effect to balance it.
				if(candidateAtom != null){
					balanced = false;
					//  For each delete effect of the operator that affects a predicate in candidate.atoms:
					for(Atom deleteEffect : deleteEffects){
						if(candidatePredicates.contains(deleteEffect.predicate)){
							// Make a copy of the delete effect, but...
							Term[] terms = new Term[deleteEffect.terms.length];
							for(Term deleteTerm : deleteEffect.terms){
								boolean termSet = false;
								for(Term addTerm : addEffect.terms){									
									// If you find a variable that matches the addEffect and is associated with a parameters
									if(addTerm.equals(deleteTerm) && addEffectTermsMapped.contains(addTerm)){
										// Rename it to match the parameter name
										terms[deleteEffect.terms.indexOf(deleteTerm)] = parameterTermsMapped.get(addEffectTermsMapped.indexOf(addTerm));
										termSet = true;
									}
								}
								// If you didn't find any matches, use the existing name
								if(!termSet)
									terms[deleteEffect.terms.indexOf(deleteTerm)] = deleteTerm;
							}
							Atom renamedDeleteEffect = new Atom(deleteEffect.predicate, new ImmutableArray<Term>(terms));
							// If we found one, this add effect is balanced.
							if(renamedDeleteEffect != null && candidate.covers(renamedDeleteEffect)){
								// and o'.precondition ^ -e.atom entails o'.precondition ^ e'.atom ???
								balanced = true;
				}}}}
				// If any of these add effects cannot be balanced
				if(!balanced) 
					return addEffect; // Reject the candidate and return the offending add effect
		}}
		return null; // This operator is balanced.
	}
	
	/**
	 * Refine the given candidate
	 * @param candidate
	 * @param operator
	 * @param addEffect
	 * @return true if a candidate was refined
	 */
	public ArrayList<Invariant> refineCandidate(Invariant candidate, Operator operator, Atom addEffect){
		ArrayList<Invariant> refinedCandidates = new ArrayList<Invariant>();

		// Get the atom from the candidate that matches addEffect
		Atom candidateAtom = null;
		for(Atom atom : candidate.atoms){
			if((atom.predicate == addEffect.predicate) && (atom.terms.length == addEffect.terms.length)){
				for(Term t : atom.terms){
					if(    !(t instanceof Constant && !addEffect.terms.get(atom.terms.indexOf(t)).equals(t))
						&& !(candidate.parameters.contains(t) && addEffect.terms.get(atom.terms.indexOf(t)) instanceof Constant)){
						candidateAtom = atom;
		}}}}
		
		// Make a list of variables in addEffect corresponding to the indices of parameters in candidateAtom
		ArrayList<Term> addEffectVariables = new ArrayList<Term>();
		for(int i=0; i<candidateAtom.terms.length; i++){
			if(candidate.parameters.contains(candidateAtom.terms.get(i))){
				addEffectVariables.add(addEffect.terms.get(i));
			}
		}
		
		// Get a list of all the atoms in the operator effect
		ArrayList<Atom> effects = new ArrayList<Atom>();
		if(operator.effect instanceof Literal)
			effects.add(Utilities.getAtom(operator.effect));
		else
			for(Expression effect : ((Conjunction)operator.effect).arguments)
				effects.add(Utilities.getAtom(effect));

		// For each atom over any variables in addEffectVariables
		ArrayList<Atom> atomsToRename = new ArrayList<Atom>();
		for(Atom atom : effects){
			for(int i=0; i<atom.terms.length; i++){
				if(addEffectVariables.contains(atom.terms.get(i)) && !atom.equals(addEffect)){ 
					// (and if it has at most one other variable besides however many are in addEffectVariables), then..
					atomsToRename.add(atom);
		}}}

		// Rename the variable(s) in each atom to match the candidate's parameter(s)
		for(Atom atom : atomsToRename){
			Term[] newTerms = new Term[atom.terms.length];
			for(Term term : atom.terms){
				if(addEffectVariables.contains(term))
					newTerms[atom.terms.indexOf(term)] = candidateAtom.terms.get(addEffect.terms.indexOf(term));
				else newTerms[atom.terms.indexOf(term)] = term;
			}
			Atom newAtom = new Atom(atom.predicate, new ImmutableArray<Term>(newTerms));

			// If the candidate doesn't cover it, copy the candidate and add this atom to it
			if(!candidate.covers(newAtom)){
				ArrayList<Atom> newCandidateAtoms = new ArrayList<Atom>();
				for(Atom oldAtom : candidate.atoms){
					// Skip atoms that are covered by the new atom
					if(!newAtom.covers(oldAtom, candidate.parameters))
						newCandidateAtoms.add(oldAtom);
				}
				newCandidateAtoms.add(newAtom);

				// Copy the parameters and skip unused ones
				ArrayList<Variable> newParameters = new ArrayList<Variable>();
				boolean used;
				for(Variable v : candidate.parameters){
					used = false;
					for(Atom a : newCandidateAtoms){
						if(a.terms.contains(v))
							used = true;
					}
					if(used)
						newParameters.add(v);
				}
				Invariant newCandidate = new Invariant(newParameters, newCandidateAtoms);

				// If this operator isn't too heavy, add this to the candidate list
				if(!checkOperatorTooHeavy(operator, newCandidate))
					refinedCandidates.add(newCandidate);
		}}
		return refinedCandidates;
	}

	
	/**
	 * Removes an invariant from the list if it is either: 
	 * - logically equivalent to another invariant in the list
	 * - identical to another invariant but has fewer counted variables
	 * 
	 * @return true if something was removed
	 */
	public boolean pruneInvariants(){
		
		for(Invariant a : invariants){
			for(Invariant b : invariants){
				if(!a.equals(b)){ 
					// If a dominates b, remove b
					if(a.dominates(b)){
						invariants.remove(b);
						return true;
					}
					// If they're not redundant, check if they have all the same atoms but with different numbers of counted variables
					else{
						// First check if all their atoms match
						boolean unmatchedAtom = false;
						if(a.atoms.size()==b.atoms.size()){
							for(Atom a_atom : a.atoms){
								for(Atom b_atom : b.atoms){
									// For atoms of the equivalent indices, which is technically unsound but whatever... if they're not equal,
									if( a.atoms.indexOf(a_atom)==b.atoms.indexOf(b_atom) && !a_atom.equals(b_atom)){
										// then there is at least one unmatched atom
										unmatchedAtom = true;
									}
								}
							}
							// If there are no unmatched atoms, then they all match, so remove the invariant with fewer counted variables
							if(unmatchedAtom==false){
								if(a.countedVariables.size() > b.countedVariables.size()){
									invariants.remove(b);
									return true;
								}
								else if(a.countedVariables.size() < b.countedVariables.size()){
									invariants.remove(a);
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
		
	/** ------------------------------------------ 3. Grounding ---------------------------------------- 
	 * Obtain a grounded representation of the normalized PDDL task
	 */
	
	/**
	 * Computes the set of reachable atoms for a relaxed problem, which is a superset of the reachable atoms in the real problem
	 */
	public void computeReachableAtoms(){
		/* Build a Relaxed Problem, which differs from the Problem in 2 ways:
		* - Negative literals in operator preconditions and effect conditions are assumed to be always true
		* - Delete effects of operators are ignored
		*/
		ArrayList<Operator> relaxedOperators = new ArrayList<Operator>();
		for(Operator operator : normalizedOperators){
			ArrayList<Expression> relaxedPreconditions = new ArrayList<Expression>();
			ArrayList<Expression> relaxedEffects = new ArrayList<Expression>();
			for(Expression precondition : ((Conjunction)operator.precondition).arguments){
				if(!(precondition instanceof NegatedLiteral))
					relaxedPreconditions.add(precondition);
			}
			for(Expression effect : ((Conjunction)operator.effect).arguments){
				if(!(effect instanceof NegatedLiteral))
					relaxedEffects.add(effect);
			}			
			Conjunction relaxedPrecondition = new Conjunction(new ImmutableArray<Expression>(relaxedPreconditions.toArray(new Expression[relaxedPreconditions.size()])));
			Conjunction relaxedEffect = new Conjunction(new ImmutableArray<Expression>(relaxedEffects.toArray(new Expression[relaxedEffects.size()])));			
			relaxedOperators.add(new Operator(operator.name, operator.parameters, relaxedPrecondition, relaxedEffect));
		}
		
		Domain relaxedDomain = new Domain("relaxed_"+domain.name, domain.constants, new ImmutableArray<Operator>(relaxedOperators.toArray(new Operator[relaxedOperators.size()])));
		relaxedProblem = new NormalizedProblem(new Problem("relaxed_"+name, relaxedDomain, new ImmutableArray<Constant>(normalizedObjects.toArray(new Constant[normalizedObjects.size()])), normalizedInitial, goal));

		/*
		* A ground atom is reachable in the relaxed task iff it is true in the initial state or there exists some operator of the relaxed
		* task that can make it true.
		*/
		
		reachableAtoms = new ArrayList<Atom>();
		for(Expression e : ((Conjunction)normalizedInitial.toExpression()).arguments){
			if(!reachableAtoms.contains(Utilities.getAtom(e)))
				reachableAtoms.add(Utilities.getAtom(e)); // or stripTypes? wtf is up with that anyway?
		}
		for(Step step : relaxedProblem.steps){
			for(Expression e : ((Conjunction)step.effect).arguments){
				if(!reachableAtoms.contains(Utilities.getAtom(e)))
					reachableAtoms.add(Utilities.getAtom(e)); // and here too
			}
		}
	}
	
	/**
	 * Calculate the grounded mutex groups based on the invariants
	 */
	public void calculateMutexGroups(){
		mutexGroups = new ArrayList<ArrayList<Atom>>();
		/* 
		// Get the list of all reachable atoms. 
		for(Step step : normalizedProblemWithSteps.steps){
			if(step.effect instanceof Literal){
				Expression e = step.effect;
				if(!(e instanceof NegatedLiteral)){
					Atom atom = Utilities.stripTypesFromAtom(e);
					if(!reachableAtoms.contains(atom))
						reachableAtoms.add(atom);
				}
			}else{
				ImmutableArray<Expression> stepEffects = ((Conjunction)step.effect).arguments;
				for(Expression e : stepEffects){
					if(!(e instanceof NegatedLiteral)){
						Atom atom = Utilities.stripTypesFromAtom(e);
						if(!reachableAtoms.contains(atom))
							reachableAtoms.add(atom);
					}
		}}}
		
		// The initial state is all technically reachable so let's do this
		for(Expression e : ((Conjunction)normalizedProblemWithSteps.initial.toExpression()).arguments){
			if(!reachableAtoms.contains(Utilities.stripTypesFromAtom(e)))
				reachableAtoms.add(Utilities.stripTypesFromAtom(e));
		}

		*/

		InvariantInstance instance;
		ArrayList<Term> mutableProblemObjects = new ArrayList<Term>();
		for(Term t : normalizedObjects){ 
			mutableProblemObjects.add(t);
		}
		// For each invariant
		for(Invariant invariant : invariants){
			// For every possible combination of objects that could be assigned to its parameters
			List<List<Term>> powerset = Utilities.powerset(mutableProblemObjects);
			for(List<Term> objectset : powerset){
				if(objectset.size()==invariant.parameters.size()){
					// Create an "instance" of that invariant with those parameters.
					instance = new InvariantInstance(invariant.parameters, invariant.atoms, (ArrayList<Term>)objectset);
					// If that instance's weight is 1 in the initial state,
					int count = 0;
					for(Expression e : ((Conjunction)normalizedInitial.toExpression()).arguments){
						if(instance.covers(Utilities.stripTypesFromAtom(e)))
							count++;
					}
					if(count==1){
						// Create a mutex group containing all atoms in reachableAtoms that are covered by this instance.
						ArrayList<Atom> mutexGroup = new ArrayList<Atom>();
						for(Literal l : reachableAtoms){
							if(instance.covers(Utilities.stripTypesFromAtom(l)) && !mutexGroup.contains(Utilities.stripTypesFromAtom(l)))
								mutexGroup.add(Utilities.stripTypesFromAtom(l));
						}
						if(mutexGroup.size()>0)
							mutexGroups.add(mutexGroup);
					}
				}
			}			
		}
	}
	
	/** ------------------------------------------ 4. MPT Generation --------------------------------------- **/

	/**
	 * Choose variables based on mutex groups calculated from invariants...
	 * @return the list of state variables
	 */
	public void chooseVariables(){
		stateVariables = new ArrayList<StateVariable>();

		// Keep a list of atoms from reachableAtoms that have been covered by state variables 
		ArrayList<Atom> covered = new ArrayList<Atom>();
		
		// Make a list of the mutex groups sorted by size
		ArrayList<ArrayList<Atom>> sortedMutexGroups = new ArrayList<ArrayList<Atom>>();
		while(sortedMutexGroups.size() < mutexGroups.size()){
			int maxSize = 0;
			ArrayList<Atom> nextMutexGroup = null;
			for(ArrayList<Atom> mutexGroup : mutexGroups){
				if(mutexGroup.size() > maxSize){
					nextMutexGroup = mutexGroup;
					maxSize = mutexGroup.size();
				}
			}
			if(nextMutexGroup != null){
				sortedMutexGroups.add(nextMutexGroup);
				mutexGroups.remove(nextMutexGroup);
			}
		}
		// While there are still mutex groups
		int varCount = 0;
		for(ArrayList<Atom> mutexGroup : sortedMutexGroups){
			// Pick a mutex group of maximum size (which is > 1)
			if(mutexGroup.size() > 1){
				// Create a state variable with domain = the atoms in that mutex group (NO_VALUE is added in constructor)
				stateVariables.add(new StateVariable("var"+ ++varCount, mutexGroup));
				// Update the list of covered atoms
				for(Atom atom : mutexGroup){
					covered.add(atom);
				}
			}
		}
		// Create a state variable for each remaining uncovered reachable atom, with domain = {that atom, NO_VALUE}
		for(Atom atom : reachableAtoms){
			if(!covered.contains(atom)){
				ArrayList<Atom> binaryDomain = new ArrayList<Atom>();
				binaryDomain.add(atom);
				stateVariables.add(new StateVariable("var"+ ++varCount, binaryDomain));
			}
		}
	}


	/**
	 * Generate new MPT Problem
	 */
	public void generateMPT(){

		// Get the list of grounded steps for the normalized problem 
		Domain normalizedDomain = new Domain("normalized_"+domain.name, domain.constants, new ImmutableArray<Operator>(normalizedOperators.toArray(new Operator[normalizedOperators.size()])));
		NormalizedProblem normalizedProblem = new NormalizedProblem(new Problem("normalized_"+name, normalizedDomain, new ImmutableArray<Constant>(normalizedObjects.toArray(new Constant[normalizedObjects.size()])), normalizedInitial, goal));

		// Convert those steps to MPTSteps by converting their preconditions and effects to lists of state variable assignments
		ArrayList<MPTStep> mptSteps = new ArrayList<MPTStep>();	
		for(Step step : normalizedProblem.steps){
			ArrayList<Assignment> mptPreconditions = new ArrayList<Assignment>();
			ArrayList<Assignment> mptEffects = new ArrayList<Assignment>();
			boolean skip = false;
			for(Expression e : ((Conjunction)step.precondition).arguments){
				// Skip any step for which I find a precondition that doesn't match any state variable
				StateVariable v;
				Atom atom = Utilities.stripTypesFromAtom(e); //If e is negated, this just gets the positive literal

				v = getVar(atom);
				if(v==null){
					//System.out.println("Can't find a variable for atom "+atom); //TODO: Why can't I find a variable for (alive player)???
					skip = true;
				}
				
				if(e instanceof NegatedLiteral)
					mptPreconditions.add(new NegatedAssignment(v, atom));
				else
					mptPreconditions.add(new Assignment(v, atom));
			}
			if(!skip){ 
				//	For add effects, if p becomes true, 
				ArrayList<Atom> addEffects = Utilities.getAddEffects(step); // not tested
				for(Atom addEffect : addEffects){
					StateVariable var = getVar(addEffect);
					// Add a new assignment for var(p) = p
					mptEffects.add(new Assignment(var, addEffect));
				}
				
				//	For delete effects...
				ArrayList<Atom> deleteEffects = Utilities.getDeleteEffects(step);
				for(Atom deleteEffect : deleteEffects){
					//	If it also triggers an add effect for the same variable, then ignore it
					StateVariable var = getVar(deleteEffect);
					boolean ignore = false;
					for(Atom addEffect : addEffects){
						if(getVar(addEffect).equals(var)) 
							ignore = true;
					}
					if(!ignore){
						// Otherwise, assign NO_VALUE to var(p), BUT with the effect precondition of var(p)=p
						//Assignment precondition = new Assignment(var, deleteEffect);
						// This works, but for some reason mptPreconditions.contains(precondition) doesn't
  /* grr					boolean isPrecondition = false;
						for(Assignment a : mptPreconditions)
							if (a.equals(precondition)) 
								isPrecondition = true;
  						if(isPrecondition)
	*/						mptEffects.add(new Assignment(var, StateVariable.NO_VALUE));
	//					else
	//						mptEffects.add(new Assignment(var, StateVariable.NO_VALUE, precondition));
					}				
				}	
				// Remove duplicate preconditions
				mptPreconditions = Utilities.clearDuplicates(mptPreconditions);
				mptEffects = Utilities.clearDuplicates(mptEffects); // didn't run into any of these, but sure go ahead
				// If there are multiple assignments for the same variable in either the preconditions or effects, 
				// then this is an invalid step, so skip it.
				if(!Utilities.containsDuplicateVariables(mptPreconditions) && !Utilities.containsDuplicateVariables(mptEffects))
					mptSteps.add(new MPTStep(step.name, mptPreconditions, mptEffects));
			}
		}

		// Create new MPT domain
		MPTDomain newDomain = new MPTDomain(this.domain.name, stateVariables);

		// Create list of initial assignments 
		ArrayList<Assignment> mptInitial = new ArrayList<Assignment>();		
		ArrayList<Atom> initialAtoms = new ArrayList<Atom>();
		for(Expression e : ((Conjunction)normalizedInitial.toExpression()).arguments){
			initialAtoms.add(Utilities.stripTypesFromAtom(e));
		}
		for(StateVariable var : stateVariables){
			// Search the initial state for each of the atoms in this variable's domain
			boolean assigned = false;
			for(Atom atom : var.domain){
				if(initialAtoms.contains(atom)){
					// If you find it, assign it that value and move to the next state variable
					mptInitial.add(new Assignment(var, atom));
					assigned = true;
					break;
				}
			}
			// Otherwise assign it NO_VALUE
			if(!assigned)
				mptInitial.add(new Assignment(var,StateVariable.NO_VALUE));			
		}
		// Create list of goal assignments
		ArrayList<Assignment> mptGoals = new ArrayList<Assignment>();
		ArrayList<Atom> goalAtoms = new ArrayList<Atom>();
		if(this.goal instanceof Literal)
			goalAtoms.add(Utilities.stripTypesFromAtom(this.goal));
		else{
			for(Expression e : ((Conjunction)this.goal).arguments){
				goalAtoms.add(Utilities.stripTypesFromAtom(e));
			}
		}
		// For each atom in the goals, find its variable and add an assignment to the goals list
		for(Atom atom : goalAtoms){
			StateVariable var = getVar(atom);
			if(var!=null)
				mptGoals.add(new Assignment(var, atom));
		}

		// Create the new MPT problem
		newProblem = new MPTProblem(this, newDomain, mptInitial, mptGoals, mptSteps);
	}

	
	/**
	 * Finds the state variable that contains the given (grounded) atom in its domain
	 * @param atom
	 * @return
	 */
	public StateVariable getVar(Atom atom){
		for(StateVariable v : stateVariables){
			if(v.domain.contains(atom)){
				return v;
			}
		}
		return null;
	}
}
