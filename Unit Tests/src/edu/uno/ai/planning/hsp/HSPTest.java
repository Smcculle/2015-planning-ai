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
import hsp2.HeuristicSearch;
import hsp2.StateSpaceNode;
import hsp2.StateSpacePlanner;
import hsp2.StateSpaceProblem;
import hsp2.StateSpaceSearch;
import hsp2.TotalOrderPlan;

public class HSPTest {

//	@Test
	public void testInit() throws FileNotFoundException{
		
	}
	
	@Test
	public void testInit2() throws FileNotFoundException{
		Problem rocket = createEasyCargoProblem();
		StateSpaceProblem ss = new StateSpaceProblem(rocket);
		HeuristicSearch x = new HeuristicSearch(ss);
		StateSpaceNode node = x.getRoot();
		assertEquals(node.plan.size(),0);
		node.expand();
		assertEquals(x.countExpanded(),12);
		StateSpaceNode childnode = null;
		for (StateSpaceNode child: node.children){
			assertEquals(child.plan.size(),1);
			childnode = child;
		}
		assertEquals(childnode.state.toString(), "(and (at plane_atl msy) (at cargo_msy atl))");
		assertEquals(x.problem.goal.toString(),"(and (at cargo_msy msy) (at plane_atl atl))");
		assertEquals(x.calculateHeuristic(childnode.state),2);
		
		
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
