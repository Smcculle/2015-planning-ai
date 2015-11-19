/**
 * Tests for Threat class
 */
package edu.uno.ai.planning.pop;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.*;

import org.junit.Test;

public class ThreatTest {

	
	public Class<Threat> describedClass(){
		return Threat.class;
	}
	
	@Test
	public void is_a_flaw() {
		assertThat(describedClass(), typeCompatibleWith(Flaw.class));
	}
}
