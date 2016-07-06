package cz.plsi.webInfo.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
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

public class TeamStageActionParallelTest {

	private static final String TEST_MESSAGE = "Test message";
	private static final String WELCOME_START = "Tak vás tu vítáme! ";
	private static final String WELCOME_END = ". stanoviště, že vám to ale trvalo.";
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

		createStagesForBranch(5, "L", 4);
		createStagesForBranch(5, "A", 0);
		createStagesForBranch(5, "B", 0);
		createStagesForBranch(5, "C", 0);
		
	}

	private void createStagesForBranch(int numberOfStages, String stageBranch, int constraint) {
		for (int i = 1; i < numberOfStages; i++) {
			EntityCommon stage = null;
			if (i == 1) {
				stage = new Stage(STAGE + i + stageBranch, i, null, HELP_1 + i, HELP_2 + i, RESULT + i, stageBranch, constraint);
			} else if (i == 2) {
				stage = new Stage(STAGE + i + stageBranch, i, null, HELP_1 + i, null, null, stageBranch);
				
			} else {
				stage = new Stage(STAGE + i + stageBranch, i, null, HELP_1 + i, HELP_2 + i, RESULT + i, stageBranch);
				
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
	public void testGetHelp() {
		List<String> errors = new ArrayList<>();
		
		Team team = new Team();
		team.setName(TEAM + 1);
		team = team.getList().get(0);
		team = (Team) EMF.find(team); 
		team.setHelpsCount(-1);
		
		EMF.update(team);
		
		// team 1 je na stanovišti 1
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1 + "B", 1);
		EMF.add(teamStage);

		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1 + "A", 1);
		EMF.add(teamStage);
		TeamStageAction teamStageAction = new TeamStageAction();
		
		// team 1 bych chycen širokem
		String actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 0, errors);
		assertEquals(TeamStageAction.HELP_STOLEN, actual);
		
		// team 1 použije již použité heslo
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 0, errors);
		assertEquals(1, errors.size());
		errors.clear();

		// bez chyby tým dostane nápovědu.
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 1, errors);
		assertEquals(HELP_1_R + 1, actual);
		
		// team 1 použije již použité heslo
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 1, errors);
		assertEquals(1, errors.size());
		errors.clear();

		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 3, errors);
		assertEquals(HELP_2_R + 1, actual);

		// druhý tým je na druhém stanovišti
		// první stále na prvním stanovyšti
		teamStage = new TeamStage(TEAM + 2, STAGE + 2 + "B", 2);
		EMF.add(teamStage);
		
		// druhý tým použije heslo již použitý týmem jedna
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 1, errors);
		errors.size();
		assertEquals(HELP_1_R + 2, actual); // dostanu 1. nápovědu z 2. stanoviště

		// team dva si řekne o další nápovědu, ale na druhém stanovišti již žádná není k dispozici
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 2, errors);
		errors.size();
		assertEquals(1, errors.size()); // již není nápověda
		errors.clear();
		
		// druhý tým je na třetím stanovišti a může použít heslo 2 znovu
		teamStage = new TeamStage(TEAM + 2, STAGE + 3 + "B", 3);
		EMF.add(teamStage);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 2, errors);
		assertTrue(errors.isEmpty());
		assertEquals(HELP_1_R + 3, actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 3, errors);
		assertTrue(errors.isEmpty());
		assertEquals(HELP_2_R + 3, actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 6, errors);
		assertTrue(errors.isEmpty());
		assertEquals(RESULT_R + 3, actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 7, errors);
		assertFalse(errors.isEmpty());
		
		
		
	}

	@Test
	public void testNextStage() {
		List<String> errors = new ArrayList<>();

		// team 1 je na stanovišti 1
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);

		TeamStageAction teamStageAction = new TeamStageAction();
		// team 1 již je na první stage
		teamStageAction.nextStage(TEAM + 1, STAGE + 1 + "A", errors);
		assertEquals(1, errors.size());
		errors.clear();
		
		// team 1 nemůže pokračovat na první stage B větve, nebyl na první stage
		String nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "B", errors);
		assertEquals(1, errors.size());
		errors.clear();

		// team 1 nemůže pokračovat na první stage lineární větve, nemá dostatečný počet splněných stanovišť
		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 1 + "L", errors);
		assertEquals(1, errors.size());
		errors.clear();
		
		// team 1 může pokračovat na druhou stage větve A
		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);
		assertEquals(WELCOME_START + "A." + 2 + WELCOME_END, nextStageActual);

		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3 + "A", errors);
		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4 + "A", errors);
		assertEquals(WELCOME_START + "A." + 4 + WELCOME_END, nextStageActual);
		
		// team 1 může pokračovat na první stage lineární větve, má dostatečný počet splněných stanovišť
		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 1 + "L", errors);
		assertEquals(WELCOME_START + "L." + 1 + WELCOME_END, nextStageActual);

		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		nextStageActual = teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);
		assertEquals(1, errors.size());
		errors.clear();

		// team 4 zadal 1. stanoviště
		nextStageActual = teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "A", errors);
		assertEquals(WELCOME_START + "A." + 1 + WELCOME_END,nextStageActual);

		// team 4 zadal 2. stanoviště
		nextStageActual = teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2 + "A", errors);
		assertEquals(WELCOME_START + "A." + 2 + WELCOME_END,nextStageActual);

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
		EntityCommon teamStage = new TeamStage(TEAM + 1, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		
		MessageToTeams messageToTeams = new MessageToTeams();
		messageToTeams.setMessage(TEST_MESSAGE);
		messageToTeams.setFromStageNumber(1);
		messageToTeams.setToStageNumber(2);
		EMF.add(messageToTeams);
		
		
		
		// team 1 může pokračovat na druhou stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);
		// team 1 může pokračovat na druhou stage (nedovoluje se přeskočení)
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4 + "A", errors);
		assertEquals(1, errors.size());
		errors.clear();
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3 + "A", errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4 + "A", errors);
		

		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);

		// team 4 zadal 1. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "A", errors);

		// team 4 zadal 2. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2 + "A", errors);

		// team 4 zadal neexistující kód stanoviště chyba
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);

		
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		
		assertEquals(7, results.size());
		assertEquals("Váš tým je aktuálně na 1. místě.", results.get(Integer.valueOf(-2)));
		assertTrue(results.get(Integer.valueOf(0)).startsWith("Vede tým 'team_1', který byl na 4. stanovišti v "));
		// obsahuje zprávu pro týmy
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(8, results.size());
		assertEquals("Váš tým je aktuálně na 2. místě.", results.get(Integer.valueOf(-2)));
		assertTrue(results.get(Integer.valueOf(0)).startsWith("Vede tým 'team_1', který byl na 4. stanovišti v "));
	}
	
	@Test
	public void testGetStatistics() {
		TeamStageAction teamStageAction = new TeamStageAction();

		List<String> errors = new ArrayList<>();

		// team 1 je na stanovišti 1
		TeamStage teamStage = new TeamStage(TEAM + 1, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		
		Map<Integer, String> resultStats;
		teamStage = new TeamStage();
		
		teamStage.getList();
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(2, resultStats.size());
		assertEquals("1. stanoviště: 2", resultStats.get(Integer.valueOf(40)));
		
		// team 1 může pokračovat na druhou stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);

		// team 1 může pokračovat na čtvrtou stage (nedovoluje se přeskočení)
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3 + "A", errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4 + "A", errors);

		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);

		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(3, resultStats.size());
		assertEquals("1. stanoviště: 1", resultStats.get(Integer.valueOf(40)));
		assertEquals("4. stanoviště: 1", resultStats.get(Integer.valueOf(41)));
		
		// team 4 zadal 1. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "A", errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(3, resultStats.size());
		assertEquals("1. stanoviště: 2", resultStats.get(Integer.valueOf(40)));
		assertEquals("4. stanoviště: 1", resultStats.get(Integer.valueOf(41)));

		// team 4 zadal 2. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2 + "A", errors);

		// team 4 zadal neexistující kód stanoviště chyba
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);

		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(4, resultStats.size());
		assertEquals("1. stanoviště: 1", resultStats.get(Integer.valueOf(40)));
		assertEquals("2. stanoviště: 1", resultStats.get(Integer.valueOf(41)));
		assertEquals("4. stanoviště: 1", resultStats.get(Integer.valueOf(42)));
	}

}
