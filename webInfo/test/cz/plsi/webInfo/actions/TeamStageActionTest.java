package cz.plsi.webInfo.actions; 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.EntityCommon;
import cz.plsi.webInfo.shared.dataStore.entities.Help;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;

public class TeamStageActionTest {
	
	private static final String TEAM = "team_";
	private static final String HELP = "help_";
	private static final String STAGE = "stage_";
	private static final String HELP_1 = "help_1_";
	private static final String HELP_2 = "help_2_";
	private static final String RESULT = "result_";
	private final LocalServiceTestHelper helper =
	        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
	    helper.setUp();
	    
	    for (int i = 0; i < 20; i++) {
	    	EntityCommon team = new Team(TEAM + i);
	    	EMF.add(team);
		}
	    
	    for (int i = 0; i < 15; i++) {
	    	EntityCommon help = new Help(HELP + i);
	    	EMF.add(help);
	    }
	    
	    for (int i = 0; i < 12; i++) {
	    	EntityCommon stage = new Stage(STAGE + i, i, HELP_1 + i, HELP_2 + i, RESULT + i);
	    	EMF.add(stage);
	    }
	}
	
	@After
	public void tearDown() {
	    helper.tearDown();
	}


	@Test
	public void testGetHelp() {
		List<String> errors = new ArrayList<>();
		
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1);
		EMF.add(teamStage);
		
		teamStage = new TeamStage(TEAM + 2, STAGE + 1);
		EMF.add(teamStage);
		
		String actual = TeamStageAction.getHelp(Team.getCode(TEAM + 1), HELP + 1, errors);
		assertEquals(HELP_1 + 1, actual);
		
		actual = TeamStageAction.getHelp(Team.getCode(TEAM + 1), HELP + 1, errors);
		assertNull(actual);
		assertEquals(1, errors.size());
		errors.clear();
		
		actual = TeamStageAction.getHelp(Team.getCode(TEAM + 1), HELP + 3, errors);
		assertEquals(HELP_2 + 1, actual);
		
		
		// druh� t�m je na druh�m stanovi�ti
		// prvn� st�le na prvn�m
		teamStage = new TeamStage(TEAM + 2, STAGE + 2);
		EMF.add(teamStage);
		// druh� t�m pou�ije heslo ji� pou�it� t�mem 1
		actual = TeamStageAction.getHelp(Team.getCode(TEAM + 2), HELP + 1, errors);
		errors.size();
		assertEquals(HELP_1 + 2, actual); // dostanu 1. n�pov�du z 2. stanovi�t�
	}

	@Test
	public void testNextStage() {
		List<String> errors = new ArrayList<>();
		
		// team 1 je na stanovi�ti 1
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1);
		EMF.add(teamStage);
		// team 2 je na stanovi�ti 1		
		teamStage = new TeamStage(TEAM + 2, STAGE + 1);
		EMF.add(teamStage);
		
		// team 1 ji� je na prvn� stage
		boolean nextStageActual = TeamStageAction.nextStage(TEAM + 1, STAGE + 1, errors);
		assertFalse(nextStageActual);
		assertEquals(1, errors.size());
		errors.clear();
		
		// team 1 m��e pokra�ovat na druhou stage
		nextStageActual = TeamStageAction.nextStage(Team.getCode(TEAM + 1), STAGE + 2, errors);
		assertTrue(nextStageActual);
		
		// team 1 m��e pokra�ovat na �tvrtou stage (dovoluje se p�esko�en�)
		nextStageActual = TeamStageAction.nextStage(Team.getCode(TEAM + 1), STAGE + 4, errors);
		assertTrue(nextStageActual);
		
		// team 1 zadal znovu druhou stage chyba, ji� na�t�ven� stage
		nextStageActual = TeamStageAction.nextStage(Team.getCode(TEAM + 1), STAGE + 2, errors);
		assertFalse(nextStageActual);
		assertEquals(1, errors.size());
		errors.clear();
		
		// team 4 zadal 1. stanovi�t�
		nextStageActual = TeamStageAction.nextStage(Team.getCode(TEAM + 4), STAGE + 1, errors);
		assertTrue(nextStageActual);
		
		// team 4 zadal 2. stanovi�t�
		nextStageActual = TeamStageAction.nextStage(Team.getCode(TEAM + 4), STAGE + 2, errors);
		assertTrue(nextStageActual);
		
		// team 4 zadal neexistuj�c� k�d stanovi�t� chyba
		nextStageActual = TeamStageAction.nextStage(Team.getCode(TEAM + 4), STAGE + "_NOT_EXISTS", errors);
		assertFalse(nextStageActual);
		assertEquals(1, errors.size());
	}

	@Test
	public void testGetResults() {
		TeamStageAction.getResults("");
		fail("Not yet implemented");
	}

}
