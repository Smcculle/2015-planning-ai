package edu.uno.ai.planning.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uno.ai.planning.*;
import edu.uno.ai.planning.logic.*;

/**
 * Reads and parsed PDDL files to convert them into objects.
 * 
 * @author Stephen G. Ware
 */
public class Parser {

	/** The keyword used to identify a domain */
	protected static final String DOMAIN_KEYWORD = ":domain";
	
	/** The keyword used to identify a domain's constants */
	protected static final String CONSTANTS_KEYWORD = ":constants";
	
	/** The keyword used to identify an operator */
	protected static final String OPERATOR_KEYWORD = ":action";
	
	/** The keyword used to identify an operator's parameters */
	protected static final String PARAMETERS_KEYWORD = ":parameters";
	
	/** The keyword used to identify an operator's precondition */
	protected static final String PRECONDITION_KEYWORD = ":precondition";
	
	/** The keyword used to identify an operator's effect */
	protected static final String EFFECT_KEYWORD = ":effect";
	
	/** The keyword used to identify a problem */
	protected static final String PROBLEM_KEYWORD = ":problem";
	
	/** The keyword used to identify a problem's objects */
	protected static final String OBJECTS_KEYWORD = ":objects";
	
	/** The keyword used to identify a problem's initial state */
	protected static final String INITIAL_STATE_KEYWORD = ":initial";
	
	/** The keyword used to identify a problem's goal */
	protected static final String GOAL_KEYWORD = ":goal";
	
	/** The keyword used to identify a negation */
	protected static final String NEGATION_KEYWORD = "not";
	
	/** The keyword used to identify a conjunction */
	protected static final String CONJUNCTION_KEYWORD = "and";
	
	/** The keyword used to identify a disjunction */
	protected static final String DISJUNCTION_KEYWORD = "or";
	
	/** A map of object parsers that handle objects of a specific type */
	private HashMap<Class<?>, ObjectParser<?>> parsers = new HashMap<>();
	
	/** A map of objects that are defined in the current scope */
	private HashMap<String, Object> defined = new HashMap<>();
	
	/**
	 * Constructs a new parser.
	 */
	public Parser() {
		register(Domain.class, DOMAIN_PARSER);
		register(Constant[].class, CONSTANTS_PARSER);
		register(Operator.class, OPERATOR_PARSER);
		register(Variable[].class, PARAMETER_PARSER);
		register(Problem.class, PROBLEM_PARSER);
		register(Term.class, TERM_PARSER);
		register(Constant.class, CONSTANT_PARSER);
		register(Variable.class, VARIABLE_PARSER);
		register(Expression.class, EXPRESSION_PARSER);
		register(Literal.class, LITERAL_PARSER);
		register(Negation.class, NEGATION_PARSER);
		register(NegatedLiteral.class, NEGATED_LITERAL_PARSER);
		register(Conjunction.class, CONJUNCTION_PARSER);
		register(Predication.class, PREDICATION_PARSER);
	}
	
	/**
	 * Registers an {@link edu.uno.ai.planning.io.ObjectParser}
	 * as the means of parsing a given king of object.
	 * 
	 * @param type the type of object this parser parses
	 * @param parser the object parser
	 * @param <E> the type of object this parser parses
	 */
	protected <E> void register(Class<E> type, ObjectParser<E> parser) {
		parsers.put(type, parser);
	}
	
	/**
	 * Returns the {@link edu.uno.ai.planning.io.ObjectParser}
	 * responsible for parsing objects of the given type.
	 * 
	 * @param type the object type
	 * @param <E> the object type
	 * @return the object parser for that type of object
	 */
	@SuppressWarnings("unchecked")
	protected <E> ObjectParser<E> getParser(Class<E> type) {
		return (ObjectParser<E>) parsers.get(type);
	}
	
	/**
	 * Reads and parses a given file as the given type of object.
	 * 
	 * @param file the file to read
	 * @param type the type of object to construct
	 * @param <E> the type of object to construct
	 * @return the object
	 * @throws IOException if an IO exception occurs while reading the file
	 * @throws FormatException if the file is not properly formatted
	 */
	public <E> E parse(File file, Class<E> type) throws IOException {
		return parse(Node.parse(file), type);
	}
	
	/**
	 * Parses a given node as the given type of object.  Any changes made to
	 * the parser during parsing (e.g. defining objects) will be undone before
	 * this method returns.  One exception applies: any time a domain or
	 * problem is parsed, those objects become permanently defined for this
	 * parser.
	 * 
	 * @param node the node to parse
	 * @param type the type of object to construct
	 * @param <E> the type of the object to construct
	 * @return the object
	 * @throws FormatException if the node is not properly formatted
	 */
	@SuppressWarnings("unchecked")
	protected <E> E parse(Node node, Class<E> type) {
		if(node == null)
			return null;
		ObjectParser<E> parser = getParser(type);
		if(parser == null)
			throw new IllegalArgumentException("No method exists for parsing \"" + type.getName() + "\"");
		HashMap<Class<?>, ObjectParser<?>> parserBackup = (HashMap<Class<?>, ObjectParser<?>>) parsers.clone();
		HashMap<String, Object> definedBackup = (HashMap<String, Object>) defined.clone();
		E object = null;
		try {
			object = parser.parse(node, this);
			if(object == null)
				throw new FormatException("Failed to parse \"" + node + "\" as " + type.getSimpleName());
		}
		finally {
			parsers = parserBackup;
			defined = definedBackup;
		}
		if(object instanceof Domain)
			define(((Domain) object).name, object);
		if(object instanceof Problem)
			define(((Problem) object).name, object);
		return object;
	}
	
	/**
	 * Attempts to parse a node as a given kind of object, but if that parsing
	 * fails, this method returns null instead of throwing an exception.
	 * 
	 * @param node the node to parse
	 * @param type the type of object to construct
	 * @param <E> the type of object to construct
	 * @return the object or null if parsing failed
	 */
	protected <E> E tryParse(Node node, Class<E> type) {
		try {
			return parse(node, type);
		}
		catch(FormatException ex) {
			// do nothing
		}
		return null;
	}
	
	/**
	 * Starting with a given node, this method parsers that node and all its
	 * next siblings as a given type of object and returns an arry of those
	 * objects.
	 * 
	 * @param node the first node to parse
	 * @param type the type of object to construct
	 * @param <E> the type of object to construct
	 * @return an array of objects
	 * @throws FormatException if any node is not properly formatted
	 */
	@SuppressWarnings("unchecked")
	protected <E> E[] parseAll(Node node, Class<E> type) {
		if(node == null)
			return (E[]) Array.newInstance(type, 0);
		ArrayList<E> objects = new ArrayList<>();
		while(node != null) {
			objects.add(parse(node, type));
			node = node.next;
		}
		return objects.toArray((E[]) Array.newInstance(type, objects.size()));
	}
	
	/**
	 * Defines an object by a given name.
	 * 
	 * @param name the object's name
	 * @param object the object
	 */
	protected void define(String name, Object object) {
		defined.put(name, object);
	}
	
	/**
	 * Defines a set of constants by their names.
	 * 
	 * @param constants the constants to define
	 */
	protected void defineAll(Constant...constants) {
		for(Constant constant : constants)
		defined.put(constant.name, constant);
	}
	
	/**
	 * Defines a set of constants by their names.
	 * 
	 * @param constants the constants to define
	 */
	protected void defineAll(Iterable<Constant> constants) {
		for(Constant constant : constants)
		defined.put(constant.name, constant);
	}
	
	/**
	 * Returns an object defined by the given name and of the given type if
	 * such an object exists.
	 * 
	 * @param name the name of the defined object
	 * @param type the type of the defined object
	 * @param <E> the type of the defined object
	 * @return the defined object, or null if no such object is defined
	 */
	@SuppressWarnings("unchecked")
	protected <E> E get(String name, Class<E> type) {
		Object object = defined.get(name);
		if(object == null)
			return null;
		else if(type.isAssignableFrom(object.getClass()))
			return (E) object;
		else
			throw new FormatException("No " + type.getSimpleName() + " named \"" + name + "\" defined");
	}
	
	/**
	 * Returns an object defined by the given name and of the given type. If no
	 * such object exists, an exception is thrown.
	 * 
	 * @param name the name of the defined object
	 * @param type the type of the defined object
	 * @param <E> the type of the defined object
	 * @return the defined object
	 * @throws FormatException if no such object is defined
	 */
	@SuppressWarnings("unchecked")
	protected <E> E require(String name, Class<E> type) {
		Object object = get(name, type);
		if(object == null)
			throw new FormatException("No " + type.getSimpleName() + " named \"" + name + "\" defined");
		else
			return (E) object;
	}
	
	/** Parses domains */
	private static final ObjectParser<Domain> DOMAIN_PARSER = new ObjectParser<Domain>() {

		@Override
		public Domain parse(Node node, Parser parser) {
			node.asList(2, -1).first.asSymbol(DOMAIN_KEYWORD);
			String name = node.asList().first.next.asSymbol().value;
			Node constantsList = node.asList().find(CONSTANTS_KEYWORD);
			Constant[] constants = new Constant[0];
			if(constantsList != null)
				constants = parser.parse(constantsList, Constant[].class);
			parser.defineAll(constants);
			ArrayList<Operator> ops = new ArrayList<>();
			Node child = node.asList().first;
			while(child != null) {
				if(child.isList() && child.asList().first.isSymbol(OPERATOR_KEYWORD))
					ops.add(parser.parse(child, Operator.class));
				child = child.next;
			}
			Operator[] operators = ops.toArray(new Operator[ops.size()]);
			return new Domain(name, constants, operators);
		}
	};
	
	/** Parses constants */
	private static final ObjectParser<Constant[]> CONSTANTS_PARSER = new ObjectParser<Constant[]>() {

		@Override
		public Constant[] parse(Node node, Parser parser) {
			ArrayList<Constant> constants = new ArrayList<>();
			node = node.asList(2, -1).first.next;
			while(node != null) {
				if(node.asSymbol().value.startsWith("?"))
					throw new FormatException("Constant must not begin with \"?\"");
				String name = node.asSymbol().value;
				String type = Settings.DEFAULT_TYPE;
				if(node.next != null && node.next.isSymbol("-")) {
					node = node.next.requireNext();
					type = node.asSymbol().value;
				}
				constants.add(new Constant(type, name));
				node = node.next;
			}
			return constants.toArray(new Constant[constants.size()]);
		}
	};
	
	/** Parses operators */
	private static final ObjectParser<Operator> OPERATOR_PARSER = new ObjectParser<Operator>() {

		@Override
		public Operator parse(Node node, Parser parser) {
			node.asList(8, 8).first.asSymbol(OPERATOR_KEYWORD);
			String name = node.asList().first.next.asSymbol().value;
			Variable[] parameters = parser.parse(node.asList().get(PARAMETERS_KEYWORD), Variable[].class);
			for(Variable parameter : parameters)
				parser.define(parameter.name, parameter);
			Expression precondition = parser.parse(node.asList().get(PRECONDITION_KEYWORD), Expression.class);
			Expression effect = parser.parse(node.asList().get(EFFECT_KEYWORD), Expression.class);
			return new Operator(name, parameters, precondition, effect);
		}
	};
	
	/** Parses a list of parameters */
	private static final ObjectParser<Variable[]> PARAMETER_PARSER = new ObjectParser<Variable[]>() {

		@Override
		public Variable[] parse(Node node, Parser parser) {
			ArrayList<Variable> parameters = new ArrayList<>();
			node = node.asList().first;
			while(node != null) {
				if(!node.asSymbol().value.startsWith("?"))
					throw new FormatException("Parameter must begin with \"?\"");
				String name = node.asSymbol().value.substring(1);
				String type = Settings.DEFAULT_TYPE;
				if(node.next != null && node.next.isSymbol("-")) {
					node = node.next.requireNext();
					type = node.asSymbol().value;
				}
				parameters.add(new Variable(type, name));
				node = node.next;
			}
			return parameters.toArray(new Variable[parameters.size()]);
		}
	};
	
	/** Parses problems */
	private static final ObjectParser<Problem> PROBLEM_PARSER = new ObjectParser<Problem>() {

		@Override
		public Problem parse(Node node, Parser parser) {
			node.asList(3, -1).first.asSymbol(PROBLEM_KEYWORD);
			String name = node.asList().first.next.asSymbol().value;
			Domain domain = parser.require(node.asList().first.next.next.asList(2, 2).first.next.asSymbol().value, Domain.class);
			parser.defineAll(domain.constants);
			List objectsList = node.asList().find(OBJECTS_KEYWORD);
			Constant[] objects = new Constant[0];
			if(objectsList != null)
				objects = parser.parse(objectsList, Constant[].class);
			parser.defineAll(objects);
			ArrayList<Constant> obj = new ArrayList<>();
			for(Constant d : domain.constants)
				obj.add(d);
			for(Constant p : objects)
				obj.add(p);
			objects = obj.toArray(new Constant[obj.size()]);
			MutableState initial = new MutableState();
			parser.parse(node.asList().find(INITIAL_STATE_KEYWORD).first.next, Expression.class).impose(initial);
			Expression goal = parser.parse(node.asList().find(GOAL_KEYWORD).first.next, Expression.class);
			return new Problem(name, domain, objects, initial, goal);
		}
	};
	
	/** Parses terms */
	private static final ObjectParser<Term> TERM_PARSER = new ObjectParser<Term>() {

		@Override
		public Term parse(Node node, Parser parser) {
			Term term = parser.tryParse(node, Constant.class);
			if(term == null)
				term = parser.tryParse(node, Variable.class);
			return term;
		}
	};
	
	/** Parses constants */
	private static final ObjectParser<Constant> CONSTANT_PARSER = new ObjectParser<Constant>() {

		@Override
		public Constant parse(Node node, Parser parser) {
			if(node.asSymbol().value.startsWith("?"))
				throw new FormatException("Constant cannot begin with \"?\"");
			return parser.require(node.asSymbol().value, Constant.class);
		}
	};
	
	/** Parses variables */
	private static final ObjectParser<Variable> VARIABLE_PARSER = new ObjectParser<Variable>() {

		@Override
		public Variable parse(Node node, Parser parser) {
			if(!node.asSymbol().value.startsWith("?"))
				throw new FormatException("Variable must begin with \"?\"");
			return parser.require(node.asSymbol().value.substring(1), Variable.class);
		}
	};
	
	/** Parses expressions */
	private static final ObjectParser<Expression> EXPRESSION_PARSER = new ObjectParser<Expression>() {

		@Override
		public Expression parse(Node node, Parser parser) {
			Expression exp = parser.tryParse(node, Negation.class);
			if(exp == null)
				exp = parser.tryParse(node, Conjunction.class);
			if(exp == null)
				exp = parser.tryParse(node, Literal.class);
			return exp;
		}
	};
	
	/** Parses literals */
	private static final ObjectParser<Literal> LITERAL_PARSER = new ObjectParser<Literal>() {

		@Override
		public Literal parse(Node node, Parser parser) {
			Literal lit = parser.tryParse(node, NegatedLiteral.class);
			if(lit == null)
				lit = parser.tryParse(node, Predication.class);
			return lit;
		}
	};
	
	/** Parses negations */
	private static final ObjectParser<Negation> NEGATION_PARSER = new ObjectParser<Negation>() {

		@Override
		public Negation parse(Node node, Parser parser) {
			NegatedLiteral nl = parser.tryParse(node, NegatedLiteral.class);
			if(nl != null)
				return nl;
			node = node.asList(2, 2).requireFirst();
			node.asSymbol(NEGATION_KEYWORD);
			return new Negation(parser.parse(node.next, Expression.class));
		}
	};
	
	/** Parses negated literals */
	private static final ObjectParser<NegatedLiteral> NEGATED_LITERAL_PARSER = new ObjectParser<NegatedLiteral>() {

		@Override
		public NegatedLiteral parse(Node node, Parser parser) {
			node = node.asList(2, 2).requireFirst();
			node.asSymbol(NEGATION_KEYWORD);
			return new NegatedLiteral(parser.parse(node.next, Literal.class));
		}
	};
	
	/** Parses conjunctions */
	private static final ObjectParser<Conjunction> CONJUNCTION_PARSER = new ObjectParser<Conjunction>() {

		@Override
		public Conjunction parse(Node node, Parser parser) {
			node = node.asList(2, -1).requireFirst();
			node.asSymbol(CONJUNCTION_KEYWORD);
			return new Conjunction(parser.parseAll(node.next, Expression.class));
		}
	};
	
	/** Parses predications */
	private static final ObjectParser<Predication> PREDICATION_PARSER = new ObjectParser<Predication>() {

		@Override
		public Predication parse(Node node, Parser parser) {
			node = node.asList().requireFirst();
			String predicate = node.asSymbol().value;
			return new Predication(predicate, parser.parseAll(node.next, Term.class));
		}
	};
}
