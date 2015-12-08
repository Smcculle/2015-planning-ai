package edu.uno.ai.planning.jjsatplan;

import java.util.ArrayList;
import java.util.ListIterator;

public class SATSolver {
	/** Contains the Boolean Variables when they are given a value */
	protected static ArrayList<BooleanVariable> solution = new ArrayList<BooleanVariable>();
	public static int nodesExpanded;
	public static int nodesVisited;

	public static ArrayList<BooleanVariable> getModel(SATProblem problem, ArrayList<BooleanVariable> variableList, int limit){
		nodesExpanded = 0;
		nodesVisited = 0;
		solution.clear();

		if (satisfiable(problem, variableList, limit)){
//			System.out.println("NODES EXPANDED: " + nodesExpanded + "  NODES VISITED: " + nodesVisited);
			return solution;
		}
		else{
//			System.out.println("NOOOO SOLUTION");
			return null;
		}
	}

	protected static ArrayList<BooleanVariable> setMainListValue(ArrayList<BooleanVariable> mainList, BooleanVariable booleanVariable, Boolean value){
		for(BooleanVariable BV : mainList){
			if(booleanVariable.name.equals(BV.name))
				BV.value = value;
		}

		return mainList;
	}

	protected static Boolean hasUnit(ArrayList<ArrayList<BooleanVariable>> conjunction){
		for(ArrayList<BooleanVariable> tempDisjunction : conjunction){
			if(tempDisjunction.size() == 1)
				return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	protected static SATProblem unitPropagation(SATProblem problem){
		BooleanVariable unitVariable = null;

		for(ArrayList<BooleanVariable> tempDisjunction : problem.conjunction){
			if(tempDisjunction.size() == 1){
				if(tempDisjunction.get(0).negation == Boolean.FALSE){
					unitVariable = new BooleanVariable(tempDisjunction.get(0).name, Boolean.TRUE, Boolean.FALSE);
					solution.add(unitVariable);
				}else{
					unitVariable = new BooleanVariable(tempDisjunction.get(0).name, Boolean.FALSE, Boolean.TRUE);
					solution.add(unitVariable);
				}
				break;
			}
		}

		if(unitVariable == null){
			return problem;
		}

//		System.out.println("UNIT variable is " + unitVariable);

		ArrayList<ArrayList<BooleanVariable>> copyConjunction = copyConjunction(problem.conjunction);

		int numDisjunction = 0;
		for(ArrayList<BooleanVariable> disjunctionTest : copyConjunction){
			if(BooleanVariable.containsEqualBooleanVariable(disjunctionTest, unitVariable)){
//				System.out.println("Removeing the entire clause " + disjunctionTest);
				problem.conjunction.remove(numDisjunction);
				numDisjunction--;
			}

			if(BooleanVariable.containsOppositeBooleanVariable(disjunctionTest, unitVariable)){
				ArrayList<BooleanVariable> copyDisjunction = problem.conjunction.get(numDisjunction);
//				System.out.println("Removing only the literal from clause " + disjunctionTest);

				copyDisjunction = BooleanVariable.removeBooleanVariable(copyDisjunction, unitVariable);
				if(copyDisjunction.size() == 0)
					return null;
				problem.conjunction.remove(numDisjunction);
				problem.conjunction.add(numDisjunction, copyDisjunction);
			}

			numDisjunction++;
		}


		return problem;
	}

	public static ArrayList<BooleanVariable> getPures(ArrayList<ArrayList<BooleanVariable>> conjunction){
		ArrayList<ArrayList<BooleanVariable>> copyConjunction = new ArrayList<ArrayList<BooleanVariable>>();
		copyConjunction = copyConjunction(conjunction);
		ArrayList<String> impures = new ArrayList<String>();
		ArrayList<BooleanVariable> pures = new ArrayList<BooleanVariable>();

		for(ArrayList<BooleanVariable> disjunction : copyConjunction){
			for(BooleanVariable BV : disjunction){
				if(impures.contains(BV.name))
					continue;
				else if(BooleanVariable.containsEqualBooleanVariable(pures, BV))
					continue;
				else if(BooleanVariable.containsOppositeBooleanVariable(pures, BV)){
					impures.add(BV.name);
					pures = BooleanVariable.removeBooleanVariable(pures, BV);
					continue;
				}else if(BV.negation == Boolean.FALSE){
					BV.value = Boolean.TRUE;
					pures.add(BV);
				}else{
					BV.value = Boolean.FALSE;
					pures.add(BV);
				}
			}
		}

		for(BooleanVariable BV1 : pures)
			solution.add(BV1);

		return pures;
	}

	protected static ArrayList<ArrayList<BooleanVariable>> removePures(ArrayList<ArrayList<BooleanVariable>> conjunction, ArrayList<BooleanVariable> pures){

		ListIterator<ArrayList<BooleanVariable>> con = conjunction.listIterator();
		ListIterator<BooleanVariable> dis;
		ArrayList<BooleanVariable> disjunction;

		while(con.hasNext()){

			disjunction = con.next();
			dis = disjunction.listIterator();

			while(dis.hasNext()){
				BooleanVariable BV = dis.next();

				if(BooleanVariable.containsEqualBooleanVariable(pures, BV)){
					con.remove();
					break;
				}
			}
		}

		return conjunction;
	}

	private static ArrayList<BooleanVariable> getVariables(ArrayList<ArrayList<BooleanVariable>> conjunction){
		ArrayList<BooleanVariable> variables = new ArrayList<BooleanVariable>();

		for(ArrayList<BooleanVariable> disjunction : conjunction){
			for(BooleanVariable BV : disjunction){
				if(!BooleanVariable.containsEqualBooleanVariable(variables, BV) && !BooleanVariable.containsOppositeBooleanVariable(variables, BV) && BV.value == null)
					variables.add(BV);
			}
		}

		return variables;
	}

	private static ArrayList<ArrayList<BooleanVariable>> copyConjunction(ArrayList<ArrayList<BooleanVariable>> conjunction){
		ArrayList<ArrayList<BooleanVariable>> newConjunction = new ArrayList<ArrayList<BooleanVariable>>();
		for(ArrayList<BooleanVariable> disjunction : conjunction){
			ArrayList<BooleanVariable> newDisjunction = new ArrayList<BooleanVariable>();
			for(BooleanVariable BV : disjunction){
				BooleanVariable newBV = new BooleanVariable(BV.name, BV.value, BV.negation);
				newDisjunction.add(newBV);
			}
			newConjunction.add(newDisjunction);
		}

		return newConjunction;
	}

	private static ArrayList<BooleanVariable> copyBooleanVariableList(ArrayList<BooleanVariable> BVList){
		ArrayList<BooleanVariable> newList = new ArrayList<BooleanVariable>();
		for(BooleanVariable BV : BVList){
			BooleanVariable newBV = new BooleanVariable(BV.name, BV.value, BV.negation);
			newList.add(newBV);
		}

		return newList;
	}

	private static Boolean valueOfConjunction(ArrayList<ArrayList<BooleanVariable>> conjunction){
		Boolean disjunctionValue;
		Boolean unkownDisjunction = Boolean.FALSE;

		if(conjunction.size() == 0)
			return Boolean.TRUE;

		for(ArrayList<BooleanVariable> disjunction : conjunction){
			disjunctionValue = Boolean.FALSE;
			for(BooleanVariable BV : disjunction){
				if(BV.value == Boolean.TRUE && BV.negation == Boolean.FALSE){
					disjunctionValue = Boolean.TRUE;
					break;
				}else if(BV.value == Boolean.FALSE && BV.negation == Boolean.TRUE){
					disjunctionValue = Boolean.TRUE;
					break;
				}

				if(BV.value == null)
					disjunctionValue = null;
			}

			if(disjunctionValue == Boolean.FALSE)
				return Boolean.FALSE;
			if(disjunctionValue == null)
				unkownDisjunction = Boolean.TRUE;
		}

		if(unkownDisjunction == Boolean.TRUE)
			return null;
		return Boolean.TRUE;
	}

	protected static Boolean satisfiable(SATProblem problem, ArrayList<BooleanVariable> variableList, int limit){
		ListIterator<ArrayList<BooleanVariable>> con = problem.conjunction.listIterator();
		ArrayList<BooleanVariable> disjunction;
		nodesVisited++;
		if (nodesVisited > limit || nodesExpanded > limit) return false;

		while(con.hasNext()){
			disjunction = con.next();
			if(disjunction.isEmpty())
				con.remove();
		}

		while(hasUnit(problem.conjunction) || !getPures(problem.conjunction).isEmpty()){
			problem.mainList = getVariables(problem.conjunction);
			while(hasUnit(problem.conjunction)){
				problem = unitPropagation(problem);
				if(problem == null)
					return Boolean.FALSE;
			}

			ArrayList<BooleanVariable> pures = getPures(problem.conjunction);

			for(BooleanVariable BV : pures){
				if(BV.negation == Boolean.FALSE)
					problem.mainList = setMainListValue(problem.mainList, BV, Boolean.TRUE);
				else
					problem.mainList = setMainListValue(problem.mainList, BV, Boolean.FALSE);
			}

			problem.conjunction = removePures(problem.conjunction, pures);
			problem.mainList = getVariables(problem.conjunction);
		}

		problem.mainList = getVariables(problem.conjunction);

		if(valueOfConjunction(problem.conjunction) == Boolean.TRUE){
			for(BooleanVariable BV : variableList){
				if(BV.value == Boolean.TRUE && BV.negation == Boolean.FALSE)
					solution.add(BV);
				else if(BV.value == Boolean.FALSE && BV.negation == Boolean.TRUE)
					solution.add(BV);
			}
			return Boolean.TRUE;
		}

		if(valueOfConjunction(problem.conjunction) == Boolean.FALSE){
			return Boolean.FALSE;
		}

		ArrayList<ArrayList<BooleanVariable>> newConjunction1 = copyConjunction(problem.conjunction);
		ArrayList<ArrayList<BooleanVariable>> newConjunction2 = copyConjunction(problem.conjunction);

		ArrayList<BooleanVariable> newMainList1 = copyBooleanVariableList(problem.mainList);
		ArrayList<BooleanVariable> newMainList2 = copyBooleanVariableList(problem.mainList);

		for(BooleanVariable BV : problem.mainList){
			if(BV.value == null){
				newConjunction1.add(new ArrayList<BooleanVariable>(){{
					add(new BooleanVariable(BV.name, null, Boolean.FALSE));
				}});
				newConjunction2.add(new ArrayList<BooleanVariable>(){{
					add(new BooleanVariable(BV.name, null, Boolean.TRUE));
				}});
				break;
			}
		}

		SATProblem newProblem1 = new SATProblem(newConjunction1, newMainList1);
		SATProblem newProblem2 = new SATProblem(newConjunction2, newMainList2);

		nodesExpanded = nodesExpanded + 2;

		ArrayList<BooleanVariable> backupAtThisLevel = new ArrayList<BooleanVariable>(solution);

		boolean trueBranch = satisfiable(newProblem1, newMainList1, limit);
		if (!trueBranch){
			solution = backupAtThisLevel;
			boolean falseBranch = satisfiable(newProblem2, newMainList2, limit);
			if (!falseBranch){
				solution = new ArrayList<BooleanVariable>(backupAtThisLevel);
			}
			return falseBranch;
		}
		return trueBranch;
	}

	public static void print(ArrayList<ArrayList<BooleanVariable>> conjunction){
		String str = "";
		for(ArrayList<BooleanVariable> disjunction : conjunction){
			str += "(";
			for(BooleanVariable BV : disjunction){
				if(BV.negation == Boolean.TRUE)
					str += "~";
				str += BV.name + " v ";
			}
			str = str.substring(0, str.length() - 3);
			str += ")\n";
		}
		System.out.println(str);
	}

	public static String printDisjunction(ArrayList<BooleanVariable> disjunction){
		String str = "(";
		for(BooleanVariable BVprint : disjunction){
			if(BVprint.negation == Boolean.TRUE)
				str += "~";
			str += BVprint.name + " v ";
		}

		if(!disjunction.isEmpty())
			str = str.substring(0, str.length() - 3);
		str += ")";

		return str;
	}
}
























