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

	/** List of pure variables in the conjunction with the value fixed */
	protected Set<Variable> pures;

	/** List of variables in the original SAT problem */
	protected List<BooleanVariable> originalVariables;

	public WalkSAT(int maxTries, int maxFlips, double randomPickProbability) {
		this.maxTries = maxTries;
		this.maxFlips = maxFlips;
		this.randomPickProbability = randomPickProbability;
	}

	@Override
	public List<BooleanVariable> getModel(final SATProblem satProblem) {
		Problem problem = convertProblem(satProblem);
		Clause unsatisfiedClause;
		Variable variable;

		// Remove pure literals from the conjunction. If it turns out impossible,
		// return null (as a failure)
		problem = purify(problem);
		if (problem == null) {
			return null;
		}

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
					variable = problem.pickLeastDamagingVariable(unsatisfiedClause);
				}

				variable.flip();
			}
		}
		return null;
	}

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

	protected List<BooleanVariable> convertSolution(Set<Variable> solution) {
		Map<String, Variable> lookupTable = new HashMap<>();
		solution.forEach(variable -> lookupTable.put(variable.name, variable));
		if (pures != null) {
			pures.forEach(variable -> lookupTable.put(variable.name, variable));
		}

		for (BooleanVariable boolVar : originalVariables) {
			Variable var = lookupTable.get(boolVar.name);
			if (var == null) {
				throw new RuntimeException("Missing value for variable " + boolVar.name);
			}
			boolVar.value = var.getValue();
		}
		return originalVariables;
	}

	protected Problem purify(Problem problem) {
		List<Clause> clauses = problem.clauses;

		while (clauses.stream().anyMatch(Clause::findPureAndFreeze)) {
			if (clauses.stream().filter(Clause::isUnsatisfiable).count() > 0) {
				return null;
			}
			clauses = clauses.stream()
				.map(Clause::removeFrozen)
				.filter(clause -> !clause.isEmpty())
				.collect(Collectors.toList());
		}

		pures = problem.variables.stream()
			.filter(Variable::isFrozen)
			.collect(Collectors.toSet());
		return new Problem(clauses);
	}

	protected boolean shouldPickRandomly() {
		return random.nextDouble() < randomPickProbability;
	}
}

class Problem {
	public final List<Clause> clauses;
	public final Set<Variable> variables;

	Problem(List<Clause> clauses) {
		this.clauses = clauses;
		this.variables = clauses.stream()
			.flatMap(clause -> clause.variables.stream())
			.collect(Collectors.toSet());
	}

	public boolean isSatisfied() {
		return clauses.stream().allMatch(Clause::isSatisfied);
	}

	public void randomSolution() {
		variables.forEach(Variable::pickRandomValue);
	}

	public Set<Variable> getSolution() {
		return variables;
	}

	public Clause getUnsatisfiedClause() {
		// Lazy evaluation... yay!
		try {
			return clauses.stream()
				.filter(clause -> !clause.satisfied())
				.findAny().get();
		} catch (NoSuchElementException e) {
			throw new RuntimeException("No unsatisfied clause. This method should not be called");
		}
	}

	/**
	 * IMPORTANT: Assumes isSatisfied() to be called prior to this function
	 * as the method is using cached results.
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

	public boolean isSatisfied() {
		if (isEmpty()) {
			return true;
		}
		this.satisfied = literals.stream().anyMatch(Literal::isSatisfied);
		return this.satisfied;
	}

	public boolean satisfied() {
		return this.satisfied;
	}

	public Variable pickRandomVariable() {
		// Ugly O(n) way. Set should become something with random accet.
		int index = WalkSAT.random.nextInt(variables.size());
		Iterator<Variable> iter = variables.iterator();
		for (int i = 0; i < index; i++) {
			iter.next();
		}
		return iter.next();
	}

	public boolean isEmpty() {
		return literals.size() == 0;
	}

	// Purification stuff...

	public boolean isPure() {
		return literals.size() == 1;
	}

	public boolean isUnsatisfiable() {
		return isPure() && !isSatisfied() && literals.get(0).isFrozen();
	}

	public boolean findPureAndFreeze() {
		if (!isPure()) {
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

	public boolean isSatisfied() {
		return (variable.isSatisfied() && !negated) || (!variable.isSatisfied() && negated);
	}

	// Purification stuff...

	public boolean isFrozen() {
		return variable.isFrozen();
	}
}

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

	public boolean isSatisfied() {
		return getValue();
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		if (frozen) {
			throw new RuntimeException("Cannot set value on a frozen variable!");
		}
		this.value = value;
	}

	public void flip() {
		setValue(!value);
	}

	public void pickRandomValue() {
		setValue(WalkSAT.random.nextBoolean());
	}

	// Purification stuff...

	public void freeze() {
		frozen = true;
	}

	public boolean isFrozen() {
		return frozen;
	}
}
