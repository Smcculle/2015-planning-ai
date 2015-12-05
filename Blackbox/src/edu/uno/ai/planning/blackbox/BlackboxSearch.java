package edu.uno.ai.planning.blackbox;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.SATPlan.*;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.Node;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.pg.StepNode;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BlackboxSearch extends Search {

	public final PlanGraph graph;

	public BlackboxSearch(PlanGraph graph) {
		super(graph.problem);
		this.graph = graph;
	}

	@Override
	public int countVisited() {
		return 0;
	}

	@Override
	public int countExpanded() {
		return 0;
	}

	@Override
	public void setNodeLimit(int limit) {

	}

	@Override
	public Plan findNextSolution() {
		System.out.println("Plan size " + graph.size());
		System.out.println("Plan leveled off " + graph.hasLeveledOff());
		ArrayList<Clause> conjunction = new ArrayList<>();
		Map<String, Step> stepInstances = new HashMap<>();

		List<LiteralNode> literals = graph.getLiteralNodes();
		List<StepNode> steps =  graph.getStepNodes();

		// (1) Initial state facts at the first level and goals at the last
		conjunction.addAll(makeInitState(graph.problem.initial));
		asStream(graph.goals).forEach(goal ->
			conjunction.add(new Clause(variable(name(goal, graph.size() - 1)))));

		for (int i = 1; i < graph.size(); i++) {
			final int level = i;
			List<LiteralNode> literalsInLevel = literals.stream().filter(literal -> literal.exists(level)).collect(Collectors.toList());
			List<StepNode> stepsInLevel = steps.stream().filter(step -> step.exists(level)).collect(Collectors.toList());

			// (2) Each fact implies disjunction of all steps that have this fact as an effect.
			literalsInLevel.forEach(literal ->
				conjunction.addAll(implySteps(literal, level)));

			// (3) Each step implies all its preconditions
			stepsInLevel.forEach(step -> {
				conjunction.addAll(implyPreconditions(step, level));

				// Remember instance mapping to actual steps so that we can
				// later generate the solution.
				stepInstances.put(name(step, level), step.step);
			});

			// (4) Mutually exclusive steps and facts
			conjunction.addAll(makeMutexes((List<Node>)(List<?>) literalsInLevel, level));
			conjunction.addAll(makeMutexes((List<Node>)(List<?>) stepsInLevel, level));
		}

		SATProblem problem = new SATProblem((ArrayList<ArrayList<BooleanVariable>>)(ArrayList<?>) conjunction, new ArrayList<>());
		ISATSolver solver = new WalkSAT(10, 1000, 0.5);
		List<BooleanVariable> solution = solver.getModel(problem);

		if (solution == null) {
			if (graph.hasLeveledOff()) {
				throw new SearchLimitReachedException();
			}
			return null;
		} else {
			return makePlan(solution, stepInstances);
		}
	}

	protected Plan makePlan(List<BooleanVariable> solution, Map<String,Step> stepInstances) {
		Set<String> uniqueSteps = solution.stream()
			.filter(var -> var.value) // remove variables that are false
			.filter(var -> !var.negation) // remove negations
			.map(var -> var.name)
			.collect(Collectors.toSet());

		return new ListPlan(uniqueSteps.stream()
			.sorted()
			.map(stepInstances::get)
			.filter(step -> step != null)
			.filter(graph.problem.steps::contains)
			.collect(Collectors.toList()));

	}

	protected ArrayList<Clause> makeInitState(State state) {
		Expression expression = state.toExpression();
		if (expression instanceof Conjunction) {
			return new ArrayList<>(asStream(((Conjunction) expression).arguments)
				.map(arg -> new Clause(variable(name(arg, 0))))
				.collect(Collectors.toList()));
		} else if (expression instanceof Predication) {
			return new ArrayList<>(Collections.singletonList(new Clause(variable(name(expression, 0)))));
		} else {
			throw new IllegalArgumentException("Initial state in weird format.");
		}
	}

	/**
	 * Imply steps that have given fact as an effect on given level.
	 * 	(effect) => (step1) v (step2) v (step3)
	 * 	~(effect) v (step1) v (step2) v (step3)
	 *
	 * @param fact that is produced
	 * @param level current level
	 * @return list of clauses
	 */
	protected ArrayList<Clause> implySteps(LiteralNode fact, final int level) {
		// Add steps that produce given fact
		Clause clause = new Clause(asStream(fact.getProducers(level))
			.map(step -> variable(name(step, level)))
			.toArray(BooleanVariable[]::new));

		// Add fact
		clause.add(negation(variable(name(fact, level))));

		return new ArrayList<>(Collections.singletonList(clause));
	}

	/**
	 * Imply preconditions of a step fact on a given level
	 * 	(step) => (precondition1) & (precondition2) & (precondition3)
	 * 	(step) => (precondition1) & (step) => (precondition2) & ~(step) => (precondition3)
	 * 	(~(step) v (precondition1)) & (~(step) v (precondition2)) & (~(step) v (precondition3))
	 *
	 * @param step we want preconditions for
	 * @param level current level
	 */
	protected ArrayList<Clause> implyPreconditions(StepNode step, final int level) {
		return new ArrayList<>(asStream(step.getPreconditions(level))
			.map(precondition ->
				implication(variable(name(step, level)), variable(name(precondition, level - 1))))
			.collect(Collectors.toList()));
	}

	/**
	 * Make mutex for each pair of nodes.
	 * @param nodes mutually exclusive nodes
	 * @param level current level
	 */
	protected ArrayList<Clause> makeMutexes(List<Node> nodes, final int level) {
		ArrayList<Clause> mutexes = new ArrayList<>();
		Node[] array = nodes.toArray(new Node[nodes.size()]);

		for (int i = 0; i < array.length; i++) {
			for (int j = i + 1; j < array.length; j++) {
				if (array[i].mutex(array[j], level)) {
					mutexes.add(new Clause(
						negation(variable(name(array[i], level))),
						negation(variable(name(array[j], level)))
					));
				}
			}
		}

		return mutexes;
	}

	protected Clause implication(BooleanVariable a, BooleanVariable b) {
		return new Clause(negation(a), b);
	}

	protected BooleanVariable negation(BooleanVariable variable) {
		return new BooleanVariable(variable.name, variable.value, !variable.negation);
	}

	protected BooleanVariable variable(String name) {
		return new BooleanVariable(name, null, false);
	}

	protected String name(Expression expression, int level) {
		return level + "|" + expression;
	}

	protected String name(Node node, int level) {
		return level + "|" + node;
	}

	protected <T> Stream<T> asStream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	class Clause extends ArrayList<BooleanVariable> {
		public Clause(BooleanVariable... variables) {
			super(Arrays.asList(variables));
		}

		public String toString() {
			return String.join(" v ", stream()
				.map(var -> (var.negation ? "~" : "") + var.name)
				.collect(Collectors.toList()));
		}
	}

	class ListPlan extends ArrayList<Step> implements Plan {
		public ListPlan(Collection<Step> steps) {
			super(steps);
		}
	}
}
