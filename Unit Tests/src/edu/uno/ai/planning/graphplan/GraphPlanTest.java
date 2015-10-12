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
			assertEquals(cake.prob.toString(), (new Benchmark("cake", "cake_test").getProblem().toString()));
			//System.out.println("Level " + cake.pg.getLevel());
		//	System.out.println(cake);
		//	System.out.println(cake.prob);
		//	System.out.println(new Benchmark("cake", "cake_test").getProblem());
			
		
			Graphplan rocket = new Graphplan(new Benchmark("rocket", "rocket_test").getProblem());
			assertNotNull(rocket);
			assertNotNull(rocket.pg);
			assertEquals(rocket.prob.toString(), (new Benchmark("rocket", "rocket_test").getProblem().toString()));

		}
		catch(IOException e){
		}

	}
	
	@Test
	public void testMakePlanGraph(){
		try{
			Graphplan rocket = new Graphplan(new Benchmark("rocket", "rocket_test").getProblem());
			assertNotNull(rocket.pg);
			System.out.println(rocket.pg);
			System.out.println("\n\n");
			System.out.println("\n\n");
			System.out.println("\n\n");
			System.out.println("\n\n");
			System.out.println(new PlanGraph(new Benchmark("rocket", "rocket_test").getProblem() ));
	//		assertEquals(rocket.pg.toString(), (new PlanGraph(new Benchmark("rocket", "rocket_test").getProblem())).toString());
			rocket.makePlanGraph((new Benchmark("cake", "cake_test").getProblem()));
			assertFalse(rocket.pg.equals(new PlanGraph((new Benchmark("rocket", "rocket_test").getProblem()))));
		//	assertEquals(rocket.pg, new PlanGraph((new Benchmark("cake", "cake_test").getProblem())));

			Graphplan cake = new Graphplan(new Benchmark("cake", "cake_test").getProblem());
			assertNotNull(cake);			
	//		assertEquals(cake.pg.toString(), (new PlanGraph((new Benchmark("cake", "cake_test").getProblem()))).toString());
			cake.makePlanGraph((new Benchmark("rocket", "rocket_test").getProblem()));
		//	assertEquals(rocket.pg.toString(), (new PlanGraph((new Benchmark("rocket", "rocket_test").getProblem()))).toString());
			assertFalse(rocket.pg.toString().equals(new PlanGraph((new Benchmark("cake", "cake_test").getProblem())).toString()));
		}
		catch(IOException e){}
	}

	@Test
	public void testNextPG(){
		//System.out.println("hi");

		try{
			Graphplan cake = new Graphplan(new Benchmark("cake", "cake_test").getProblem());
		//	assertEquals(cake.pg, new PlanGraph((new Benchmark("cake", "cake_test").getProblem())));
			assertEquals(cake.pg, cake.nextPG(cake.pg));
			cake.extend();
			//System.out.println(cake.parentList);
		}
		catch(IOException e){
			//e.printStackTrace();
		}
	}
	
}
