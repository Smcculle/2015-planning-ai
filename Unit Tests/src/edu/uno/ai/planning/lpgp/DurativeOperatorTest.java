package edu.uno.ai.planning.lpgp;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.DurativeDomain;
import edu.uno.ai.planning.DurativeOperator;
import edu.uno.ai.planning.Operator;
import edu.uno.ai.planning.io.TemporalParser;

public class DurativeOperatorTest {
	
	private Domain satellite = null;
	private String err = "";
	
	private Map<String,Object> actionMap = new HashMap<String,Object>();
	
	@Before
	public void createSatelliteDomain(){
		TemporalParser parser = new TemporalParser();
		try{
			File pddlFile = new File("benchmarks/simple_timed_sat.pddl");
			satellite = parser.parse(pddlFile, DurativeDomain.class);
		}catch(IOException ex){ 
			err = ex.getMessage();
		}
		assertNotNull(satellite);
		assertTrue(err.equals(""));

		ArrayList<Operator> actions = satellite.operators.clone();
		for(Operator o : actions)
			actionMap.put(o.name, o);
	}
	
	@Test
	public void testDurativeOperator(){
		assertNotNull((DurativeOperator) actionMap.get("turn_to-start"));
		assertEquals(5, ((DurativeOperator) actionMap.get("turn_to-start")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("turn_to-inv"));
		assertNotNull((DurativeOperator) actionMap.get("turn_to-end"));
		assertEquals(5, ((DurativeOperator) actionMap.get("turn_to-end")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("switch_on-start"));
		assertEquals(2, ((DurativeOperator) actionMap.get("switch_on-start")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("switch_on-inv"));
		assertNotNull((DurativeOperator) actionMap.get("switch_on-end"));
		assertEquals(2, ((DurativeOperator) actionMap.get("switch_on-end")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("switch_off-start"));
		assertEquals(1, ((DurativeOperator) actionMap.get("switch_off-start")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("switch_off-inv"));
		assertNotNull((DurativeOperator) actionMap.get("switch_off-end"));
		assertEquals(1, ((DurativeOperator) actionMap.get("switch_off-end")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("calibrate-start"));
		assertEquals(5, ((DurativeOperator) actionMap.get("calibrate-start")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("calibrate-inv"));
		assertNotNull((DurativeOperator) actionMap.get("calibrate-end"));
		assertEquals(5, ((DurativeOperator) actionMap.get("calibrate-end")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("take_image-start"));
		assertEquals(7, ((DurativeOperator) actionMap.get("take_image-start")).getDuration());
		assertNotNull((DurativeOperator) actionMap.get("take_image-inv"));
		assertNotNull((DurativeOperator) actionMap.get("take_image-end"));
		assertEquals(7, ((DurativeOperator) actionMap.get("take_image-end")).getDuration());
	}

}
