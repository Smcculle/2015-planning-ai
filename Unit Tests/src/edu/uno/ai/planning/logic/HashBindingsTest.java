package edu.uno.ai.planning.logic;

import org.junit.Test;

public class HashBindingsTest extends BindingsTest {

	@Test
	public void immutability() {
		super.immutability(new HashBindings());
	}

	@Test
	public void setEqual() {
		super.setEqual(new HashBindings());
	}

	@Test
	public void setEqualVariables() {
		super.setEqualVariables(new HashBindings());
	}

	@Test
	public void setEqualConstants() {
		super.setEqualConstants(new HashBindings());
	}

	@Test
	public void setNotEqualConstants() {
		super.setNotEqualConstants(new HashBindings());
	}

	@Test
	public void setNotEqual() {
		super.setNotEqual(new HashBindings());
	}

	@Test
	public void addExistingConstrain() {
		super.addExistingConstrain(new HashBindings());
	}

	@Test
	public void transitivity() {
		super.transitivity(new HashBindings());
	}
	@Test
	public void transitivityWithConstant() {
		super.transitivity(new HashBindings());
	}

	@Test
	public void deepTransitivity() {
		super.deepTransitivity(new HashBindings());
	}

	@Test
	public void deepTransitivityNotEqual() {
		super.deepTransitivityNotEqual(new HashBindings());
	}

	@Test
	public void setEqualTwoInstancesOfTheSameTerm() {
		super.setEqualTwoInstancesOfTheSameTerm(new HashBindings());
	}

	@Test
	public void setNotEqualTwoInstancesOfTheSameTerm() {
		super.setNotEqualTwoInstancesOfTheSameTerm(new HashBindings());
	}
	
	@Test
	public void cannotOnlySetEqualSameTypes(){
		super.cannotOnlySetEqualSameTypes(new HashBindings());
	}
}
