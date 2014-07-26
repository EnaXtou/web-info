package cz.plsi.webInfo.actions; 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1);
		EMF.add(teamStage);
		
		teamStage = new TeamStage(TEAM + 2, STAGE + 1);
		EMF.add(teamStage);
		
		String actual = TeamStageAction.getHelp(Team.getCode(TEAM + 1), HELP + 1);
		assertEquals(HELP_1 + 1, actual);
		
		actual = TeamStageAction.getHelp(Team.getCode(TEAM + 1), HELP + 1);
		assertEquals(HELP_1 + 1, actual);

		actual = TeamStageAction.getHelp(Team.getCode(TEAM + 1), HELP + 1);
		assertEquals("Již použité heslo.", actual);
		
		actual = TeamStageAction.getHelp(Team.getCode(TEAM + 1), HELP + 3);
		assertEquals(HELP_2 + 1, actual);
		
		
		// druhý tým je na druhém stanovišti
		// první stále na prvním
		teamStage = new TeamStage(TEAM + 2, STAGE + 2);
		EMF.add(teamStage);
		// druhý tým použije heslo již použité týmem 1
		actual = TeamStageAction.getHelp(Team.getCode(TEAM + 2), HELP + 1);
		assertEquals(HELP_1 + 2, actual); // dostanu 1. nápovìdu z 2. stanovištì
	}

	@Test
	public void testNextStage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetResults() {
		fail("Not yet implemented");
	}

}
