package edu.uno.ai.planning.SATPlan;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tobias Potocek <tobiaspotocek@gmail.com>
 */
public class WalkSAT implements ISATSolver {
	public static Random random = new Random();

	/** How many times we restart the algorithm before stopping */
	public final int maxTries;

	/** How many flips of variables we allow before restarting the algorithm */
	public final int maxFlips;

	/**
	 * Probability determining how often we choose to pick a random variable to
	 * flip instead of finding the "least damaging" one.
	 **/
	public final double randomPickProbability;

	/** List of variables in the conjunction with the value fixed during simplification */
	protected Set<Variable> frozenVariables;

	/** List of variables in the original SAT problem */
	protected List<BooleanVariable> originalVariables;

	/** Number of visited variables (i. e. when looking for which variable to flip */
	protected int variableVisited;

	/** Number of variables actually and permanently flipped */
	protected int variableFlipped;

	/**
	 * Instantiate a new WalkSAT.
	 * @param maxTries how many times we restart the algorithm before stopping
	 * @param maxFlips how many flips of variables we allowe before restarting the algorithm
	 * @param randomPickProbability probability determining how often we choose
	 *                              to pick a random variable to flip instead of
	 *                              finding the "least damaging" one.
	 */
	public WalkSAT(int maxTries, int maxFlips, double randomPickProbability) {
		this.maxTries = maxTries;
		this.maxFlips = maxFlips;
		this.randomPickProbability = randomPickProbability;
	}

	/**
	 * Find satisfying model for input SAT problem.
	 * @param satProblem problem we want to solve (CNF formula)
	 * @return solution or null if not found within the limit
	 */
	@Override
	public List<BooleanVariable> getModel(final SATProblem satProblem) {
		Problem problem = convertProblem(satProblem);
		Clause unsatisfiedClause;
		Variable variable;

		variableVisited = 0;
		variableFlipped = 0;

		// Perform Unit Propagation optimization on the problem. If it turns out
		// impossible,  return null (as a failure)
		problem = unitPropagation(problem);
		if (problem == null) {
			return null;
		}

		/*
		Set<Variable> pures = new HashSet<>(problem.variables);
		Map<Variable, Boolean> lastVal = new HashMap<>();
		problem.clauses.forEach(clause -> clause.literals.forEach(literal -> {
			if (lastVal.get(literal.variable) == null) {
				lastVal.put(literal.variable, literal.negated);
			} else if (lastVal.get(literal.variable) != literal.negated) {
				ures.remove(literal.variable);
			}
		}));
		System.out.println("Variable total: " + problem.variables.size());
		System.out.println("Pure variable total: " + pures.size());
		*/

		for (int i = 0; i < maxTries; i++) {
			problem.randomSolution();

			for (int j = 0; j < maxFlips; j++) {
				if (problem.isSatisfied()) {
					return convertSolution(problem.getSolution());
				}

				// Find a clause that is currently unsatisfied and "flip" one
				// of the variables within the clause. The variable can be
				// chosen either randomly or using a simple greedy heuristics.
				unsatisfiedClause = problem.getUnsatisfiedClause();
				if (shouldPickRandomly()) {
					variable = unsatisfiedClause.pickRandomVariable();
				} else {
					variableFlipped += unsatisfiedClause.literals.size();
					variable = problem.pickLeastDamagingVariable(unsatisfiedClause);
				}

				variable.flip();
				variableVisited++;
			}
		}
		return null;
	}

	@Override
	public int countVisited() {
		return variableVisited;
	}

	@Override
	public int countExpanded() {
		return variableFlipped;
	}

	/**
	 * Convert the original SAT problem into our internal format that uses
	 * immutable structures but mutable ("flipable") variables.
	 * @param problem original SAT problem
	 * @return identical problem converted into our format.
	 */
	protected Problem convertProblem(SATProblem problem) {
		Map<String, Variable> lookupTable = new HashMap<>();
		originalVariables = new LinkedList<>();
		List<Clause> clauses = problem.conjunction.stream()
			.map(clause -> {
				List<Literal> literals = clause.stream()
					.map(boolVar -> {
						originalVariables.add(boolVar);
						Variable var = lookupTable.get(boolVar.name);
						if (var == null) {
							var = new Variable(boolVar.name);
							lookupTable.put(boolVar.name, var);
						}
						return new Literal(var, boolVar.negation);
					}).collect(Collectors.toList());
				return new Clause(literals);
			}).collect(Collectors.toList());
		return new Problem(clauses);
	}

	/**
	 * Convert solution in our internal format to the format expected by the
	 * outer SAT solver.
	 * @param solution found solution in our internal format
	 * @return converted solution
	 */
	protected List<BooleanVariable> convertSolution(Set<Variable> solution) {
		Map<String, Variable> lookupTable = new HashMap<>();
		solution.forEach(variable -> lookupTable.put(variable.name, variable));
		if (frozenVariables != null) {
			frozenVariables.forEach(variable -> lookupTable.put(variable.name, variable));
		}

		for (BooleanVariable boolVar : originalVariables) {
			Variable var = lookupTable.get(boolVar.name);
			if (var == null) {
				throw new RuntimeException("Missing value for variable " + boolVar.name);
			}
			boolVar.value = boolVar.negation != var.getValue();
		}
		return originalVariables;
	}

	/**
	 * Unit propagation. Find and remove variables inside "unit" clauses, i. e.
	 * clauses that contain only single literal (variable). The value of such
	 * a variable can be immediately inferred.
	 *
	 * @param problem the problem that want to simplify using unit propagation
	 * @return new instance of a problem without unit clauses or null if the
	 * 	problem turns out to be unsatisfiable.
	 */
	protected Problem unitPropagation(Problem problem) {
		List<Clause> clauses = problem.clauses;

		// Keep removing unit clauses as long as there are some to be
		// removed (removing one variable from all clauses might cause
		// another clause to become unit). This cycle ensures that
		// when it's over, it is not possible to remove any more variables.

		// Each cycle consists of two passes. In the first pass I find all
		// unit clauses and fix the variable's value ("freeze" the variable). In
		// the second pass, I remove all frozen variables and empty clauses.
		while (clauses.stream().anyMatch(Clause::freezeIfUnitClause)) {
			// If a unit clause turned out unsatisfiable, stop the cycle and
			// return null (the problem has no solution)
			if (clauses.stream().filter(Clause::isUnsatisfiable).count() > 0) {
				return null;
			}

			// Second pass: Remove frozen variables (their values have been
			// "frozen") and consequently all clauses that became empty.
			clauses = clauses.stream()
				.map(Clause::removeFrozen)
				.filter(clause -> !clause.isEmpty())
				.collect(Collectors.toList());
		}

		Problem simplified = new Problem(clauses);

		// It might have happened that in one of those second passes we
		// removed a complete satisfied clause that contained a variable
		// that didn't show up anywhere else. We find those extra lost variables
		// and freeze them as well. Their value is not important but we
		// have to remember them  so that we can correctly restore the solution.
		problem.variables.stream()
			.filter(variable -> !variable.isFrozen() && !simplified.variables.contains(variable))
			.forEach(Variable::freeze);

		// Remember frozen variables and their values so that we can add them
		// later to the solution
		frozenVariables = problem.variables.stream()
			.filter(Variable::isFrozen)
			.collect(Collectors.toSet());
		return new Problem(clauses);
	}

	protected boolean shouldPickRandomly() {
		return random.nextDouble() < randomPickProbability;
	}
}

/**
 * Immutable instance of a problem. Contains list of clauses and maintains
 * list of all contained variables for quick access.
 */
class Problem {
	public final List<Clause> clauses;
	public final Set<Variable> variables;

	Problem(List<Clause> clauses) {
		this.clauses = clauses;
		this.variables = clauses.stream()
			.flatMap(clause -> clause.variables.stream())
			.collect(Collectors.toSet());
	}

	/**
	 * Returns true if all clauses are satisfied with current truth assignment.
	 * @return true if the problem is satisfied.
	 */
	public boolean isSatisfied() {
		return clauses.stream().allMatch(Clause::isSatisfied);
	}

	/**
	 * Generates random solution. Goes through all the variables and randomly
	 * decides their value.
	 */
	public void randomSolution() {
		variables.forEach(Variable::pickRandomValue);
	}

	/**
	 * Return current solution. Basically just list of all variables in the
	 * formula with their values assigned.
	 * @return current solution
	 */
	public Set<Variable> getSolution() {
		return variables;
	}

	/**
	 * Returns any clause unsatisfied by the current solution.
	 *
	 * IMPORTANT: to improve speed this method uses cached results from the last
	 * Clause#isSatisfied() call. Also if there is no unsatisfied clause,
	 * RuntimeException is thrown, i. e. this method shouldn't be called on
	 * satisfied problem.
	 * @return unsatisfied clause
	 */
	public Clause getUnsatisfiedClause() {
		// Lazy evaluation... yay!
		try {
			return clauses.stream()
				.filter(clause -> !clause.satisfied())
				.findFirst().get();
		} catch (NoSuchElementException e) {
			throw new RuntimeException("No unsatisfied clause. This method should not be called");
		}
	}

	/**
	 * Picks least damaging variable to flip from the clause according to
	 * a simple heuristics. We pick that particular variable that when flipped
	 * would cause least currently satisfied clauses to become unsatisfied.
	 *
	 * IMPORTANT: Assumes isSatisfied() to be called prior to this function
	 * as the method is using cached results for speed up.
	 * @param clause list of variable we're choosing from
	 * @return variable that when flipped would cause least damage
	 */
	public Variable pickLeastDamagingVariable(Clause clause) {
		// Get list of clauses that are currently satisfied (we're using the
		// cached Clause#satisfied method to speed up the process)
		// TODO: pick only those that contain current variables
		List<Clause> satisfied = clauses.stream()
			.filter(Clause::satisfied).collect(Collectors.toList());

		// Remember the best result
		Variable bestVariable = null;
		long bestResult = -1;
		long result;

		// Go through the all variables, try to flip each one of them and see
		// how many of those originally satisfied clauses became unsatisfied.
		// We're trying to find the variable that when flipped would cause the
		// least clauses to become unsatisfied.
		for (Variable variable : clause.variables) {
			variable.flip();

			// Get number of clauses that remained satisfied (now we use the
			// uncached call Clause#isSatisfied to enforce updating).
			result = satisfied.stream().filter(Clause::isSatisfied).count();
			if (result >= bestResult) {
				bestResult = result;
				bestVariable = variable;
			}
			variable.flip();
		}

		return bestVariable;
	}
}

/**
 * Immutable instance of a clause. Contains a list of literals and maintains a
 * set of all variables for quick access.
 */
class Clause {
	public final List<Literal> literals;
	public final Set<Variable> variables;
	private boolean satisfied = false;

	Clause(List<Literal> literals) {
		this.literals = literals;
		this.variables = literals.stream()
			.map(literal -> literal.variable)
			.collect(Collectors.toSet());
	}

	/**
	 * Return if at least one of the literals is satisfied. If the clause is
	 * empty, returns also true.
	 * @return true if the clause is satisfied
	 */
	public boolean isSatisfied() {
		if (isEmpty()) {
			return true;
		}
		this.satisfied = literals.stream().anyMatch(Literal::isSatisfied);
		return this.satisfied;
	}

	/**
	 * Return cached result of the last isSatisfied() call.
	 * @return true if the clause is satisfied
	 */
	public boolean satisfied() {
		return this.satisfied;
	}

	/**
	 * Pick randomly any variable from the clause.
	 * @return a randomly picked variable
	 */
	public Variable pickRandomVariable() {
		// Ugly O(n) way. Set should become something with random access
		int index = WalkSAT.random.nextInt(variables.size());
		Iterator<Variable> iter = variables.iterator();
		for (int i = 0; i < index; i++) {
			iter.next();
		}
		return iter.next();
	}

	/**
	 * Return if the clause is empty (zero literals)
	 * @return true if the clause is empty.
	 */
	public boolean isEmpty() {
		return literals.size() == 0;
	}

	// ***************** Unit propagation & purification *********************

	/**
	 * Return if the clause is unit clause, i. e. it contains only one literal.
	 * @return true if the clause is unit clause
	 */
	public boolean isUnit() {
		return literals.size() == 1;
	}

	/**
	 * Return true if the clause is unsatisfiable but only in the unit
	 * propagation context context. It doesn't actually check whether the
	 * clause is satisfiable, it returns false only if the clause is a unit
	 * clause, unsatisfied and the variable is frozen (cannot be flipped).
	 * Then it's clearly unsatisfiable.
	 * @return true if the clause is unsatisfiable
	 */
	public boolean isUnsatisfiable() {
		return isUnit() && !isSatisfied() && literals.get(0).isFrozen();
	}

	/**
	 * If this is a unit clause, try to set the satisfying variable value and
	 * freeze the variable.
	 * @return true if the clause was unit clause.
	 */
	public boolean freezeIfUnitClause() {
		if (!isUnit()) {
			return false;
		}

		Literal literal = literals.get(0);
		if (!literal.isSatisfied()) {
			if (!literal.isFrozen()) {
				literal.variable.flip();
			}
		}
		literal.variable.freeze();
		return true;
	}

	/**
	 * Remove all frozen literals from the clause. If the frozen literal is
	 * actually satisfied, it satisfies the clause and it is pointless to
	 * reason about the rest. As the clause is immutable, we have to return a
	 * new instance.
	 * @return new instance of clause with removed frozen variables
	 */
	public Clause removeFrozen() {
		// If any of the frozen literals is satisfied, the whole clause is
		// satisfied and we can remove it.
		if (literals.stream().filter(Literal::isFrozen).anyMatch(Literal::isSatisfied)) {
			return new Clause(new LinkedList<>());
		} else {
			List<Literal> pruned = literals.stream()
				.filter(literal -> !literal.isFrozen())
				.collect(Collectors.toList());
			return new Clause(pruned);
		}
	}
}

/**
 * Immutable instance of a literal. It can be negated or not. Contains single
 * variable (it pretty much works as a variable container)
 */
class Literal {
	public final Variable variable;
	public final boolean negated;

	Literal(Variable variable) {
		this(variable, false);
	}

	Literal(Variable variable, boolean negated) {
		this.variable = variable;
		this.negated = negated;
	}

	/**
	 * Return if the literal is satisfied based on the current variable value
	 * and the negation sign.
	 * @return true if the literal is satisfied
	 */
	public boolean isSatisfied() {
		return (variable.isSatisfied() && !negated) || (!variable.isSatisfied() && negated);
	}

	// ***************** Unit propagation & purification  *********************

	/**
	 * Return if the literal is frozen, i. e. the contained variable is frozen.
	 * @return true if the literal is frozen
	 */
	public boolean isFrozen() {
		return variable.isFrozen();
	}
}

/**
 * Variable representation. Contains truth value that can be changed.
 */
class Variable {
	public final String name;
	private boolean value;
	private boolean frozen = false;

	Variable(String name) {
		this(name, false);
	}

	Variable(String name, boolean value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * If the value is true
	 * @return value
	 */
	public boolean isSatisfied() {
		return getValue();
	}

	public boolean getValue() {
		return value;
	}

	/**
	 * Tries to set a new value. If the variable is frozen, it will fail with
	 * a RuntimeException (i. e. before setting you should always check that
	 * the variable is not frozen)
	 * @param value value to be set
	 */
	public void setValue(boolean value) {
		if (frozen) {
			throw new RuntimeException("Cannot set value on a frozen variable!");
		}
		this.value = value;
	}

	/**
	 * Flips the truth value (false to true or true to false)
	 */
	public void flip() {
		setValue(!value);
	}

	/**
	 * Randomly picks a new truth value.
	 */
	public void pickRandomValue() {
		setValue(WalkSAT.random.nextBoolean());
	}

	// ***************** Unit propagation & purification *********************

	/**
	 * Freeze variable which will prevent any further changes (it becomes
	 * immutable)
	 */
	public void freeze() {
		frozen = true;
	}

	/**
	 * Returns if the variable is frozen.
	 * @return true if its frozen.
	 */
	public boolean isFrozen() {
		return frozen;
	}
}
