package com.studioirregular.bonniesbrunch;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.studioirregular.bonniesbrunch.entity.GameEntity;

public class InputSystem {

//	private static final boolean DO_LOG = false;
	private static final String TAG = "input-system";
	
	// singleton
	public static InputSystem getInstance() {
		if (sInstance == null) {
			sInstance = new InputSystem();
		}
		return sInstance;
	}
	
	public static void releaseInstance() {
		if (sInstance != null) {
			sInstance = null;
		}
	}
	
	private static InputSystem sInstance = null;
	
	private InputSystem() {
		if (Config.DEBUG_LOG_ALLOCATION) Log.d(TAG, "InputSystem allocated");
		tracker = VelocityTracker.obtain();
	}
	
	public static interface Touchable {

		public boolean dispatch(TouchEvent event, GameEntity parent);
	}
	
	public static class TouchEvent {
		public static final int DOWN	= 1;
		public static final int MOVE	= 2;
		public static final int UP		= 3;
		
		public float x;
		public float y;
		public int type;
		
		public TouchEvent(int type, float screenX, float screenY) {
			if (Config.DEBUG_LOG_ALLOCATION) Log.d(TAG, "TouchEvent allocate");
			this.type = type;
			setupTouchPoint(screenX, screenY);
		}
		
		private void setupTouchPoint(float screenX, float screenY) {
			ContextParameters params = ContextParameters.getInstance();
			x = screenX * params.scaleViewToGameX;
			y = screenY * params.scaleViewToGameY;
			
			x -= params.viewportOffsetX;
			y -= params.viewportOffsetY;
			
			// clamp to viewport
			x = (x < 0) ? 0 : (params.gameWidth < x ? params.gameWidth : x);
			y = (y < 0) ? 0 : (params.gameHeight < y ? params.gameHeight : y);
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " type:" + type + ",x:" + x + ",y:" + y;
		}
		
	}
	
	public void onTouchEvent(MotionEvent event) {
		final float x = event.getRawX();
		final float y = event.getRawY();
		int eventType = 0;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			eventType = TouchEvent.DOWN;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			eventType = TouchEvent.MOVE;
		} else if (event.getAction() == MotionEvent.ACTION_UP ||
				event.getAction() == MotionEvent.ACTION_CANCEL) {
			eventType = TouchEvent.UP;
		}
		
		addEvent(new TouchEvent(eventType, x, y));
		
		if (trackVelocity) {
			tracker.addMovement(event);
		}
	}
	
	public TouchEvent next() {
		TouchEvent result = null;
		
		synchronized (events) {
			if (events.isEmpty()) {
				return null;
			}
			result = events.remove(0);
		}
		return result;
	}
	
	public void setTrackingVelocity(boolean track) {
		trackVelocity = track;
	}
	
	public void computeVelocity() {
		tracker.computeCurrentVelocity(1000);
	}
	
	public float getXVelocity() {
		return tracker.getXVelocity();
	}
	
	public float getYVelocity() {
		return tracker.getYVelocity();
	}
	
	private void addEvent(TouchEvent event) {
		synchronized (events) {
			events.add(event);
		}
	}
	
	private List<TouchEvent> events = new ArrayList<TouchEvent>();
	private boolean trackVelocity;
	private VelocityTracker tracker;
}
