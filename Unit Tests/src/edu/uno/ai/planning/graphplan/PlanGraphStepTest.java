package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import org.junit.*;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.util.ImmutableArray;

public class PlanGraphStepTest {
	
	private PlanGraphStep test1;
	private PlanGraphStep test2;
	private PlanGraphStep test3;
	
	@Before
	public void createPlanGraphStep(){
		String name = "testStep";
		Predication preConds = new Predication("(testStep preConds)", new ImmutableArray<Term>(new Term[]{}));
		Predication effects = new Predication("(testStep effects)", new ImmutableArray<Term>(new Term[]{}));
		int initialLevel = 4;
		test1 = new PlanGraphStep(name, preConds, effects, initialLevel);

		Predication step2PreConds = new Predication("(not (is funky))", new ImmutableArray<Term>(new Term[]{}));
		Predication step2Effects = new Predication("(is funky)", new ImmutableArray<Term>(new Term[]{}));
		Step step2 = new Step("funkify", step2PreConds, step2Effects);
		int initialLevel2 = 2;
		test2 = new PlanGraphStep(step2, initialLevel2);
		
		Predication step3PreConds = new Predication("(not (fear reaper))", new ImmutableArray<Term>(new Term[]{}));
		Predication step3Effects = new Predication("(fear reaper)", new ImmutableArray<Term>(new Term[]{}));
		Step step3 = new Step("fearReaper", step3PreConds, step3Effects);
		test3 = new PlanGraphStep(step3);
	}
	
	@Test
	public void getInitialLevel(){
		assertEquals(4, test1.getInitialLevel());
		assertEquals(2, test2.getInitialLevel());
		assertEquals(-1, test3.getInitialLevel());
	}
	
	@Test
	public void setInitialLevel(){
		assertNotEquals(9, test2.getInitialLevel());
		test2.setInitialLevel(9);
		assertEquals(9, test2.getInitialLevel());
	}
	
	@Test
	public void getChildNodes(){
		
	}
	
	@Test
	public void addPlanGraphChild(){
		
	}
	
	@Test
	public void getParentNodes(){
		
	}
	
	@Test
	public void addPlanGraphParent(){
		
	}
	
	@Test
	public void testToString(){
		
	}
}
