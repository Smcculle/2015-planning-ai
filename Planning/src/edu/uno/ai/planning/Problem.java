package edu.uno.ai.planning;

import java.util.ArrayList;

import edu.uno.ai.planning.logic.Bindings;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

/**
 * A planning problem exists in some {@link Domain} and defines the initial
 * state of the world and a goal to be achieved.
 * 
 * @author Stephen G. Ware
 */
public class Problem {

	/** The name of the problem */
	public final String name;
	
	/** The domain in which this problem exists */
	public final Domain domain;
	
	/** All the objects that exist in the world */
	public final ImmutableArray<Constant> objects;
	
	/** The initial state of the world, which specifies the disposition of every object */
	public final State initial;
	
	/** Some goal that needs to be achieved by any solution to this problem */
	public final Expression goal;
	
	/**
	 * Constructs a new problem.
	 * 
	 * @param name the name of the problem
	 * @param domain the domain in which it exists
	 * @param objects the objects in the world
	 * @param initial the initial state
	 * @param goal the goal
	 */
	public Problem(String name, Domain domain, ImmutableArray<Constant> objects, State initial, Expression goal) {
		this.name = name;
		this.domain = domain;
		this.objects = objects;
		this.initial = initial;
		this.goal = goal;
	}
	
	/**
	 * Constructs a new problem.
	 * 
	 * @param name the name of the problem
	 * @param domain the domain in which it exists
	 * @param objects the objects in the world
	 * @param initial the initial state
	 * @param goal the goal
	 */
	public Problem(String name, Domain domain, Constant[] objects, State initial, Expression goal) {
		this(name, domain, new ImmutableArray<>(objects), initial, goal);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	/**
	 * Checks if a given plan is a solution to this problem.
	 * 
	 * @param plan the plan to test
	 * @return true if the plan is a solution to the problem, false otherwise
	 */
	public boolean isSolution(Plan plan) {
		MutableState current = new MutableState(initial);
		for(Step step : plan) {
			if(step.precondition.isTrue(current))
				step.effect.impose(current);
			else
				return false;
		}
		return goal.isTrue(current);
	}
	
	/**
	 * Returns all the objects in the world which are of a given type.
	 * 
	 * @param type the type of objects to return
	 * @return the (possibly empty) set of objects
	 */
	public Iterable<Constant> getObjectsByType(String type) {
		Variable variable = new Variable(type, "variable");
		ArrayList<Constant> objects = new ArrayList<>();
		for(Constant object : this.objects)
			if(variable.unify(object, Bindings.EMPTY) != null)
				objects.add(object);
		return objects;
	}
	
	@Override
	public String toString() {
		return "[" + name + " in " + domain.name + "]";
	}
}
