package edu.uno.ai.planning.lpgp;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.DurativeDomain;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.io.TemporalParser;

public class DurativeProblemTest {
	
	private static String CARGO_DOMAIN = "benchmarks/cargo.pddl";
	private static String CARGO_PROBLEM = "benchmarks/deliver_1.pddl";
	private static String STS_DOMAIN = "benchmarks/simple_timed_sat.pddl";
	private static String STS_PROBLEM = "benchmarks/simple_sat_1.pddl";
	
	private Domain satelliteDomain = null;
	private Problem satelliteProblem = null;
	private Domain cargoDomain = null;
	private Problem cargoProblem = null;
	private String err = "";
	
	@Before
	public void createSatelliteDomain(){
		TemporalParser parser = new TemporalParser();
		try{
			File stsPddlFile = new File(STS_DOMAIN);
			File stsProblemFile = new File(STS_PROBLEM);
			File cargoPddlFile = new File(CARGO_DOMAIN);
			File cargoProblemFile = new File(CARGO_PROBLEM);
			satelliteDomain = parser.parse(stsPddlFile, Domain.class);
			satelliteProblem = parser.parse(stsProblemFile, Problem.class);
			cargoDomain = parser.parse(cargoPddlFile, Domain.class);
			cargoProblem = parser.parse(cargoProblemFile, Problem.class);
		}catch(IOException ex){ 
			err = ex.getMessage();
		}
		assertNotNull(satelliteDomain);
		assertNotNull(cargoDomain);
		assertNotNull(satelliteProblem);
		assertNotNull(cargoProblem);
		assertTrue(err.equals(""));
	}
	
	@Test
	public void testDurativeProblem(){
		assertTrue(satelliteProblem.domain instanceof DurativeDomain);
		assertTrue(cargoProblem.domain instanceof DurativeDomain);
		assertTrue(((DurativeDomain) satelliteProblem.domain).isDurativeDomain());
		assertFalse(((DurativeDomain) cargoProblem.domain).isDurativeDomain());
	}

}
