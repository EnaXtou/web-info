package cz.plsi.webInfo.shared.dataStore.entities;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cz.plsi.webInfo.shared.dataStore.EMF;


public class HelpTest {
	
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
		EntityCommon help = new Help("help1");
		EMF.add(help);
		
		help = new Help("help2");
		EMF.add(help);
		
		help = new Help("help2");
		EMF.add(help);
		
		help = new Help("help3");
		EMF.add(help);
		
		help = new Help("help1");
		EMF.add(help);
		assertEquals(3, help.count());
		
		List<? extends EntityCommon> allEntities = help.getAll();
		for (EntityCommon entity : allEntities) {
			System.out.println(entity);
		}
	}
	

}
