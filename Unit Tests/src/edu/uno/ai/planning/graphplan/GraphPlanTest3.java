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

public class GraphPlanTest3{
	
	Problem createRocket() {
		try {
			return new Benchmark("rocket", "rocket_test").getProblem();
		} catch (IOException e) {
			return null;
		}
	}

	Problem createCake() {
		try {
			return new Benchmark("cake", "cake_test").getProblem();
		} catch (IOException e) {
			return null;
		}
	}
	
	@Test 
	public void testInit(){
/**		Problem rocketp = createRocket();
		Graphplan2 rocket = new Graphplan2(rocketp);
		assertFalse(rocket.search());
		rocket.extend();
		assertTrue(rocket.p.nodes.size() == 1);
		assertFalse(rocket.search());
		rocket.extend();
		assertTrue(rocket.p.nodes.size() == 2);
		assertFalse(rocket.search());
		*/
		
		Problem cakep = createCake();
		Graphplan2 cake = new Graphplan2(cakep);
		System.out.println(cake.nodesPrint());

		assertFalse(cake.search());
		cake.extend();
		System.out.println(cake.nodesPrint());

		/**	assertFalse(cake.search());
		cake.extend();
		assertFalse(cake.search());
		System.out.println(cake.nodesPrint());
		cake.extend();
		System.out.println(cake.nodesPrint());
		*/
	}
}























