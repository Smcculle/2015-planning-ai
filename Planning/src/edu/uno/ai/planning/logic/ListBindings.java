package edu.uno.ai.planning.logic;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the {@link Bindings} data structure based on a linked
 * list, which is easy to clone and modify but provides slower lookups.
 *
 * An instance of ListBindings is also actually a node in the linked list. One
 * node contains information about (at most) one constant, the list of variables
 * that are equal to this constant (they are constrained to codesignate) and the
 * list of variables that are not allowed to be equal to the constant (they are
 * constrained not to codesignate with any of the variables in the former list).
 *
 * Each update will create a new node at the beginning of the list containing
 * the most recent information. That means that eventually some constrains will
 * be duplicated within the list which, however, doesn't break the functionality
 * (only the newest nodes are taken into account) and has only small performance
 * and memory impact.
 *
 * From the public API perspective, ListBindings behaves like a compact
 * data structure. On the inside, recursive search through the whole list is
 * used to obtain required information.
 *
 * @author Tobiáš Potoček <tobiaspotocek@gmail.com>
 */
public class ListBindings implements Bindings {

	/** The constant that should substitute variables in cdSet */
	protected final Constant constant;

	/** The list of variables that are constrained to codesignate */
	protected final Set<Variable> cdSet;

	/**
	 * The list of variables and constants that are constrained not to
	 * codesignate with any of the variables in the cdSet.
	 */
	protected final Set<Term> ncdSet;

	/** The next binding node in the list */
	protected final ListBindings next;

	/** Default constructor for creating an empty bindings */
	public ListBindings() {
		constant = null;
		cdSet = new HashSet<>();
		ncdSet = new HashSet<>();
		next = null;
	}

	/**
	 * Initialize new bindings node
	 * @param next the next node in the linked list
	 * @param constant the constant that should substitute variables in cdSet
	 * @param cdSet variables constrained to codesignate
	 * @param ncdSet variables constrained not to codesignate
	 */
	public ListBindings(ListBindings next, Constant constant,
						Set<Variable> cdSet, Set<Term> ncdSet) {
		this.constant = constant;
		this.cdSet = cdSet;
		this.ncdSet = ncdSet;
		this.next = next;
	}

	@Override
	public Term get(Term term) {
		if (cdSet.contains(term)) {
			return constant == null ? term : constant;
		} else if (next != null) {
			return next.get(term);
		} else {
			return term;
		}
	}

	@Override
	public Bindings setEqual(Term t1, Term t2) {
		if (t1 instanceof Constant && t2 instanceof Constant) {
			return setEqualTwoConstants((Constant) t1, (Constant) t2);
		} else if (t1 instanceof Variable && t2 instanceof Variable) {
			return setEqualTwoVariables((Variable) t1, (Variable) t2);
		} else {
			if (t1 instanceof Variable && t2 instanceof Constant) {
				return setEqualVariableAndConstant((Variable) t1, (Constant) t2);
			} else if (t2 instanceof Variable && t1 instanceof Constant) {
				return setEqualVariableAndConstant((Variable) t2, (Constant) t1);
			} else {
				throw new IllegalArgumentException("Unknown Term type!");
			}
		}
	}

	@Override
	public Bindings setNotEqual(Term t1, Term t2) {
		if (t1 instanceof Constant && t2 instanceof Constant) {
			return setNotEqualTwoConstants((Constant) t1, (Constant) t2);
		} else if (t1 instanceof Variable && t2 instanceof Variable) {
			return setNotEqualTwoVariables((Variable) t1, (Variable) t2);
		} else {
			if (t1 instanceof Variable && t2 instanceof Constant) {
				return setNotEqualVariableAndConstant((Variable) t1, (Constant) t2);
			} else if (t2 instanceof Variable && t1 instanceof Constant) {
				return setNotEqualVariableAndConstant((Variable) t2, (Constant) t1);
			} else {
				throw new IllegalArgumentException("Unknown Term type!");
			}
		}
	}

	/**
	 * Find bindings node that contains given variable in its cdSet. Performs
	 * recursive search within the whole list.
	 * @param v variable we are looking for
	 * @return bindings node or null if not found
	 */
	protected ListBindings findBindingForVariable(Variable v) {
		if (cdSet.contains(v)) {
			return this;
		} else {
			return next == null ? null : next.findBindingForVariable(v);
		}
	}

	/**
	 * Find bindings node with given constant. Performs recursive search within
	 * the whole list.
	 * @param c constant we are looking for
	 * @return ListBindings node or null if not found
	 */
	protected ListBindings findBindingForConstant(Constant c) {
		if (constant == c) {
			return this;
		} else {
			return next == null ? null : next.findBindingForConstant(c);
		}
	}

	/**
	 * Constrain two variables to be the same, if possible.
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @return a new set of bindings or null in case of failure.
	 */
	private ListBindings setEqualTwoVariables(Variable v1, Variable v2) {
		ListBindings b1 = findBindingForVariable(v1);
		ListBindings b2 = findBindingForVariable(v2);

		if (b1 == null) {
			b1 = createBindingsWithCdSet(v1);
		}
		if (b2 == null) {
			b2 = createBindingsWithCdSet(v2);
		}

		return mergeCdSets(b1, b2);
	}

	/**
	 * Constrain two constant to be the same, if possible.
	 *
	 * This method exists only for completeness. Whether or not the two given
	 * constants are the same is their intrinsic feature. Two different
	 * constants cannot be the same (we return null) and if those two constants
	 * are actually just one constant, we don't need to store that fact in our
	 * set (we just return this).
	 * @param c1 the first constant
	 * @param c2 the second constant
	 * @return this if the constants are the same, null if not
	 */
	private ListBindings setEqualTwoConstants(Constant c1, Constant c2) {
		return c1 == c2 ? this : null;
	}

	/**
	 * Constrain a constant and a variable to be the same, if possible. This
	 * also means that the variable v will be substituted by the constant c.
	 * @param v the variable
	 * @param c the constant
	 * @return a new set of bindings or null in case of failure
	 */
	private ListBindings setEqualVariableAndConstant(Variable v, Constant c) {
		ListBindings bv = findBindingForVariable(v);
		ListBindings bc = findBindingForConstant(c);

		if (bv == null) {
			bv = createBindingsWithCdSet(v);
		}
		if (bc == null) {
			bc = createBindingsWithCdSet(c);
		}

		return mergeCdSets(bv, bc);
	}

	/**
	 * Combine cdSets from the given two bindings nodes and add it as a new node
	 * to the beginning of our linked list.
	 * @param b1 the first bindings node
	 * @param b2 the second bindings node
	 * @return a new set of bindings or null in case of failure
	 */
	private ListBindings mergeCdSets(ListBindings b1, ListBindings b2) {
		if (b1 == b2) {
			return this;
		} else if (b1.constant != null && b2.constant != null) {
			if (b1.constant == b2.constant) {
				throw new IllegalStateException("Same constant in two different bindings!");
			}
			return null;
		} else {
			HashSet<Variable> newCdSet = new HashSet<>(b1.cdSet);
			newCdSet.addAll(b2.cdSet);

			HashSet<Term> newNcdSet = new HashSet<>(b1.ncdSet);
			newNcdSet.addAll(b2.ncdSet);

			if (intersection(newCdSet, newNcdSet).size() > 0) {
				return null;
			} else {
				return new ListBindings(this,
						b1.constant != null ? b1.constant : b2.constant,
						newCdSet, newNcdSet);
			}
		}
	}

	/**
	 * Constrain two variables not to be the same, if possible.
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @return a new set of bindings or null in case of failure.
	 */
	private ListBindings setNotEqualTwoVariables(Variable v1, Variable v2) {
		ListBindings b1 = findBindingForVariable(v1);
		ListBindings b2 = findBindingForVariable(v2);

		if (b1 == null) {
			b1 = createBindingsWithCdSet(v1);
		}
		if (b2 == null) {
			b2 = createBindingsWithCdSet(v2);
		}

		if (b1.cdSet.contains(v2) || b2.cdSet.contains(v1)) {
			return null;
		} else {
			return updateNcdSets(b1, b2, v1, v2);
		}
	}

	/**
	 * Constrain two constants not to be the same, if possible.
	 *
	 * This method exists only for completeness. Whether or not the two given
	 * constants are the same is their intrinsic feature. If the are the same
	 * constant, they cannot equal (we return null), if they are not the same,
	 * we return this as we don't have to store that information in our bindings
	 * set.
	 * @param c1 the first constant
	 * @param c2 the second constant
	 * @return this if the constant are different, null otherwise
	 */
	private ListBindings setNotEqualTwoConstants(Constant c1, Constant c2) {
		return c1 == c2 ? null : this;
	}

	/**
	 * Constrain a variable and a constant not to be the same, if possible.
	 * @param v the variable
	 * @param c the constant
	 * @return a new set of bindings or null in case of failure.
	 */
	private ListBindings setNotEqualVariableAndConstant(Variable v, Constant c) {
		ListBindings bv = findBindingForVariable(v);
		ListBindings bc = findBindingForConstant(c);

		if (bv == null) {
			bv = createBindingsWithCdSet(v);
		}
		if (bc == null) {
			bc = createBindingsWithCdSet(c);
		}

		if (bv.constant == c) {
			return null;
		} else {
			return updateNcdSets(bv, bc, v, c);
		}
	}

	/**
	 * Add the term t1 to the ncdSet of the bindings node b2 and add the term t2
	 * to the ncdSet of the bindings node b1. Add both updated nodes to the
	 * beginning of our linked list.
	 * @param b1 the bindings node containing t1
	 * @param b2 the bindings node containing t2
	 * @param t1 the first term, either a constant or a variable
	 * @param t2 the second therm, either a constant or a variable
	 * @return a new set of bindings or null in case of failure.
	 */
	private ListBindings updateNcdSets(ListBindings b1, ListBindings b2,
									   Term t1, Term t2) {
		if (b1.ncdSet.contains(t2) && b2.ncdSet.contains(t1)) {
			return this;
		} else {
			HashSet<Variable> b1CdSet = new HashSet<>(b1.cdSet);
			HashSet<Term> b1NcdSet = new HashSet<>(b1.ncdSet);
			b1NcdSet.add(t2);

			HashSet<Variable> b2CdSet = new HashSet<>(b2.cdSet);
			HashSet<Term> b2NcdSet = new HashSet<>(b2.ncdSet);
			b2NcdSet.add(t1);

			return new ListBindings(
					new ListBindings(this, b2.constant, b2CdSet, b2NcdSet),
					b1.constant, b1CdSet, b1NcdSet
			);
		}
	}

	/**
	 * Create empty bindings node with the constant c set.
	 * @param c the constant to be set to the bindings node
	 * @return new bindings node
	 */
	private ListBindings createBindingsWithCdSet(Constant c) {
		return new ListBindings(null, c, new HashSet<>(), new HashSet<>());
	}

	/**
	 * Create empty bindings node with the variable v in its cdSet.
	 * @param v the variable to be added to the cdSet
	 * @return new bindings node
	 */
	private ListBindings createBindingsWithCdSet(Variable v) {
		HashSet<Variable> cdSet = new HashSet<>();
		cdSet.add(v);
		return new ListBindings(null, null, cdSet, new HashSet<>());
	}

	/**
	 * Get terms that are both in the cdSet and in the ncdSet.
	 * @param cdSet the cdSet to be searched
	 * @param ncdSet the ncdSet to be searched
	 * @return set of terms found in both input sets
	 */
	private Set intersection(Set<Variable> cdSet, Set<Term> ncdSet) {
		HashSet<Term> intersection = new HashSet<>(ncdSet);
		intersection.retainAll(cdSet);
		return intersection;
	}
}
