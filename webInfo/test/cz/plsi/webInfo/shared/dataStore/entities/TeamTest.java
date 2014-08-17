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
		assertEquals(1, team.count());
		
		team = new Team("team2");
		EMF.add(team);
		assertEquals(2, team.count());
		
		team = new Team("team2");
		EMF.add(team);
		assertEquals(2, team.count());
		
		team = new Team("team3");
		EMF.add(team);
		assertEquals(3, team.count());
		
		team = new Team("team1");
		EMF.add(team);
		assertEquals(3, team.count());
		
		List<? extends EntityCommon> allTeamsAndStages = (new Team()).getList();
		for (EntityCommon teamStageForPrint : allTeamsAndStages) {
			System.out.println(teamStageForPrint);
		}
	}
	
	@Test
	public void TestGetList() {
		EntityCommon team = new Team("team1");
		EMF.add(team);
		
		team = new Team("team2");
		EMF.add(team);
		
		team = new Team("team3");
		EMF.add(team);
		
		team = new Team("team4");
		EMF.add(team);
		
		team = new Team("tym1");
		EMF.add(team);
		
		//pouze poslední tým
		assertEquals(1, (new Team("tym1")).getList().size());
		assertTrue((new Team("tym1")).getList().get(0).equals(new Team("tym1")));
		
		//pouze team4
		assertEquals(1, (new Team("team4")).getList().size());
		assertTrue((new Team("team4")).getList().get(0).equals(new Team("team4")));
		
		//neexistující team
		assertEquals(0, (new Team("tejmNejni")).getList().size());
	}
	

}
