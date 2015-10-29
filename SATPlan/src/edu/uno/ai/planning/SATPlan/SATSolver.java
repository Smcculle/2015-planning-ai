package edu.uno.ai.planning.SATPlan;

import java.util.ArrayList;
import java.util.ListIterator;

public class SATSolver {
	static ArrayList<BooleanVariable> solution = new ArrayList<BooleanVariable>();	
	
	public static ArrayList<BooleanVariable> getModel(SATProblem problem, ArrayList<BooleanVariable> variableList){
		if (satisfiable(problem, variableList)){
			return solution;
		}
		else{
			return null;
		}
	}
	
	private static ArrayList<BooleanVariable> setMainListValue(ArrayList<BooleanVariable> mainList, BooleanVariable booleanVariable, Boolean value){
		for(BooleanVariable BV : mainList){
			if(booleanVariable.name == BV.name)
				BV.value = value;
		}
		
		return mainList;
	}
	
	private static Boolean hasUnit(ArrayList<ArrayList<BooleanVariable>> conjunction){
		for(ArrayList<BooleanVariable> tempDisjunction : conjunction){
			if(tempDisjunction.size() == 1)
				return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	private static SATProblem unitPropagation(SATProblem problem){
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
	
	private static ArrayList<BooleanVariable> getPures(ArrayList<ArrayList<BooleanVariable>> conjunction){
		ArrayList<ArrayList<BooleanVariable>> copyConjunction = new ArrayList<ArrayList<BooleanVariable>>();
		copyConjunction = copyConjunction(conjunction);
		ArrayList<String> impures = new ArrayList<String>();
		ArrayList<BooleanVariable> pures = new ArrayList<BooleanVariable>();
		
		for(ArrayList<BooleanVariable> disjunction : copyConjunction){
			for(BooleanVariable BV : disjunction){
//				System.out.println(BV.name);
				if(impures.contains(BV.name))
					continue;		
				else if(BooleanVariable.containsEqualBooleanVariable(pures, BV))
					continue;
				else if(BooleanVariable.containsOppositeBooleanVariable(pures, BV)){
					impures.add(BV.name);
//					System.out.println("Remove.");
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
	
	private static ArrayList<ArrayList<BooleanVariable>> removePures(ArrayList<ArrayList<BooleanVariable>> conjunction, ArrayList<BooleanVariable> pures){
		
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
	
	private static Boolean satisfiable(SATProblem problem, ArrayList<BooleanVariable> variableList){
		//System.out.println("\n------------------\nTry to Satisfy\n------------------");
		
		ListIterator<ArrayList<BooleanVariable>> con = problem.conjunction.listIterator();
		ArrayList<BooleanVariable> disjunction;

		while(con.hasNext()){			
			disjunction = con.next();
			if(disjunction.isEmpty())
				con.remove();
		}
		
		while(hasUnit(problem.conjunction) || !getPures(problem.conjunction).isEmpty()){

			
			
		//while(hasUnit(problem.conjunction)){
//				System.out.println("\nConjunction before UPP (size " + problem.conjunction.size() + ")");
//				print(problem.conjunction);
//				System.out.println("\nVariables before UPP:");
				problem.mainList = getVariables(problem.conjunction);
//				printVariables(problem.mainList);
			
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

//				System.out.println("\nConjunction after UPP (size " + problem.conjunction.size() + ")");
//				print(problem.conjunction);
//				System.out.println("\nVariables after UPP:");
				problem.mainList = getVariables(problem.conjunction);
//				printVariables(problem.mainList);
		}
		

		problem.mainList = getVariables(problem.conjunction);
		
//			System.out.println("\nVariables:");
//			for(BooleanVariable BV : problem.mainList){
//				System.out.println(BV.name + " = " + BV.value);
//			}
		
		if(valueOfConjunction(problem.conjunction) == Boolean.TRUE){
			System.out.println("SOULUTION FOUND!");
			for(BooleanVariable BV : variableList){
				if(BV.value == Boolean.TRUE)
					solution.add(BV);
			}
			return Boolean.TRUE;
		}
		
		if(valueOfConjunction(problem.conjunction) == Boolean.FALSE){
			System.out.println("CONJ FALSE");
			return Boolean.FALSE;
		}
		
		if(valueOfConjunction(problem.conjunction) == null){
			//System.out.println("KEEP LOOKING.");
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
//			System.out.println("\nnewMainList1:");
//			for(BooleanVariable BV : newMainList1){
//				System.out.println(BV.name + " = " + BV.value);
//			}
//			System.out.println("\nnewMainList2:");
//			for(BooleanVariable BV : newMainList2){
//				System.out.println(BV.name + " = " + BV.value);
//			}
		
		newConjunction1 = setVariableValue(newConjunction1, newMainList1.get(count));
		newConjunction2 = setVariableValue(newConjunction2, newMainList2.get(count));
		
		SATProblem newProblem1 = new SATProblem(newConjunction1, newMainList1);
		SATProblem newProblem2 = new SATProblem(newConjunction2, newMainList2);
		
//			System.out.println("\nConjunction1 before simplify (size " + newProblem1.conjunction.size() + ")");
//			print(newProblem1.conjunction);
//			System.out.println("\nVariables:");
//			for(BooleanVariable BV : newProblem1.mainList){
//				System.out.println(BV.name + " = " + BV.value);
//			}
			
		newProblem1 = simplifyConjunction(newProblem1, newMainList1.get(count));
		
//			System.out.println("\nConjunction1 after simplify (size " + newProblem1.conjunction.size() + ")");
//			print(newProblem1.conjunction);
//			
//			System.out.println("\nConjunction2 before simplify (size " + newProblem2.conjunction.size() + ")");
//			print(newProblem2.conjunction);
//			System.out.println("\nVariables:");
//			for(BooleanVariable BV : newProblem2.mainList){
//				System.out.println(BV.name + " = " + BV.value);
//			}
			
		newProblem2 = simplifyConjunction(newProblem2, newMainList2.get(count));
			
//			System.out.println("\nConjunction2 before simplify (size " + newProblem2.conjunction.size() + ")");
//			print(newProblem2.conjunction);
		
		return satisfiable(newProblem1, newMainList1) || satisfiable(newProblem2, newMainList2);
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

//		for(BooleanVariable BV1 : problem.mainList){
//			System.out.println(BV1.name + " = " + BV1.negation);
//		}
		
		while(con.hasNext()){
//			System.out.println("\nGet disjunction.");
			disjunction = con.next();
			dis = disjunction.listIterator();
			
//			for(BooleanVariable BV2 : problem.mainList){
//				System.out.println("name = " + BV2.name + " value = " + BV2.value + " negation = " + BV2.negation);
//			}
			
//			System.out.println("\nBefore getting disjunction");
//			print(problem.conjunction);			
//			
			while(dis.hasNext()){
//				System.out.println("\nGetting BV");
//				for(BooleanVariable BV3 : disjunction){
//					System.out.println("name = " + BV3.name + " value = " + BV3.value + " negation = " + BV3.negation);
//				}
//				print(problem.conjunction);
				BooleanVariable BV4 = dis.next();
//				System.out.println("\nGet BV " + BV4.name);
//				
//				System.out.println("\nMain BV name is" + BVmain.name);
				
				if(!BV4.name.equals(BVmain.name)){
//					System.out.println("name = " + BV4.name + " value = " + BV4.value + " negation = " + BV4.negation);
					continue;
				}
				
				if(BV4.negation == Boolean.FALSE && BV4.value == Boolean.TRUE){
//					System.out.println("\nRemove disjunction1 " + BV4.name + " " + BV4.value);
					con.remove();
//					print(problem.conjunction);
					continue;
				}else if(BV4.negation == Boolean.TRUE && BV4.value == Boolean.FALSE){
//					System.out.println("\nRemove disjunction2 " + BV4.name + " " + BV4.value);
					con.remove();
//					print(problem.conjunction);
					continue;
				}else if(BV4.negation == Boolean.TRUE && BV4.value == Boolean.TRUE){
//					System.out.println("\nRemove BV " + BV4.name);
					dis.remove();
					//break;
				}else if(BV4.negation == Boolean.FALSE && BV4.value == Boolean.FALSE){
//					System.out.println("\nRemove BV " + BV4.name);
					dis.remove();
					//break;
				}
			}
		}
		
		return problem;
	}
	
	public static void print(ArrayList<ArrayList<BooleanVariable>> conjunction){
//		String str = "";
//		for(ArrayList<BooleanVariable> disjunction : conjunction){
//			str += "(";
//			for(BooleanVariable BV : disjunction){
//				if(BV.negation == Boolean.TRUE)
//					str += "~";
//				str += BV.name + " v ";
//			}
//			if (str.length() >= 3)
//				str = str.substring(0, str.length() - 3);
//			str += ") ^ ";
//		}
//		
//		if(!conjunction.isEmpty())
//			str = str.substring(0, str.length() - 3);
//		System.out.println(str);
	}
	
	public static void printVariables(ArrayList<BooleanVariable> variables){
//		for(BooleanVariable BV : solution){
//			System.out.println(BV.name + " value = " + BV.value + " negation = " + BV.negation);
//		}
//		for(BooleanVariable BV : variables){
//			System.out.println(BV.name + " = value = " + BV.value + " negation = " + BV.negation);
//		}
	}
}
