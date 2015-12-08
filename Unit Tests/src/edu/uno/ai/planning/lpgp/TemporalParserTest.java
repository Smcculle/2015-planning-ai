package edu.uno.ai.planning.lpgp;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.DurativeDomain;
import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.io.TemporalParser;

public class TemporalParserTest {
	
	private static String CARGO_DOMAIN = "benchmarks/cargo.pddl";
	private static String STS_DOMAIN = "benchmarks/simple_timed_sat.pddl";

	@Test
	public void parseNonDurativeDomain(){
		TemporalParser parser = new TemporalParser();
		String err = "";
		Domain cargo = null;
		try{
			cargo = parser.parse(new File(CARGO_DOMAIN), Domain.class);
		}catch(IOException ex){ 
			err = ex.getMessage();
		}
		assertTrue(err.equals(""));
		assertNotNull(cargo);
		assertEquals(3,cargo.operators.length);
	}

	@Test
	public void parseDurativeDomain(){
		TemporalParser parser = new TemporalParser();
		String err = "";
		Domain satellite = null;
		try{
			File pddlFile = new File(STS_DOMAIN);
			satellite = parser.parse(pddlFile, DurativeDomain.class);
		}catch(IOException ex){ 
			err = ex.getMessage();
		}
		assertTrue(err.equals(""));
		assertNotNull(satellite);
		// Make sure we have all expected operators
		assertEquals(15,satellite.operators.length);
		// Make sure expected operators exist by name
		for(String name : new String[] {"turn_to-start","turn_to-inv","turn_to-end","switch_on-start","switch_on-inv","switch_on-end",
				"switch_off-start","switch_off-inv","switch_off-end","calibrate-start","calibrate-inv","calibrate-end","take_image-start",
				"take_image-inv","take_image-end"}){
			boolean opFound = false;
			for(Operator op : satellite.operators){
				if(op.name.equals(name)){
					opFound = true;
					break;
				}
			}
			assertTrue(opFound);
		}
	}
}
