package cz.plsi.webInfo.shared.dataStore.entities;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;


public class TeamStageHelpTest {
	
		private final LocalServiceTestHelper helper =
		        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
 
		@Before
		public void setUp() {
		    helper.setUp();
		}
		
		@After
		public void tearDown() {
		    helper.tearDown();
		}
 

	
	@Test
	public void AddAndCountTest() {
//		assertEquals(0,(new TeamStageHelp().count()));
		TeamStage teamStage = new TeamStage("team1", "stage1", 1);
		EMF.add(teamStage);
		
		teamStage = new TeamStage("team2", "stage2", 2);
		EMF.add(teamStage);
		
		teamStage = TeamStage.getTeamStage("team2", "stage2");
		
		TeamStageHelp teamStageHelp = new TeamStageHelp(teamStage, "help", null);
		EMF.add(teamStageHelp);
		
		assertEquals(1, teamStageHelp.count());
		
		teamStageHelp = new TeamStageHelp(teamStage, "help2", null);
		EMF.add(teamStageHelp);
		
		assertEquals(2, teamStageHelp.count());
		
		teamStageHelp = new TeamStageHelp(teamStage, "help", null);
		EMF.add(teamStageHelp);
		assertEquals(2, teamStageHelp.count());
		
		teamStage = TeamStage.getTeamStage("team1", "stage1");
		teamStageHelp = new TeamStageHelp(teamStage, "help", null);
		EMF.add(teamStageHelp);
		assertEquals(3, teamStageHelp.count());
		
		List<? extends EntityCommon> allEntities = teamStageHelp.getList();
		for (EntityCommon entityForPrint : allEntities) {
			System.out.println(entityForPrint);
			
		}
	}
	
	@Test
	public void getListTest() {
//		assertEquals(0,(new TeamStageHelp().count()));
		TeamStage teamStage = new TeamStage("team1", "stage1", 1);
		EMF.add(teamStage);
		
		teamStage = new TeamStage("team2", "stage2", 2);
		EMF.add(teamStage);
		
		teamStage = TeamStage.getTeamStage("team2", "stage2");
		
		TeamStageHelp teamStageHelp = new TeamStageHelp(teamStage, "help", null);
		EMF.add(teamStageHelp);
		
		assertEquals(1, teamStageHelp.count());
		
		teamStageHelp = new TeamStageHelp(teamStage, "help2", null);
		EMF.add(teamStageHelp);
		
		assertEquals(2, teamStageHelp.count());
		
		teamStage = TeamStage.getTeamStage("team1", "stage1");
		teamStageHelp = new TeamStageHelp(teamStage, "help", null);
		EMF.add(teamStageHelp);
		
		teamStageHelp = new TeamStageHelp();
		assertEquals(3, teamStageHelp.getList().size());

		teamStageHelp.setTeamName("team1");
		assertEquals(1, teamStageHelp.getList().size());
		
		teamStageHelp.setTeamName("team2");
		assertEquals(2, teamStageHelp.getList().size());
		
		teamStageHelp.setTeamName("team2");
		teamStageHelp.setHelp("help2");
		assertEquals(1, teamStageHelp.getList().size());
		
		teamStageHelp.setHelp("help10");
		teamStageHelp.setTeamName("team2");
		assertEquals(0, teamStageHelp.getList().size());
		
	}
	

}
