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
		TeamStage teamStage = new TeamStage("team1", "stage1");
		EMF.add(teamStage);
		
		teamStage = new TeamStage("team2", "stage2");
		EMF.add(teamStage);
		
		teamStage = TeamStage.getTeamStage("team2", "stage2");
		
		TeamStageHelp teamStageHelp = new TeamStageHelp(teamStage, "help");
		EMF.add(teamStageHelp);
		
		assertEquals(1, teamStageHelp.count());
		
		teamStageHelp = new TeamStageHelp(teamStage, "help2");
		EMF.add(teamStageHelp);
		
		assertEquals(2, teamStageHelp.count());
		
		teamStageHelp = new TeamStageHelp(teamStage, "help");
		EMF.add(teamStageHelp);
		assertEquals(2, teamStageHelp.count());
		
		teamStage = TeamStage.getTeamStage("team1", "stage1");
		teamStageHelp = new TeamStageHelp(teamStage, "help");
		EMF.add(teamStageHelp);
		assertEquals(3, teamStageHelp.count());
		
		List<? extends EntityCommon> allEntities = teamStageHelp.getAll();
		for (EntityCommon entityForPrint : allEntities) {
			System.out.println(entityForPrint);
			
		}
	}
	

}
