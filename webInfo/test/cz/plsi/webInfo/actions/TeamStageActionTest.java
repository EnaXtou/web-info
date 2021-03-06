package cz.plsi.webInfo.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.EntityCommon;
import cz.plsi.webInfo.shared.dataStore.entities.Help;
import cz.plsi.webInfo.shared.dataStore.entities.MessageToTeams;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;
import cz.plsi.webInfo.shared.dataStore.entities.TeamStage;

public class TeamStageActionTest {

	private static final String ONLY_LINEAR = "OnlyLinear";
	private static final String TEST_MESSAGE = "Test message";
	private static final String WELCOME_START = "Tak vás tu vítáme! Stanoviště " + ONLY_LINEAR + "." ;
	private static final String WELCOME_END = "";
	private static final String CODE = "_code";
	private static final String TEAM = "team_";
	private static final String HELP = "help_";
	private static final String STAGE = "stage_";
	private static final String HELP_1_R = "Nápověda: help_1_";
	private static final String HELP_2_R = "Nápověda: help_2_";
	private static final String RESULT_R = "Řešení: result_";
	private static final String HELP_1 = "help_1_";
	private static final String HELP_2 = "help_2_";
	private static final String RESULT = "result_";
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();

		for (int i = 0; i < 20; i++) {
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
	
	
	@Ignore("Již testováno v testu cz.plsi.webInfo.actions.TeamStageActionParallelTest")
	@Test
	public void testGetHelp() {
		List<String> errors = new ArrayList<>();
		
		Team team = new Team();
		team.setName(TEAM + 1);
		team = team.getList().get(0);
		team = (Team) EMF.find(team); 
		// Chycení širokem - odebrání nápovědy
		team.setHelpsCount(-1);
		
		EMF.update(team);
		
		// team 1 je na stanovišti 1
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1, 1);
		EMF.add(teamStage);

		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1, 1);
		EMF.add(teamStage);
		TeamStageAction teamStageAction = new TeamStageAction();
		
		// team 1 bych chycen širokem
		String actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 0, "L", errors);
		assertEquals(TeamStageAction.HELP_STOLEN, actual);
		
		// team 1 použije již použité heslo
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 0, "L", errors);
		assertEquals(1, errors.size());
		errors.clear();

		// bez chyby tým dostane nápovědu.
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 1, "L", errors);
		assertEquals(HELP_1_R + 1, actual);
		
		// team 1 použije již použité heslo
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 1, "L", errors);
		assertEquals(1, errors.size());
		errors.clear();

		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 3, "L", errors);
		assertEquals(HELP_2_R + 1, actual);

		// druhý tým je na druhém stanovišti
		// první stále na prvním stanovyšti
		teamStage = new TeamStage(TEAM + 2, STAGE + 2, 2);
		EMF.add(teamStage);
		
		// druhý tým použije heslo již použitý týmem jedna
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 1, "L", errors);
		errors.size();
		assertEquals(HELP_1_R + 2, actual); // dostanu 1. nápovědu z 2. stanoviště

		// team dva si řekne o další nápovědu, ale na druhém stanovišti již žádná není k dispozici
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 2, "L", errors);
		errors.size();
		assertEquals(1, errors.size()); // již není nápověda
		errors.clear();
		
		// druhý tým je na třetím stanovišti a může použít heslo 2 znovu
		teamStage = new TeamStage(TEAM + 2, STAGE + 3, 3);
		EMF.add(teamStage);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 2, "L", errors);
		assertTrue(errors.isEmpty());
		assertEquals(HELP_1_R + 3, actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 3, "L", errors);
		assertTrue(errors.isEmpty());
		assertEquals(HELP_2_R + 3, actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 6, "L", errors);
		assertTrue(errors.isEmpty());
		assertEquals(RESULT_R + 3, actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 7, "L", errors);
		assertFalse(errors.isEmpty());
		
		
		
	}

	@Test
	public void testNextStage() {
		List<String> errors = new ArrayList<>();

		// team 1 je na stanovišti 1
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1, 1, ONLY_LINEAR);
		EMF.add(teamStage);
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1, 1, ONLY_LINEAR);
		EMF.add(teamStage);

		TeamStageAction teamStageAction = new TeamStageAction();
		// team 1 již je na první stage
		teamStageAction.nextStage(TEAM + 1, STAGE + 1, errors);
		assertEquals(1, errors.size());
		errors.clear();

		// team 1 může pokračovat na druhou stage
		String nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);
		assertEquals(WELCOME_START + 2 + WELCOME_END, nextStageActual);

		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3, errors);
		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4, errors);
		assertEquals(WELCOME_START + 4 + WELCOME_END, nextStageActual);

		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);
		assertEquals(1, errors.size());
		errors.clear();

		// team 4 zadal 1. stanoviště
		nextStageActual = teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1, errors);
		assertEquals(WELCOME_START + 1 + WELCOME_END,nextStageActual);

		// team 4 zadal 2. stanoviště
		nextStageActual = teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2, errors);
		assertEquals(WELCOME_START + 2 + WELCOME_END,nextStageActual);

		// team 4 zadal neexistující kód stanoviště chyba
		nextStageActual = teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);
		assertEquals(1, errors.size());
	}

	@Test
	public void testGetResults() {
		TeamStageAction teamStageAction = new TeamStageAction();
		Map<Integer, String> results = teamStageAction.getResults(TEAM + 4 + CODE);

		assertEquals("Žádné výsledky nejsou k dispozici, pouze čas požadavku.", 1, results.size());

		List<String> errors = new ArrayList<>();

		// team 1 je na stanovišti 1
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1, 1, ONLY_LINEAR);
		EMF.add(teamStage);
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1, 1, ONLY_LINEAR);
		EMF.add(teamStage);
		
		MessageToTeams messageToTeams = new MessageToTeams();
		messageToTeams.setMessage(TEST_MESSAGE);
		messageToTeams.setFromStageNumber(1);
		messageToTeams.setToStageNumber(2);
		EMF.add(messageToTeams);
		
		
		
		// team 1 může pokračovat na druhou stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);
		// team 1 může pokračovat na druhou stage (nedovoluje se přeskočení)
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4, errors);
		assertEquals(1, errors.size());
		errors.clear();
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3, errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4, errors);
		

		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);

		// team 4 zadal 1. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1, errors);

		// team 4 zadal 2. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2, errors);

		// team 4 zadal neexistující kód stanoviště chyba
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);

		
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		
		assertEquals(7, results.size());
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 1. místě"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_1', který vyluštil 3 startovní šifry v "));
		// obsahuje zprávu pro týmy
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(8, results.size());
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 2. místě"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_1', který vyluštil 3 startovní šifry v "));
	}
	
	@Test
	public void testGetStatistics() {
		TeamStageAction teamStageAction = new TeamStageAction();

		List<String> errors = new ArrayList<>();

		// team 1 je na stanovišti 1
		TeamStage teamStage = new TeamStage(TEAM + 1, STAGE + 1, 1, ONLY_LINEAR);
		EMF.add(teamStage);
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1, 1, ONLY_LINEAR);
		EMF.add(teamStage);
		
		Map<Integer, String> resultStats;
		teamStage = new TeamStage();
		
		teamStage.getList();
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(2, resultStats.size());
		assertEquals(" 0 :  2", resultStats.get(Integer.valueOf(40)));
		
		// team 1 může pokračovat na druhou stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);

		// team 1 může pokračovat na čtvrtou stage (nedovoluje se přeskočení)
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3, errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4, errors);

		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);

		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(3, resultStats.size());
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(41)));
		assertEquals(" 3 :  1", resultStats.get(Integer.valueOf(40)));
		
		// team 4 zadal 1. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1, errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(3, resultStats.size());
		assertEquals(" 0 :  2", resultStats.get(Integer.valueOf(41)));
		assertEquals(" 3 :  1", resultStats.get(Integer.valueOf(40)));

		// team 4 zadal 2. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2, errors);

		// team 4 zadal neexistující kód stanoviště chyba
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);

		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(4, resultStats.size());
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(42)));
		assertEquals(" 1 :  1", resultStats.get(Integer.valueOf(41)));
		assertEquals(" 3 :  1", resultStats.get(Integer.valueOf(40)));
	}
	
	@Test
	public void testGetTeamMessageHistory() {
		
		List<String> errors = new ArrayList<>();
		TeamStageAction teamStageAction = new TeamStageAction();
		
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2, errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 1, errors);
		teamStageAction.nextStage(TEAM + 2 + CODE, STAGE + 1, errors);
		teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 2, ONLY_LINEAR, errors);
		Map<Integer, String> teamMessageHistory = teamStageAction.getTeamMessageHistory(TEAM + 1 + CODE);
		assertThat(teamMessageHistory.size(), CoreMatchers.equalTo(3));
		assertThat(teamMessageHistory.get(new Integer(0)), CoreMatchers.containsString(HELP + 2));
		assertThat(teamMessageHistory.get(new Integer(1)), CoreMatchers.containsString(STAGE + 1));
		assertThat(teamMessageHistory.get(new Integer(2)), CoreMatchers.containsString("Chyba"));
	}
}
