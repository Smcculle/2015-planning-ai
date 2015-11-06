package edu.uno.ai.planning;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	edu.uno.ai.planning.util.LiteralCollectorTest.class,
	edu.uno.ai.planning.logic.HashBindingsTest.class,
	edu.uno.ai.planning.logic.ListBindingsTest.class,
	edu.uno.ai.planning.logic.NormalFormsTest.class,
})
public class AllTests {/* nothing */}
