package cz.plsi.webInfo.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;





import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;
import cz.plsi.webInfo.shared.dataStore.entities.Stage;
import cz.plsi.webInfo.shared.dataStore.entities.Team;


public class TeamStageActionParallelTimeHelpTest {

	private static final String TEAM_1_CODE = TeamStageActionParallelTest.TEAM + 1 + TeamStageActionParallelTest.CODE;
	private static final String TEAM_2_CODE = TeamStageActionParallelTest.TEAM + 2 + TeamStageActionParallelTest.CODE;
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	
	@Before
	public void setUp() {
		helper.setUp();
		TeamStageActionParallelTest.createStagesForBranch(3, "A", 0, 0);
		TeamStageActionParallelTest.createStagesForBranch(3, "B", 0, 0);
		TeamStageActionParallelTest.createStagesForBranch(3, "L", 3, 0);
		
		Stage stage = new Stage(TeamStageActionParallelTest.STAGE + 2 + "A");
		stage = (Stage) EMF.find(stage);
		stage.setResult("Time result A.2 after 10 seconds.");
		stage.setTimeToResult(1.0 / 6);
		EMF.update(stage);
		
		stage = new Stage(TeamStageActionParallelTest.STAGE + 1 + "B");
		stage = (Stage) EMF.find(stage);
		stage.setResult("Time result B.1 after 15 seconds.");
		stage.setTimeToResult(1.0 / 4);
		EMF.update(stage);
		
		stage = new Stage(TeamStageActionParallelTest.STAGE + 1 + "L");
		stage = (Stage) EMF.find(stage);
		stage.setResult("Time result L.1 after 10 seconds.");
		stage.setTimeToResult(1.0 / 6);
		EMF.update(stage);
		
		Team team = new Team(TeamStageActionParallelTest.TEAM + 1);
		team.setCode(TEAM_1_CODE);
		EMF.add(team);
		team = new Team(TeamStageActionParallelTest.TEAM + 2);
		team.setCode(TEAM_2_CODE);
		EMF.add(team);
		
	}
	
	
	@Test
	public void testHelpAfterTimePassed() throws InterruptedException {
		List<String> errors = new ArrayList<>();
		TeamStageAction teamStageAction = new TeamStageAction();
		
		teamStageAction.nextStage(TEAM_1_CODE, TeamStageActionParallelTest.STAGE + 1 + "A", errors);
		teamStageAction.nextStage(TEAM_2_CODE, TeamStageActionParallelTest.STAGE + 1 + "A", errors);
		Map<Integer, String> results = teamStageAction.getResults(TEAM_1_CODE);
		Thread.sleep(12000);
		// Žádná nápověda se nezobrazí - tým není na stanovišti s nápovědou
		assertEquals(5, results.size());
		
		
		teamStageAction.nextStage(TEAM_1_CODE, TeamStageActionParallelTest.STAGE + 2 + "A", errors);
		results = teamStageAction.getResults(TEAM_1_CODE);
		// Žádná nápověda se nezobrazí - tým právě přišel na stanoviště, nápověda se zobrazí až za 10 vteřin
		assertEquals(6, results.size());
		Thread.sleep(12000);
		// Řešení se zobrazí - tým A je na stanovišti již 12 vteřin
		results = teamStageAction.getResults(TEAM_1_CODE);
		assertEquals(8, results.size());
		String resultOfStage = results.get(-15);
		assertEquals("Řešení A.2: Time result A.2 after 10 seconds.", resultOfStage);
		
		// Řešení se nezobrazí - tým 2 není na stanovišti, které má časované řešení
		results = teamStageAction.getResults(TEAM_2_CODE);
		assertEquals(6, results.size());
		resultOfStage = results.get(-15);
		
		teamStageAction.nextStage(TEAM_2_CODE, TeamStageActionParallelTest.STAGE + 1 + "B", errors);
		// Řešení se zobrazí - tým A je na stanovišti již 12 vteřin
		results = teamStageAction.getResults(TEAM_1_CODE);
		assertEquals(7, results.size());
		resultOfStage = results.get(-15);
		assertEquals("Řešení A.2: Time result A.2 after 10 seconds.", resultOfStage);
		
		Thread.sleep(5000);
		results = teamStageAction.getResults(TEAM_2_CODE);
		// Ještě nepřišel čas na řešení
		assertEquals(5, results.size());
		Thread.sleep(11000);
		// Řešení se zobrazí 
		results = teamStageAction.getResults(TEAM_2_CODE);
		assertEquals(7, results.size());
		resultOfStage = results.get(-15);
		assertEquals("Řešení B.1: "
				+ "Time result B.1 after 15 seconds.", resultOfStage);
		
		teamStageAction.nextStage(TEAM_2_CODE, TeamStageActionParallelTest.STAGE + 2 + "B", errors);
		results = teamStageAction.getResults(TEAM_2_CODE);
		// Tým dva již postoupil na další stage, nebude se mu zobrazovat nápověda
		assertEquals(6, results.size());
		
		// Týmu 1 se stále zobrazuje
		results = teamStageAction.getResults(TEAM_1_CODE);
		assertEquals(8, results.size());
		resultOfStage = results.get(-15);
		assertEquals("Řešení A.2: Time result A.2 after 10 seconds.", resultOfStage);
		
		teamStageAction.nextStage(TEAM_1_CODE, TeamStageActionParallelTest.STAGE + 1 + "B", errors);
		Thread.sleep(16000);
		// Tým jedna vidí obě nápovědy
		results = teamStageAction.getResults(TEAM_1_CODE);
		assertEquals(8, results.size());
		resultOfStage = results.get(-15);
		assertEquals("Řešení A.2: Time result A.2 after 10 seconds.", resultOfStage);
		resultOfStage = results.get(-16);
		assertEquals("Řešení B.1: Time result B.1 after 15 seconds.", resultOfStage);
		
		teamStageAction.nextStage(TEAM_1_CODE, TeamStageActionParallelTest.STAGE + 1 + "L", errors);
		String help = teamStageAction.getHelp(TEAM_1_CODE, "reseni", "L", errors);
		assertFalse(errors.isEmpty());
		Thread.sleep(16000);
		// Tým postoupil do lineární části, nápovědy k paralelním větvím se již nezobrazují - až po vyžádání řešení
		results = teamStageAction.getResults(TEAM_1_CODE);
		assertEquals("Řešení pro lineární část se objevuje až na základě požadavku!", 8, results.size());
		help = teamStageAction.getHelp(TEAM_1_CODE, "reseni", "L", errors);
		assertThat(help, CoreMatchers.equalTo("Řešení L.1: Time result L.1 after 10 seconds."));
	}

}
