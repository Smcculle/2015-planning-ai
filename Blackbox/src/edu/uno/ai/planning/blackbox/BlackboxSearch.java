package edu.uno.ai.planning.blackbox;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.logic.*;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.Node;
import edu.uno.ai.planning.pg.PlanGraph;
import edu.uno.ai.planning.pg.StepNode;
import edu.uno.ai.planning.util.ImmutableArray;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
		Set<StepNode> stepsInLevel;
		Set<LiteralNode> literalsInLevel;
		List<Conjunction> conjunctions = new LinkedList<>();

		// TODO 1: initial state
		System.out.println(graph.problem.initial.toExpression());
		Expression expression = graph.problem.initial.toExpression();
		// ((Conjunction) graph.problem.initial.toExpression()).arguments.forEach(argument -> System.out.println(argument.getClass()));

		for (int i = 1; i <= graph.size(); i++) {
			final int level = i;
			System.out.println("============ Level " + level + " ============");
			stepsInLevel = new HashSet<>();
			literalsInLevel = new HashSet<>();

			for (LiteralNode goal : graph.goals) {
				if (goal.exists(level)) {
					// TODO 2: each fact implies disjunction of all steps that have this fact as effect
					conjunctions.add(implySteps(goal, level));
					literalsInLevel.add(goal);
					goal.getProducers(level).forEach(stepsInLevel::add);
				}
			}

			// TODO 3: operators imply their preconditions
			stepsInLevel.forEach(step -> implyPreconditions(step, level));

			// TODO 4: mutexes
			makeMutexes((Set<Node>)(Set<?>) literalsInLevel, level);
			makeMutexes((Set<Node>)(Set<?>) stepsInLevel, level);

			System.out.println();
		}

		Expression[] arguments = conjunctions.stream()
			.flatMap(conjunction -> asStream(conjunction.arguments))
			.toArray(Expression[]::new);
		for (Expression argument : arguments) {
			System.out.println(argument);
		}
		// System.out.println(new Conjunction(new ImmutableArray<>(arguments)));
		// System.out.println((new Conjunction(new ImmutableArray<>(conjunction, Expression.class))).toCNF());

		throw new SearchLimitReachedException();
	}

	protected Conjunction implySteps(LiteralNode node, int level) {
		// Fact
		Predication fact = new Predication(makeName(node, level));

		// List of steps that produce given fact
		Expression[] arguments = asStream(node.getProducers(level))
			.map(step -> new Predication(makeName(step, level)))
			.toArray(Expression[]::new);
		Expression steps = arguments.length > 1 ? new Disjunction(new ImmutableArray<>(arguments)) : arguments[0];

		System.out.println("Fact: " + fact + " => " + steps);
		// System.out.println(NormalForms.isCNF(makeImplication(fact, steps)));
		System.out.println(makeImplication(fact, steps).toCNF());
		return (Conjunction) makeImplication(fact, steps).toCNF();
	}

	protected void implyPreconditions(StepNode step, int level) {
		System.out.println(makeName(step, level));
		System.out.println("Implied preconditions: ");
		step.getPreconditions(level).forEach(literal -> System.out.println(makeName(literal, level - 1)));
		System.out.println();
	}

	protected void makeMutexes(Set<Node> nodes, int level) {
		nodes.forEach(node1 -> nodes.forEach(node2 -> {
			if (node1.mutex(node2, level)) {
				System.out.println("Mutex: " + makeName(node1, level) + " " + makeName(node2, level));
			}
		}));
	}

	protected Expression makeImplication(Expression a, Expression b) {
		return new Disjunction(new Negation(a), b);
	}

	protected String makeName(Node node, int level) {
		return level + "|" + node;
	}

	protected <T> Stream<T> asStream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
}
