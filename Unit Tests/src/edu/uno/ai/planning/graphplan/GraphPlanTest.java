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
	/**
	@Test
	public void testMakePlanGraph(){
		try{

		}
		catch(IOException e){}
	}
*/

	/**@Test
	public void testNextPG(){
		//System.out.println("hi");

		try{
			Graphplan cake = new Graphplan(new Benchmark("cake", "cake_test").getProblem());
		//	assertEquals(cake.pg, new PlanGraph((new Benchmark("cake", "cake_test").getProblem())));
			//assertEquals(cake.pg, cake.nextPG(cake.pg));
		//	System.out.println(cake.pg.isGoalNonMutex(cake.problem.goal));
		//	System.out.println(cake.parentList.get(0).isGoalNonMutex(cake.problem.goal));
		////	System.out.println(cake.currentLevel);
		//	System.out.println(cake.highestLevel);
			//cake.extend();
			//System.out.println(cake.parentList);
		}
		catch(IOException e){
			//e.printStackTrace();
		}
	}
*/
	
	@Test 
	public void testExtend(){
		try{
			Graphplan cake = new Graphplan(new Benchmark("cake", "cake_test").getProblem());
			//System.out.println(cake.currentLevel);
			assertTrue(cake.currentLevel == 0);
			cake.extend();
			assertTrue(cake.currentLevel == 1);
			cake.extend();
			
		}
		catch(IOException e){}

	}
	
	@Test 
	public void testNextPG(){
		try{
			int z;
			Graphplan rocket = new Graphplan(new Benchmark("rocket", "rocket_test").getProblem());
			z = rocket.parentList.size();
			rocket.nextPG(rocket.pg);
			assertTrue(rocket.parentList.size() != z);
			assertTrue(rocket.parentList.size() == z * 2);
			rocket.nextPG(rocket.pg);
			assertTrue(rocket.parentList.size() == z * 3);
			
			Graphplan cake = new Graphplan(new Benchmark("cake", "cake_test").getProblem());
			z = cake.parentList.size();
			cake.nextPG(cake.pg);
			assertTrue(cake.parentList.size() == z * 2);
			cake.nextPG(cake.pg);
			assertTrue(cake.parentList.size() == z * 3);
			
		}
		catch(IOException e){}

	}
	
}
