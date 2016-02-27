package com.studioirregular.bonnie.timingsystem.test;

import junit.framework.TestCase;
import android.util.Log;

import com.studioirregular.bonnie.foodsystem.FoodSystem.Food;
import com.studioirregular.bonnie.timingsystem.TimingSystem;
import com.studioirregular.bonnie.timingsystem.TimingSystem.Callback;

public class TestBasicOperations extends TestCase {
	
	private static final String TAG = "test-time";
	
	private static final int TOLERANCE_PERCENTAGE = 2;
	
	private Object dummy = new Object();
	private boolean onTimeup = false;
	
	public void testScheduleTimeup() {
		TimingSystem time = new TimingSystem();
		
		final long TEST_DURATION = 1000;
		final long startTime = System.currentTimeMillis();
		
		Callback callback = new Callback() {

			@Override
			public void onTimeup(Object obj) {
				onTimeup = true;
				
				// check timing
				long elapsedTime = System.currentTimeMillis() - startTime;
				long diff = Math.abs(TEST_DURATION - elapsedTime);
				long tolerableDiff = TEST_DURATION * TOLERANCE_PERCENTAGE / 100;
				assertTrue("diff:" + diff, diff < tolerableDiff);
				
				// check obj
				assertEquals(Food.getBagel(), obj);
			}
			
		};
		time.schedule(callback, Food.getBagel(), TEST_DURATION);
		
		long timeSlice = 1000/60;
		updateLoop(time, timeSlice, 1500);
		
		assertTrue(onTimeup);
	}
	
	private void sleep(final long period) {
		Log.d(TAG, "sleep " + period);
		
		synchronized(dummy) {
			try {
				dummy.wait(period);
			} catch (InterruptedException e) {
				fail("waitForPeriod raise exception:" + e);
			}
		}
	}
	
	private void updateLoop(TimingSystem system, final long timeSlice, final long totalTime) {
		long start = System.currentTimeMillis();
		long prevUpdateTime = start;
		long updateTime = 0;
		long timeDiff = 0;
		
		while (true) {
			updateTime = System.currentTimeMillis();
			timeDiff = updateTime - prevUpdateTime;
			
			system.update(timeDiff);
			
			final long now = System.currentTimeMillis();
			final long sliceElapsed = now - updateTime;
			if (sliceElapsed < timeSlice) {
				sleep(timeSlice - sliceElapsed);
			}
			
			if (now - start >= totalTime) {
				break;
			}
			
			prevUpdateTime = updateTime;
		}
	}
}
