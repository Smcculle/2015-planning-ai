package edu.uno.ai.planning.logic;

import static org.junit.Assert.*;

import edu.uno.ai.planning.Settings;

public class BindingsTest {

	private static final Constant c1 = new Constant(Settings.DEFAULT_TYPE, "c1");
	private static final Variable v1 = new Variable(Settings.DEFAULT_TYPE, "v1");
	
	protected void setEquals(Bindings empty) {
		Bindings original = empty;
		assertSame(original.get(v1), v1);
		Bindings modified = original.setEqual(v1, c1);
		assertSame(modified.get(v1), c1);
		assertSame(original.get(v1), v1);
	}
}
