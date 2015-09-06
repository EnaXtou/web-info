package cz.plsi.webInfo.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.EntityCommon;
import cz.plsi.webInfo.shared.dataStore.entities.Help;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;

public class ExportTeamStageActionTest {

	private static final String CODE = "_code";
	private static final String TEAM = "team_";
	private static final String HELP = "help_";
	private static final String STAGE = "stage_";
	private static final String HELP_1 = "help_1_";
	private static final String HELP_2 = "help_2_";
	private static final String RESULT = "result_";
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();

		for (int i = 0; i < 4; i++) {
			Team team = new Team(TEAM + i);
			team.setCode(TEAM + i + CODE);
			EMF.add(team);
		}

		for (int i = 0; i < 15; i++) {
			EntityCommon help = new Help(HELP + i);
			EMF.add(help);
		}

		for (int i = 1; i < 12; i++) {
			EntityCommon stage = null;
			if (i == 2) {
				stage = new Stage(STAGE + i, i, HELP_1 + i, null, null);
				
			} else {
				stage = new Stage(STAGE + i, i, HELP_1 + i, HELP_2 + i, RESULT + i);
				
			}
			EMF.add(stage);
		}
		
		EntityCommon stage2 = new Stage("KONEC", -1, null, null, null);
		EMF.add(stage2);
		
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	@Ignore
	public void testGetResults() {
		TeamStageAction teamStageAction = new TeamStageAction();
		List<String> errors = new ArrayList<>();

		// team 1 je na stanovišti 1
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1, 1);
		EMF.add(teamStage);
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1, 1);
		EMF.add(teamStage);
		
		
		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		teamStageAction.nextStage(TEAM + 0 + CODE, STAGE + 1, errors);
		
		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		teamStageAction.nextStage(TEAM + 3 + CODE, STAGE + 1, errors);
		
		// team 1 může pokračovat na druhou stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);
		
		// team 1 může pokračovat na druhou stage (dovoluje se přeskočení)
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3, errors);

		// team 1 může pokračovat na druhou stage (dovoluje se přeskočení)
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4, errors);

		// team 4 zadal 1. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1, errors);

		// team 4 zadal 2. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2, errors);

		// team 4 zadal neexistující kód stanoviště chyba
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);

		
		ExportTeamStageAction exportTeamStageAction = new ExportTeamStageAction();
		
		List<String[]> exportedTeamsOnStages = exportTeamStageAction.exportTeamsOnStages();
		// devět záznamů + záhlaví
		assertEquals(10, exportedTeamsOnStages.size());
		
		ArrayList<String[]> expected = new ArrayList<>();
		String[] expectedLine = new String[] {"Time", TEAM + 0, TEAM + 1, TEAM + 2, TEAM + 3, TEAM + 4};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "34", "1", "34", "34", "34"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "34", "1", "2", "34", "34"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "3", "1", "2", "34", "34"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "3", "1", "2", "4", "34"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "3", "1", "2", "4", "34"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "3", "1", "2", "4", "34"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "3", "1", "2", "4", "34"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "3", "1", "2", "4", "5"};
		expected.add(expectedLine);
		expectedLine = new String[] {"0", "4", "1", "3", "5", "2"};
		expected.add(expectedLine);
		String[] actualLine;
		for (int i = 0; i < expected.size(); i++) {
			expectedLine = expected.get(i);
			actualLine = exportedTeamsOnStages.get(i);
			actualLine[0] = "0"; //neporovnávej datumy
			assertArrayEquals(expectedLine, actualLine);
		}
		
		
	}

}
