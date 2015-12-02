package edu.uno.ai.planning.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.DurativeOperator;
import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.logic.Variable;
import edu.uno.ai.planning.util.ImmutableArray;

public class TemporalParser extends Parser {

	/** The keyword used to identify a durative operator */
	protected static final String DURATIVE_OPERATOR_KEYWORD = ":durative-action";

	/** The keyword used to identify an durative operator's duration */
	protected static final String DURATION_KEYWORD = ":duration";

	/** The keyword used to identify an durative operator's condition */
	protected static final String CONDITION_KEYWORD = ":condition";
	
	/** The keyword used to identify a durative start condition/effect */
	protected static final String START_KEYWORD = "at start";
	
	/** The keyword used to identify a durative end condition/effect */
	protected static final String END_KEYWORD = "at end";
	
	/** The keyword used to identify a durative overall condition/effect */
	protected static final String OVERALL_KEYWORD = "over all";

	/**
	 * Constructs a new parser that will account for 
	 * durative properties within actions.
	 */
	public TemporalParser() {
		super();
		register(Integer.class, DURATION_VALUE_PARSER);
		register(Domain.class, DOMAIN_PARSER);
		register(DurativeOperator[].class, DURATIVE_OPERATOR_PARSER);
	}
	
	@Override
	public <E> E parse(File file, Class<E> type) throws IOException {
		return parse(Node.parse(file), type);
	}

	/** Parses durative operators */
	private static final ObjectParser<DurativeOperator[]> DURATIVE_OPERATOR_PARSER = new ObjectParser<DurativeOperator[]>() {

		@Override
		public DurativeOperator[] parse(Node node, Parser parser) {
			// Get Durative Operator consistent values
			node.asList(10, 10).first.asSymbol(DURATIVE_OPERATOR_KEYWORD);
			String name = node.asList().first.next.asSymbol().value;
			Integer duration = parser.parse(node.asList().get(DURATION_KEYWORD), Integer.class);
			Variable[] parameters = parser.parse(node.asList().get(PARAMETERS_KEYWORD), Variable[].class);
			for(Variable parameter : parameters)
				parser.define(parameter.name, parameter);
			DurativeOperator[] durativeOps = new DurativeOperator[3];
			durativeOps[0] = getDurativeStartOp(parser, name, duration, parameters, node.asList().get(CONDITION_KEYWORD), node.asList().get(EFFECT_KEYWORD));
			// create durative invariant op
			durativeOps[1] = getDurativeInvariantOp(parser, name, duration, parameters, node.asList().get(CONDITION_KEYWORD), node.asList().get(EFFECT_KEYWORD));
			// create durative end op
			durativeOps[2] = getDurativeEndOp(parser, name, duration, parameters, node.asList().get(CONDITION_KEYWORD), node.asList().get(EFFECT_KEYWORD));
			
			return durativeOps;
		}
	};
	
	/** Parses operators */
	private static final ObjectParser<Integer> DURATION_VALUE_PARSER = new ObjectParser<Integer>() {

		@Override
		public Integer parse(Node node, Parser parser) {
			node.asList(3, 3).first.asSymbol().value.equals("=");
			node.asList(3, 3).first.next.asSymbol().value.equals("?duration");
			return Integer.parseInt(node.asList(3, 3).first.next.next.asSymbol().value);
		}
	};
	
	/** Parses domains */
	/**
	 * Override Domain Parser to handle durative operations
	 */
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
				if(child.isList()){
					Node firstChildNode = child.asList().first; 
					if(firstChildNode.isSymbol(OPERATOR_KEYWORD))
						ops.add(parser.parse(child, Operator.class));
					// Handle Durative Actions
					else if(firstChildNode.isSymbol(DURATIVE_OPERATOR_KEYWORD))
						for(DurativeOperator op : parser.parse(child, DurativeOperator[].class))
							ops.add(op);
						
				}
				child = child.next;
			}
			Operator[] operators = ops.toArray(new Operator[ops.size()]);
			return new Domain(name, constants, operators);
		}
	};
	
	/**
	 * Create Durative Start Action from names, duration, condition node, and effect node
	 * @param name - name for the durative action
	 * @param duration - value of the duration of the action
	 * @param conditionNode - Node containing the conditions for operation 
	 * @param effectNode - Node containing the effects for operation
	 * @return the start op for a durative action given the start elements of conditionNode 
	 * 		and effectNode nodes as well as the name, duration, and parameters of the durativeOperator 
	 */
	private static DurativeOperator getDurativeStartOp(Parser parser, String name, Integer duration, Variable[] parameters, Node conditionNode, Node effectNode){
		// get start preconditions from conditions
		Expression[] conditionsArr = parseDurativeExpression(parser,conditionNode,START_KEYWORD);
		ImmutableArray<Expression> iaConditions = new ImmutableArray<Expression>(conditionsArr);
		int i = 0;
		if(iaConditions.length == 0)
			i = i+ 1;
		Expression conditions = new Conjunction(iaConditions);
		// get start effects from effects
		Expression[] effectsArr = parseDurativeExpression(parser,effectNode,START_KEYWORD);
		Expression[] allEffectsArr = new Expression[effectsArr.length+1];
		System.arraycopy( effectsArr, 0, allEffectsArr, 0, effectsArr.length );
		// add invariant effect to start op
		allEffectsArr[allEffectsArr.length - 1] = new Predication(name+"-inv", new ImmutableArray<Term>(parameters));
		ImmutableArray<Expression> iaEffects = new ImmutableArray<Expression>(allEffectsArr);
		Expression effects = new Conjunction(iaEffects);
		DurativeOperator startOp = new DurativeOperator(name+"-start", duration, parameters, conditions, effects);
		return startOp;
	}
	
	private static DurativeOperator getDurativeInvariantOp(Parser parser, String name, Integer duration, Variable[] parameters, Node conditionNode, Node effectNode){
		// get invariant preconditions from conditions
		Expression[] conditionsArr = parseDurativeExpression(parser,conditionNode,OVERALL_KEYWORD); 
		Expression[] allConditionsArr = new Expression[conditionsArr.length+1];
		System.arraycopy( conditionsArr, 0, allConditionsArr, 0, conditionsArr.length );
		// add invariant precondition to invariant op
		allConditionsArr[allConditionsArr.length - 1] = new Predication(name+"-inv", new ImmutableArray<Term>(parameters));
		ImmutableArray<Expression> iaConditions = new ImmutableArray<Expression>(allConditionsArr);
		Expression conditions = new Conjunction(iaConditions);
		// get invariant effects from effects 
		Expression[] effectsArr = parseDurativeExpression(parser,conditionNode,OVERALL_KEYWORD);
		Expression[] allEffectsArr = new Expression[effectsArr.length + 2];
		System.arraycopy( effectsArr, 0, allEffectsArr, 0, effectsArr.length );
		allEffectsArr[allEffectsArr.length - 2] = new Predication(name+"-inv", new ImmutableArray<Term>(parameters));
		allEffectsArr[allEffectsArr.length - 1] = new Predication("i"+name+"-inv", new ImmutableArray<Term>(parameters));
		ImmutableArray<Expression> iaEffects = new ImmutableArray<Expression>(allEffectsArr);
		Expression effects = new Conjunction(iaEffects);
		DurativeOperator invariantOp = new DurativeOperator(name+"-inv", duration, parameters, conditions, effects);
		
		return invariantOp;
	}
	
	private static DurativeOperator getDurativeEndOp(Parser parser, String name, Integer duration, Variable[] parameters, Node conditionNode, Node effectNode){
		// get end preconditions from conditions
		Expression[] conditionsArr = parseDurativeExpression(parser,conditionNode,END_KEYWORD); 
		Expression[] allConditionsArr = new Expression[conditionsArr.length + 1];
		System.arraycopy( conditionsArr, 0, allConditionsArr, 0, conditionsArr.length );
		allConditionsArr[allConditionsArr.length - 1] = new Predication("i"+name+"-inv", new ImmutableArray<Term>(parameters));
		ImmutableArray<Expression> iaConditions = new ImmutableArray<Expression>(allConditionsArr);
		Expression conditions = new Conjunction(iaConditions); 
		// get end effects from effects
//		int i = 0;
//		if(name.equals("calibrate"))
//			i = i + 1;
		Expression[] effectsArr = parseDurativeExpression(parser,effectNode,END_KEYWORD);
		Expression[] allEffectsArr = new Expression[effectsArr.length + 2];
		System.arraycopy( effectsArr, 0, allEffectsArr, 0, effectsArr.length );
		Predication actionInv = new Predication(name+"-inv", new ImmutableArray<Term>(parameters));
		Predication iActionInv = new Predication("i"+name+"-inv", new ImmutableArray<Term>(parameters));
		allEffectsArr[allEffectsArr.length - 2] = actionInv.negate();
		allEffectsArr[allEffectsArr.length - 1] = iActionInv.negate();
		Expression effects = new Conjunction(allEffectsArr);
		DurativeOperator endOp = new DurativeOperator(name+"-end", duration, parameters, conditions, effects);
		
		return endOp;
	}
	
	private static Expression[] parseDurativeExpression(Parser parser, Node newNode, String durativeTime){
		Node node = newNode.asList().first;
		ArrayList<Expression> expressions = new ArrayList<Expression>();
		if((node instanceof Symbol) && !node.asSymbol().value.equals(CONJUNCTION_KEYWORD)){
			if(isCorrectDurativeTime(newNode, durativeTime)){
				Expression precondition = parser.parse(newNode.asList().first.next.next, Expression.class);
				expressions.add(precondition);
			}
		}else{
			while(node != null) {
				if( !(node instanceof Symbol) && isCorrectDurativeTime(node, durativeTime)){
					Expression precondition = parser.parse(node.asList(3,3).first.next.next, Expression.class);
					expressions.add(precondition);
				}
				node = node.next;
			}
		}
		Expression[] expArr = new Expression[expressions.size()];
		expArr = expressions.toArray(expArr);
		return expArr;
	}
	
	private static boolean isCorrectDurativeTime(Node node, String durativeTime){
		String[] keys = durativeTime.split(" ");
		return node.asList(3, 3).first.asSymbol().value.equals(keys[0]) && node.asList(3, 3).first.next.asSymbol().value.equals(keys[1]);
	}
	
}
