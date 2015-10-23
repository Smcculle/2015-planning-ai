package edu.uno.ai.planning;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	edu.uno.ai.planning.logic.HashBindingsTest.class,
	edu.uno.ai.planning.logic.ListBindingsTest.class,
	edu.uno.ai.planning.logic.NormalFormsTest.class,
	edu.uno.ai.planning.graphplan.ConversionUntilTest.class,
	edu.uno.ai.planning.graphplan.PlanGraphLevelMutexTest.class,
	edu.uno.ai.planning.graphplan.PlanGraphLiteralTest.class,
	edu.uno.ai.planning.graphplan.PlanGraphStepTest.class,
	edu.uno.ai.planning.graphplan.PlanGraphTest.class
})
public class AllTests {/* nothing */}
