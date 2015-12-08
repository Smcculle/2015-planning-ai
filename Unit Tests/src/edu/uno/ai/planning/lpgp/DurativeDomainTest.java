package edu.uno.ai.planning.lpgp;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.uno.ai.planning.DurativeDomain;
import edu.uno.ai.planning.io.TemporalParser;

public class DurativeDomainTest {
	
	private static String CARGO_DOMAIN = "benchmarks/cargo.pddl";
	private static String STS_DOMAIN = "benchmarks/simple_timed_sat.pddl";
	
	private DurativeDomain satellite = null;
	private DurativeDomain cargo = null;
	private String err = "";
	
	@Before
	public void createSatelliteDomain(){
		TemporalParser parser = new TemporalParser();
		try{
			File stsPddlFile = new File(STS_DOMAIN);
			File cargoPddlFile = new File(CARGO_DOMAIN);
			satellite = parser.parse(stsPddlFile, DurativeDomain.class);
			cargo = parser.parse(cargoPddlFile, DurativeDomain.class);
		}catch(IOException ex){ 
			err = ex.getMessage();
		}
		assertNotNull(satellite);
		assertNotNull(cargo);
		assertTrue(err.equals(""));
	}
	
	@Test
	public void testDurativeDomain(){
		assertTrue(satellite.isDurativeDomain());
		assertFalse(cargo.isDurativeDomain());
	}

}
