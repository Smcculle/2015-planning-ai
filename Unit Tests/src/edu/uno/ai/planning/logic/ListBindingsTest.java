package edu.uno.ai.planning.logic;

import org.junit.Test;

public class ListBindingsTest extends BindingsTest {

	@Test
	public void immutability() {
		super.immutability(new ListBindings());
	}

	@Test
	public void setEqual() {
		super.setEqual(new ListBindings());
	}

	@Test
	public void setEqualVariables() {
		super.setEqualVariables(new ListBindings());
	}

	@Test
	public void setEqualConstants() {
		super.setEqualConstants(new ListBindings());
	}

	@Test
	public void setNotEqualConstants() {
		super.setNotEqualConstants(new ListBindings());
	}

	@Test
	public void setNotEqual() {
		super.setNotEqual(new ListBindings());
	}

	@Test
	public void addExistingConstrain() {
		super.addExistingConstrain(new ListBindings());
	}

	@Test
	public void transitivity() {
		super.transitivity(new ListBindings());
	}

	@Test
	public void deepTransitivity() {
		super.deepTransitivity(new ListBindings());
	}

	@Test
	public void setEqualTwoInstancesOfTheSameTerm() {
		super.setEqualTwoInstancesOfTheSameTerm(new ListBindings());
	}

	@Test
	public void setNotEqualTwoInstancesOfTheSameTerm() {
		super.setNotEqualTwoInstancesOfTheSameTerm(new ListBindings());
	}
}
