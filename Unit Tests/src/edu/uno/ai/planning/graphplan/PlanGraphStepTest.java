package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.util.ImmutableArray;

public class PlanGraphStepTest {
	
	private PlanGraphLiteral testPGLiteral;
	
	private Step testStep;
	
	private PlanGraphLiteral test3PreCondLiteral1;
	private PlanGraphLiteral test4PreCondLiteral1;
	private PlanGraphLiteral test4PreCondLiteral2;
	private PlanGraphLiteral test4PreCondLiteral3;
	
	private PlanGraphLiteral test3EffectLiteral1;
	private PlanGraphLiteral test4EffectLiteral1;
	private PlanGraphLiteral test4EffectLiteral2;
	private PlanGraphLiteral test4EffectLiteral3;
	
	private PlanGraphStep test1;
	private PlanGraphStep test2;
	private PlanGraphStep test3;
	private PlanGraphStep test4;
	
	@Before
	public void createPlanGraphStep(){
		String name = "testStep";
		Predication preConds = new Predication("(testStep preConds)", new ImmutableArray<Term>(new Term[]{}));
		Predication effects = new Predication("(testStep effects)", new ImmutableArray<Term>(new Term[]{}));
		testStep = new Step(name, preConds, effects);
		test1 = new PlanGraphStep(testStep);
		
		testPGLiteral = new PlanGraphLiteral(preConds);

		Predication step2PreConds = new Predication("(not (is funky))", new ImmutableArray<Term>(new Term[]{}));
		Predication step2Effects = new Predication("(is funky)", new ImmutableArray<Term>(new Term[]{}));
		Step step2 = new Step("funkify", step2PreConds, step2Effects);
		int initialLevel2 = 2;
		test2 = new PlanGraphStep(step2, initialLevel2);
		
		Predication step3PreConds = new Predication("(not (fear reaper))", new ImmutableArray<Term>(new Term[]{}));
		Predication step3Effects = new Predication("(fear reaper)", new ImmutableArray<Term>(new Term[]{}));
		Step step3 = new Step("fearReaper", step3PreConds, step3Effects);
		test3PreCondLiteral1 = new PlanGraphLiteral(step3PreConds);
		List<PlanGraphLiteral> pgPreConds1 = new ArrayList<PlanGraphLiteral>();
		pgPreConds1.add(test3PreCondLiteral1);
		test3EffectLiteral1 = new PlanGraphLiteral(step3Effects);
		List<PlanGraphLiteral> pgEffects1 = new ArrayList<PlanGraphLiteral>();
		pgEffects1.add(test3EffectLiteral1);
		test3 = new PlanGraphStep(step3, pgPreConds1, pgEffects1);

		Predication step4PreConds1 = new Predication("(is hungry)", new ImmutableArray<Term>(new Term[]{}));
		Predication step4PreConds2 = new Predication("(is angry)", new ImmutableArray<Term>(new Term[]{}));
		Predication step4PreConds3 = new Predication("(not (is full))", new ImmutableArray<Term>(new Term[]{}));
		Predication step4Effects1 = new Predication("(not (is hungry))", new ImmutableArray<Term>(new Term[]{}));
		Predication step4Effects2 = new Predication("(not (is angry))", new ImmutableArray<Term>(new Term[]{}));
		Predication step4Effects3 = new Predication("(is full)", new ImmutableArray<Term>(new Term[]{}));
		Conjunction step4PreConds = new Conjunction(step4PreConds1, step4PreConds2, step4PreConds3);
		Conjunction step4Effects = new Conjunction(step4Effects1, step4Effects2, step4Effects3);
		Step step4 = new Step("pigOut", step4PreConds, step4Effects);
		test4PreCondLiteral1 = new PlanGraphLiteral(step4PreConds1);
		test4PreCondLiteral2 = new PlanGraphLiteral(step4PreConds2);
		test4PreCondLiteral3 = new PlanGraphLiteral(step4PreConds3);
		List<PlanGraphLiteral> pgPreConds2 = new ArrayList<PlanGraphLiteral>();
		pgPreConds2.add(test4PreCondLiteral1);
		pgPreConds2.add(test4PreCondLiteral2);
		pgPreConds2.add(test4PreCondLiteral3);
		test4EffectLiteral1 = new PlanGraphLiteral(step4Effects1);
		test4EffectLiteral2 = new PlanGraphLiteral(step4Effects2);
		test4EffectLiteral3 = new PlanGraphLiteral(step4Effects3);
		List<PlanGraphLiteral> pgEffects2 = new ArrayList<PlanGraphLiteral>();
		pgEffects2.add(test4EffectLiteral1);
		pgEffects2.add(test4EffectLiteral2);
		pgEffects2.add(test4EffectLiteral3);
		int initialLevel4 = 3;
		test4 = new PlanGraphStep(step4, initialLevel4, pgPreConds2, pgEffects2);
	}
	
	@Test
	public void isPersistent(){
		assertFalse(test1.isPersistent());
		assertFalse(test2.isPersistent());
		assertFalse(test3.isPersistent());
		assertFalse(test4.isPersistent());
		
		PlanGraphStep persistentPGStep = PlanGraphStep.createPersistentStep(testStep);
		assertTrue(persistentPGStep.isPersistent());
	}
	
	@Test
	public void getInitialLevel(){
		assertEquals(-1, test1.getInitialLevel());
		assertEquals(2, test2.getInitialLevel());
		assertEquals(-1, test3.getInitialLevel());
		assertEquals(3, test4.getInitialLevel());
	}
	
	@Test
	public void setInitialLevel(){
		assertEquals(2, test2.getInitialLevel());
		test2.setInitialLevel(9);
		assertNotEquals(2, test2.getInitialLevel());
		assertEquals(9, test2.getInitialLevel());
	}
	
	@Test
	public void existsAtLevel(){
		int testLevel = 2;
		assertFalse(test1.existsAtLevel(testLevel));
		assertTrue(test2.existsAtLevel(testLevel));
		assertFalse(test3.existsAtLevel(testLevel));
		assertFalse(test4.existsAtLevel(testLevel));
		
		assertEquals(testLevel, test2.getInitialLevel());
		assertFalse(test2.existsAtLevel(1));
		assertTrue(test2.existsAtLevel(2));
		assertTrue(test2.existsAtLevel(3));
	}
	
	@Test
	public void getParentNodes(){
		assertEquals(0, test1.getParentNodes().size());
		assertEquals(0, test2.getParentNodes().size());
		assertEquals(1, test3.getParentNodes().size());
		assertEquals(3, test4.getParentNodes().size());
		assertTrue(test3.getParentNodes().contains(test3PreCondLiteral1));
		assertTrue(test4.getParentNodes().contains(test4PreCondLiteral1));
		assertTrue(test4.getParentNodes().contains(test4PreCondLiteral2));
		assertTrue(test4.getParentNodes().contains(test4PreCondLiteral3));
	}
	
	@Test
	public void getChildNodes(){
		assertEquals(0, test1.getChildNodes().size());
		assertEquals(0, test2.getChildNodes().size());
		assertEquals(1, test3.getChildNodes().size());
		assertEquals(3, test4.getChildNodes().size());
		assertTrue(test3.getChildNodes().contains(test3EffectLiteral1));
		assertTrue(test4.getChildNodes().contains(test4EffectLiteral1));
		assertTrue(test4.getChildNodes().contains(test4EffectLiteral2));
		assertTrue(test4.getChildNodes().contains(test4EffectLiteral3));
	}
	
	@Test
	public void addPlanGraphChild(){
		assertFalse(test2.getChildNodes().contains(testPGLiteral));
		test2.addChildLiteral(testPGLiteral);
		assertTrue(test2.getChildNodes().contains(testPGLiteral));
	}
	
	@Test
	public void addPlanGraphParent(){
		assertFalse(test2.getParentNodes().contains(testPGLiteral));
		test2.addParentLiteral(testPGLiteral);
		assertTrue(test2.getParentNodes().contains(testPGLiteral));
	}
	
	@Test
	public void getStep(){
		assertEquals(testStep,test1.getStep());
		assertNotEquals(testStep,test2.getStep());
		assertNotEquals(testStep,test3.getStep());
		assertNotEquals(testStep,test4.getStep());
	}
	
	@Test
	public void isEquals(){
		assertFalse(test1.equals(test2));
		assertFalse(test1.equals(test3));
		assertFalse(test1.equals(test4));

		PlanGraphStep testPGStep = new PlanGraphStep(testStep);
		assertTrue(test1.equals(testPGStep));
	}
	
	@Test
	public void testToString(){
		assertTrue("testStep[-1]".equals(test1.toString()));
		assertTrue("funkify[2]".equals(test2.toString()));
		assertTrue("fearReaper[-1]".equals(test3.toString()));
		assertTrue("pigOut[3]".equals(test4.toString()));
	}
}
