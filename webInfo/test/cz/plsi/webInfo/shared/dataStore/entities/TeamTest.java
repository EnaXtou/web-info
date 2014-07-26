package cz.plsi.webInfo.shared.dataStore.entities;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;


public class TeamTest {
	
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
		EntityCommon team = new Team("team1");
		EMF.add(team);
		
		team = new Team("team2");
		EMF.add(team);
		
		team = new Team("team2");
		EMF.add(team);
		
		team = new Team("team3");
		EMF.add(team);
		
		team = new Team("team1");
		EMF.add(team);
		assertEquals(3, team.count());
		
		List<? extends EntityCommon> allTeamsAndStages = team.getAll();
		for (EntityCommon teamStageForPrint : allTeamsAndStages) {
			System.out.println(teamStageForPrint);
		}
	}
	

}
