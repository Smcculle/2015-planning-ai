package edu.uno.ai.planning;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	edu.uno.ai.planning.util.LiteralCollectorTest.class,
	edu.uno.ai.planning.logic.HashBindingsTest.class,
	edu.uno.ai.planning.logic.ListBindingsTest.class,
	edu.uno.ai.planning.logic.NormalFormsTest.class,
	edu.uno.ai.planning.pop.DirectedAcyclicGraphTest.class,
	edu.uno.ai.planning.pop.POPGraphTest.class,
	edu.uno.ai.planning.pop.FlawTest.class,
	edu.uno.ai.planning.pop.OpenConditionTest.class,
	edu.uno.ai.planning.pop.ThreatTest.class,
	edu.uno.ai.planning.pop.CloneTypeMismatchExceptionTest.class,
	edu.uno.ai.planning.graphplan.PlanGraphTest.class,
	edu.uno.ai.planning.graphplan.PlanGraphStepTest.class,
	edu.uno.ai.planning.graphplan.PlanGraphLiteralTest.class,
	edu.uno.ai.planning.graphplan.GraphPlanTest.class,
})
public class AllTests {
	/* nothing */}
