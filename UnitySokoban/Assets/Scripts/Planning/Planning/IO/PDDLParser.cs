using Planning.Logic;
using System;
using System.Collections.Generic;
using System.IO;

namespace Planning.IO
{
    /**
 * Reads and parsed PDDL files to convert them into objects.
 * 
 * @author Stephen G. Ware
 * @ported Edward Thomas Garcia
 */
    public class PDDLParser
    {
        /** The keyword used to identify a domain */
        protected static readonly string DOMAIN_KEYWORD = ":domain";

        /** The keyword used to identify a domain's constants */
        protected static readonly string CONSTANTS_KEYWORD = ":constants";

        /** The keyword used to identify an operator */
        protected static readonly string OPERATOR_KEYWORD = ":action";

        /** The keyword used to identify an operator's parameters */
        protected static readonly string PARAMETERS_KEYWORD = ":parameters";

        /** The keyword used to identify an operator's precondition */
        protected static readonly string PRECONDITION_KEYWORD = ":precondition";

        /** The keyword used to identify an operator's effect */
        protected static readonly string EFFECT_KEYWORD = ":effect";

        /** The keyword used to identify a problem */
        protected static readonly string PROBLEM_KEYWORD = ":problem";

        /** The keyword used to identify a problem's objects */
        protected static readonly string OBJECTS_KEYWORD = ":objects";

        /** The keyword used to identify a problem's initial state */
        protected static readonly string INITIAL_STATE_KEYWORD = ":initial";

        /** The keyword used to identify a problem's goal */
        protected static readonly string GOAL_KEYWORD = ":goal";

        /** The keyword used to identify a negation */
        protected static readonly string NEGATION_KEYWORD = "not";

        /** The keyword used to identify a conjunction */
        protected static readonly string CONJUNCTION_KEYWORD = "and";

        /** The keyword used to identify a disjunction */
        protected static readonly string DISJUNCTION_KEYWORD = "or";

        /** A map of object parsers that handle objects of a specific type */
        //private HashMap<Class<?>, ObjectParser<?>> parsers = new HashMap<>();

        /** A map of objects that are defined in the current scope */
        private Dictionary<string, Object> defined = new Dictionary<string, object>();

        /**
         * Constructs a new parser.
         */
        public PDDLParser()
        {
            //register(Domain.class, DOMAIN_PARSER);
            //register(Constant[].class, CONSTANTS_PARSER);
            //register(Operator.class, OPERATOR_PARSER);
            //register(Variable[].class, PARAMETER_PARSER);
            //register(Problem.class, PROBLEM_PARSER);
            //register(Term.class, TERM_PARSER);
            //register(Constant.class, CONSTANT_PARSER);
            //register(Variable.class, VARIABLE_PARSER);
            //register(Expression.class, EXPRESSION_PARSER);
            //register(Literal.class, LITERAL_PARSER);
            //register(Negation.class, NEGATION_PARSER);
            //register(NegatedLiteral.class, NEGATED_LITERAL_PARSER);
            //register(Conjunction.class, CONJUNCTION_PARSER);
            //register(Predication.class, PREDICATION_PARSER);
        }

        /**
         * Registers an {@link edu.uno.ai.planning.io.ObjectParser}
         * as the means of parsing a given king of object.
         * 
         * @param type the type of object this parser parses
         * @param parser the object parser
         * @param <E> the type of object this parser parses
         */
        protected void register<T>(ObjectParser<T> parser)
        {
            //parsers.put(typeof(T), parser);
        }

        /**
         * Returns the {@link edu.uno.ai.planning.io.ObjectParser}
         * responsible for parsing objects of the given type.
         * 
         * @param type the object type
         * @param <E> the object type
         * @return the object parser for that type of object
         */
        //protected ObjectParser<T> getParser<T>()
        //{
        //    return (ObjectParser<E>)parsers.get(type);
        //}

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
        public T parse<T>(string file)
        {
            return parse<T>(Node.parse(file));
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
        protected T parse<T>(Node node)
        {
            if (node == null)
                return default(T);

            TypeParser<T> parser = TypeParser<T>.CreateParser();
            if (parser == null)
                throw new ArgumentException("No method exists for parsing \"" + typeof(T) + "\"");
            //HashMap < Class <?>, ObjectParser <?>> parserBackup = (HashMap < Class <?>, ObjectParser <?>>) parsers.clone();
            Dictionary<string, Object> definedBackup = new Dictionary<string, Object>(defined);
            T obj = default(T);
            try
            {
                obj = parser.parse(node, this);
                if (obj == null)
                    throw new FormatException("Failed to parse \"" + node + "\" as " + typeof(T));
            }
            finally
            {
                //    parsers = parserBackup;
                defined = definedBackup;
            }
            if (obj is Domain)
                define((obj as Domain).name, obj);
            if (obj is Problem)
                define((obj as Problem).name, obj);
            return obj;
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
        protected T tryParse<T>(Node node)
        {
            try
            {
                return parse<T>(node);
            }
            catch (FormatException ex)
            {
                if (ex != null) ex = null; // Stupid thing to get rid of warning.
                return default(T);
            }
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
        protected T[] parseAll<T>(Node node)
        {
            if (node == null)
                return new T[0];
            List<T> objects = new List<T>();
            while (node != null)
            {
                objects.Add(parse<T>(node));
                node = node.next;
            }
            return objects.ToArray();
        }

        /**
         * Defines an object by a given name.
         * 
         * @param name the object's name
         * @param object the object
         */
        protected void define(string name, Object obj)
        {
            defined.Add(name, obj);
        }

        /**
         * Defines a set of constants by their names.
         * 
         * @param constants the constants to define
         */
        protected void defineAll(params Constant[] constants)
        {
            foreach (Constant constant in constants)
                defined.Add(constant.name, constant);
        }

        /**
         * Defines a set of constants by their names.
         * 
         * @param constants the constants to define
         */
        protected void defineAll(IEnumerable<Constant> constants)
        {
            foreach (Constant constant in constants)
                defined.Add(constant.name, constant);
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
        protected T get<T>(string name)
        {
            Object obj = defined[name];
            if (obj == null)
                return default(T);
            else if (obj is T)
                return (T)obj;
            else
                throw new FormatException("No " + typeof(T) + " named \"" + name + "\" defined");
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
        protected T require<T>(string name)
        {
            Object obj = get<T>(name);
            if (obj == null)
                throw new FormatException("No " + typeof(T) + " named \"" + name + "\" defined");
            else
                return (T)obj;
        }

        class TypeParser<T>
        {
            static public TypeParser<T> CreateParser()
            {
                if (typeof(T) == typeof(Domain))
                    return new DomainParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Constant[]))
                    return new ConstantArrayParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Operator))
                    return new OperatorParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Variable[]))
                    return new VariableArrayParser() as TypeParser<T>;

                else if (typeof(T) == typeof(Problem))
                    return new ProblemParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Term))
                    return new TermParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Constant))
                    return new ConstantParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Variable))
                    return new VariableParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Expression))
                    return new ExpressionParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Literal))
                    return new LiteralParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Negation))
                    return new NegationParser() as TypeParser<T>;
                else if (typeof(T) == typeof(NegatedLiteral))
                    return new NegatedLiteralParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Conjunction))
                    return new ConjunctionParser() as TypeParser<T>;
                else if (typeof(T) == typeof(Predication))
                    return new PredicationParser() as TypeParser<T>;

                return null;
            }

            public virtual T parse(Node node, PDDLParser parser)
            {
                return default(T);
            }

            /** Parses domains */
            class DomainParser : TypeParser<Domain>
            {
                public override Domain parse(Node node, PDDLParser parser)
                {
                    node.asList(2, -1).first.asSymbol(DOMAIN_KEYWORD);
                    string name = node.asList().first.next.asSymbol().value;
                    Node constantsList = node.asList().find(CONSTANTS_KEYWORD);
                    Constant[] constants = new Constant[0];
                    if (constantsList != null)
                        constants = parser.parse<Constant[]>(constantsList);
                    parser.defineAll(constants);
                    List<Operator> ops = new List<Operator>();
                    Node child = node.asList().first;
                    while (child != null)
                    {
                        if (child.isList() && child.asList().first.isSymbol(OPERATOR_KEYWORD))
                            ops.Add(parser.parse<Operator>(child));
                        child = child.next;
                    }
                    Operator[] operators = ops.ToArray();
                    return new Domain(name, constants, operators);
                }
            }

            /* Parses a list of constants */
            class ConstantArrayParser : TypeParser<Constant[]>
            {
                public override Constant[] parse(Node node, PDDLParser parser)
                {
                    List<Constant> constants = new List<Constant>();
                    node = node.asList(2, -1).first.next;
                    while (node != null)
                    {
                        if (node.asSymbol().value.StartsWith("?"))
                            throw new FormatException("Constant must not begin with \"?\"");
                        string name = node.asSymbol().value;
                        string type = Settings.DEFAULT_TYPE;
                        if (node.next != null && node.next.isSymbol("-"))
                        {
                            node = node.next.requireNext();
                            type = node.asSymbol().value;
                        }
                        constants.Add(new Constant(type, name));
                        node = node.next;
                    }
                    return constants.ToArray();
                }
            }

            /** Parses operators */
            class OperatorParser : TypeParser<Operator>
            {
                public override Operator parse(Node node, PDDLParser parser)
                {
                    node.asList(8, 8).first.asSymbol(OPERATOR_KEYWORD);
                    string name = node.asList().first.next.asSymbol().value;
                    Variable[] parameters = parser.parse<Variable[]>(node.asList().get(PARAMETERS_KEYWORD));
                    foreach (Variable parameter in parameters)
                        parser.define(parameter.name, parameter);
                    Expression precondition = parser.parse<Expression>(node.asList().get(PRECONDITION_KEYWORD));
                    Expression effect = parser.parse<Expression>(node.asList().get(EFFECT_KEYWORD));
                    return new Operator(name, parameters, precondition, effect);
                }
            }

            /** Parses a list of parameters */
            class VariableArrayParser : TypeParser<Variable[]>
            {
                public override Variable[] parse(Node node, PDDLParser parser)
                {
                    List<Variable> parameters = new List<Variable>();
                    node = node.asList().first;
                    while (node != null)
                    {
                        if (!node.asSymbol().value.StartsWith("?"))
                            throw new FormatException("Parameter must begin with \"?\"");
                        string name = node.asSymbol().value.Substring(1);
                        string type = Settings.DEFAULT_TYPE;
                        if (node.next != null && node.next.isSymbol("-"))
                        {
                            node = node.next.requireNext();
                            type = node.asSymbol().value;
                        }
                        parameters.Add(new Variable(type, name));
                        node = node.next;
                    }
                    return parameters.ToArray();
                }
            }

            /** Parses problems */
            class ProblemParser : TypeParser<Problem>
            {
                public override Problem parse(Node node, PDDLParser parser)
                {
                    node.asList(3, -1).first.asSymbol(PROBLEM_KEYWORD);
                    string name = node.asList().first.next.asSymbol().value;
                    Domain domain = parser.require<Domain>(node.asList().first.next.next.asList(2, 2).first.next.asSymbol().value);
                    parser.defineAll(domain.constants);
                    List objectsList = node.asList().find(OBJECTS_KEYWORD);
                    Constant[] objects = new Constant[0];
                    if (objectsList != null)
                        objects = parser.parse<Constant[]>(objectsList);
                    parser.defineAll(objects);
                    List<Constant> obj = new List<Constant>();
                    foreach (Constant d in domain.constants)
                        obj.Add(d);
                    foreach (Constant p in objects)
                        obj.Add(p);
                    objects = obj.ToArray();
                    MutableState initial = new MutableState();
                    parser.parse<Expression>(node.asList().find(INITIAL_STATE_KEYWORD).first.next).Impose(initial);
                    Expression goal = parser.parse<Expression>(node.asList().find(GOAL_KEYWORD).first.next);
                    return new Problem(name, domain, objects, initial, goal);
                }
            }

            /** Parses terms */
            class TermParser : TypeParser<Term>
            {
                public override Term parse(Node node, PDDLParser parser)
                {
                    Term term = parser.tryParse<Constant>(node);
                    if (term == null)
                        term = parser.tryParse<Variable>(node);
                    return term;
                }
            }

            /** Parses constants */
            class ConstantParser : TypeParser<Constant>
            {
                public override Constant parse(Node node, PDDLParser parser)
                {
                    if (node.asSymbol().value.StartsWith("?"))
                        throw new FormatException("Constant cannot begin with \"?\"");
                    return parser.require<Constant>(node.asSymbol().value);
                }
            }

            /** Parses variables */
            class VariableParser : TypeParser<Variable>
            {
                public override Variable parse(Node node, PDDLParser parser)
                {
                    if (!node.asSymbol().value.StartsWith("?"))
                        throw new FormatException("Variable must begin with \"?\"");
                    return parser.require<Variable>(node.asSymbol().value.Substring(1));
                }
            }

            /** Parses expressions */
            class ExpressionParser : TypeParser<Expression>
            {
                public override Expression parse(Node node, PDDLParser parser)
                {
                    Expression exp = parser.tryParse<Negation>(node);
                    if (exp == null)
                        exp = parser.tryParse<Conjunction>(node);
                    if (exp == null)
                        exp = parser.tryParse<Literal>(node);
                    return exp;
                }
            }

            /** Parses literals */
            class LiteralParser : TypeParser<Literal>
            {
                public override Literal parse(Node node, PDDLParser parser)
                {
                    Literal lit = parser.tryParse<NegatedLiteral>(node);
                    if (lit == null)
                        lit = parser.tryParse<Predication>(node);
                    return lit;
                }
            }

            /** Parses negations */
            class NegationParser : TypeParser<Negation>
            {
                public override Negation parse(Node node, PDDLParser parser)
                {
                    NegatedLiteral nl = parser.tryParse<NegatedLiteral>(node);
                    if (nl != null)
                        return nl;
                    node = node.asList(2, 2).requireFirst();
                    node.asSymbol(NEGATION_KEYWORD);
                    return new Negation(parser.parse<Expression>(node.next));
                }
            }

            /** Parses negated literals */
            class NegatedLiteralParser : TypeParser<NegatedLiteral>
            {
                public override NegatedLiteral parse(Node node, PDDLParser parser)
                {
                    node = node.asList(2, 2).requireFirst();
                    node.asSymbol(NEGATION_KEYWORD);
                    return new NegatedLiteral(parser.parse<Literal>(node.next));
                }
            }

            /** Parses conjunctions */
            class ConjunctionParser : TypeParser<Conjunction>
            {
                public override Conjunction parse(Node node, PDDLParser parser)
                {
                    node = node.asList(2, -1).requireFirst();
                    node.asSymbol(CONJUNCTION_KEYWORD);
                    return new Conjunction(parser.parseAll<Expression>(node.next));

                }
            }

            /** Parses predications */
            class PredicationParser : TypeParser<Predication>
            {
                public override Predication parse(Node node, PDDLParser parser)
                {
                    node = node.asList().requireFirst();
                    string predicate = node.asSymbol().value;
                    return new Predication(predicate, parser.parseAll<Term>(node.next));
                }
            }
        }
    }
}