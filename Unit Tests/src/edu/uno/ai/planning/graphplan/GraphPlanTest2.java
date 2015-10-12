package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.*; 

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.uno.ai.planning.Benchmark;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Constant;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.logic.Predication;

public class GraphPlanTest2 {

	
	@Test 
	public void testInit(){
		
			Problem cakeProblem = createCakeProblem();
			PlanGraph cakePlanGraph = PlanGraph.create(cakeProblem);
			PlanGraph cakePlanGraphExt = new PlanGraph(cakePlanGraph);
			
			Graphplan cake = new Graphplan(cakeProblem,cakePlanGraphExt);
			cake.extend();
			cake.search();
//			System.out.println(cake.currentPlanGraph.isMutex(cake.currentPlanGraph._steps.Get(0),cake.currentPlanGraph._steps.Get(1)));
//			System.out.println(cake.achieveGoals);
//			System.out.println(cake.currentPlanGraph._mutexSteps);
			


		}

		
		private Problem createEasyCargoProblem() {
			try {
				return new Benchmark("rocket", "rocket_test").getProblem();
			} catch (IOException e) {
				return null;
			}
		}

		private Problem createCakeProblem() {
			try {
				return new Benchmark("cake", "cake_test").getProblem();
			} catch (IOException e) {
				return null;
			}
		}
	
}
