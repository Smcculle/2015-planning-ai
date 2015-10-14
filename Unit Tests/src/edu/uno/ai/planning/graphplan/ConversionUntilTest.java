package edu.uno.ai.planning.graphplan;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.Predication;
import edu.uno.ai.planning.logic.Term;
import edu.uno.ai.planning.util.ConversionUtil;

public class ConversionUntilTest {

	@Test
	public void expressionToLiterals(){
		Predication lions = new Predication("lions", new Term[] {});
		Predication tigers = new Predication("tigers", new Term[] {});
		Predication bears = new Predication("bears", new Term[] {});
		
		Expression test1 = new Conjunction(lions, tigers, bears);
		List<Literal> results1 = ConversionUtil.expressionToLiterals(test1);
		assertEquals(3, results1.size());
		assertTrue(results1.contains(lions));
		assertTrue(results1.contains(tigers));
		assertTrue(results1.contains(bears));
	}
}
