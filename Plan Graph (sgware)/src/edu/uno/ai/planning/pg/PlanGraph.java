package edu.uno.ai.planning.pg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NAryBooleanExpression;
import edu.uno.ai.planning.ss.StateSpaceProblem;

public class PlanGraph {

	public final StateSpaceProblem problem;
	public final Iterable<LiteralNode> goals;
	public final boolean mutexes;
	protected final LinkedHashMap<Literal, LiteralNode> literalMap = new LinkedHashMap<>();
	protected final LiteralNode[] literals;
	protected final LinkedHashMap<Step, StepNode> stepMap = new LinkedHashMap<>();
	protected final StepNode[] steps;
	final ArrayList<Node> toReset = new ArrayList<>();
	final ArrayList<StepNode> nextSteps = new ArrayList<>();
	private final ArrayList<Level> levels = new ArrayList<>();
	private int size = 0;
	private boolean leveledOff = false;
	
	public PlanGraph(StateSpaceProblem problem, boolean mutexes) {
		this.problem = problem;
		this.mutexes = mutexes;
		for(Step step : problem.steps)
			addEdgesForStep(new StepNode(this, step));
		ArrayList<Literal> literals = new ArrayList<>(this.literalMap.size());
		literals.addAll(this.literalMap.keySet());
		for(Literal literal : literals)
			addEdgesForStep(new StepNode(this, literal));
		ArrayList<LiteralNode> goals = new ArrayList<>();
		forEachLiteral(problem.goal.toDNF(), literal -> {
			goals.add(getLiteralNode(literal));
		});
		this.goals = goals;
		this.levels.add(new Level(this, 0));
		this.literals = this.literalMap.values().toArray(new LiteralNode[this.literalMap.size()]);
		this.steps = this.stepMap.values().toArray(new StepNode[this.stepMap.size()]);
		if(mutexes)
			computeStaticMutexes();
	}
	
	public PlanGraph(Problem problem, boolean mutexes) {
		this(new StateSpaceProblem(problem), mutexes);
	}
	
	private final void addEdgesForStep(StepNode stepNode) {
		stepMap.put(stepNode.step, stepNode);
		forEachLiteral(stepNode.step.precondition.toDNF(), literal -> {
			LiteralNode literalNode = getLiteralNode(literal);
			literalNode.consumers.add(stepNode);
			stepNode.preconditions.add(literalNode);
		});
		forEachLiteral(stepNode.step.effect.toDNF(), literal -> {
			LiteralNode literalNode = getLiteralNode(literal);
			stepNode.effects.add(literalNode);
			literalNode.producers.add(stepNode);
		});
	}
	
	private final void forEachLiteral(Expression expression, Consumer<Literal> consumer) {
		if(expression instanceof Literal)
			consumer.accept((Literal) expression);
		else
			for(Expression argument : ((NAryBooleanExpression) expression).arguments)
				forEachLiteral(argument, consumer);
	}
	
	private final LiteralNode getLiteralNode(Literal literal) {
		LiteralNode literalNode = literalMap.get(literal);
		if(literalNode == null) {
			literalNode = new LiteralNode(this, literal);
			literalMap.put(literal, literalNode);
		}
		return literalNode;
	}
	
	private final void computeStaticMutexes() {
		// A literal is always mutex with its negation.
		for(LiteralNode literalNode : literals) {
			LiteralNode negation = get(literalNode.literal.negate());
			if(negation != null)
				literalNode.mutexes.add(negation, Mutexes.ALWAYS);
		}
		// Compute static mutexes for all pairs of steps.
		for(int i=0; i<steps.length; i++) {
			for(int j=i; j<steps.length; j++) {
				if(alwaysMutex(steps[i], steps[j])) {
					steps[i].mutexes.add(steps[j], Mutexes.ALWAYS);
					steps[j].mutexes.add(steps[i], Mutexes.ALWAYS);
				}
			}
		}
	}
	
	private final boolean alwaysMutex(StepNode s1, StepNode s2) {
		// Inconsistent effects: steps which undo each others' effects are always mutex.
		for(LiteralNode s1Effect : s1.effects) {
			Literal negation = s1Effect.literal.negate();
			for(LiteralNode s2Effect : s2.effects)
				if(s2Effect.literal.equals(negation))
					return true;
		}
		// Interference: steps which undoe each other's preconditions are always mutex.
		if(s1 == s2)
			return false;
		if(interference(s1, s2) || interference(s2, s1))
			return true;
		return false;
	}
	
	private final boolean interference(StepNode s1, StepNode s2) {
		for(LiteralNode s1Effect : s1.effects) {
			Literal negation = s1Effect.literal.negate();
			for(LiteralNode s2Precondition : s2.preconditions)
				if(s2Precondition.literal.equals(negation))
					return true;
		}
		return false;
	}
	
	public LiteralNode get(Literal literal) {
		return literalMap.get(literal);
	}
	
	public StepNode get(StepNode step) {
		return stepMap.get(step);
	}
	
	public void initialize(State initial) {
		size = 1;
		for(Node node : toReset)
			node.reset();
		toReset.clear();
		for(LiteralNode node : literalMap.values())
			if(initial.isTrue(node.literal))
				node.setLevel(0);
		leveledOff = nextSteps.size() == 0;
	}
	
	public void extend() {
		Level level = new Level(this, size);
		if(levels.size() == size)
			levels.add(level);
		size++;
		addStep(0);
		if(mutexes)
			level.computeMutexes();
		if(nextSteps.size() == 0)
			leveledOff = true;
	}
	
	private final void addStep(int index) {
		if(index == nextSteps.size())
			nextSteps.clear();
		else {
			StepNode step = nextSteps.get(index);
			addStep(index + 1);
			step.setLevel(size - 1);
		}
	}
	
	public int size() {
		return size;
	}
	
	public Level getLevel(int number) {
		if(number < 0 || number >= size)
			throw new IndexOutOfBoundsException("Level " + number + " does not exist.");
		return levels.get(number);
	}
	
	public boolean goalAchieved() {
		for(LiteralNode literal : goals)
			if(literal.getLevel() == -1)
				return false;
		return true;
	}
	
	public boolean hasLeveledOff() {
		return leveledOff;
	}
	
	public List<LiteralNode> getLiteralNodes(){
		ArrayList<LiteralNode> nodes = new ArrayList<>();
		Iterator<LiteralNode> iterator = literalMap.values().iterator();
		while(iterator.hasNext()){
			nodes.add((LiteralNode)iterator.next());
		}
		return nodes;
	}

	public List<StepNode> getStepNodes(){
		return Arrays.asList(steps);
	}
}
