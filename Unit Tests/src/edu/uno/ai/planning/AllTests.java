package edu.uno.ai.planning;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ edu.uno.ai.planning.logic.HashBindingsTest.class,
		edu.uno.ai.planning.logic.ListBindingsTest.class, 
		edu.uno.ai.planning.logic.NormalFormsTest.class,
		edu.uno.ai.planning.graphplan.PlanGraphTest.class,
		edu.uno.ai.planning.graphplan.GraphPlanTest.class})
public class AllTests {
	/* nothing */}
