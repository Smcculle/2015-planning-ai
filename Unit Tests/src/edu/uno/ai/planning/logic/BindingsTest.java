package edu.uno.ai.planning.logic;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import edu.uno.ai.planning.Settings;

public class BindingsTest {

	private static final Constant c1 = new Constant(Settings.DEFAULT_TYPE, "c1");
	private static final Constant c1_ = new Constant(Settings.DEFAULT_TYPE, "c1");
	private static final Constant c2 = new Constant(Settings.DEFAULT_TYPE, "c2");
	private static final Variable v1 = new Variable(Settings.DEFAULT_TYPE, "v1");
	private static final Variable v1_ = new Variable(Settings.DEFAULT_TYPE, "v1");
	private static final Variable v2 = new Variable(Settings.DEFAULT_TYPE, "v2");
	private static final Variable v2_ = new Variable(Settings.DEFAULT_TYPE, "v2");
	private static final Variable v3 = new Variable(Settings.DEFAULT_TYPE, "v3");
	private static final Variable v4 = new Variable(Settings.DEFAULT_TYPE, "v4");

	protected void immutability(Bindings empty) {
		Bindings bindings = empty.setEqual(v1, c1);
		assertThat(empty, not(bindings));
		assertThat(empty.get(v1), is((Term) v1));
		assertThat(bindings.get(v1), is((Term) c1));
	}
	
	protected void setEqual(Bindings empty) {
		Bindings modified1 = empty.setEqual(v1, c1);
		assertSame(modified1.get(v1), c1);
		assertThat(modified1.setEqual(v1, c2), is(nullValue()));

		Bindings modified2 = empty.setEqual(c1, v1); // swapped arguments
		assertSame(modified2.get(v1), c1);
	}

	protected void setEqualVariables(Bindings empty) {
		Bindings bindings = empty.setEqual(v1, v2);
		assertThat(bindings.get(v1), is((Term) v1));
		assertThat(bindings.get(v2), is((Term) v2));

		// TODO: is this the correct behavior?
	}

	protected void setEqualConstants(Bindings empty) {
		assertThat(empty.setEqual(c1, c2), is(nullValue()));
	}

	protected void setNotEqualConstants(Bindings empty) {
		assertThat(empty.setNotEqual(c1, c1), is(nullValue()));
	}

	protected void setNotEqual(Bindings empty) {
		Bindings bindings = empty.setNotEqual(v1, v2).setNotEqual(v1, c1);
		assertThat(bindings.setEqual(v1, v2), is(nullValue()));
		assertThat(bindings.setEqual(v2, v1), is(nullValue())); // swapped argument
		assertThat(bindings.setEqual(v1, c1), is(nullValue()));
		assertThat(bindings.setEqual(c1, v1), is(nullValue())); // swapped argument
	}

	protected void addExistingConstrain(Bindings empty) {
		Bindings bindings = empty.setEqual(v1, c1).setEqual(v1, v2).setNotEqual(v2, v3);
		assertThat(bindings.setEqual(v1, c1), is(bindings));
		assertThat(bindings.setEqual(v1, v2), is(bindings));
		assertThat(bindings.setNotEqual(v2, v3), is(bindings));

		assertThat(bindings.setEqual(c1, c1), is(bindings));
		assertThat(bindings.setNotEqual(c1, c2), is(bindings));

		// TODO: bindings are immutable but what if the constrain already exists?
	}

	protected void transitivity(Bindings empty) {
		Bindings bindings1 = empty.setEqual(v1, v2).setEqual(v2, v3);
		assertThat(bindings1.setEqual(v1, v3), is(bindings1));
		assertThat(bindings1.setNotEqual(v1, v3), is(nullValue()));

		Bindings bindings2 = empty.setEqual(v1, c1).setEqual(v1, v2);
		assertThat(bindings2.setEqual(v2, c1), is(bindings2));
		assertThat(bindings2.setNotEqual(v2, c1), is(nullValue()));
		assertThat(bindings2.setEqual(v2, c2), is(nullValue()));
	}

	protected void deepTransitivity(Bindings empty) {
		Bindings bindings = empty
				.setEqual(v1, v2).setEqual(v3, v4)
				.setEqual(v1, c1)
				.setEqual(v2, v3);

		assertThat(bindings.get(v4), is((Term) c1));
	}

	protected void setEqualTwoInstancesOfTheSameTerm(Bindings empty) {
		assertThat(empty.setEqual(c1, c1_), is(empty));
		assertThat(empty.setEqual(v1, v1_), is(empty));
		assertThat(empty.setEqual(v1, c1).get(v1_), is((Term) c1));
	}

	protected void setNotEqualTwoInstancesOfTheSameTerm(Bindings empty) {
		assertThat(empty.setNotEqual(c1, c1_), is(nullValue()));
		assertThat(empty.setNotEqual(v1, v1_), is(nullValue()));
	}
}
