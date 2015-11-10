package edu.uno.ai.planning.hsp;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.TotalOrderPlan;
import hsp2.HSPSearch;

public class HSPTest {

	@Test
	public void testInit() throws FileNotFoundException{
		Problem rocket = createEasyCargoProblem();
		Problem easyStack = createEasyStack();
		StateSpaceProblem ss = new StateSpaceProblem(rocket);
		StateSpaceProblem es = new StateSpaceProblem(easyStack);
		
//		HSPSearch s = new HSPSearch(ss);
		HSPSearch x = new HSPSearch(es);
		TotalOrderPlan sol = (TotalOrderPlan) x.search();
		Iterator<Step> iter = sol.iterator();
		System.out.println(easyStack.isSolution(sol));
		
		while (iter.hasNext()){
			System.out.println(iter.next());
		}
	}
	
	private Problem createEasyCargoProblem() {
		try {
			return new Benchmark("cargo", "deliver_return_1").getProblem();
		} catch (IOException e) {
			return null;
		}
	}
	
	private Problem createEasyStack() {
		try {
			return new Benchmark("blocks", "easy_stack").getProblem();
		} catch (IOException e) {
			return null;
		}
	}
	
}
