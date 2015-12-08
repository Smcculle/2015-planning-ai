package edu.uno.ai.planning.lpgp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.uno.ai.planning.DurativeDomain;
import edu.uno.ai.planning.io.TemporalParser;

public class DurativeProblemTest {
	
	private static String CARGO_DOMAIN = "benchmarks/cargo.pddl";
	private static String STS_DOMAIN = "benchmarks/simple_timed_sat.pddl";
	
	private DurativeDomain satelliteDomain = null;
	private DurativeDomain cargoDomain = null;
	private String err = "";
	
	@Before
	public void createSatelliteDomain(){
		TemporalParser parser = new TemporalParser();
		try{
			File stsPddlFile = new File(STS_DOMAIN);
			File cargoPddlFile = new File(CARGO_DOMAIN);
			satelliteDomain = parser.parse(stsPddlFile, DurativeDomain.class);
			cargoDomain = parser.parse(cargoPddlFile, DurativeDomain.class);
		}catch(IOException ex){ 
			err = ex.getMessage();
		}
		assertNotNull(satelliteDomain);
		assertNotNull(cargoDomain);
		assertTrue(err.equals(""));
	}
	
	@Test
	public void testDurativeProblem(){

	}

}
