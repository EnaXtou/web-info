package cz.plsi.webInfo.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
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

	private static final String HELP_PREFIX = "Nápověda ";
	public static final String WELCOME_START = "Tak vás tu vítáme! Stanoviště ";
	public static final String WELCOME_END = "";
	public static final String CODE = "_code";
	public static final String TEAM = "team_";
	public static final String HELP = "help_";
	public static final String STAGE = "stage_";
	public static final String HELP_1_R = "Nápověda: help_1_";
	public static final String HELP_2_R = "Nápověda: help_2_";
	public static final String RESULT_R = "Řešení: result_";
	public static final String HELP_1 = "help_1_";
	public static final String HELP_2 = "help_2_";
	public static final String RESULT = "result_";
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private static String getHelpString(String prefix, String stage, String suffix) {
		return prefix + stage + ": " + suffix;
	}
	
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

		createStagesForBranch(5, "L", 4, 3);
		createStagesForBranch(5, "A", 0, 3);
		createStagesForBranch(5, "B", 0, 3);
		createStagesForBranch(5, "C", 0, 0);
		
	}

	public static void createStagesForBranch(int numberOfStages, String stageBranch, int constraint, int numberOfHelps) {
		for (int i = 1; i <= numberOfStages; i++) {
			EntityCommon stage = null;
			if (i == 1) {
				stage = new Stage(STAGE + i + stageBranch, i, null, 
						numberOfHelps > 0 ? HELP_1 + i : null, 
						numberOfHelps > 1 ? HELP_2 + i : null, 
						numberOfHelps > 2 ? RESULT + i : null, stageBranch, constraint);
			} else if (i == 2) {
				stage = new Stage(STAGE + i + stageBranch, i, null, HELP_1 + i, null, null, stageBranch);
				
			} else {
				stage = new Stage(STAGE + i + stageBranch, i, null, 
						numberOfHelps > 0 ? HELP_1 + i : null, 
						numberOfHelps > 1 ? HELP_2 + i : null, 
						numberOfHelps > 2 ? RESULT + i : null, stageBranch);
				
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
		
		
		Stage stage = new Stage();
		stage.setName(STAGE + 4 + "B");
		stage = (Stage) EMF.find(stage);
		stage.setHelp2(null);
		EMF.update(stage);
		
		stage = new Stage();
		stage.setName(STAGE + 5 + "B");
		stage = (Stage) EMF.find(stage);
		stage.setHelp2(null);
		EMF.update(stage);
		
		// team 1 je na stanovišti C.1
		EntityCommon teamStage = null;
		teamStage = new TeamStage(TEAM + 1, STAGE + 1 + "C", 1, "C");
		EMF.add(teamStage);

		// team 1 je na stanovišti B.1
		teamStage = new TeamStage(TEAM + 1, STAGE + 1 + "B", 1, "B");
		EMF.add(teamStage);
		
		// team 2 je na stanovišti 1
		teamStage = new TeamStage(TEAM + 2, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		TeamStageAction teamStageAction = new TeamStageAction();
		
		// team 1 bych chycen širokem
		String actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 0, "B", errors);
		assertEquals(TeamStageAction.HELP_STOLEN, actual);
		
		// team 1 použije již použité heslo
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 0, "B", errors);
		assertEquals(1, errors.size());
		errors.clear();

		// bez chyby tým dostane nápovědu.
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 1, "B", errors);
//		assertEquals(HELP_1_R + 1, actual);
		assertEquals(getHelpString(HELP_PREFIX, "B.1", HELP_1 + 1), actual);
		
		// team 1 použije již použité heslo
		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 1, "B", errors);
		assertEquals(1, errors.size());
		errors.clear();

		actual = teamStageAction.getHelp(TEAM + 1 + CODE, HELP + 3, "B", errors);
//		assertEquals(HELP_2_R + 1, actual);
		assertEquals(getHelpString(HELP_PREFIX, "B.1", HELP_2 + 1), actual);

		// druhý tým je na druhém stanovišti
		// první stále na prvním stanovyšti
		teamStage = new TeamStage(TEAM + 2, STAGE + 2 + "B", 2, "B");
		EMF.add(teamStage);
		
		// druhý tým použije heslo již použitý týmem jedna
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 1, "B", errors);
		errors.size();
		assertEquals(getHelpString(HELP_PREFIX, "B.2", HELP_1 + 2), actual); // dostanu 1. nápovědu z 2. stanoviště

		// team dva si řekne o další nápovědu, ale na druhém stanovišti již žádná není k dispozici
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 2, "B", errors);
		errors.size();
		assertEquals(1, errors.size()); // již není nápověda
		errors.clear();
		
		// druhý tým je na třetím stanovišti a může použít heslo 2 znovu
		teamStage = new TeamStage(TEAM + 2, STAGE + 3 + "B", 3, "B");
		EMF.add(teamStage);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 2, "B", errors);
		assertTrue(errors.isEmpty());
		assertEquals(getHelpString(HELP_PREFIX, "B.3", HELP_1 + 3), actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 3, "B", errors);
		assertTrue(errors.isEmpty());
		assertEquals(getHelpString(HELP_PREFIX, "B.3", HELP_2 + 3), actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 6, "B", errors);
		assertTrue(errors.isEmpty());
		assertEquals(getHelpString("Řešení ", "B.3", RESULT + 3), actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 7, "B", errors);
		assertFalse(errors.isEmpty());
		errors.clear();
		
		teamStage = new TeamStage(TEAM + 2, STAGE + 4 + "B", 4, "B");
		EMF.add(teamStage);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 5, "B", errors);
		assertTrue(errors.isEmpty());
		assertEquals(getHelpString("Nápověda ", "B.4", HELP_1 + 4), actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 7, "B", errors);
		assertTrue(errors.isEmpty());
		assertEquals(getHelpString("Řešení ", "B.4", RESULT +4), actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 8, "B", errors);
		assertFalse(errors.isEmpty());
		errors.clear();
		
		teamStage = new TeamStage(TEAM + 2, STAGE + 5 + "B", 5, "B");
		EMF.add(teamStage);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 9, "B", errors);
		assertTrue(errors.isEmpty());
		assertEquals(getHelpString("Nápověda ", "B.5", HELP_1 + 5), actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 10, "B", errors);
		assertTrue(errors.isEmpty());
		assertEquals(getHelpString("Řešení ", "B.5", RESULT +5), actual);
		
		actual = teamStageAction.getHelp(TEAM + 2 + CODE, HELP + 11, "B", errors);
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
		Stage stageToUpdate = new Stage(STAGE + 1 + "A");
		stageToUpdate.setBranch("A");
		stageToUpdate = (Stage) EMF.find(stageToUpdate);
		stageToUpdate.setMessage("Vítejte na startu!");
		EMF.update(stageToUpdate);
		
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
		assertEquals("Vítejte na startu!", nextStageActual);

		// team 4 zadal 2. stanoviště
		nextStageActual = teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2 + "A", errors);
		assertEquals(WELCOME_START + "A." + 2 + WELCOME_END,nextStageActual);

		// team 4 zadal neexistující kód stanoviště chyba
		nextStageActual = teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);
		assertEquals(1, errors.size());
	}

	@Test
	public void testGetResults() throws InterruptedException {
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
		messageToTeams.setMessage("Test message 1-2.A");
		messageToTeams.setFromStageNumber(1);
		messageToTeams.setToStageNumber(2);
		messageToTeams.setBranch("A");
		EMF.add(messageToTeams);
		
		messageToTeams = new MessageToTeams();
		messageToTeams.setMessage("Test message 3-8 points");
		messageToTeams.setFromStageNumber(3);
		messageToTeams.setToStageNumber(8);
		EMF.add(messageToTeams);
		
		messageToTeams = new MessageToTeams();
		messageToTeams.setMessage("Test message 2.L points");
		messageToTeams.setFromStageNumber(2);
		messageToTeams.setToStageNumber(2);
		messageToTeams.setBranch("L");
		EMF.add(messageToTeams);
		
		
		
		// team 1 může pokračovat na druhou stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		assertEquals(7, results.size());
		assertEquals("Test message 1-2.A", results.get(Integer.valueOf(-3)));
		
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
		// team 4 zadal 3. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 3 + "A", errors);
		// team 4 zadal 4. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 4 + "A", errors);

		// team 4 zadal neexistující kód stanoviště chyba
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);

		
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		assertEquals(7, results.size());
		assertEquals("Test message 3-8 points", results.get(Integer.valueOf(-3)));
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 1. místě s 3 startovními šiframi v"));
		
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_1', který vyluštil 3 startovní šifry v "));
		// obsahuje zprávu pro týmy
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(7, results.size());
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 2. místě s 3 startovními šiframi v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_1', který vyluštil 3 startovní šifry v "));
		
		// team 4 zadal 5. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "B", errors);
		
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		assertEquals(8, results.size());
		assertEquals("Test message 3-8 points", results.get(Integer.valueOf(-3)));
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 2. místě s 3 startovními šiframi"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_4', který vyluštil 4 startovní šifry v "));
		
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(8, results.size());
		assertEquals("Test message 3-8 points", results.get(Integer.valueOf(-3)));
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 1. místě s 4 startovními šiframi v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_4', který vyluštil 4 startovní šifry v "));
		
		// team 1 zadal 1. stanoviště s pěti body
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 1 + "B", errors);
		
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		// stejné stanoviště zredukovaná statistika -> 7
		assertEquals(7, results.size());
		assertEquals("Test message 3-8 points", results.get(Integer.valueOf(-3)));
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 2. místě s 4 startovními šiframi v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_4', který vyluštil 4 startovní šifry v "));
		// obsahuje zprávu pro týmy
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(7, results.size());
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 1. místě s 4 startovními šiframi v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_4', který vyluštil 4 startovní šifry v "));
		
		// team 1 zadal 1. stanoviště s pěti body
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "B", errors);
		// team 1 zadal 1. stanoviště s pěti body
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3 + "B", errors);
		
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		assertEquals(8, results.size());
		assertEquals("Test message 3-8 points", results.get(Integer.valueOf(-3)));
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 1. místě s 6 startovními šiframi v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_1', který vyluštil 6 startovní šifry v "));
		// obsahuje zprávu pro týmy
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(8, results.size());
		assertEquals("Test message 3-8 points", results.get(Integer.valueOf(-3)));
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 2. místě s 4 startovními šiframi v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_1', který vyluštil 6 startovní šifry v "));
		
		// team 4 zadal 1. stanoviště z lineární části, ale má pouze 5 ponožek a 1. stanoviště z lineární části (tedy potenciálně 6 ponožek), přesto vede
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "L", errors);
		
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(8, results.size());
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 1. místě na stanovišti L.1 v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_4', který byl na stanovišti L.1 v "));
		
		
		teamStageAction.nextStage(TEAM + 1 + CODE,  STAGE + 4 + "B", errors);
		results = teamStageAction.getResults(TEAM + 1 + CODE);
		assertEquals(9, results.size());
		assertEquals("Test message 3-8 points", results.get(Integer.valueOf(-3)));
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 2. místě s 7 startovními šiframi v"));
		assertThat(results.get(Integer.valueOf(0)), CoreMatchers.startsWith("Vede tým 'team_4', který byl na stanovišti L.1 v "));
		
		Thread.sleep(12000);
		teamStageAction.getHelp(TEAM + 4 + CODE, "reseni", "L", errors);
		
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2 + "L", errors);
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		assertEquals(10, results.size());
		assertEquals("Test message 2.L points", results.get(Integer.valueOf(-3)));
		
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 3 + "L", errors);
		
		errors.clear();
		
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 1 + "L", errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "L", errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3 + "L", errors);
		
		Thread.sleep(12000);
		teamStageAction.getHelp(TEAM + 1 + CODE, "reseni", "L", errors);
		
		assertTrue(errors.isEmpty());
		
		results = teamStageAction.getResults(TEAM + 4 + CODE);
		// zpráva se zobrazuje pouze na druhém stanovišti L větve
		assertEquals(8, results.size());
		
		assertThat(results.get(Integer.valueOf(-2)), CoreMatchers.startsWith("Váš tým je aktuálně na 2. místě"));
		assertThat(results.get(Integer.valueOf(1)), CoreMatchers.startsWith("Vede tým 'team_1', který byl na stanovišti L.3 v "));
	}
	
	@Test
	public void testGetStatistics() {
		TeamStageAction teamStageAction = new TeamStageAction();

		List<String> errors = new ArrayList<>();

		// team 1 je na stanovišti 1
		TeamStage teamStage = new TeamStage(TEAM + 1, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		// team 2 je na stanovišti 1 -> přidat bod ke stage => default je nula - stage jedna a stage dva ve větvi A je jedna
		teamStage = new TeamStage(TEAM + 2, STAGE + 1 + "A", 1, "A");
		EMF.add(teamStage);
		
		Map<Integer, String> resultStats;
		teamStage = new TeamStage();
		
		teamStage.getList();
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(2, resultStats.size());
		assertEquals(" 0 :  2", resultStats.get(Integer.valueOf(40)));
		
		// team 1 může pokračovat na druhou stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);

		// team 1 může pokračovat na čtvrtou stage (nedovoluje se přeskočení)
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 3 + "A", errors);
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 4 + "A", errors);

		// team 1 zadal znovu druhou stage chyba, již navštívená stage
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 2 + "A", errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(3, resultStats.size());
		assertEquals(" 3 :  1", resultStats.get(Integer.valueOf(40)));
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(41)));
		
		// team 4 zadal 1. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "A", errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(3, resultStats.size());
		assertEquals(" 3 :  1", resultStats.get(Integer.valueOf(40)));
		assertEquals(" 0 :  2", resultStats.get(Integer.valueOf(41)));

		// team 4 zadal 2. stanoviště
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2 + "A", errors);

		// team 4 zadal neexistující kód stanoviště chyba
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + "_NOT_EXISTS", errors);

		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(4, resultStats.size());
		assertEquals(" 3 :  1", resultStats.get(Integer.valueOf(40)));
		assertEquals(" 1 :  1", resultStats.get(Integer.valueOf(41)));
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(42)));
		
		// team 1 vstoupil do lineární části
		teamStageAction.nextStage(TEAM + 1 + CODE, STAGE + 1 + "L", errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(5, resultStats.size());
		assertEquals("L.1 :  1", resultStats.get(Integer.valueOf(20)));
		assertEquals(" 1 :  1", resultStats.get(Integer.valueOf(40)));
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(41)));
		
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 3 + "A", errors);
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "B", errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(5, resultStats.size());
		assertEquals("L.1 :  1", resultStats.get(Integer.valueOf(20)));
		assertEquals(" 3 :  1", resultStats.get(Integer.valueOf(40)));
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(41)));
		
		// team 4 vstoupil do lineární části
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 1 + "L", errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(4, resultStats.size());
		assertEquals("L.1 :  2", resultStats.get(Integer.valueOf(20)));
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(40)));
		
		teamStageAction.nextStage(TEAM + 4 + CODE, STAGE + 2 + "L", errors);
		
		resultStats = teamStageAction.getStatistics(teamStage.getList());
		assertEquals(5, resultStats.size());
		assertEquals("L.2 :  1", resultStats.get(Integer.valueOf(20)));
		assertEquals("L.1 :  1", resultStats.get(Integer.valueOf(21)));
		assertEquals(" 0 :  1", resultStats.get(Integer.valueOf(40)));
		
	}

}
