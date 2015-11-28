package edu.uno.ai.planning.SATPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SATSolver implements ISATSolver {

	@Override
	public List<BooleanVariable> getModel(SATProblem problem) {
		return getModel(problem, new ArrayList<>());
	}

	@Override
	public int countVisited() {
		return nodesVisited;
	}

	@Override
	public int countExpanded() {
		return nodesExpanded;
	}

	/** Contains the Boolean Variables when they are given a value */
	protected static ArrayList<BooleanVariable> solution = new ArrayList<BooleanVariable>();
	protected static int nodesExpanded;
	protected static int nodesVisited;

	public static ArrayList<BooleanVariable> getModel(SATProblem problem, ArrayList<BooleanVariable> variableList){
		if (satisfiable(problem, variableList)){
			return solution;
		}
		else{
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
		ArrayList<BooleanVariable> disjunction;
		ListIterator<ArrayList<BooleanVariable>> con = problem.conjunction.listIterator();
		ListIterator<BooleanVariable> dis;
		
		for(ArrayList<BooleanVariable> tempDisjunction : problem.conjunction){
			if(tempDisjunction.size() == 1){
				if(tempDisjunction.get(0).negation == Boolean.FALSE){
					unitVariable = new BooleanVariable(tempDisjunction.get(0).name, Boolean.TRUE, tempDisjunction.get(0).negation);
					solution.add(unitVariable);
					problem.mainList = setMainListValue(problem.mainList, unitVariable, Boolean.TRUE);
				}else{
					unitVariable = new BooleanVariable(tempDisjunction.get(0).name, Boolean.FALSE, tempDisjunction.get(0).negation);
					solution.add(unitVariable);
					problem.mainList = setMainListValue(problem.mainList, unitVariable, Boolean.FALSE);
				}
				break;
			}
		}
		
		if(unitVariable == null)
			return problem;

		while(con.hasNext()){
			
			disjunction = con.next();
			dis = disjunction.listIterator();
			
			while(dis.hasNext()){
				BooleanVariable BV = dis.next();
				
				if(BooleanVariable.booleanVariablesEqual(unitVariable, BV)){
					con.remove();
					break;
				}
				
				if(BooleanVariable.booleanVariablesOpposites(unitVariable, BV) && dis.hasNext())
					dis.remove();
				else if(BooleanVariable.booleanVariablesOpposites(unitVariable, BV) && dis.hasPrevious())
					dis.remove();
				else if(BooleanVariable.booleanVariablesOpposites(unitVariable, BV) && !dis.hasNext() && !dis.hasPrevious())
					return null;
			}
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
	
	private static ArrayList<ArrayList<BooleanVariable>> setVariableValue(ArrayList<ArrayList<BooleanVariable>> conjunction, BooleanVariable booleanVariable){
		for(ArrayList<BooleanVariable> disjunction : conjunction){
			for(BooleanVariable BV : disjunction){
				if(BV.name == booleanVariable.name)
					BV.value = booleanVariable.value;
			}
		}
		
		return conjunction;
	}
	
	private static Boolean valueOfConjunction(ArrayList<ArrayList<BooleanVariable>> conjunction){
		Boolean disjunctionValue;
		Boolean unkownDisjunction = Boolean.FALSE;
		
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
	
	private static SATProblem simplifyConjunction(SATProblem problem, BooleanVariable BVmain){
		ListIterator<ArrayList<BooleanVariable>> con = problem.conjunction.listIterator();
		ListIterator<BooleanVariable> dis;
		ArrayList<BooleanVariable> disjunction;
		
		while(con.hasNext()){
			disjunction = con.next();
			dis = disjunction.listIterator();	
			
			while(dis.hasNext()){
				BooleanVariable BV4 = dis.next();
				
				if(BV4.negation == Boolean.FALSE && BV4.value == Boolean.TRUE){
					con.remove();
					continue;
				}else if(BV4.negation == Boolean.TRUE && BV4.value == Boolean.FALSE){
					con.remove();
					continue;
				}else if(BV4.negation == Boolean.TRUE && BV4.value == Boolean.TRUE){
					dis.remove();
				}else if(BV4.negation == Boolean.FALSE && BV4.value == Boolean.FALSE){
					dis.remove();
				}
			}
		}
		
		return problem;
	}
	
	protected static Boolean satisfiable(SATProblem problem, ArrayList<BooleanVariable> variableList){
		ListIterator<ArrayList<BooleanVariable>> con = problem.conjunction.listIterator();
		ArrayList<BooleanVariable> disjunction;
		nodesVisited++;

		while(con.hasNext()){			
			disjunction = con.next();
			if(disjunction.isEmpty())
				con.remove();
		}
		
		while(hasUnit(problem.conjunction) || !getPures(problem.conjunction).isEmpty()){
			problem.mainList = getVariables(problem.conjunction);
			problem = unitPropagation(problem);
			if(problem == null)
				return Boolean.FALSE;
			
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
		
		int count = 0;
		for(BooleanVariable BV : problem.mainList){
			if(BV.value == null){
				newMainList1.get(count).value = Boolean.TRUE;
				newMainList2.get(count).value = Boolean.FALSE;
				break;
			}
			count++;
		}
		
		newConjunction1 = setVariableValue(newConjunction1, newMainList1.get(count));
		newConjunction2 = setVariableValue(newConjunction2, newMainList2.get(count));
		
		SATProblem newProblem1 = new SATProblem(newConjunction1, newMainList1);
		SATProblem newProblem2 = new SATProblem(newConjunction2, newMainList2);
			
		newProblem1 = simplifyConjunction(newProblem1, newMainList1.get(count));			
		newProblem2 = simplifyConjunction(newProblem2,newMainList2.get(count));

		nodesExpanded++;
		
		return satisfiable(newProblem1, newMainList1) || satisfiable(newProblem2, newMainList2);
	}

}
























