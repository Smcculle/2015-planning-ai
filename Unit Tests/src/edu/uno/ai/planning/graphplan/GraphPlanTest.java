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

public class GraphPlanTest {

	
	@Test 
	public void testInit(){
		try{
			Graphplan cake = new Graphplan(new Benchmark("cake", "cake_test").getProblem());
			assertNotNull(cake);
			assertNotNull(cake.pg);
			assertEquals(cake.problem.toString(), (new Benchmark("cake", "cake_test").getProblem().toString()));
			//System.out.println("Level " + cake.pg.getLevel());
		//	System.out.println(cake);
		//	System.out.println(cake.prob);
		//	System.out.println(new PlanGraph(new Benchmark("cake", "cake_test").getProblem()));
		//	System.out.println("\n\n\n" + cake.pg);
			
			
		
			Graphplan rocket = new Graphplan(new Benchmark("rocket", "rocket_test").getProblem());
			assertNotNull(rocket);
			assertNotNull(rocket.pg);
			assertEquals(rocket.problem.toString(), (new Benchmark("rocket", "rocket_test").getProblem().toString()));

		}
		catch(IOException e){
		}

	
}
