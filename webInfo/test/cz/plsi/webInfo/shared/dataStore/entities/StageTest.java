package cz.plsi.webInfo.shared.dataStore.entities;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;


public class StageTest {
	
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
		EntityCommon stage = new Stage("stage1", 1, "help1", "help1", "help1");
		EMF.add(stage);
		
		stage = new Stage("stage2", 1, "help1", "help1", "help1");
		EMF.add(stage);
		
		stage = new Stage("stage2", 1, "help1", "help1", "help1");
		EMF.add(stage);
		
		stage = new Stage("stage3", 1, "help1", "help1", "help1");
		EMF.add(stage);
		
		stage = new Stage("stage1", 1, "help1", "help1", "help1");
		EMF.add(stage);
		assertEquals(3, stage.count());
		
		List<? extends EntityCommon> allEntities = stage.getList();
		for (EntityCommon entityForPrint : allEntities) {
			System.out.println(entityForPrint);
		}
	}
	

}
