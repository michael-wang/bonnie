package com.studioirregular.bonniesbrunch.test;

import junit.framework.TestCase;

import com.studioirregular.bonniesbrunch.LevelSystem.GameLevel;
import com.studioirregular.bonniesbrunch.base.ObjectBase;
import com.studioirregular.bonniesbrunch.base.ObjectManager;
import com.studioirregular.bonniesbrunch.entity.CustomerManager;
import com.studioirregular.bonniesbrunch.entity.CustomerManager.SeatConfig;

public class ObjectSystemTest extends TestCase {

	public void testAllocationLog() {
		TestObject o = new TestObject("test");
	}
	
	class TestObject extends ObjectBase {

		public TestObject(String name) {
			this.name = name;
		}
		
		@Override
		public void update(long timeDelta, ObjectBase parent) {
			super.update(timeDelta, parent);
			lastUpdateTimeDelta = timeDelta;
			yourDad = parent;
		}
		
		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}
		
		String name;
		long lastUpdateTimeDelta;
		ObjectBase yourDad;
	}
	
	public void testObjectManager() {
		ObjectManager mgr = new ObjectManager();
		final int OBJECT_COUNT = 3;
		
		// test addition
		TestObject[] objects = new TestObject[OBJECT_COUNT];
		for (int i = 0; i < OBJECT_COUNT; i++) {
			objects[i] = new TestObject("obj-" + i);
			mgr.add(objects[i]);
		}
		mgr.update(0, null);
		
		assertEquals(OBJECT_COUNT, mgr.getCount());
		
		// check object sequence
		for (int i = 0; i < OBJECT_COUNT; i++) {
			TestObject t = (TestObject)mgr.getObject(i);
			assertNotNull(t);
			assertEquals(objects[OBJECT_COUNT - 1 - i].name, t.name);
		}
		
		// check update
		mgr.update(999, null);
		for (int i = 0; i < OBJECT_COUNT; i++) {
			assertEquals(999, objects[i].lastUpdateTimeDelta);
			assertEquals(mgr, objects[i].yourDad);
		}
		
		// check remove
		for (int i = 0; i < OBJECT_COUNT; i++) {
			mgr.remove(objects[i]);
			mgr.update(0, null);
			
			assertEquals("when i:" + i, OBJECT_COUNT - i - 1, mgr.getCount());
		}
	}
	
	public void testCustomerManager() {
		GameLevel gameLevel = new GameLevel();
		gameLevel.specialLevel = false;
		
		CustomerManager manager = new CustomerManager(0);
		SeatConfig[] configs = new SeatConfig[4];
		configs[0] = new SeatConfig(10, 67, 204, 210);
		configs[1] = new SeatConfig(192, 67, 204, 210);
		configs[2] = new SeatConfig(374, 67, 204, 210);
		configs[3] = new SeatConfig(556, 67, 204, 210);
		manager.setup(0, 0, 720, 277);
		//manager.setup(gameLevel, configs, false);
		
		assertEquals(0.0f, manager.box.left);
		assertEquals(0.0f, manager.box.top);
		assertEquals(720.0f, manager.box.width());
		assertEquals(277.0f, manager.box.height());
	}
}
