package edu.uno.ai.planning.hsp;


import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.State;
import edu.uno.ai.planning.Step;
import hsp2.BestFirstSearch2;
import hsp2.HSPSearch;
import hsp2.StateSpaceNode;
import hsp2.StateSpacePlanner;
import hsp2.StateSpaceProblem;
import hsp2.StateSpaceSearch;
import hsp2.TotalOrderPlan;

public class HSPTest {

//	@Test
//	public void testInit() throws FileNotFoundException{
//		Problem rocket = createEasyCargoProblem();
//		Problem easyStack = createEasyStack();
//		StateSpaceProblem ss = new StateSpaceProblem(rocket);
//		StateSpaceProblem es = new StateSpaceProblem(easyStack);
//		HSPSearch s = new HSPSearch(ss);
//		HSPSearch x = new HSPSearch(es);
//		assertEquals(s.getOpenList().get(0),s.getOpenList().get(0));
//		assertEquals(x.getOpenList().get(0),x.getOpenList().get(0));
//		s.getOpenList().get(0).expand();
//		x.getOpenList().get(0).expand();
//		for (StateSpaceNode child: s.getOpenList().get(0).children){
//			s.getOpenList().add(child);
//		}
//		for (StateSpaceNode child: x.getOpenList().get(0).children){
//			x.getOpenList().add(child);
//		}
//		assertNotEquals(s.getOpenList().get(0),s.getOpenList().get(1));
//		assertNotEquals(x.getOpenList().get(0),x.getOpenList().get(1));
//		assertEquals(s.getOpenList().get(7),s.getOpenList().get(11));
//		s.getOpenList().get(7).expand();
//		s.getOpenList().get(11).expand();
//		
//		assertEquals(s.getOpenList().size(),13);
//		for (StateSpaceNode child: s.getOpenList().get(11).children){
//			s.getOpenList().add(child);
//		}
//		assertNotEquals(s.getOpenList().size(),13);
//		s.getOpenList().get(16).expand();
//		assertEquals(s.getOpenList().get(7),s.getOpenList().get(11));
//		assertEquals(s.getOpenList().get(7).plan.size(),s.getOpenList().get(11).plan.size());
////		TotalOrderPlan sol = (TotalOrderPlan) s.search();
////		Iterator<Step> iter = sol.iterator();
////		System.out.println(rocket.isSolution(sol));
////		
////		while (iter.hasNext()){
////			System.out.println(iter.next());
////		}
//	}
	
	@Test
	public void testInit2() throws FileNotFoundException{
		Problem rocket = createEasyCargoProblem();
		Problem easyStack = createEasyStack();
		StateSpaceProblem ss = new StateSpaceProblem(rocket);
		StateSpaceProblem es = new StateSpaceProblem(easyStack);
		
		BestFirstSearch2 x = new BestFirstSearch2(ss);

		
		TotalOrderPlan sol = (TotalOrderPlan) x.findNextSolution();
		Iterator<Step> iter = sol.iterator();
	
		
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
