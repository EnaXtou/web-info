package cz.plsi.webInfo.shared.dataStore.entities;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;


public class TeamStageTest {
	
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
		EntityCommon teamStage = new TeamStage("team1", "stage1");
		EMF.add(teamStage);
		
		teamStage = new TeamStage("team2", "stage2");
		EMF.add(teamStage);
		
		teamStage = new TeamStage("team2", "stage3");
		EMF.add(teamStage);
		
		teamStage = new TeamStage("team2", "stage3");
		EMF.add(teamStage);
		
		teamStage = new TeamStage("team1", "stage1");
		EMF.add(teamStage);
		assertEquals(3, teamStage.count());
		
		List<? extends EntityCommon> allTeamsAndStages = teamStage.getList();
		for (EntityCommon teamStageForPrint : allTeamsAndStages) {
			System.out.println(teamStageForPrint);
			
		}
	}
	

}
