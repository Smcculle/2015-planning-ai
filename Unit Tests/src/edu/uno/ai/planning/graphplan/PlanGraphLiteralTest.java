package edu.uno.ai.planning.graphplan;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.util.ImmutableArray;

public class PlanGraphLiteralTest {
	
	private PlanGraphLiteral test1;
	private PlanGraphLiteral test2;
	private PlanGraphLiteral test3;
	private PlanGraphLiteral test4;
	
	@Before
	public void createPlanGraphLiteral(){
		Predication step1PreConds = new Predication("step1PreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication step2PreConds = new Predication("step2PreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication step3PreConds = new Predication("step3PreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication step3Effects = new Predication("step3Effects", new ImmutableArray<Term>(new Term[] {}));
		Step step1 = new Step("step1", step1PreConds, step2PreConds);
		Step step2 = new Step("step2", step2PreConds, step3PreConds);
		Step step3 = new Step("step3", step3PreConds, step3Effects);
		PlanGraphStep pgStep1 = new PlanGraphStep(step1);
		PlanGraphStep pgStep2 = new PlanGraphStep(step2);
		PlanGraphStep pgStep3 = new PlanGraphStep(step3);
		int initialLevel1 = 3;
		Predication predication1 = new Predication("is", new Term[]{});
		List<PlanGraphStep> children1 = new ArrayList<PlanGraphStep>();
		children1.add(pgStep1);
		children1.add(pgStep2);
		List<PlanGraphStep> parents1 = new ArrayList<PlanGraphStep>();
		parents1.add(pgStep3);
		test1 = new PlanGraphLiteral(predication1, initialLevel1, children1, parents1);

		Predication step4PreConds = new Predication("step4PreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication step5PreConds = new Predication("step5PreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication step6PreConds = new Predication("step6PreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication step7PreConds = new Predication("step7PreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication step7Effects = new Predication("step7Effects", new ImmutableArray<Term>(new Term[] {}));
		Step step4 = new Step("step4", step4PreConds, step5PreConds);
		Step step5 = new Step("step5", step5PreConds, step6PreConds);
		Step step6 = new Step("step6", step6PreConds, step7PreConds);
		Step step7 = new Step("step7", step7PreConds, step7Effects);
		PlanGraphStep pgStep4 = new PlanGraphStep(step4);
		PlanGraphStep pgStep5 = new PlanGraphStep(step5);
		PlanGraphStep pgStep6 = new PlanGraphStep(step6);
		PlanGraphStep pgStep7 = new PlanGraphStep(step7);
		Predication predication2 = new Predication("catch tiger", new Term[]{});
		List<PlanGraphStep> children2 = new ArrayList<PlanGraphStep>();
		children2.add(pgStep4);
		children2.add(pgStep5);
		List<PlanGraphStep> parents2 = new ArrayList<PlanGraphStep>();
		parents2.add(pgStep6);
		parents2.add(pgStep7);
		test2 = new PlanGraphLiteral(predication2, children2, parents2);

		Predication predication3 = new Predication("is flying", new Term[]{});
		int initialLevel2 = 8;
		test3 = new PlanGraphLiteral(predication3, initialLevel2);

		Predication predication4 = new Predication("fears", new Term[]{});
		test4 = new PlanGraphLiteral(predication4);
	}
	
	@Test
	public void getInitialLevel(){
		assertEquals(3, test1.getInitialLevel());
		assertEquals(-1, test2.getInitialLevel());
		assertEquals(8, test3.getInitialLevel());
		assertEquals(-1, test4.getInitialLevel());
	}
	
	@Test
	public void setInitialLevel(){
		test2.setInitialLevel(5);
		assertEquals(5, test2.getInitialLevel());
	}
	
	@Test
	public void getLiteral(){
		assertTrue(test1.getLiteral() instanceof Predication);
		Predication testPred1 = (Predication) test1.getLiteral();
		assertEquals("is",testPred1.predicate);

		assertTrue(test2.getLiteral() instanceof Predication);
		Predication testPred2 = (Predication) test2.getLiteral();
		assertEquals("catch tiger",testPred2.predicate);

		assertTrue(test3.getLiteral() instanceof Predication);
		Predication testPred3 = (Predication) test3.getLiteral();
		assertEquals("is flying",testPred3.predicate);

		assertTrue(test4.getLiteral() instanceof Predication);
		Predication testPred4 = (Predication) test4.getLiteral();
		assertEquals("fears",testPred4.predicate);
	}
	
	@Test
	public void getParentNodes(){
		assertEquals(1,test1.getParentNodes().size());
		assertEquals(2,test2.getParentNodes().size());
		assertEquals(0,test3.getParentNodes().size());
		assertEquals(0,test4.getParentNodes().size());
	}
	
	@Test
	public void addParentStepNode(){
		Predication newStepPreConds = new Predication("newStepPreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication newStepEffects = new Predication("newStepEffects", new ImmutableArray<Term>(new Term[] {}));
		Step newStep = new Step("new step", newStepPreConds, newStepEffects);
		PlanGraphStep pgNewStep = new PlanGraphStep(newStep); 
		assertFalse(test3.getParentNodes().contains(pgNewStep));
		assertEquals(0,test3.getParentNodes().size());
		test3.addParentStep(pgNewStep); 
		assertEquals(1,test3.getParentNodes().size());
		assertTrue(test3.getParentNodes().contains(pgNewStep));
	}
	
	@Test
	public void getChildrenNodes(){
		assertEquals(2,test1.getChildNodes().size());
		assertEquals(2,test2.getChildNodes().size());
		assertEquals(0,test3.getChildNodes().size());
		assertEquals(0,test4.getChildNodes().size());
	}
	
	@Test
	public void addChildNode(){
		Predication newStepPreConds = new Predication("newStepPreConds", new ImmutableArray<Term>(new Term[] {}));
		Predication newStepEffects = new Predication("newStepEffects", new ImmutableArray<Term>(new Term[] {}));
		Step newStep = new Step("new step", newStepPreConds, newStepEffects);
		PlanGraphStep pgNewStep = new PlanGraphStep(newStep);
		assertFalse(test4.getChildNodes().contains(pgNewStep));
		assertEquals(0,test4.getChildNodes().size());
		test4.addChildStep(pgNewStep);
		assertTrue(test4.getChildNodes().contains(pgNewStep));
		assertEquals(1,test4.getChildNodes().size());
	}

	@Test
	public void equals(){
		Predication predication2 = new Predication("catch tiger", new Term[]{});
		PlanGraphLiteral testLiteral = new PlanGraphLiteral(predication2);
		assertTrue(testLiteral.equals(test2));
	}
	
	@Test
	public void testToString(){
		assertEquals("(is)[3]",test1.toString());
		assertEquals("(catch tiger)[-1]",test2.toString());
		assertEquals("(is flying)[8]",test3.toString());
		assertEquals("(fears)[-1]",test4.toString());
	}
	
}
